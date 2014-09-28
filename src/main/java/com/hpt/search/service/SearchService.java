package com.hpt.search.service;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;

import com.hpt.search.pojo.GroupResult;
import com.hpt.search.pojo.SearchResult;

/**
 * 
 * @Title:SearchService
 * @description:提供搜索引擎服务
 * @author 赵俊夫
 * @date 2014-6-26 下午1:13:14
 * @version V1.0
 */
public interface SearchService {
	/**
	 * 创建或者更新索引
	 * @param t 搜索实体
	 * @return
	 * @throws Exception 
	 */
	public <T> int createOrUpdateIndex(T t) throws Exception;
	public <T> int createOrUpdateIndexBatch(List<T> t) throws Exception;
	
	/**
	 * 删除索引
	 * @param t
	 * @return
	 * @throws Exception 
	 */
	public <T> int deleteIndex(T t) throws Exception;
	
	public <T> SearchResult<T> findSearchResult(Class<T> clazz,String keywords) throws Exception;
	
	public <T> SearchResult<T> findSearchResult(Class<T> clazz,String keywords,int page,int pageSize) throws Exception;
	
	public <T> SearchResult<T> findSearchResult(Class<T> clazz,String keywords,Filter filter,int page,int pageSize) throws Exception;
	
	public <T> SearchResult<T> findSearchResult(Class<T> clazz,String keywords,Sort sort,int page,int pageSize) throws Exception;
	
	public <T> SearchResult<T> findSearchResult(Class<T> clazz,String keywords,Filter filter,Sort sort,int page,int pageSize) throws Exception;
	/**
	 * 按关键字搜索实体列表
	 * @param clazz
	 * @param keywords
	 * @param filter 过滤器
	 * @param sort	排序器
	 * @param doDocScores 为true情况下每个命中的结果下都会被评分
	 * @param doMaxScore 为true情况下对最大分值的搜索结果进行评分
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 * @throws ParseException
	 */
	public <T> SearchResult<T> findSearchResult(Class<T> clazz, String keywords,
			Filter filter,Sort sort, boolean doDocScores, boolean doMaxScore,
			int page, int pageSize) throws Exception;

	
	public IndexSearcher createIndexSearch() throws CorruptIndexException, IOException;
	/**
	 * 分组统计
	 * @param clazz
	 * @param keywords
	 * @param groupField
	 * @param valueField
	 * @param grouplimit
	 * @return
	 * @throws Exception
	 */
	public <T>  GroupResult group(Class<T> clazz,String keywords,String groupField,String valueField,int grouplimit) throws Exception;
}
