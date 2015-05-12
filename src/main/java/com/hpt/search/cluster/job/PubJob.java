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

import com.hpt.search.ConfigHolder;
import com.hpt.search.cluster.handler.LogSendHandler;
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
	
	
	private void moveFileToArchiver(File[] pubTodos) {
		for(File f:pubTodos){
			String filename=f.getName();
			FileUtil.cutGeneralFile(ConfigHolder.logPubTodo+SearchGlobal.pathSeparator+filename, ConfigHolder.logPubArchiver);
		}
	}
	private void copyFileToError(File errorFile,String host,int port) {
		String filename=errorFile.getName();
		FileOutputStream output = null;
		FileInputStream input = null;
		//将发布错误的日志记录到错误文件夹中,格式:ip_port-time-class.log
		try {
			input= new FileInputStream(new File(ConfigHolder.logPubTodo+SearchGlobal.pathSeparator+filename));
			File of =new File(ConfigHolder.logPubError+SearchGlobal.pathSeparator+host+"_"+port+"-"+filename);
			if(!of.getParentFile().exists()){
				of.getParentFile().mkdirs();
			}
			output = new FileOutputStream(of);
			IOUtils.copy(input, output);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try{
				output.close();
				input.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	@Override
	public synchronized void run() {
		File pubTodoDir = new File(ConfigHolder.logPubTodo);
		File[] pubTodos = pubTodoDir.listFiles();
		log.debug(pubTodos.length+" logs");
		if(pubTodos == null || pubTodos.length==0){
			return;
		}
		String[] groups = ConfigHolder.group.split(",");
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
					data = IOUtils.toString(ips, SearchGlobal.encode);
					ips.close();
					Client.sendData2Server(host, port, new LogSendHandler(filename, data));
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
