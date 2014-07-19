package com.hpt.search.pojo;
import java.util.List;
/**
 * 
 * @Title:SearchResult
 * @description:搜索引擎查询返回的DTO
 * @author 赵俊夫
 * @date 2014-6-26 下午3:13:03
 * @version V1.0
 * @param <T>
 */
public class SearchResult<T> {
	private Long size;
	private List<T> result = null;
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public List<T> getResult() {
		return result;
	}
	public void setResult(List<T> result) {
		this.result = result;
	}
	
	
	
}
