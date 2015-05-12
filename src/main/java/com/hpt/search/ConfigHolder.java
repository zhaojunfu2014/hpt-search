package com.hpt.search;

import java.util.ResourceBundle;

import com.hpt.search.common.SearchGlobal;

public class ConfigHolder {
	public static ResourceBundle bundle = null;
	public static String logbase = null;
	public static String me = null;
	public static long mePort;
	public static String group = null;
	public static String logPub = null;
	public static String logPubTodo = null;
	public static String logPubArchiver = null;
	public static String logPubError = null;
	public static String logSub = null;
	public static String logSubTodo = null;
	public static String logSubArchiver = null;
	public static long periodPub;
	public static long periodRedo;
	public static long periodPubFromError;
	public static long periodHeartBeat;
	public static String mode;
	
	static{
		bundle = java.util.ResourceBundle.getBundle(SearchGlobal.configFile);
		logbase = bundle.getString("lucene.cluster.logbase")==null?"":bundle.getString("lucene.cluster.logbase");
		me = bundle.getString("lucene.cluster.me")==null?"":bundle.getString("lucene.cluster.me");
		String mePortStr = me.split(":")[1];
		mePort = Long.parseLong(mePortStr);
		group = bundle.getString("lucene.cluster.group")==null?"":bundle.getString("lucene.cluster.group");
		group = group.replace(me+",", "");
		group = group.replace(","+me, "");
		logPub = logbase+SearchGlobal.pathSeparator+SearchGlobal.logPub;
		logPubTodo = logPub+SearchGlobal.pathSeparator+SearchGlobal.logTodo;
		logPubArchiver = logPub+SearchGlobal.pathSeparator+SearchGlobal.logArchiver;
		logPubError = logPub+SearchGlobal.pathSeparator+SearchGlobal.logError;
		logSub = logbase+SearchGlobal.pathSeparator+SearchGlobal.logSub;
		logSubTodo = logSub+SearchGlobal.pathSeparator+SearchGlobal.logTodo;
		logSubArchiver = logSub+SearchGlobal.pathSeparator+SearchGlobal.logArchiver;
		String periodPubStr = bundle.getString("lucene.cluster.period.pub");
		String periodRedoStr = bundle.getString("lucene.cluster.period.redo");
		String periodPubFromErrorStr = bundle.getString("lucene.cluster.period.pubFromError");
		String periodHeartBeatStr = bundle.getString("lucene.cluster.period.heartbeat");
		mode = bundle.getString("lucene.cluster.mode");
		periodPub = Long.parseLong(periodPubStr);
		periodRedo = Long.parseLong(periodRedoStr);
		periodHeartBeat = Long.parseLong(periodHeartBeatStr);
		periodPubFromError =  Long.parseLong(periodPubFromErrorStr);
	}
}
