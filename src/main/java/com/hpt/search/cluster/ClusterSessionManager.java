package com.hpt.search.cluster;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.hpt.search.ConfigHolder;
import com.hpt.search.pojo.SearchRecord;

/**
 * 
 * @Title:ClusterSessionManager
 * @description:节点管理器
 * @author 赵俊夫
 * @date 2015-5-11 下午2:17:07
 * @version V1.0
 */
public class ClusterSessionManager {
	private static List<Session> nodeList = new ArrayList<Session>(0);
	
	static{
		if("master".equalsIgnoreCase(ConfigHolder.mode)){
			//加载主节点
			nodeList.add( getLocalSession());
			//加载从节点
			String[] nodes = ConfigHolder.group.split(",");
			for(String node : nodes){
				nodeList.add( new Session(node, "slave", Session.STATUS_ONLINE, 0l, 0l, 0l,0l,0l,"unknow"));
			}
		}
	}
	
	public static void update(String id,Session session){
		if(nodeList!=null){
			for(Session s:nodeList){
				if(id.equalsIgnoreCase(s.getId())){
					s.update(session);
				}
			}
		}
	}
	
	public static void changeStatus(String id,String status){
		if(nodeList!=null){
			for(Session s:nodeList){
				if(id.equalsIgnoreCase(s.getId())){
					s.setStatus(status);
				}
			}
		}
	}
	
	
	public static void offline(String id){
		changeStatus(id, Session.STATUS_OFFLINE);
	}
	public static void online(String id){
		changeStatus(id, Session.STATUS_ONLINE);
	}
	
	public static List<Session> getNodeList(){
		return nodeList;
	}
	
	public static Session getLocalSession(){
		File pubTodo = new File(ConfigHolder.logPubTodo);
		File pubArch = new File(ConfigHolder.logPubArchiver);
		File subTodo = new File(ConfigHolder.logSubTodo);
		File subArch = new File(ConfigHolder.logSubArchiver);
		File error = new File(ConfigHolder.logPubError);
		
		long publogs = pubTodo.exists()?pubTodo.list().length:0;
		long pubSlogs = pubArch.exists()?pubArch.list().length:0;
		long revlogs = subTodo.exists()?subTodo.list().length:0;
		long revSlogs = subArch.exists()?subArch.list().length:0;
		long errlogs = error.exists()?error.list().length:0;
		
		Session s = new Session(ConfigHolder.me, ConfigHolder.mode, Session.STATUS_ONLINE, publogs, revlogs, errlogs, pubSlogs, revSlogs,ConfigHolder.logbase);
		s.setSearchCount(SearchRecord.searchCount.get());
		s.setSearchTime(SearchRecord.searchTime.get());
		return s;
	}
	
}
