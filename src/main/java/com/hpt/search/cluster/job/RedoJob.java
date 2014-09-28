package com.hpt.search.cluster.job;

import java.io.File;
import java.util.ResourceBundle;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.hpt.search.cluster.log.LogParser;
import com.hpt.search.common.SearchGlobal;
import com.hpt.search.service.SearchService;
import com.hpt.search.service.impl.LuceneSearchServiceImpl;
import com.hpt.search.util.FileUtil;

/**
 * 
 * @Title:RedoJob
 * @description:日志重做任务
 * @author 赵俊夫
 * @date 2014-7-19 上午1:51:20
 * @version V1.0
 */
public class RedoJob extends TimerTask{
	private static final Logger log= Logger.getLogger(RedoJob.class);
	protected static ResourceBundle bundle = null;
	private String logbase = null;
	private String logSub = null;
	private String logSubTodo = null;
	private String logSubArchiver = null;
	private SearchService searchService = new LuceneSearchServiceImpl();
	public RedoJob() {
		loadCfg();
	}
	/**
	 * 读取配置文件
	 */
	protected void loadCfg() {
		bundle = java.util.ResourceBundle.getBundle(SearchGlobal.configFile);
		logbase = bundle.getString("lucene.cluster.logbase")==null?"":bundle.getString("lucene.cluster.logbase");
		logSub = logbase+SearchGlobal.pathSeparator+SearchGlobal.logSub;
		logSubTodo = logSub+SearchGlobal.pathSeparator+SearchGlobal.logTodo;
		logSubArchiver = logSub+SearchGlobal.pathSeparator+SearchGlobal.logArchiver;
	}
	private void moveFileToArchiver(File[] subTodos) {
		for(File f:subTodos){
			String filename=f.getName();
			FileUtil.cutGeneralFile(logSubTodo+SearchGlobal.pathSeparator+filename, logSubArchiver);
		}
	}
	@Override
	public synchronized void run() {
		//将订阅到的日志重做
		File subTodoDir = new File(logSubTodo);
		File[] subTodos = subTodoDir.listFiles();
		log.debug(subTodos.length+" logs");
		if(subTodos == null || subTodos.length==0){
			return;
		}
		for(File f:subTodos){
			Object o = LogParser.parseLog(logSubTodo, f.getName());
			try {
				searchService.createOrUpdateIndex(o);
			} catch (Exception e) {
				log.error(e,e);
			}
		}
		moveFileToArchiver(subTodos);
		log.debug(subTodos.length+" logs finished");
	}
}
