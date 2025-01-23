package com.example.hnh.group;

import com.example.hnh.global.S3Service;
import com.example.hnh.group.dto.GroupDetailResponseDto;
import com.example.hnh.group.dto.GroupRankingResponseDto;
import com.example.hnh.group.dto.GroupRequestDto;
import com.example.hnh.group.dto.GroupResponseDto;
import com.example.hnh.interestgroup.InterestGroupRepository;
import com.example.hnh.member.Member;
import com.example.hnh.member.MemberRepository;
import com.example.hnh.member.MemberRole;
import com.example.hnh.user.User;
import com.example.hnh.user.UserRepository;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final RedisRankingRepository redisRankingRepository;
    private final InterestGroupRepository interestGroupRepository;

    public GroupService(GroupRepository groupRepository, S3Service s3Service, UserRepository userRepository, MemberRepository memberRepository, RedisRankingRepository redisRankingRepository, InterestGroupRepository interestGroupRepository) {
        this.groupRepository = groupRepository;
        this.s3Service = s3Service;
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
        this.redisRankingRepository = redisRankingRepository;
        this.interestGroupRepository = interestGroupRepository;
    }


    /**
     * 그룹 생성 API
     * @param loginUser
     * @param categoryId
     * @param groupName
     * @param detail
     * @param image
     * @return
     * @throws IOException
     */
    @Transactional
    public GroupResponseDto createGroup(User loginUser, Long categoryId, String groupName, String detail, MultipartFile image)
    throws IOException {

        // 그룹 이름 중복 확인
        if (groupRepository.existsByName(groupName)) {
            throw new IllegalArgumentException("이미 동일한 그룹 이름이 존재합니다.");
        }

        // 이미지 업로드
        String s3ImagePath = s3Service.uploadImage(image);

        // 그룹 생성
        Group group = new Group(loginUser.getId(), categoryId, groupName, detail, s3ImagePath);

        // 그룹 저장
        groupRepository.save(group);

        // 그룹 생성자를 GROUP_ADMIN으로 추가
        Member member = new Member(MemberRole.GROUP_ADMIN, loginUser, group);
        memberRepository.save(member);

        // Redis에 그룹 추가 (초기 관심 수 0)
        redisRankingRepository.addGroupToRanking(group.getId(), 0);

        // GroupResponseDto 변환 및 반환
        return GroupResponseDto.toDto(group);

    }

    /**
     * 그룹 단건 조회 API
     * @param groupId
     * @return
     */
    public GroupDetailResponseDto findGroup(Long groupId) {
        // 그룹 조회
        Group group = groupRepository.findByGroupOrElseThrow(groupId);

        // 그룹 상태 확인
        checkGroupStatus(group);

        // 그룹 관리자 정보 조회
        User user = userRepository.findById(group.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 그룹 멤버 이름 리스트 조회
        List<String> members = memberRepository.findByGroupId(groupId)
                .stream()
                .map(member -> member.getUser().getName()) // `user`를 통해 이름 추출
                .collect(Collectors.toList());

        // GroupDetailResponseDto 변환 및 반환
        return GroupDetailResponseDto.toDto(group, user.getName(), members);
    }

    /**
     * 그룹 수정 API
     * @param groupId
     * @param requestDto
     * @return
     */
    public GroupResponseDto updateGroup(Long groupId, GroupRequestDto requestDto) {
        // 그룹 조회
        Group group = groupRepository.findByGroupOrElseThrow(groupId);

        // 그룹 상태 확인
        checkGroupStatus(group);

        // 그룹 정보 업데이트
        group.updateGroup(requestDto.getGroupName(), requestDto.getDetail(), requestDto.getImagePath());

        // 수정된 내용 저장
        groupRepository.save(group);

        // DTO로 변환하여 반환
        return GroupResponseDto.toDto(group);
    }

    /**
     * 그룹 삭제 API
     * @param groupId
     * @param loginUser
     */
    public void deleteGroup(Long groupId, User loginUser) {
        // 그룹 조회
        Group group = groupRepository.findByGroupOrElseThrow(groupId);

        // 유저가 그룹에 속해있는지 확인
        Member member = memberRepository.findByUserIdAndGroupIdOrElseThrow(loginUser.getId(), groupId);

        // 그룹 상태 확인
        checkGroupStatus(group);

        // 그룹 상태 변경
        if("active".equals(group.getStatus())) {
            group.setStatus("deleted");
        }

        groupRepository.save(group);
    }

    /**
     * 그룹 상태 확인 메서드
     * @param group
     */
    public void checkGroupStatus(Group group) {

        if ("deleted".equals(group.getStatus())) {
            throw new IllegalArgumentException("삭제된 그룹입니다.");
        }
    }


    /**
     * 그룹 전체 랭킹 조회 API
     * @return
     */
    public List<GroupRankingResponseDto> findAllGroupsWithRanking() {
        // Redis에서 랭킹 데이터 가져오기
        Set<ZSetOperations.TypedTuple<Object>> rankedGroups = redisRankingRepository.getTopRankedGroups();
        // Redis에서 그룹 랭킹 데이터를 ZSet 형식으로 가져옴 (그룹 ID와 스코어 포함)

        List<GroupRankingResponseDto> dtos = new ArrayList<>();

        // Redis에 없는 그룹 데이터를 동기화
        List<Group> allGroups = groupRepository.findAll(); // DB에서 모든 그룹 가져오기

        for (Group group : allGroups) {
            boolean isGroupInRedis = rankedGroups.stream()
                    .anyMatch(rankedGroup -> Long.valueOf(rankedGroup.getValue().toString()).equals(group.getId()));

            if (!isGroupInRedis) {
                // Redis에 없는 경우 DB의 기존 관심 수 가져오기
                int interestCount = interestGroupRepository.countByGroupIdAndStatus(group.getId(), "active");
                redisRankingRepository.addGroupToRanking(group.getId(), interestCount); // Redis에 추가
            }
        }

        // Redis에서 동기화된 데이터 다시 가져오기
        rankedGroups = redisRankingRepository.getTopRankedGroups();

        // 결과를 저장할 DTO 리스트 초기화
        for (ZSetOperations.TypedTuple<Object> rankedGroup : rankedGroups) {

            // Redis에서 가져온 그룹 ID를 Long 타입으로 변환
            Long groupId = Long.valueOf(rankedGroup.getValue().toString());

            // 그룹 및 생성자 정보 조회
            Group group = groupRepository.findByGroupOrElseThrow(groupId);
            User user = userRepository.findByIdOrElseThrow(group.getUserId());

            // DTO 변환
            dtos.add(GroupRankingResponseDto.toDto(group, user.getName(), rankedGroup.getScore().intValue()));
            // 그룹과 사용자 정보, Redis에서 가져온 관심 수를 이용해 DTO 생성 후 리스트에 추가
        }

        return dtos;

    }
}
