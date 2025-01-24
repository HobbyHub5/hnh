package com.example.hnh.user;

import com.example.hnh.global.error.errorcode.ErrorCode;
import com.example.hnh.global.error.exception.CustomException;
import com.example.hnh.global.util.AuthenticationScheme;
import com.example.hnh.global.util.JwtProvider;
import com.example.hnh.global.util.RedisRefreshTokenRepository;
import com.example.hnh.user.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Security::AccountService")
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RedisRefreshTokenRepository redisRefreshTokenRepository;


    //회원 가입
    public UserResponseDto signUp(UserRequestDto requestDto){

        User user = new User(requestDto);
        user.setPassword(bCryptPasswordEncoder.encode(requestDto.getPassword()));
        User saveUser = userRepository.save(user);

        return new UserResponseDto(saveUser);
    }

    //로그인
    public JwtAuthResponse login(AccountRequest accountRequest) {
        User user = this.userRepository.findByEmail(accountRequest.getEmail())
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        this.validatePassword(accountRequest.getPassword(), user.getPassword());

        // 사용자 인증 후 인증 객체를 저장
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        accountRequest.getEmail(),
                        accountRequest.getPassword())
        );
        // 토큰 생성
        String accessToken = this.jwtProvider.generateToken(authentication);
        String refreshToken = this.jwtProvider.generateRefreshToken(authentication);
        log.info("accessToken 생성: {}", accessToken);
        log.info("refreshToken 생성: {}", refreshToken);

        return new JwtAuthResponse(AuthenticationScheme.BEARER.getName(), accessToken , refreshToken);
    }

    public String accessTokenRefresh(Long userId , String bearerToken){
        String refreshToken = getTokenFromHeader(bearerToken);
        String email = this.jwtProvider.getUsername(refreshToken);
        if (this.jwtProvider.validToken(refreshToken) && redisRefreshTokenRepository.validationToken(userId,refreshToken)){
            return this.jwtProvider.generateAccessTokenByRefreshToken(email);
        }else {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public String getTokenFromHeader(String bearerToken){
        final String headerPrefix = AuthenticationScheme.generateType(AuthenticationScheme.BEARER);

        boolean tokenFound =
                StringUtils.hasText(bearerToken) && bearerToken.startsWith(headerPrefix);
        if (tokenFound) {
            return bearerToken.substring(headerPrefix.length());
        }
        return null;
    }


    //회원 조회
    public UserResponseDto getUser(Long userId , User loginUser){
        User findUser = userRepository.findByIdOrElseThrow(userId);
        log.info(loginUser.getName());
        log.info(validateUser(userId).toString());
        return new UserResponseDto(findUser);
    }

    //비밀번호 변경
    public String modifyPassword(Long userId , String currentPassword , String newPassword){
        User user = this.userRepository.findByIdOrElseThrow(userId);
        this.validatePassword(currentPassword,user.getPassword());
        if (Objects.equals(currentPassword, newPassword)){
            throw new CustomException(ErrorCode.PASSWORD_UPDATE_ERROR);
        }
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
        return "변경되었습니다.";
    }


    //회원 탈퇴
    @Transactional
    public ResignResponseDto resignUser(Long userId , User loginUser , String password){
        //로그인유저와 탈퇴 요청유저가 일치하는지 확인
        if (!Objects.equals(userId , loginUser.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
        User findUser = this.userRepository.findByIdOrElseThrow(loginUser.getId());
        this.validatePassword(password,findUser.getPassword());
        findUser.setStatus(String.valueOf(UserStatus.DELETED).toLowerCase());
        userRepository.save(findUser);
        return new ResignResponseDto(findUser);
    }

    public Boolean validateUser(Long userId){
        User findUser = this.userRepository.findByIdOrElseThrow(userId);
        return !Objects.equals(findUser.getStatus(), String.valueOf(UserStatus.DELETED).toLowerCase());
    }

    private void validatePassword(String rawPassword, String encodedPassword)
            throws IllegalArgumentException {
        boolean notValid = !this.bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
        if (notValid) {
            throw new CustomException(ErrorCode.PASSWORD_ERROR);
        }
    }

}
