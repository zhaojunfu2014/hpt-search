package com.hpt.search.service.impl;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Sort;

import com.hpt.search.ConfigHolder;
import com.hpt.search.cluster.Session;
import com.hpt.search.cluster.log.Log;
import com.hpt.search.cluster.log.LogCreator;
import com.hpt.search.common.SearchGlobal;
import com.hpt.search.pojo.GroupResult;
import com.hpt.search.pojo.SearchRecord;
import com.hpt.search.pojo.SearchResult;
/**
 * 
 * @Title:LuceneClusterServiceImpl
 * @description:搜索引擎服务-集群模式
 * @author 赵俊夫
 * @date 2014-7-19 上午1:39:28
 * @version V1.0
 */
public class LuceneClusterServiceImpl extends LuceneSearchServiceImpl {
	private static final Logger log= Logger.getLogger(LuceneClusterServiceImpl.class);
	private String logbase = null;
	private String logPub = null;
	private String logPubTodo = null;
	
	
	
	public LuceneClusterServiceImpl() {
		loadCfg();
	}
	/**
	 * 读取配置文件
	 */
	protected void loadCfg() {
		super.loadCfg();
		logbase = bundle.getString("lucene.cluster.logbase")==null?"":bundle.getString("lucene.cluster.logbase");
		logPub = logbase+SearchGlobal.pathSeparator+SearchGlobal.logPub;
		logPubTodo = logPub+SearchGlobal.pathSeparator+SearchGlobal.logTodo;
	}
	
	public <T> int createOrUpdateIndex(T t,boolean batch) throws Exception {
		super.createOrUpdateIndex(t,batch);
		if(Session.MODE_MASTER.equalsIgnoreCase(ConfigHolder.mode)){
			//存入发布日志
			log(t);
		}
		return 1;
	}

	@Override
	public <T> SearchResult<T> findSearchResult(Class<T> clazz,
			String keywords, Filter filter, Sort sort, boolean doDocScores,
			boolean doMaxScore, int page, int pageSize)
			throws CorruptIndexException, IOException, ParseException {
		SearchResult<T> result = super.findSearchResult(clazz, keywords, filter, sort, doDocScores,
				doMaxScore, page, pageSize);
		//计数
		SearchRecord.searchCount.addAndGet(1);
		SearchRecord.searchTime.addAndGet(result.getTime());
		
		return result;
	}
	
	@Override
	public <T> GroupResult group(Class<T> clazz, String keywords,
			String groupField, String valueField, int grouplimit)
			throws Exception {
		GroupResult result =  super.group(clazz, keywords, groupField, valueField, grouplimit);
		
		//计数
		SearchRecord.searchCount.addAndGet(1);
		SearchRecord.searchTime.addAndGet(result.getTime());
		
		return result;
	}
	
	public <T> int deleteIndex(T t) throws Exception {
		super.deleteIndex(t);
		//存入发布日志,删除索引不推荐使用,集群环境不同步删除操作
		//log(t);
		return 1;
	}
	
	private <T> void log(T t) {
		Log log = LogCreator.createLog(t);
		LogCreator.saveToFile(log, logPubTodo);
	}
}
