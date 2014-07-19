package com.hpt.search.cluster.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;
import java.util.TimerTask;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.hpt.search.cluster.net.Client;
import com.hpt.search.common.SearchGlobal;
import com.hpt.search.util.FileUtil;

/**
 * 
 * @Title:PubJob
 * @description:日志发布任务
 * @author 赵俊夫
 * @date 2014-7-19 上午1:47:27
 * @version V1.0
 */
public class PubJob extends TimerTask{
	private static final Logger log= Logger.getLogger(PubJob.class);
	private String logbase = null;
	private String me = null;
	private String group = null;
	private String logPub = null;
	private String logPubTodo = null;
	private String logPubArchiver = null;
	protected static ResourceBundle bundle = null;
	
	
	public PubJob() {
		loadCfg();
	}
	/**
	 * 读取配置文件
	 */
	protected void loadCfg() {
		bundle = java.util.ResourceBundle.getBundle(SearchGlobal.configFile);
		logbase = bundle.getString("lucene.cluster.logbase")==null?"":bundle.getString("lucene.cluster.logbase");
		me = bundle.getString("lucene.cluster.me")==null?"":bundle.getString("lucene.cluster.me");
		group = bundle.getString("lucene.cluster.group")==null?"":bundle.getString("lucene.cluster.group");
		group = group.replace(me+",", "");
		group = group.replace(","+me, "");
		logPub = logbase+SearchGlobal.pathSeparator+SearchGlobal.logPub;
		logPubTodo = logPub+SearchGlobal.pathSeparator+SearchGlobal.logTodo;
		logPubArchiver = logPub+SearchGlobal.pathSeparator+SearchGlobal.logArchiver;
	}
	private void moveFileToArchiver(File[] pubTodos) {
		for(File f:pubTodos){
			String filename=f.getName();
			FileUtil.cutGeneralFile(logPubTodo+SearchGlobal.pathSeparator+filename, logPubArchiver);
		}
	}
	@Override
	public synchronized void run() {
		File pubTodoDir = new File(logPubTodo);
		File[] pubTodos = pubTodoDir.listFiles();
		if(pubTodos == null || pubTodos.length==0){
			return;
		}
		String[] groups = group.split(",");
		String host = null;
		String portstr = null;
		int port;
		String filename = null;
		String data = null;
		
		for(String group:groups){
			try{
				for(File f:pubTodos){
					host = group.split(":")[0];
					portstr = group.split(":")[1];
					port = Integer.parseInt(portstr);
					filename = f.getName();
					try {
						InputStream ips = new FileInputStream(f);
						data = IOUtils.toString(ips);
						ips.close();
					} catch (FileNotFoundException e) {
						log.error(e,e);
					} catch (IOException e) {
						log.error(e,e);
					}
					Client.send2Server(host, port, filename, data);
				}
			}catch (Exception e) {
				log.error(e,e);
			}
		}
		moveFileToArchiver(pubTodos);
		
	}
	
}
