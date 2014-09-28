package com.hpt.search.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.apache.lucene.search.grouping.GroupDocs;
import org.apache.lucene.search.grouping.GroupingSearch;
import org.apache.lucene.search.grouping.TopGroups;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;

import com.hpt.search.annotation.SearchColum;
import com.hpt.search.annotation.SearchEntity;
import com.hpt.search.common.SearchGlobal;
import com.hpt.search.pojo.GroupResult;
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
	protected IndexWriter indexWriter = null;
	protected SimpleHTMLFormatter formatter = null;
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
	@Override
	public <T> int createOrUpdateIndexBatch(List<T> t) throws Exception {
		for(T tt:t){
			createOrUpdateIndex(tt,true);
		}
		flushIndexWriter();
		return t.size();
	}

	@Override
	public <T> int createOrUpdateIndex(T t) throws Exception {
		return createOrUpdateIndex(t,false);
	}
	
	public <T> int createOrUpdateIndex(T t,boolean batch) throws Exception {
		//操作增，删,改索引库的
		IndexWriter writer = null;
		if(batch){
			writer = createIndexWriter(OpenMode.CREATE_OR_APPEND);
		}else{
			writer =LuceneUtils.createIndexWriter(OpenMode.CREATE_OR_APPEND);
		}
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
		if(!batch){
			// 释放资源
			writer.close();
		}
		flushIndexSearch();
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
		flushIndexSearch();
		return 1;
	}

	@Override
	public <T> SearchResult<T> findSearchResult(Class<T> clazz, String keywords,
			Filter filter,Sort sort, boolean doDocScores, boolean doMaxScore,
			int page, int pageSize) throws CorruptIndexException, IOException, ParseException {
		long starttime = System.currentTimeMillis();
		SearchEntity se=clazz.getAnnotation(SearchEntity.class);
		String[] fs = se.searchFields();
		IndexSearcher searcher = createIndexSearch();
		Query query = LuceneUtils.createQuery(fs, keywords);
		QueryScorer scorer=new QueryScorer(query); 
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
			wrapData(scorer,searcher,results,searchResult,clazz);
		}else{
			//分页
			TopDocs tds = null;
			if(sort==null){
				TopScoreDocCollector results = TopScoreDocCollector.create(page*pageSize, false);
				searcher.search(query, filter, results);
				tds = results.topDocs((page-1)*pageSize, pageSize);
			}else{
				TopFieldCollector results = TopFieldCollector.create(sort, page*pageSize, false, false, false, false);
				searcher.search(query, filter, results);
				tds = results.topDocs((page-1)*pageSize, pageSize);
			}
			//封装数据
			wrapData(scorer,searcher,tds,searchResult,clazz);
		}
		long endtime = System.currentTimeMillis();
		//耗时
		searchResult.setTime(endtime-starttime);
		return searchResult;
	}
	@Override
	public <T> GroupResult group(Class<T> clazz,String keywords,String groupField, String valueField,
			int grouplimit) throws Exception {
		long st = System.currentTimeMillis();
		SearchEntity se = clazz.getAnnotation(com.hpt.search.annotation.SearchEntity.class);
		String[] queryArr = se.searchFields();
		IndexSearcher searcher = createIndexSearch();
		GroupingSearch  gSearch=new GroupingSearch(groupField);
		Query q=LuceneUtils.createQuery( queryArr, keywords);
		TopGroups t=gSearch.search(searcher, q, 0, grouplimit);//设置返回数据
		GroupDocs[] g=t.groups;//获取分组总数  
		GroupResult result = new GroupResult();
		result.setTotalHitCount(t.totalHitCount);
		result.setDistinctCount(g.length);
		Map<String,Long> countMap =  new HashMap<String,Long>();
		Map<String,Object> valueMap= new HashMap<String,Object>(); 
		 for(int i=0;i<g.length;i++){  
             ScoreDoc []sd=g[i].scoreDocs;  
             String gfv = searcher.doc(sd[0].doc).get(groupField);  
             String vfv = searcher.doc(sd[0].doc).get(valueField);  
             valueMap.put(gfv, vfv);
             countMap.put(gfv, new Long(g[i].totalHits));
		 }  
		 long et = System.currentTimeMillis();
		 result.setCountMap(countMap);
		 result.setValueMap(valueMap);
		 result.setTime(et-st);
		 return result;
	}
	
	/**
	 * 包装数据
	 * @param searcher
	 * @param results
	 * @param searchResult
	 * @param clazz
	 * @throws IOException
	 */
	public <T> void wrapData(QueryScorer scorer,IndexSearcher searcher,TopDocs results,SearchResult<T> searchResult,Class<T> clazz) throws IOException {
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
	public IndexSearcher createIndexSearch() throws CorruptIndexException,IOException {
		if(indexSearcher==null){
			indexSearcher = LuceneUtils.createIndexSearcher();
		}
		return indexSearcher;
	}
	protected IndexWriter createIndexWriter(OpenMode mode) throws Exception {
		if(indexWriter==null){
			indexWriter = LuceneUtils.createIndexWriter(mode);
		}
		return indexWriter;
	}
	protected void flushIndexWriter(){
		if(this.indexWriter!=null){
			try {
				this.indexWriter.close();
				this.indexWriter = null;
			} catch (IOException e) {
				log.error(e,e);
			}
		}
	}
	/**
	 * 释放掉索引搜索器-当索引有更新时需要重新打开IndexReader
	 */
	protected void flushIndexSearch(){
		try {
			if(this.indexSearcher!=null){
				this.indexSearcher.getIndexReader().close();
				this.indexSearcher = null;
			}
		} catch (IOException e) {
			log.error(e,e);
		}
	}

	

	
	
}
