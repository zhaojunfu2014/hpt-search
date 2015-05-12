package com.hpt.search.cluster.job;

import java.io.File;
import java.io.FileInputStream;
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
 * @Title:ErrorJob
 * @description:错误日志处理任务
 * @author 赵俊夫
 * @date 2014-7-22 下午7:55:27
 * @version V1.0
 */
public class ErrorJob extends TimerTask{
	private static final Logger log= Logger.getLogger(ErrorJob.class);
	
	@Override
	public synchronized void run() {
		File errorTodoDir = new File(ConfigHolder.logPubError);
		File[] errorTodos = errorTodoDir.listFiles();
		log.debug(errorTodos.length+" logs");
		for(File f:errorTodos){
			String fnameext = f.getName();
			String fname = fnameext.substring(0,fnameext.indexOf(".log"));
			String[] fs = fname.split("-");
			String ipport =  fs[0];
			//主机号
			String host = ipport.split("_")[0];
			String portstr = ipport.split("_")[1];
			//端口
			int port = Integer.parseInt(portstr);
			String pubFilename = fs[1]+"-"+fs[2]+".log";
			FileInputStream fips;
			String data = null;
			try {
				fips = new FileInputStream(f);
				data = IOUtils.toString(fips);
				fips.close();
//				Client.send2Server(host, port,pubFilename , data);
				Client.sendData2Server(host, port, new LogSendHandler(pubFilename, data));
				FileUtil.cutGeneralFile(ConfigHolder.logPubError+SearchGlobal.pathSeparator+fnameext, ConfigHolder.logPubArchiver);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e,e);
			}
		}
		log.debug(errorTodos.length+" logs finished");
	}
	public static void main(String[] args) {
		String fnameext = "192.10.10.1_999-11111-com.cn.entity.log";
		String fname = fnameext.substring(0,fnameext.indexOf(".log"));
		System.out.println(fname);
	}
}
