package com.hpt.search.cluster.page;

import com.alibaba.fastjson.JSONObject;
import com.hpt.junit.SearchTestEntity;
import com.hpt.search.HptSearcher;
import com.hpt.search.pojo.SearchResult;

public class SimpleSearchPage extends AbstractPage {

	@Override
	public String createPage() {
		try {
			SearchResult<SearchTestEntity>  t = HptSearcher.getService().findSearchResult(com.hpt.junit.SearchTestEntity.class, "赵俊夫");
			return JSONObject.toJSONString(t.getResult());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getEncode() {
		return null;
	}

	@Override
	public String getContentType() {
		return null;
	}

}
