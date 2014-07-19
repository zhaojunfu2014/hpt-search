package com.hpt.search.cluster;

import com.hpt.search.cluster.job.JobRunner;
import com.hpt.search.cluster.net.Server;
/**
 * 
 * @Title:ClusterMain
 * @description:启动集群服务
 * @author 赵俊夫
 * @date 2014-7-19 下午10:10:22
 * @version V1.0
 */
public class ClusterMain {
	public static void main(String[] args) {
		//启动集群日志订阅服务器
		new Server().startup();
		//启动日志发布/重做任务
		new JobRunner().run();
	}
}
