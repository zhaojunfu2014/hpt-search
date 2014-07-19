package com.hpt.search.service.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Sort;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.hpt.search.annotation.SearchEntity;
import com.hpt.search.common.SearchGlobal;
import com.hpt.search.pojo.SearchResult;


/**
 * Solr 索引实现
 * @Title:SolrSearchServiceImpl
 * @description:
 * @author 蒋东平
 * @date 2014-7-3 下午12:00:16
 * @version V1.0
 */
public class SolrSearchServiceImpl extends BaseSearchService {

	private String connectUrl;
	private int socketTimeout;
	private int connectionTimeout;
	private int defaultMaxConnectionsPerHost;
	private int maxTotalConnections;
	private boolean isFollowRedirects;
	private boolean isAllowCompression;
	private int maxRetries;

	@Override
	public <T> int createOrUpdateIndex(T t) throws Exception {
		HttpSolrServer server = getServer();
		server.addBean(t);
		server.commit();
		return 0;
	}

	@Override
	public <T> int deleteIndex(T t) throws Exception {

		String value = analyIdFieldValue(t);

		HttpSolrServer server = getServer();
		server.deleteById(value);
		server.commit();

		return 0;
	}

	@Override
	public <T> SearchResult<T> findSearchResult(Class<T> clazz,
			String keywords, Filter filter, Sort sort, boolean doDocScores,
			boolean doMaxScore, int page, int pageSize) throws Exception {

		SearchResult<T> result = null;

		HttpSolrServer server = getServer();
		SolrQuery query = new SolrQuery();

		if (filter != null) {
			System.out.println(filter);
			query.addFilterQuery(filter.toString());
			
		}
		query.setQuery(keywords);
		query.setSort("id", ORDER.desc);
		query.setStart(page * pageSize);
		query.setRows(pageSize);

		result = queryResult(clazz,server, query);

		return result;
	}

	@SuppressWarnings("unchecked")
	private static <T> SearchResult<T> queryResult(Class<T> clazz,HttpSolrServer server,
			SolrQuery query) {
		SearchResult<T> result = new SearchResult<T>();
		try {

			QueryResponse response = server.query(query);
			result.setResult((List<T>) response
					.getBeans(clazz));
			result.setSize((long) result.getResult().size());
		} catch (SolrServerException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 获取连接
	 * 
	 * @return solr 连接
	 */
	private HttpSolrServer getServer() {
		HttpSolrServer server = null;
		try {
			server = new HttpSolrServer(connectUrl);
			server.setSoTimeout(socketTimeout);
			server.setConnectionTimeout(connectionTimeout);
			server
					.setDefaultMaxConnectionsPerHost(defaultMaxConnectionsPerHost);
			server.setMaxTotalConnections(maxTotalConnections);
			server.setFollowRedirects(isFollowRedirects);

			server.setAllowCompression(isAllowCompression);
			server.setMaxRetries(maxRetries);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return server;
	}

	/**
	 * 分析主键字段名
	 * 
	 * @param o
	 *            com.hpt.search.annotation.SearchEntity
	 * @return主键字段名
	 */
	private static String analyIdFieldName(Object o) {

		SearchEntity se = o.getClass().getAnnotation(
				com.hpt.search.annotation.SearchEntity.class);
		String idField = se.idFiled();
		return idField;
	}

	/**
	 * 分析主键字符串值
	 * 
	 * @param o
	 *            com.hpt.search.annotation.SearchEntity
	 * @return 主键值
	 */
	private static String analyIdFieldValue(Object o) {
		try {
			String idField = analyIdFieldName(o);
			String value = BeanUtils.getProperty(o, idField);
			return value;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public SolrSearchServiceImpl(String config) {
		Properties properties = new Properties();
		try {
			properties.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(config));
			this.connectUrl = properties.get("solr.url").toString();
			this.socketTimeout = Integer.valueOf(properties.get(
					"solr.socketTimeout").toString());
			this.connectionTimeout = Integer.valueOf(properties.get(
					"solr.connectionTimeout").toString());
			this.defaultMaxConnectionsPerHost = Integer.valueOf(properties.get(
					"solr.defaultMaxConnectionsPerHost").toString());
			this.maxTotalConnections = Integer.valueOf(properties.get(
					"solr.maxTotalConnections").toString());
			this.isFollowRedirects = properties.get("solr.isFollowRedirects")
					.toString().equalsIgnoreCase("true");
			this.isAllowCompression = properties.get("solr.isAllowCompression")
					.toString().equalsIgnoreCase("true");
			this.maxRetries = Integer.valueOf(properties.get("solr.maxRetries")
					.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SolrSearchServiceImpl() {
		this(SearchGlobal.configFile+".properties");
	}

}
