package com.hpt.search.service.impl;

import org.apache.log4j.Logger;

import com.hpt.search.cluster.log.Log;
import com.hpt.search.cluster.log.LogCreator;
import com.hpt.search.common.SearchGlobal;
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
	private String me = null;
	private String group = null;
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
	
	public <T> int createOrUpdateIndex(T t) throws Exception {
		super.createOrUpdateIndex(t);
		//存入发布日志
		log(t);
		return 1;
	}

	
	
	public <T> int deleteIndex(T t) throws Exception {
		super.deleteIndex(t);
		//存入发布日志
		//log(t);
		return 1;
	}
	
	private <T> void log(T t) {
		Log log = LogCreator.createLog(t);
		LogCreator.saveToFile(log, logPubTodo);
	}
}
