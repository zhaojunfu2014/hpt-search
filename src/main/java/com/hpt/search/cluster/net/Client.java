package com.hpt.search.cluster.net;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.hpt.search.cluster.handler.CommandHandler;
import com.hpt.search.common.SearchGlobal;
import com.hpt.search.exception.ClusterException;

/**
 * 
 * @Title:Client
 * @description:网络客户端
 * @author 赵俊夫
 * @date 2014-7-19 下午3:17:37
 * @version V1.0
 */
public class Client {
	private static final Logger log= Logger.getLogger(Client.class);
	/**
	 * 发送日志文件到服务器
	 * @param host
	 * @param port
	 * @param filename
	 * @param data
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	@Deprecated
	public static void send2Server(String host,int port,String filename,String data) throws UnknownHostException, IOException{
		//与服务端建立连接  
		Socket client = new Socket(host, port);
		//建立连接后就可以往服务端写数据了  
	     Writer writer = null;
	     PrintWriter pw = null;
	     try{
		     writer = new OutputStreamWriter(client.getOutputStream(),SearchGlobal.encode);
		     pw = new PrintWriter(writer);
		     pw.println(filename);
		     pw.println(data);
	     }catch (Exception e) {
	    	 e.printStackTrace();
	     }finally{
	    	 try{
		    	 pw.flush();
			     pw.close();
			     client.close();
	    	 }catch (Exception e) {
				log.error(e,e);
			}
	     }
	    
	}
	public static boolean sendData2Server(String host,int port,CommandHandler sendhandler) throws Exception{
		PrintWriter writer = null; 
		Socket client =null;
		try{
			 client = new Socket(host, port);
	    	 writer = SocketUtils.getWriterFromSocket(client);
	    	 sendhandler.handleSend(writer,client);
	     }catch (Exception e) {
			if(e instanceof java.net.ConnectException){
				throw new ClusterException("集群节点失联 "+host+":"+port);
			}else{
				throw e;
			}
	     }finally{
	    	 SocketUtils.close(writer,client);
	     }
		 return true;
	}
	
	
	
	
	/**
	 * 检测目标服务器是否可用
	 * @param host
	 * @param port
	 * @return
	 */
	public static boolean isOnline(String host,int port){
		Socket client = null;
		try {
			client = new Socket(host, port);
		} catch (Exception e) {
			log.error(e,e);
		}
		boolean online = client.isConnected();
		return online;
		
	}
}
