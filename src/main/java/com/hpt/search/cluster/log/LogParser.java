package com.hpt.search.cluster.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSON;
import com.hpt.search.common.SearchGlobal;

/**
 * 
 * @Title:LogParser
 * @description:日志解析器
 * @author 赵俊夫
 * @date 2014-7-19 上午1:46:27
 * @version V1.0
 */
public class LogParser {
	
	public static Object parseLog(String dir,String filename){
		String[] p = filename.split("-");
		String clazz = p[1].replace(Log.ext,"");
		clazz = clazz.split("-")[0];
		Class tc = null;
		InputStream ips = null;
		String data = null;
		try {
			tc = Class.forName(clazz);
			ips = new FileInputStream(new File(dir+SearchGlobal.pathSeparator+filename));
			data = IOUtils.toString(ips);
			ips.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Object t =JSON.parseObject(data,tc);
		return t;
	}
}
