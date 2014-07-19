package com.hpt.search.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 
 * @Title:SearchEntity
 * @description:搜索实体
 * @author 赵俊夫
 * @date 2014-6-26 下午2:25:29
 * @version V1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SearchEntity {
	/**
	 * 搜索时 被列入搜索的选项
	 * @return
	 */
	public String[] searchFields();
	/**
	 * Doc数据的唯一标识
	 * @return
	 */
	public String idFiled() default "id";
}
