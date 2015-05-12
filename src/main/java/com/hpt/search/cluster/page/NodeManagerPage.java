package com.hpt.search.cluster.page;

import java.util.List;

import com.hpt.search.cluster.ClusterSessionManager;
import com.hpt.search.cluster.Session;

/**
 * 
 * @Title:NodeManagerPage
 * @description:节点管理页面
 * @author 赵俊夫
 * @date 2015-5-11 下午3:22:13
 * @version V1.0
 */
public class NodeManagerPage extends AbstractPage{

	@Override
	public String createPage() {
		List<Session> sessionList = ClusterSessionManager.getNodeList();
		StringBuilder html= new StringBuilder();
		html.append("<html><body><center>");
		html.append("<style type=\"text/css\">");
		html.append("table{border-collapse:collapse;border-spacing:0;border-left:1px solid #888;border-top:1px solid #888;background:#efefef;}");
		html.append("th,td{border-right:1px solid #888;border-bottom:1px solid #888;padding:5px 15px;}");
		html.append("th{font-weight:bold;background:#ccc;}");
		html.append("</style>");
		html.append("<div style='margin-bottom:10px;'>HPT-Search 集群节点管理</div>");
		html.append("<div style='margin-bottom:10px;font-size : 12px;'>总节点数:"+(sessionList.size())+" 宕机节点数:"+getDownNode(sessionList)+"</div>");
		html.append("<table border=\"1\">");
		html.append("<tr>");
		html.append("<td>节点</td><td>节点类型</td><td>待发布日志</td><td>已发布日志</td><td>待处理日志</td><td>已处理日志</td><td>错误日志</td><td>状态</td><td>日志路径</td><td>搜索次数</td><td>平均速度(ms/次)</td>");
		html.append("</tr>");
		
		for(Session s:sessionList){
			if(!"master".equalsIgnoreCase(s.getMode())){
				if(Session.STATUS_OFFLINE.equals(s.getStatus())){
					html.append("<tr style='background:#FFA07A;'>");
				}else{
					html.append("<tr style='background:#ADFF2F;'>");
				}
			}
			html.append("<td>"+s.getId()+"</td>");
			html.append("<td>"+s.getMode()+"</td>");
			html.append("<td>"+s.getPublogs()+"</td>");
			html.append("<td>"+s.getPubSlogs()+"</td>");
			html.append("<td>"+s.getRevlogs()+"</td>");
			html.append("<td>"+s.getRevSlogs()+"</td>");
			html.append("<td>"+s.getErrlog()+"</td>");
			html.append("<td>"+s.getStatus()+"</td>");
			html.append("<td>"+s.getLogPath()+"</td>");
			html.append("<td>"+s.getSearchCount()+"</td>");
			html.append("<td>"+s.getSearchTime()/s.getSearchCount()+"</td>");
			html.append("</tr>");
		}
		
		html.append("</table>");
		
		html.append("</center></body></html>");
		return html.toString();
	}

	private int getDownNode(List<Session> sessionList) {
		int n = 0;
		for(Session s:sessionList){
			if(Session.STATUS_OFFLINE.equalsIgnoreCase(s.getStatus())){
				n++;
			}
		}
		return n;
	}

	@Override
	public String getEncode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

}
