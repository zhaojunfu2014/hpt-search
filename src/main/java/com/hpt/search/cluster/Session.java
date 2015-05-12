package com.hpt.search.cluster;

public class Session {
	public static final String STATUS_ONLINE =   "on";
	public static final String STATUS_OFFLINE =  "off";
	public static final String MODE_MASTER="master";
	public static final String MODE_SLAVE="slave";
	
	private String id;
	private String mode;
	private String status;
	private Long publogs;
	private Long pubSlogs;
	private Long revlogs;
	private Long revSlogs;
	private Long errlog;
	
	private Long searchCount=1l;
	private Long searchTime=0l;
	
	private String logPath;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getPublogs() {
		return publogs;
	}
	public void setPublogs(Long publogs) {
		this.publogs = publogs;
	}
	public Long getRevlogs() {
		return revlogs;
	}
	public void setRevlogs(Long revlogs) {
		this.revlogs = revlogs;
	}
	public Long getErrlog() {
		return errlog;
	}
	public void setErrlog(Long errlog) {
		this.errlog = errlog;
	}
	
	public Long getPubSlogs() {
		return pubSlogs;
	}
	public void setPubSlogs(Long pubSlogs) {
		this.pubSlogs = pubSlogs;
	}
	public Long getRevSlogs() {
		return revSlogs;
	}
	public void setRevSlogs(Long revSlogs) {
		this.revSlogs = revSlogs;
	}
	
	public String getLogPath() {
		return logPath;
	}
	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}
	
	
	
	public Long getSearchCount() {
		return searchCount>0l?searchCount:1l;
	}
	public void setSearchCount(Long searchCount) {
		this.searchCount = searchCount;
	}
	public Long getSearchTime() {
		return searchTime;
	}
	public void setSearchTime(Long searchTime) {
		this.searchTime = searchTime;
	}
	public Session(String id, String mode, String status, Long publogs,
			Long revlogs, Long errlog,Long pubSlogs,Long revSlogs,String logPath) {
		this.id = id;
		this.mode = mode;
		this.status = status;
		this.publogs = publogs;
		this.revlogs = revlogs;
		this.errlog = errlog;
		this.pubSlogs = pubSlogs;
		this.revSlogs = revSlogs;
		this.logPath = logPath;
	}
	public void update(Session source) {
		this.id = source.getId();
		this.mode = source.getMode();
		this.status = source.getStatus();
		this.publogs = source.getPublogs();
		this.revlogs = source.getRevlogs();
		this.errlog = source.getErrlog();
		this.pubSlogs = source.getPubSlogs();
		this.revSlogs = source.getRevSlogs();
		this.logPath = source.getLogPath();
		this.searchCount = source.getSearchCount();
		this.searchTime = source.getSearchTime();
	}
	
	public Session() {
	}
	
	
}
