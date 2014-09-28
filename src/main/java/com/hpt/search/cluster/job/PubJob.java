package com.hpt.search.cluster.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
	private String logPubError = null;
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
		logPubError = logPub+SearchGlobal.pathSeparator+SearchGlobal.logError;
	}
	private void moveFileToArchiver(File[] pubTodos) {
		for(File f:pubTodos){
			String filename=f.getName();
			FileUtil.cutGeneralFile(logPubTodo+SearchGlobal.pathSeparator+filename, logPubArchiver);
		}
	}
	private void copyFileToError(File errorFile,String host,int port) {
		String filename=errorFile.getName();
		//将发布错误的日志记录到错误文件夹中,格式:ip_port-time-class.log
		try {
			FileInputStream input = new FileInputStream(new File(logPubTodo+SearchGlobal.pathSeparator+filename));
			File of =new File(logPubError+SearchGlobal.pathSeparator+host+"_"+port+"-"+filename);
			if(!of.getParentFile().exists()){
				of.getParentFile().mkdirs();
			}
			FileOutputStream output = new FileOutputStream(of);
			IOUtils.copy(input, output);
			output.close();
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public synchronized void run() {
		File pubTodoDir = new File(logPubTodo);
		File[] pubTodos = pubTodoDir.listFiles();
		log.debug(pubTodos.length+" logs");
		if(pubTodos == null || pubTodos.length==0){
			return;
		}
		String[] groups = group.split(",");
		String host = null;
		String portstr = null;
		int port = 0;
		String filename = null;
		String data = null;
		
		for(String group:groups){
			int index = 0;
			for(File f:pubTodos){
				host = group.split(":")[0];
				portstr = group.split(":")[1];
				port = Integer.parseInt(portstr);
				filename = f.getName();
				try {
					InputStream ips = new FileInputStream(f);
					data = IOUtils.toString(ips);
					ips.close();
					Client.send2Server(host, port, filename, data);
				} catch (Exception e) {
					log.error(e,e);
					log.debug("copy this file to error dir");
					copyFileToError(pubTodos[index], host, port);
				}
				index++;
			}
		}
		moveFileToArchiver(pubTodos);
		log.debug(pubTodos.length+" logs finished");
	}
	
}
