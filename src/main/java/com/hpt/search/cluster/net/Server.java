package com.hpt.search.cluster.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.hpt.search.ConfigHolder;
import com.hpt.search.cluster.handler.CommandHandler;
import com.hpt.search.common.SearchGlobal;

/**
 * 
 * @Title:Server
 * @description:服务器
 * @author 赵俊夫
 * @date 2014-7-19 下午3:17:24
 * @version V1.0
 */
public class Server {
	private static final Logger log= Logger.getLogger(Server.class);
	
	public void startup(){
		 Thread th = new Thread(new Runnable() {  
             public void run() { 
             	try {
					listen();
				} catch (IOException e) {
					log.error(e,e);
					e.printStackTrace();
				}
             }
         });
         th.start(); //启动线程运行  
	}
	public void listen() throws IOException{
		final ServerSocket server = new ServerSocket((int)ConfigHolder.mePort);
		log.debug("hpt-search server is listening at port:"+(int)ConfigHolder.mePort);
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
            	e.printStackTrace();
                log.error(e,e);  
            }  
        }  
	}
	/**
	 * 处理客服端的请求
	 * @param socket
	 */
	public void handleRequest(Socket socket){
		Reader reader = null;
		BufferedReader br=null;
		try {
		      //接受客户端传来的数据
		      reader = new InputStreamReader(socket.getInputStream(),SearchGlobal.encode);  
		      br = new BufferedReader(reader);
		      String command = br.readLine();//命令处理器
		      CommandHandler cmdHandler = null;
		      if(command!=null){
		    	  try {
					cmdHandler = (CommandHandler) Class.forName(command).newInstance();
				}  catch (Exception e) {
					e.printStackTrace();
				}
		      }
		      cmdHandler.handleRecive(br,socket);//使用处理器来解析数据
		      
//		      String data = br.readLine();
//		      File f = new File(logSubTodo+SearchGlobal.pathSeparator+filename);
//		      f.getParentFile().mkdirs();
//		      fos = new FileOutputStream(f);
//		      fos.write(data.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e,e);
		} finally{
			  try {
				br.close();
				reader.close();
		        socket.close();
			} catch (IOException e) {
				e.printStackTrace();
				log.error(e,e);
			}
		     
		}
	}
	
	public static void main(String[] args) {
		new Server().startup();
	}
}
