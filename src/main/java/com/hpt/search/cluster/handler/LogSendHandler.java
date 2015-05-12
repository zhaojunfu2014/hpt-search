package com.hpt.search.cluster.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.hpt.search.ConfigHolder;
import com.hpt.search.common.SearchGlobal;
/**
 * 
 * @Title:LogSendHandler
 * @description:重做日志处理器
 * @author 赵俊夫
 * @date 2015-5-11 下午2:00:47
 * @version V1.0
 */
public class LogSendHandler implements CommandHandler {
	private String filename;
	private String data;
	
	@Override
	public boolean handleSend(PrintWriter writer,Socket socket) {
		writer.println(Command.LOG);
		writer.println(getFilename());
		writer.println(getData());
		return true;
	}
	
	@Override
	public boolean handleRecive(BufferedReader br,Socket socket) {
		FileOutputStream fos = null;
		try{
			String filename = br.readLine();
			String data = br.readLine();
			File f = new File(ConfigHolder.logSubTodo+SearchGlobal.pathSeparator+filename);
			f.getParentFile().mkdirs();
			fos = new FileOutputStream(f);
			fos.write(data.getBytes());
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			try {
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public LogSendHandler(String filename, String data) {
		this.filename = filename;
		this.data = data;
	}

	public LogSendHandler() {
		super();
	}
	

}
