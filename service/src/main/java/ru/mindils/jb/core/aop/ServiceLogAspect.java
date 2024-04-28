package ru.mindils.jb.core.aop;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ServiceLogAspect {

  @Around("execution(* ru.mindils.jb.*.service.*.*(..))")
  public Object logServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    String methodName = methodSignature.getName();
    String className = methodSignature.getDeclaringType().getSimpleName();

    Object[] args = joinPoint.getArgs();
    log.info(
        "Entering method: {}.{}() with parameters: {}",
        className,
        methodName,
        Arrays.toString(args));

    Object result = null;
    try {
      result = joinPoint.proceed();
      log.info("Exiting method: {}.{}() with return value: {}", className, methodName, result);
    } catch (Throwable throwable) {
      log.error(
          "Exception in method: {}.{}(). Message: {}",
          className,
          methodName,
          throwable.getMessage());
      throw throwable;
    }

    return result;
  }
}
