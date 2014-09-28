package com.hpt.search.pojo;

import java.util.Map;

/**
 * 
 * @Title:GroupResult
 * @description:分组统计
 * @author 赵俊夫
 * @date 2014-7-30 下午2:24:08
 * @version V1.0
 */
public class GroupResult {
	//总数据数
	private long totalHitCount;
	//去重复后的数量
	private long distinctCount;
	private long time;
	
	private Map<String,Long> countMap;
	private Map<String,Object> valueMap; 
	
	public long getTotalHitCount() {
		return totalHitCount;
	}
	public void setTotalHitCount(long totalHitCount) {
		this.totalHitCount = totalHitCount;
	}
	public long getDistinctCount() {
		return distinctCount;
	}
	public void setDistinctCount(long distinctCount) {
		this.distinctCount = distinctCount;
	}
	public Map<String, Long> getCountMap() {
		return countMap;
	}
	public void setCountMap(Map<String, Long> countMap) {
		this.countMap = countMap;
	}
	public Map<String, Object> getValueMap() {
		return valueMap;
	}
	public void setValueMap(Map<String, Object> valueMap) {
		this.valueMap = valueMap;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	
}
