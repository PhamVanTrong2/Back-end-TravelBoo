package com.bootravel.common.aspect;

import com.bootravel.common.database.holder.ConnectionHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Aspect
@Component
public class ConnectionAspect {

    @Autowired
    private ConnectionHolder connectionHolder;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.GetMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping) || @annotation(org.springframework.web.bind.annotation.DeleteMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PatchMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PathVariable) || @annotation(org.springframework.web.bind.annotation.RequestMapping) " +
            "|| @annotation(javax.annotation.PostConstruct)")
    public void apiMethod() {
    }

    @After("apiMethod()")
    public void releaseConnection() {
        connectionHolder.releaseConnection();
    }

    public Object methodAction(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            Object output = proceedingJoinPoint.proceed();
            connectionHolder.releaseConnection();
            return output;
        } catch (Throwable throwable) {
            connectionHolder.rollbackConnection();
            throw throwable;
        }

    }

}
