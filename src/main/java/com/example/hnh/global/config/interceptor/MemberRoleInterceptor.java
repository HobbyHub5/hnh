package com.example.hnh.global.config.interceptor;

import com.example.hnh.global.config.annotation.AccessibleMember;
import com.example.hnh.global.config.auth.UserDetailsImpl;
import com.example.hnh.global.error.errorcode.ErrorCode;
import com.example.hnh.global.error.exception.CustomException;
import com.example.hnh.member.Member;
import com.example.hnh.member.MemberRole;
import com.example.hnh.user.User;
import io.jsonwebtoken.lang.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

@Slf4j
@Component
public class MemberRoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 리소스가 정적 리소스인지 확인?
        if (!(handler instanceof HandlerMethod)) {
            return true; // 핸들러가 메서드가 아닌 경우 허용
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        AccessibleMember accessibleMember = handlerMethod.getMethodAnnotation(AccessibleMember.class);

        // 어노테이션이 없는 경우 모든 요청 허용
        if (accessibleMember == null) {
            return true;
        }

        // 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증 정보가 없으면 예러
        if (authentication == null) {
            throw new CustomException(ErrorCode.FORBIDDEN_ERROR);
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User loginUser = userDetails.getUser();

        // 세션 확인
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("member") == null) {
            throw new CustomException(ErrorCode.FORBIDDEN_ERROR); // 세션에 필요한 정보가 없으면 예외 발생
        }

        Member sessionAttribute = (Member) session.getAttribute("member");

        //로그인한 유저와 세션에 저장한 유저가 같은 유저인지 확인
        if (!Objects.equals(loginUser.getId(), sessionAttribute.getUser().getId())){
            throw new CustomException(ErrorCode.FORBIDDEN_ERROR);
        }


        MemberRole[] requiredRoles = accessibleMember.requiredRoles();
        if (!Arrays.asList(requiredRoles).contains(sessionAttribute.getRole())){
            throw new CustomException(ErrorCode.FORBIDDEN_ERROR);
        }

        return true;
    }
}
