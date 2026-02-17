package com.mfajardo.spring_backend_starter.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("@annotation(logEvent)")
    public Object logExecution(ProceedingJoinPoint joinPoint, LogEvent logEvent) throws Throwable {
        long start = System.currentTimeMillis();
        Object result;
        boolean success = true;

        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            success = false;
            log.error("Action={} method={} args={} failed: {}", logEvent.action(),
                    joinPoint.getSignature(), joinPoint.getArgs(), e.getMessage());
            throw e;
        }

        long duration = System.currentTimeMillis() - start;
        log.info("Action={} method={} user={} success={} duration={}ms",
                logEvent.action(),
                joinPoint.getSignature(),
                getCurrentUserId(),
                success,
                duration);

        return result;
    }

    private String getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return "anonymous";
    }
}
