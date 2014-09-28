package com.hpt.search.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.hpt.search.annotation.SearchColum;
import com.hpt.search.annotation.SearchEntity;
import com.hpt.search.common.SearchGlobal;

/**
 * 
 * @Title:LuceneUtils
 * @description:基于lucene的搜索引擎工具
 * @author 赵俊夫
 * @date 2014-6-23 下午5:10:10
 * @version V1.0
 */
public class LuceneUtils {
	private static ResourceBundle bundle = null;
	private static String indexDir = null;
	private static Version version= Version.LUCENE_43;
	private static final Logger log= Logger.getLogger(LuceneUtils.class);
	static{
		try{
			bundle = java.util.ResourceBundle.getBundle(SearchGlobal.configFile);
			indexDir = bundle.getString("lucene.indexDir");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取分词器
	 */
	public static Analyzer getAnalyzer() {
		// ik分词器
		IKAnalyzer analyzer = new IKAnalyzer();
        analyzer.setUseSmart(true);
		return analyzer;
	}
	/**
	 * 创建一个索引器的操作类
	 * 
	 * @param openMode
	 * @return
	 * @throws Exception
	 */
	public static IndexWriter createIndexWriter(OpenMode openMode)
			throws Exception {
		// 索引存放位置设置
		Directory dir = FSDirectory.open(new File(indexDir));
		// 索引配置类设置
		IndexWriterConfig iwc = new IndexWriterConfig(version, getAnalyzer());
		iwc.setOpenMode(openMode);
		IndexWriter writer = new IndexWriter(dir, iwc);
		return writer;
	}
	/**
	 * 创建一个查询器
	 * 
	 * @param queryFileds
	 *            在哪些字段上进行查询
	 * @param queryString
	 *            查询内容
	 * @return
	 * @throws ParseException
	 */
	public static Query createQuery(String[] queryFileds, String queryString) throws ParseException {
		QueryParser parser = new MultiFieldQueryParser(version, queryFileds,
				getAnalyzer());
		Query query = parser.parse(queryString);
		return query;
	}
	/***************************************************************************
	 * 创建一个搜索的索引器
	 * 
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	@SuppressWarnings("deprecation")
	public static IndexSearcher createIndexSearcher()
			throws CorruptIndexException, IOException {
		IndexReader reader = IndexReader.open(FSDirectory.open(new File(
				indexDir)));
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}
	/**
	 * 将doc转为实体
	 * @param doc
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static <T> T transDocToEntity(Document doc,Class<T> clazz,Highlighter highlight) throws InstantiationException, IllegalAccessException{
		T t = clazz.newInstance();
		java.lang.reflect.Field[] fields =clazz.getDeclaredFields();
		SearchEntity se = t.getClass().getAnnotation(com.hpt.search.annotation.SearchEntity.class);
		//doc的id标识-相当于主键
		String idField = se.idFiled();
		for(java.lang.reflect.Field f:fields){
			SearchColum searchColum = f.getAnnotation(SearchColum.class);
			if(searchColum ==null){
				continue;
			}
			String fieldName = f.getName();
			Class fieldType = f.getType();
			String fn= searchColum.name();
			boolean ishighLight = searchColum.highLight();
			String value = null;
			if(fn==null||"".equals(fn)){
				fn = fieldName;
			}
			if(fn.equals(idField)){
				value = doc.get(getClassKey(t, fn));
			}else{
				value = doc.get(fn);
			}
			
			try {
				if(value!=null){
					if(ishighLight==true&&highlight!=null&&fieldType==java.lang.String.class){
						//高亮处理
						TokenStream tokenStream = getAnalyzer().tokenStream(fn, new StringReader(value));
						String highLightValue = highlight.getBestFragment(tokenStream, value); 
						if(highLightValue!=null){
							value = highLightValue;
						}
					}
					BeanUtils.setProperty(t, fieldName, value);
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidTokenOffsetsException e) {
				e.printStackTrace();
			}
		}
		return (T)t;
	}
	/**
	 * 获取实体的标识
	 * @param t
	 * @param fn
	 * @return
	 */
	public  static <T> String getClassKey(T t,String fn){
		return t.getClass().getName()+"_"+fn;
	}
}
