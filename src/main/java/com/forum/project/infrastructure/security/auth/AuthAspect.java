package com.forum.project.infrastructure.security.auth;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.auth.service.AuthorizationService;
import com.forum.project.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthAspect {

    private final AuthorizationService authorizationService;

    @Before("@annotation(AuthCheck) && args(userId, header)")
    public void checkAuthorizationForUser(AuthCheck authCheck, Long userId, String header) {
        Long jwtUserId = authorizationService.extractUserId(header);

        if (!Objects.equals(userId, jwtUserId)) {
            throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL);
        }
    }

    @Pointcut("@annotation(com.forum.project.infrastructure.security.auth.ExtractUser)")
    public void extractUserPointcut() {}

    @Around("extractUserPointcut() && args(.., token)")
    public Object extractUser(ProceedingJoinPoint joinPoint, String token) throws Throwable {
        // Extract user from token
        User user = authorizationService.extractUserByHeader(token);

        // Add user as the last argument to the method
        Object[] args = joinPoint.getArgs();
        Object[] modifiedArgs = Arrays.copyOf(args, args.length + 1);
        modifiedArgs[modifiedArgs.length - 1] = user;

        // Proceed with the modified arguments
        return joinPoint.proceed(modifiedArgs);
    }

////    @Around("@annotation(ExtractUser)")
//    @Around("execution(* com.forum..*.*(.., @ExtractUser (*), ..))")
//    public Object extractUser(ProceedingJoinPoint joinPoint) throws Throwable {
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        Method method = methodSignature.getMethod();
//        Object[] args = joinPoint.getArgs();
//
//        // @ExtractUser 어노테이션이 붙은 파라미터 찾기
//        for (int i = 0; i < args.length; i++) {
//            if (isUserArgument(method, i)) {
//                // Token 값 추출
//                String token = findTokenArgument(method, args);
//
//                // token을 이용해 User 객체 추출
//                User user = authenticationService.extractUserByToken(token);
//
//                // @ExtractUser 어노테이션이 붙은 파라미터에 User 객체 할당
//                args[i] = user;
//                break;
//            }
//        }
//
//        return joinPoint.proceed(args);
//    }
//
//    private boolean isUserArgument(Method method, int index) {
//        Annotation[][] paramAnnotations = method.getParameterAnnotations();
//        for (Annotation annotation : paramAnnotations[index]) {
//            if (annotation instanceof ExtractUser) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private String findTokenArgument(Method method, Object[] args) {
//        Annotation[][] paramAnnotations = method.getParameterAnnotations();
//        for (int i = 0; i < paramAnnotations.length; i++) {
//            for (Annotation annotation : paramAnnotations[i]) {
//                if (annotation instanceof RequestHeader && ((RequestHeader) annotation).value().equals("Authorization")) {
//                    return (String) args[i];
//                }
//            }
//        }
//        throw new IllegalArgumentException("Authorization token not found");
//    }
}
