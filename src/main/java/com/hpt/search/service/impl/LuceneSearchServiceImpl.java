package com.hpt.search.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;

import com.hpt.search.annotation.SearchColum;
import com.hpt.search.annotation.SearchEntity;
import com.hpt.search.common.SearchGlobal;
import com.hpt.search.pojo.SearchResult;
import com.hpt.search.util.LuceneUtils;
/**
 *  
 * @Title:DefualtSearchServiceImpl
 * @description:搜索引擎服务-默认实现
 * @author 赵俊夫
 * @date 2014-6-26 下午2:32:30
 * @version V1.0
 */
public class LuceneSearchServiceImpl extends BaseSearchService{
	private static final Logger log= Logger.getLogger(LuceneSearchServiceImpl.class);
	protected static ResourceBundle bundle = null;
	protected IndexSearcher indexSearcher = null;
	protected SimpleHTMLFormatter formatter = null;
	protected QueryScorer scorer=null;
	//是否打开高亮功能
	protected boolean highLight = false;
	protected String highLightPre = "";
	protected String highLightExt = "";
	
	public LuceneSearchServiceImpl() {
		loadCfg();
	}

	/**
	 * 读取配置文件
	 */
	protected void loadCfg() {
		bundle = java.util.ResourceBundle.getBundle(SearchGlobal.configFile);
		String highlight = bundle.getString("highlight");
		highLightPre = bundle.getString("highlight.pre")==null?"":bundle.getString("highlight.pre");
		highLightExt = bundle.getString("highlight.ext")==null?"":bundle.getString("highlight.ext");
		formatter = new SimpleHTMLFormatter(highLightPre,highLightExt);
		if("on".equalsIgnoreCase(highlight)){
			highLight = true;
		}
	}
	
	
	@SuppressWarnings("unused")
	@Override
	public <T> int createOrUpdateIndex(T t) throws Exception {
		//操作增，删,改索引库的
		IndexWriter writer = LuceneUtils.createIndexWriter(OpenMode.CREATE_OR_APPEND);
		//进行写入文档
		Document doc = new Document();
		java.lang.reflect.Field[] fields = t.getClass().getDeclaredFields();
		SearchEntity se = t.getClass().getAnnotation(com.hpt.search.annotation.SearchEntity.class);
		//doc的id标识-相当于主键
		String idField = se.idFiled();
		String idValue = null;
		//根据传入对象的@SearchColum描述进行索引写入
		for(java.lang.reflect.Field f:fields){
			SearchColum searchColum = f.getAnnotation(SearchColum.class);
			if(searchColum==null){
				continue;
			}
			String fieldName = f.getName();
			String fn= searchColum.name();
			if(fn==null||"".equalsIgnoreCase(fn)){
				fn = fieldName;
			}
			//是否持久化
			boolean isstore = searchColum.store();
			//是否创建索引
			boolean index = searchColum.index();
			//获取字段权重
			float boost = searchColum.boost();
			//获取字段的值
			String value = BeanUtils.getProperty(t, fieldName);
			
			Field lfield = null;
			if(idField.equals(fieldName)){
				//实体的标识主键-特殊处理
				//不创建索引
				lfield =  new StringField(LuceneUtils.getClassKey(t, fn), value, isstore?Store.YES:Store.NO);
				idValue = value;
			}else if(index){
				//创建索引
				lfield =  new TextField(fn, value, isstore?Store.YES:Store.NO);
			}else{
				//不创建索引
				lfield =  new StringField(fn, value, isstore?Store.YES:Store.NO);
			}
			lfield.setBoost(boost);
			doc.add(lfield);
		}
		// 文件路径
		Field pathField = new StringField("path", t.getClass().getName(),Field.Store.YES);
		doc.add(pathField);
		// 文件最后修改时间
		doc.add(new StringField("modified",String.valueOf(new Date().getTime()), Field.Store.YES));
		if(idValue!=null&&!"".equals(idValue)){
			//更新
			writer.updateDocument(new Term(LuceneUtils.getClassKey(t, idField),idValue),doc );
		}else{
			//新增
			writer.addDocument(doc);
		}
		// 释放资源
		writer.close();
		return 1;
	}

	@Override
	public <T> int deleteIndex(T t) throws Exception {
		SearchEntity se = t.getClass().getAnnotation(com.hpt.search.annotation.SearchEntity.class);
		//doc的id标识-相当于主键
		String idField = se.idFiled();
		//获取字段的值
		String value = BeanUtils.getProperty(t, idField);
		//操作增，删,改索引库的
		IndexWriter writer = LuceneUtils.createIndexWriter(OpenMode.CREATE_OR_APPEND);
		writer.deleteDocuments(new Term(LuceneUtils.getClassKey(t, idField), value));
		// 释放资源
		writer.close();
		return 1;
	}

	@Override
	public <T> SearchResult<T> findSearchResult(Class<T> clazz, String keywords,
			Filter filter,Sort sort, boolean doDocScores, boolean doMaxScore,
			int page, int pageSize) throws CorruptIndexException, IOException, ParseException {
		SearchEntity se=clazz.getAnnotation(SearchEntity.class);
		String[] fs = se.searchFields();
		IndexSearcher searcher = getIndexSearch();
		Query query = LuceneUtils.createQuery(fs, keywords);
		scorer=new QueryScorer(query); 
		//存放结果
		SearchResult<T> searchResult = new SearchResult<T>();
		int pageLimit = Integer.MAX_VALUE;
		if(page==0){
			//不分页
			TopDocs results = null;
			if(sort==null){
				results = searcher.search(query,pageLimit);
			}else{
				results = searcher.search(query, filter, pageLimit, sort, doDocScores, doMaxScore);
			}
			//封装数据
			wrapData(searcher,results,searchResult,clazz);
		}else{
			//分页
			TopDocs tds = null;
			if(sort==null){
				TopScoreDocCollector results = TopScoreDocCollector.create(page*pageSize, doDocScores);
				searcher.search(query, filter, results);
				tds = results.topDocs((page-1)*pageSize, pageSize);
			}else{
				TopFieldCollector results = TopFieldCollector.create(sort, page*pageSize, false, doDocScores, doMaxScore, false);
				searcher.search(query, filter, results);
				tds = results.topDocs((page-1)*pageSize, pageSize);
			}
			//封装数据
			wrapData(searcher,tds,searchResult,clazz);
		}
		return searchResult;
	}

	protected IndexSearcher getIndexSearch() throws CorruptIndexException,
			IOException {
		if(indexSearcher==null){
			indexSearcher = LuceneUtils.createIndexSearcher();
		}
		return indexSearcher;
	}
	/**
	 * 包装数据
	 * @param searcher
	 * @param results
	 * @param searchResult
	 * @param clazz
	 * @throws IOException
	 */
	public <T> void wrapData(IndexSearcher searcher,TopDocs results,SearchResult<T> searchResult,Class<T> clazz) throws IOException {
		/**创建Fragmenter*/  
        Fragmenter fragmenter = null;
        Highlighter highlight= null;
        if(highLight){
	        fragmenter= new SimpleSpanFragmenter(scorer);  
	        highlight =new Highlighter(formatter,scorer);  
	        highlight.setTextFragmenter(fragmenter); 
        }
        
		List<T> searchList = searchResult.getResult();
		if(searchList==null){
			searchList = new ArrayList<T>();
		}
		Long size = (searchResult.getSize()==null?0L:searchResult.getSize())+new Long(results.totalHits);
		searchResult.setSize(size);
		
		// 记录
		for (ScoreDoc sr : results.scoreDocs) {
			// 文档编号
			int docID = sr.doc;
			// 真正的内容
			Document doc = searcher.doc(docID);
			try {
				T t = LuceneUtils.transDocToEntity(doc,clazz,highlight);
				searchList.add(t);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		searchResult.setResult(searchList);
		
	}

	

	
	
}
