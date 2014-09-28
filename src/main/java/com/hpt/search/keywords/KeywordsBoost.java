package com.hpt.search.keywords;
/**
 * 
 * @Title:QueryBoost
 * @description:搜索词打分
 * @lucene评分公式:
 * score(q,d)=coord(q,d)·queryNorm(q)· ∑(tf(t in d)·idf(t)2·t.getBoost()·norm(t,d))
 * @author 赵俊夫
 * @date 2014-7-24 下午2:47:23
 * @version V1.0
 */
public class KeywordsBoost {
	private static String queryBoostOpt = "^";
	private static String space = " ";
	private static int factor = 50;
	/**
	 * 根据搜索词的先后顺序设置权重来影响评分
	 * @param keywords
	 * @param multiple 倍数,即前后两个关键词之间相隔多少倍,默认1,常数50
	 * @return
	 */
	public static String getKeywords(String keywords,int multiple ){
		StringBuilder result = new StringBuilder();
		//将关键字拆分
		String[] keys = keywords.split(space);
		int size = keys.length;
		int index = 0;
		int boost;
		for(String k:keys){
			boost =(int) (Math.pow(multiple,size-index-1)*factor);
			k = k+queryBoostOpt+boost;
			result.append(k);
			if(index!=(size-1)){
				result.append(space);
			}
			index++;
		}
		return result.toString();
	}
	
	public static String getKeywords(String keywords){
		return getKeywords(keywords,1);
	}
}
