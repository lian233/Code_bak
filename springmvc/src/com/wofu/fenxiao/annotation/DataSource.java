package com.wofu.fenxiao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据源注解
 * @author Administrator
 *
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
	public static String defaultdatasource="defaultdatasource";
	public static String rdsdatasource="rdsdatasource";
	String name() default DataSource.defaultdatasource;
}
