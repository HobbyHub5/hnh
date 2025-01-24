package com.example.hnh.user;

import com.example.hnh.global.config.auth.UserDetailsImpl;
import com.example.hnh.global.util.AuthenticationScheme;
import com.example.hnh.user.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userDto) {
        return new ResponseEntity<>(userService.signUp(userDto), HttpStatus.CREATED);
    }

    // 사용자 로그인
    @PostMapping("/login")
    public ResponseEntity<CommonResponseBody<String>> login(
            @Valid @RequestBody AccountRequest accountRequest) {
        JwtAuthResponse authResponse = this.userService.login(accountRequest);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(authResponse.getTokenAuthScheme(),
                authResponse.getRefreshToken());

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(new CommonResponseBody<>("로그인 성공" , authResponse.getAccessToken()));
    }

    @GetMapping("/{userId}/tokenRefresh")
    public ResponseEntity<CommonResponseBody<String>> accessTokenRefresh(
            @RequestHeader("Authorization") String refreshToken,
            @PathVariable Long userId
    ){
        return ResponseEntity.ok().body(new CommonResponseBody<>("accessTokenRefresh" , userService.accessTokenRefresh(userId,refreshToken)));
    }

    //회원 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long userId ,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails){
        return new ResponseEntity<>(userService.getUser(userId , userDetails.getUser()), HttpStatus.OK);
    }

    //회원 비밀번호 변경
    @PatchMapping("/{userId}")
    public ResponseEntity<CommonResponseBody<String>> updatePassword(
            @PathVariable Long userId,
            @Valid @RequestBody UserPasswordUpdateRequestDto requestDto){
        return ResponseEntity.ok().body(
                new CommonResponseBody<>(
                        userService.modifyPassword(userId,requestDto.getCurrentPassword(),requestDto.getNewPassword())
                ));
    }

    @DeleteMapping("/{userId}/resign")
    public ResponseEntity<CommonResponseBody<ResignResponseDto>> resignUser(
            @PathVariable Long userId,
            @RequestBody ResignRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return ResponseEntity.ok().body(new CommonResponseBody<>(
                "회원 탈퇴 되었습니다.",userService.resignUser(userId,userDetails.getUser(),requestDto.getPassword())
        ));
    }

}