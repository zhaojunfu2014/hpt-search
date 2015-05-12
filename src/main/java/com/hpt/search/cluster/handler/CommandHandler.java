package com.hpt.search.cluster.handler;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 
 * @Title:CommandHandler
 * @description:网络命令执行器
 * @author 赵俊夫
 * @date 2015-5-11 下午1:24:14
 * @version V1.0
 */
public interface CommandHandler {
	/**
	 * 执行规则-发送
	 * @return
	 */
	boolean handleSend(PrintWriter writer,Socket socket);
	
	/**
	 * 执行规则-接收
	 * @param socket 
	 * @return
	 */
	boolean handleRecive(BufferedReader br, Socket socket);
}
