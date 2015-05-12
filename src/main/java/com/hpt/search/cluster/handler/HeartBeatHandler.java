package com.hpt.search.cluster.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.alibaba.fastjson.JSONObject;
import com.hpt.search.cluster.ClusterSessionManager;
import com.hpt.search.cluster.Session;
import com.hpt.search.cluster.net.SocketUtils;
/**
 * 
 * @Title:StatusHandler
 * @description:集群节点心跳检测
 * @author 赵俊夫
 * @date 2015-5-11 下午2:02:37
 * @version V1.0
 */
public class HeartBeatHandler implements CommandHandler {

	@Override
	public boolean handleSend(PrintWriter writer,Socket socket) {
		BufferedReader br  = null;
		try{
			writer.println(Command.HEARTBEAT);
			writer.flush();
			br = SocketUtils.getReaderFromSocket(socket);
			String heartInfo = br.readLine();//接收返回的信息
			Session s = JSONObject.parseObject(heartInfo, Session.class);
			//更新节点状态
			ClusterSessionManager.update(s.getId(), s);
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public boolean handleRecive(BufferedReader br,Socket socket) {
		//获取到本节点的信息
		Session s = ClusterSessionManager.getLocalSession();
		//发送给发送方
		String sessionData= JSONObject.toJSONString(s);
		PrintWriter pw = null;
		try {
			pw = SocketUtils.getWriterFromSocket(socket);
			pw.println(sessionData);
			pw.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			try{
				pw.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

}
