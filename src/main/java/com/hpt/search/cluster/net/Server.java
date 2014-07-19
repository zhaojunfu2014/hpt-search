package com.hpt.search.cluster.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.hpt.search.common.SearchGlobal;

/**
 * 
 * @Title:Server
 * @description:日志服务器
 * @author 赵俊夫
 * @date 2014-7-19 下午3:17:24
 * @version V1.0
 */
public class Server {
	private static final Logger log= Logger.getLogger(Server.class);
	protected static ResourceBundle bundle = null;
	private String host;
	private String port;
	private int portv;
	private String logSubTodo = null;
	
	public Server() {
		loadConfig();
	}
	public void loadConfig(){
		bundle = java.util.ResourceBundle.getBundle(SearchGlobal.configFile);
		String hostport =bundle.getString("lucene.cluster.me")==null?"":bundle.getString("lucene.cluster.me");
		host = hostport.split(":")[0];
		port = hostport.split(":")[1];
		portv = Integer.parseInt(port);
		String logbase = bundle.getString("lucene.cluster.logbase")==null?"":bundle.getString("lucene.cluster.logbase");
		String sub = logbase+SearchGlobal.pathSeparator+SearchGlobal.logSub;
		logSubTodo = sub+SearchGlobal.pathSeparator+SearchGlobal.logTodo;
	}
	public void startup(){
		 Thread th = new Thread(new Runnable() {  
             public void run() { 
             	try {
					listen();
				} catch (IOException e) {
					log.error(e,e);
				}
             }
         });
         th.start(); //启动线程运行  
	}
	public void listen() throws IOException{
		final ServerSocket server = new ServerSocket(portv);
		log.debug("hpt-search server is listening at port:"+portv);
		while (true) {  
            try {   
                final Socket socket = server.accept();  
                log.debug("hpt-search server revice a request");
                Thread th = new Thread(new Runnable() {  
                    public void run() { 
                    	handleRequest(socket);  
                    }
                });
                th.start(); //启动线程运行  
            } catch (Exception e) {  
                log.error(e,e);  
            }  
        }  
	}
	/**
	 * 处理客服端的日志,保存到订阅目录中
	 * @param socket
	 */
	public void handleRequest(Socket socket){
		Reader reader = null;
		try {
		      //接受客户端传来的数据
		      reader = new InputStreamReader(socket.getInputStream());  
		      BufferedReader br = new BufferedReader(reader);
		      String filename = br.readLine();
		      String data = br.readLine();
		      File f = new File(logSubTodo+SearchGlobal.pathSeparator+filename);
		      f.getParentFile().mkdirs();
		      FileOutputStream fos = new FileOutputStream(f);
		      fos.write(data.getBytes());
		      fos.flush();
		      fos.close();
		      socket.close();
		} catch (IOException e) {
			log.error(e,e);
		} 
	}
	
	public static void main(String[] args) {
		new Server().startup();
	}
}
