package com.example.hnh.global.error.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_IMAGE_FORMAT(BAD_REQUEST, "지원되지 않는 이미지 형식입니다"),
    PASSWORD_ERROR(BAD_REQUEST, "패스워드 에러"),
    PASSWORD_UPDATE_ERROR(BAD_REQUEST, "새로운 비밀번호는 현재 비밀번호를 사용할 수 없습니다."),
    INVALID_IMAGE_URL(BAD_REQUEST, "지원되지 않는 이미지 주소 형식입니다"),
    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    UNAUTHORIZED_AUTHOR(UNAUTHORIZED, "작성자만 수정/삭제가 가능합니다."),
    INVALID_TOKEN(UNAUTHORIZED,"인증되지 않는 토큰입니다."),

    /* 403 FORBIDDEN : 권한이 없음 */
    UNAUTHORIZED_USER(FORBIDDEN, "권한이 없습니다. 해당유저만 가능합니다."),
    FORBIDDEN_ERROR(FORBIDDEN,"권한이 없습니다."),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    RESOURCES_NOT_FOUND(NOT_FOUND, "해당 리소트 값을 찾을 수 없습니다."),
    USER_NOT_FOUND(NOT_FOUND, "해당 id로 인한 유저 정보를 찾을 수 없습니다"),
    MEMBER_NOT_FOUND(NOT_FOUND, "해당 멤버를 찾을 수 없습니다."),
    BOARD_NOT_FOUND(NOT_FOUND, "해당 게시물을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),
    REPLY_NOT_FOUND(NOT_FOUND, "해당 답글을 찾을 수 없습니다."),
    GROUP_NOT_FOUND(NOT_FOUND, "해당 그룹을 찾을 수 없습니다."),


    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(CONFLICT, "데이터가 이미 존재합니다"),
    DUPLICATE_MEMBER(CONFLICT, "이미 가입된 그룹입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String detail;
}
