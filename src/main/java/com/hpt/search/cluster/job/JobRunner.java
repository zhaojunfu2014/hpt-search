package com.hpt.search.cluster.job;

import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.hpt.search.common.SearchGlobal;

/**
 * 
 * @Title:JobRunner
 * @description:任务运行器
 * @author 赵俊夫
 * @date 2014-7-19 上午1:51:07
 * @version V1.0
 */
public class JobRunner {
	private static final Logger log= Logger.getLogger(JobRunner.class);
	protected static ResourceBundle bundle = null;
	
	private long periodPub;
	private long periodRedo;
	private long periodPubFromError;

	//日志发布任务
	private TimerTask pub = new PubJob();
	//日志订阅任务
	private TimerTask redo =  new RedoJob();
	//错误日志重传任务
	private TimerTask error = new ErrorJob();
	
	public JobRunner(){
		loadCfg();
	}
	/**
	 * 读取配置文件
	 */
	protected void loadCfg() {
		bundle = java.util.ResourceBundle.getBundle(SearchGlobal.configFile);
		String periodPubStr = bundle.getString("lucene.cluster.period.pub");
		String periodRedoStr = bundle.getString("lucene.cluster.period.redo");
		String periodPubFromErrorStr = bundle.getString("lucene.cluster.period.pubFromError");
		periodPub = Long.parseLong(periodPubStr);
		periodRedo = Long.parseLong(periodRedoStr);
		periodPubFromError =  Long.parseLong(periodPubFromErrorStr);
	}
	public void run(){
		Timer timer=new Timer();   
		timer.schedule(pub,new Date(),periodPub);   
		timer.schedule(redo,new Date(),periodRedo);   
		timer.schedule(error, new Date(),periodPubFromError);
	}
}
