package com.forum.project.domain.auth.aspect;

import com.forum.project.core.common.IpAddressUtil;
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

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class IpAddressAspect {
    private final HttpServletRequest request;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMapping() {}

    @Around("requestMapping() && execution(* *(.., @ExtractIp (*), ..))")
    public Object injectIpAddress(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();

        String ipaddress = IpAddressUtil.getClientIp(request);

        log.info("ip : {}", ipaddress);
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(ExtractIp.class)) {
                args[i] = ipaddress;
                break;
            }
        }

        return joinPoint.proceed(args);
    }
}
