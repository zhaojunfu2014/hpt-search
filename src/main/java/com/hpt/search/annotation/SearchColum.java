package com.hpt.search.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.lucene.document.Field;
/**
 * 
 * @Title:SearchColum
 * @description:搜索引擎注解
 * @author 赵俊夫
 * @date 2014-6-26 下午1:27:36
 * @version V1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SearchColum {
	/**
	 * 索引Field对应的名称
	 * @return
	 */
	public String name() default "";
	/**
	 * 字段是否保存
	 * @return
	 */
	public boolean store() default true;
	/**
	 * 字段是否建立索引
	 * @return
	 */
	public boolean index() default false;
	/**
	 * 是否高亮
	 * @return
	 */
	public boolean highLight() default false;
	/**
	 * 字段权重
	 * @return
	 */
	public float boost() default 1;
	
	
}
