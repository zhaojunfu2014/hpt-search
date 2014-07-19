package com.hpt.search.cluster;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hpt.search.cluster.job.JobRunner;
import com.hpt.search.cluster.net.Server;

/**
 * 
 * @Title:ClusterServlet
 * @description:集群服务启动入口
 * @author 赵俊夫
 * @date 2014-7-19 下午5:42:24
 * @version V1.0
 */
public class ClusterServlet extends HttpServlet{
	@Override
	public void init(){
		//启动集群日志订阅服务器
		new Server().startup();
		//启动日志发布/重做任务
		new JobRunner().run();
	}
}
