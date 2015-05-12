package com.hpt.search.cluster.job;

import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.hpt.search.ConfigHolder;

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
	

	//日志发布任务
	private TimerTask pub = null; 
	//日志订阅任务
	private TimerTask redo = null;
	//错误日志重传任务
	private TimerTask error = null;
	//心跳检测任务
	private TimerTask heart = null;
	
	public void run(){
		Timer timer=new Timer();   
		if("master".equals(ConfigHolder.mode)){
			pub =new PubJob();
			error = new ErrorJob();
			heart= new HeartBeatJob();
			
			timer.schedule(pub,new Date(),ConfigHolder.periodPub);  
			timer.schedule(error, new Date(),ConfigHolder.periodPubFromError);
			timer.schedule(heart, new Date(),ConfigHolder.periodHeartBeat);
		}else{
			redo =  new RedoJob();
			timer.schedule(redo,new Date(),ConfigHolder.periodRedo);  
		}
	}
}
