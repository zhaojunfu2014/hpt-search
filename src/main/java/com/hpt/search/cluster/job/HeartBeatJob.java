package com.hpt.search.cluster.job;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.hpt.search.ConfigHolder;
import com.hpt.search.cluster.ClusterSessionManager;
import com.hpt.search.cluster.handler.HeartBeatHandler;
import com.hpt.search.cluster.net.Client;
/**
 * 
 * @Title:HeartBeatJob
 * @description:节点心跳检测任务
 * @author 赵俊夫
 * @date 2015-5-11 下午2:55:25
 * @version V1.0
 */
public class HeartBeatJob  extends TimerTask{
	private static final Logger log= Logger.getLogger(HeartBeatJob.class);
	
	@Override
	public void run() {
		log.debug("进行心跳检测...");
		String[] nodes = ConfigHolder.group.split(",");
		//更新主节点
		ClusterSessionManager.update(ConfigHolder.me,ClusterSessionManager.getLocalSession() );
		for(String node:nodes){
			String[] hostS = node.split(":");
			try {
				Client.sendData2Server(hostS[0], Integer.parseInt(hostS[1]), new HeartBeatHandler());
				//如果离线的节点重新上线，则更新状态
				ClusterSessionManager.online(node);
			}  catch (Exception e) {
				e.printStackTrace();
				//如果节点访问失败,则更新它为离线
				ClusterSessionManager.offline(node);
			}
		}
		log.debug("心跳检测完成...");
	}

}
