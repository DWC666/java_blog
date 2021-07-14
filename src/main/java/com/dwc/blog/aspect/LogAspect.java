package com.dwc.blog.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 通过AOP来实现日志记录
 */
@Aspect
@Component
public class LogAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    // 定义切面
    // 向com.dwc.blog.controller包及其子包下的所有类的方法织入切面
    @Pointcut("execution(* com.dwc.blog.controller..*(..))")
    public void aspectPoint(){
    }

    @Before("aspectPoint()")
    public void before(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURL().toString();
        String ip = request.getRemoteAddr();
        String classAndMethod = joinPoint.getSignature().getDeclaringTypeName() + "@" + joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        RequestLog log = new RequestLog(url, ip, classAndMethod, args);
        logger.info("Request : {}", log);
    }

    @After("aspectPoint()")
    public void after(){
//        logger.info("---------after------------");
    }

    @AfterReturning(returning = "result", pointcut = "aspectPoint()")
    public void afterReturn(Object result){
        logger.info("result: {}", result);
    }

    private class RequestLog{
        private String url;
        private String ip;
        private String classAndMethod;
        private Object[] args;

        public RequestLog(String url, String ip, String classAndMethod, Object[] args) {
            this.url = url;
            this.ip = ip;
            this.classAndMethod = classAndMethod;
            this.args = args;
        }

        @Override
        public String toString() {
            return "{" +
                    "url='" + url + '\'' +
                    ", ip='" + ip + '\'' +
                    ", classAndMethod='" + classAndMethod + '\'' +
                    ", args=" + Arrays.toString(args) +
                    '}';
        }
    }
}
