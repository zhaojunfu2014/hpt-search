package com.hpt.junit;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;

import com.hpt.search.HptSearcher;
import com.hpt.search.cluster.log.Log;
import com.hpt.search.cluster.log.LogCreator;
import com.hpt.search.cluster.log.LogParser;
import com.hpt.search.pojo.SearchResult;
import com.hpt.search.service.SearchService;

public class SearchTest {
	private SearchService service = HptSearcher.getService();

	/**
	 * 创建索引 配置文件：resources/search.properties -indexDir:索引文件存放磁盘路径
	 */
	@Test
	public void create() {
		for (int i = 0; i < 5; i++) {
			SearchTestEntity t = new SearchTestEntity("赵大大力" + i,
					"精通java编程以及切水果" + i, "赵大大力" + i);
			try {
				service.createOrUpdateIndex(t);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 关键字搜索-无分页
	 */
	@Test
	public void search() {
		try {
			SearchResult<SearchTestEntity> r = service.findSearchResult(
					com.hpt.junit.SearchTestEntity.class, "大力");
			System.out.println(r.getSize());
			for(SearchTestEntity t:r.getResult()){
				System.out.println(t);
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 关键字搜索-分页
	 */
	@Test
	public void searchPage() {
		try {
			SearchResult<SearchTestEntity> r = service.findSearchResult(
					com.hpt.junit.SearchTestEntity.class, "张大力", 1, 2);
			System.out.println(r.getSize());
			for(SearchTestEntity t:r.getResult()){
				System.out.println(t);
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 索引更新
	 */
	@Test
	public void update() {
		SearchTestEntity t = new SearchTestEntity("赵大力" + 3,
				"嘻嘻java编程以及切水果hahahoho" + 3, "大力" + 3);
		try {
			service.createOrUpdateIndex(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
		search();
	}

	/**
	 * 索引删除
	 */
	@Test
	public void del() {
		SearchTestEntity t = new SearchTestEntity("赵大力" + 2,
				"嘻嘻java编程以及切水果hahahoho" + 2, "大力" + 2);
		try {
			service.deleteIndex(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
		search();
	}
	@Test
	public void testLog(){
		SearchTestEntity t = new SearchTestEntity("赵大力" + 2,
				"嘻嘻java编程以及切水果hahahoho" + 2, "大力" + 2);
		Log log = LogCreator.createLog(t);
		LogCreator.saveToFile(log, "h:/tmp/search");
	}
	@Test
	public void testParse(){
		Object o = LogParser.parseLog("h:/tmp/search","1405753191163-com.hpt.junit.SearchTestEntity.log");
		SearchTestEntity t = (SearchTestEntity) o;
		System.out.println(t);
	}
}
