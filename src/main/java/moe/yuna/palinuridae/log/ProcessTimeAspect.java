/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package moe.yuna.palinuridae.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author rika
 */
@Aspect
@Component
public class ProcessTimeAspect {

    @Around("execution(* moe.yuna.palinuridae.xutilsmodel.XUtilsModelDao.*(..))")
    public Object xutilProcessTime(ProceedingJoinPoint pjp) throws Throwable{
        long nanoTime = System.currentTimeMillis();
        Object result = pjp.proceed(pjp.getArgs());
        LoggerFactory.getLogger(pjp.getTarget().getClass()).debug("====xutil process time:"+(System.currentTimeMillis()- nanoTime) + "ms=========");
        return result;
    }
    
    @Around("execution(* moe.yuna.palinuridae.core.*BaseDao.*(..))")
    public Object logProcessTime(ProceedingJoinPoint pjp) throws Throwable{
        long nanoTime = System.currentTimeMillis();
        Object result = pjp.proceed(pjp.getArgs());
        LoggerFactory.getLogger(pjp.getTarget().getClass()).debug("====process time:"+(System.currentTimeMillis()- nanoTime) + "ms=========");
        return result;
    }

}
