package com.wofu.fenxiao.utils;

import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import com.wofu.fenxiao.annotation.DataSource;

/**
 * 选择数据源切面
 * @author bolinli
 *
 */
@Component
@Aspect
public class DataSourceAspect {
	 @Pointcut("execution(* com.wofu.fenxiao.service.impl.*Impl.*(..))")    
    public void pointCut(){};    
      
    @Before(value = "pointCut()")  
     public void before(JoinPoint point)  
        {  
    		System.out.println("aop..............");
            Object target = point.getTarget();  
            System.out.println(target.toString());  
            String method = point.getSignature().getName();  
            System.out.println(method);  
            Class<?>[] classz = target.getClass().getInterfaces();  
            Class<?>[] parameterTypes = ((MethodSignature) point.getSignature())  
                    .getMethod().getParameterTypes();  
            try {  
                Method m = classz[0].getMethod(method, parameterTypes);  
                System.out.println(m.getName());  
                if (m != null && m.isAnnotationPresent(DataSource.class)) {  
                    DataSource data = m.getAnnotation(DataSource.class);  
                    DataSourceHolder.setDataSource(data.name());  
                }  
                  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }
}
