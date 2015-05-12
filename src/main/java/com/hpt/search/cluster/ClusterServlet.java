package com.hpt.search.cluster;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hpt.search.cluster.job.JobRunner;
import com.hpt.search.cluster.net.Server;
import com.hpt.search.cluster.page.AbstractPage;

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
	public void init() throws ServletException {
		super.init();
		//启动集群日志订阅服务器
		new Server().startup();
		//启动日志发布/重做任务
		new JobRunner().run();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String action = req.getParameter("page");
		if(action!=null){
			AbstractPage ap = AbstractPage.getPage(action);
			String html = ap.createPage();
			resp.setCharacterEncoding(ap.getEncode()==null?"utf-8":ap.getEncode());
			resp.setContentType(ap.getContentType()==null?"text/html":ap.getContentType());
			resp.getWriter().print(html);
			resp.getWriter().flush();
		}
	}
}
