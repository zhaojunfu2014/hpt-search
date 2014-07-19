package com.hpt.search.cluster.net;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

/**
 * 
 * @Title:Client
 * @description:日志客户端
 * @author 赵俊夫
 * @date 2014-7-19 下午3:17:37
 * @version V1.0
 */
public class Client {
	private static final Logger log= Logger.getLogger(Client.class);
	
	public static void send2Server(String host,int port,String filename,String data){
		//与服务端建立连接  
	     try {
			Socket client = new Socket(host, port);
			//建立连接后就可以往服务端写数据了  
		     Writer writer = new OutputStreamWriter(client.getOutputStream());
		     PrintWriter pw = new PrintWriter(writer);
		     pw.println(filename);
		     pw.println(data);
		     pw.flush();
		     pw.close();
		     client.close();
		} catch (UnknownHostException e) {
			log.error(e,e);
		} catch (IOException e) {
			log.error(e,e);
		}  
	}
}
