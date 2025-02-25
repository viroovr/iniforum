package com.forum.project.domain.auth.aspect;

import com.forum.project.core.common.TokenUtil;
import com.forum.project.domain.auth.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserIdAspect {
    private final TokenService tokenService;
    private final HttpServletRequest request;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMapping(){}

    @Around("requestMapping() && execution(* *(.., @ExtractUserId (*), ..))")
    public Object injectUserId(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();

        //[1, CommentRequestDto(content=testContent), null]
        log.info(Arrays.toString(args));
        //com.forum.project.domain.comment.controller.CommentController
        log.info(signature.getDeclaringTypeName());
        //addComment
        log.info(signature.getMethod().getName());
        //[java.lang.Long questionId, com.forum.project.domain.comment.dto.CommentRequestDto commentRequestDto, java.lang.Long userId]
        log.info(Arrays.toString(parameters));

        String authorizationHeader = request.getHeader("Authorization");

        String token = TokenUtil.extractToken(authorizationHeader);
        Long userId = tokenService.getUserId(token);
        log.info("userId : {}", userId);
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(ExtractUserId.class)) {
                args[i] = userId;
                break;
            }
        }

        return joinPoint.proceed(args);
    }
}
