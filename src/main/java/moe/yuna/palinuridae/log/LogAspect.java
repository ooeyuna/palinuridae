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
public class LogAspect {
    
    @Around("execution(* moe.yuna.palinuridae.dialect..*.*(..))")
    public Object logSql(ProceedingJoinPoint pjp) throws Throwable{
        Object result = pjp.proceed(pjp.getArgs());
        LoggerFactory.getLogger(pjp.getTarget().getClass()).debug("====Prepare Sql:"+result);
        return result;
    }
    
    @Around("execution(* moe.yuna.palinuridae.core.*BaseDao.find*(..))")
    public Object logFindById(ProceedingJoinPoint pjp) throws Throwable{
        Object result = pjp.proceed(pjp.getArgs());
        LoggerFactory.getLogger(pjp.getTarget().getClass()).debug("====result:"+result);
        return result;
    }
    
    @Around("execution(* moe.yuna.palinuridae.core.*BaseDao.select*(..))")
    public Object logSelect(ProceedingJoinPoint pjp) throws Throwable{
        long currentTimeMillis = System.currentTimeMillis();
        Object result = pjp.proceed(pjp.getArgs());
        long currentTimeMillis1 = System.currentTimeMillis();
        LoggerFactory.getLogger(pjp.getTarget().getClass()).debug("====result:"+result);
        return result;
    }    
}
