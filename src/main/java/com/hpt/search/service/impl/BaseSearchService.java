package com.hpt.search.service.impl;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Sort;

import com.hpt.search.pojo.SearchResult;
import com.hpt.search.service.SearchService;

/**
 * 搜索引擎基础实现
 * @Title:BaseSearchService
 * @description:
 * @author 蒋东平
 * @date 2014-7-3 上午11:59:01
 * @version V1.0
 */
public abstract class BaseSearchService implements SearchService {

	@Override
	public abstract <T> int createOrUpdateIndex(T t) throws Exception;

	@Override
	public abstract <T> int deleteIndex(T t) throws Exception;

	@Override
	public abstract <T> SearchResult<T> findSearchResult(Class<T> clazz,
			String keywords, Filter filter, Sort sort, boolean doDocScores,
			boolean doMaxScore, int page, int pageSize) throws Exception;

	@Override
	public <T> SearchResult<T> findSearchResult(Class<T> clazz, String keywords)
			throws Exception {

		SearchResult<T> searchResult = null;
		searchResult = this.findSearchResult(clazz, keywords, null, null, 0,
				Integer.MAX_VALUE);

		return searchResult;
	}

	@Override
	public <T> SearchResult<T> findSearchResult(Class<T> clazz,
			String keywords, int page, int pageSize) throws Exception {

		SearchResult<T> searchResult = null;
		searchResult = this.findSearchResult(clazz, keywords, null, null, page,
				pageSize);

		return searchResult;
	}

	@Override
	public <T> SearchResult<T> findSearchResult(Class<T> clazz,
			String keywords, Filter filter, int page, int pageSize)
			throws Exception {

		SearchResult<T> searchResult = null;
		searchResult = this.findSearchResult(clazz, keywords, filter, null,
				page, pageSize);

		return searchResult;
	}

	@Override
	public <T> SearchResult<T> findSearchResult(Class<T> clazz,
			String keywords, Sort sort, int page, int pageSize)
			throws Exception {

		SearchResult<T> searchResult = null;
		searchResult = this.findSearchResult(clazz, keywords, null, sort, page,
				pageSize);

		return searchResult;
	}

	@Override
	public <T> SearchResult<T> findSearchResult(Class<T> clazz,
			String keywords, Filter filter, Sort sort, int page, int pageSize)
			throws Exception {

		SearchResult<T> searchResult = null;
		searchResult = this.findSearchResult(clazz, keywords, filter, sort,
				false, false, page, pageSize);

		return searchResult;
	}
}
