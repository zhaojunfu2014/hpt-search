package com.hpt.search.cluster.job;

import java.io.File;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.hpt.search.ConfigHolder;
import com.hpt.search.HptSearcher;
import com.hpt.search.cluster.log.LogParser;
import com.hpt.search.common.SearchGlobal;
import com.hpt.search.service.SearchService;
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

	private SearchService searchService = HptSearcher.getService();
	
	private void moveFileToArchiver(File[] subTodos) {
		for(File f:subTodos){
			String filename=f.getName();
			FileUtil.cutGeneralFile(ConfigHolder.logSubTodo+SearchGlobal.pathSeparator+filename, ConfigHolder.logSubArchiver);
		}
	}
	@Override
	public synchronized void run() {
		//将订阅到的日志重做
		File subTodoDir = new File(ConfigHolder.logSubTodo);
		File[] subTodos = subTodoDir.listFiles();
		log.debug(subTodos.length+" logs");
		if(subTodos == null || subTodos.length==0){
			return;
		}
		for(File f:subTodos){
			Object o = LogParser.parseLog(ConfigHolder.logSubTodo, f.getName());
			try {
				searchService.createOrUpdateIndex(o);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e,e);
			}
		}
		moveFileToArchiver(subTodos);
		log.debug(subTodos.length+" logs finished");
	}
}
