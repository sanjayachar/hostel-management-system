package com.hostel.auth.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Around("execution(* com.hostel.auth.controller..*(..)) || " + "execution(* com.hostel.auth.service..*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        long startTime = System.currentTimeMillis();
        log.info("METHOD START -> {}.{} | Args: {}", className, methodName, Arrays.toString(args));
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("METHOD END -> {}.{} | Result: {} | Time: {} ms", className, methodName, result, executionTime);
            return result;
        } catch (Throwable ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("METHOD ERROR -> {}.{} | Exception: {} | Time: {} ms", className, methodName, ex.getMessage(), executionTime);
            throw ex;
        }
    }
}