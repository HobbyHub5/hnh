package com.example.hnh.board;

import com.example.hnh.board.dto.BoardResponseDto;
import com.example.hnh.board.dto.SearchAllBoardResponseDto;
import com.example.hnh.board.dto.SearchBoardResponseDto;
import com.example.hnh.board.dto.UpdateBoardResponseDto;
import com.example.hnh.global.MessageResponseDto;
import com.example.hnh.global.error.errorcode.ErrorCode;
import com.example.hnh.global.error.exception.CustomException;
import com.example.hnh.global.S3Service;
import com.example.hnh.member.Member;
import com.example.hnh.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public BoardResponseDto createBoard(Long userId, Long groupId, String title, String detail, MultipartFile image) throws IOException {
        Member member = memberRepository.findByUserIdAndGroupIdOrElseThrow(userId, groupId);

        String imagePath = s3Service.uploadImage(image);

        Board board = new Board(title, imagePath, detail, 0L, 0L, member.getGroup(), member);
        Board savedBoard = boardRepository.save(board);

        return new BoardResponseDto(
                savedBoard.getId(),
                savedBoard.getMember().getId(),
                savedBoard.getTitle(),
                savedBoard.getDetail(),
                savedBoard.getImagePath(),
                savedBoard.getCreatedAt());
    }

    public Page<SearchAllBoardResponseDto> getBoards(Long groupId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Board> boards = boardRepository.findAllByGroupId(groupId, pageable);

        return boards.map(board -> new SearchAllBoardResponseDto(
                board.getId(),
                board.getMember().getId(),
                board.getTitle(),
                board.getImagePath(),
                board.getView(),
                board.getLikeCount(),
                board.getCreatedAt(),
                board.getModifiedAt()));
    }

    public SearchBoardResponseDto getBoard(Long boardId) {

        Board board = boardRepository.findByBoardIdOrElseThrow(boardId);
        Long viewCount = getViewCount(boardId);

        return new SearchBoardResponseDto(board, viewCount);
    }

    public UpdateBoardResponseDto updateBoard(Long boardId, Long userId, Long groupId, String title, String detail, MultipartFile image) throws IOException {
        Member member = memberRepository.findByUserIdAndGroupIdOrElseThrow(userId, groupId);
        Board board = boardRepository.findByBoardIdOrElseThrow(boardId);

        if(!board.getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_AUTHOR);
        }

        s3Service.deleteImage(board.getImagePath());
        String imagePath = s3Service.uploadImage(image);

        board.updateBoard(title, imagePath, detail);

        return new UpdateBoardResponseDto(
                board.getId(),
                board.getMember().getId(),
                board.getTitle(),
                board.getDetail(),
                board.getImagePath(),
                board.getModifiedAt());
    }

    @Transactional
    public MessageResponseDto deleteBoard(Long userId, Long groupId, Long boardId) {
        Member member = memberRepository.findByUserIdAndGroupIdOrElseThrow(userId, groupId);
        Board board = boardRepository.findByBoardIdOrElseThrow(boardId);

        if(!board.getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_AUTHOR);
        }

        board.setStatus("deleted");

        return new MessageResponseDto("삭제 완료되었습니다.");
    }

    @Transactional
    public void incrementView(Long boardId) {
        String redisKey = getRedisKey(boardId);

        redisTemplate.opsForValue().increment(redisKey);
    }

    public Long getViewCount(Long boardId) {

        String redisKey = getRedisKey(boardId);
        String viewCount = redisTemplate.opsForValue().get(redisKey);

        //현재 redisKey가 있으면 해당하는 value 리턴, 아니면 게시물의 조회수를 value로 저장 후 리턴
        if(viewCount != null) {
            return Long.parseLong(viewCount);
        }else {
            Board board = boardRepository.findByBoardIdOrElseThrow(boardId);
            redisTemplate.opsForValue().set(redisKey, String.valueOf(board.getView()));
            return board.getView();
        }
    }

    //조회수 동기화(1분마다 동기화)
    @Transactional
    @Scheduled(fixedRate = 60000)
    public void syncViewToDatabase() {
        Set<String> keys = redisTemplate.keys("board:view:*");
        if(keys != null){
            for(String key : keys){
                Long boardId = Long.parseLong(key.split(":")[2]);
                String viewCount = redisTemplate.opsForValue().get(key);

                if(viewCount != null) {
                    Board board = boardRepository.findByBoardIdOrElseThrow(boardId);
                    board.setView(Long.parseLong(viewCount));
                    boardRepository.save(board);
                }
            }
        }
    }

    private String getRedisKey(Long boardId) {
        return "board:view:" + boardId;
    }
}