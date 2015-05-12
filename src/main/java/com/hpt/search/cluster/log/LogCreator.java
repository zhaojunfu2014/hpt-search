package com.hpt.search.cluster.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.hpt.search.common.SearchGlobal;

/**
 * 
 * @Title:LogCreator
 * @description:日志创建器
 * @author 赵俊夫
 * @date 2014-7-19 上午1:46:15
 * @version V1.0
 */
public class LogCreator {
	
	/**
	 * 根据实体创建日志对象
	 * @param t
	 * @return
	 */
	public static <T> Log createLog(T t){
		String filename = UUID.randomUUID().toString().replace("-", "");
		String data = JSON.toJSONString(t);
		Log log = new Log(filename, data,t.getClass());
		return log;
	}
	
	public  static void saveToFile(Log log,String logPub){
		Writer ops = null;
		String file =logPub+SearchGlobal.pathSeparator+log.getFilename()+"-"+log.getClazz()+log.ext;
		try {
			File f = new File(file);
			if(!f.getParentFile().exists()){
				f.getParentFile().mkdirs();
			}
			FileOutputStream fos=new FileOutputStream(f);
			ops = new OutputStreamWriter(fos, SearchGlobal.encode);
			ops.write(log.getData());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				ops.flush();
				ops.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
