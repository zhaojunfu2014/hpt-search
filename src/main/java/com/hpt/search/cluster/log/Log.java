package com.hpt.search.cluster.log;


/**
 * 
 * @Title:Log
 * @description:日志文件
 * @author 赵俊夫
 * @date 2014-7-19 上午1:46:07
 * @version V1.0
 */
public class Log {
	public static String ext = ".log";
	private String filename;
	private String data;
	private String clazz;
	
	
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
	
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	
	public Log() {
	}
	public Log(String filename, String data,Class clazz) {
		this.clazz = clazz.getName();
		this.filename = filename;
		this.data = data;
	}
	
	
}
