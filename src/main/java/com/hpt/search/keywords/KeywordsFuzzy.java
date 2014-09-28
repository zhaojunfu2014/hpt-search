package com.hpt.search.keywords;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 
 * @Title:KeywordsFuzzy
 * @description:关键字模糊查询
 * @author 赵俊夫
 * @date 2014-9-28 下午4:37:01
 * @version V1.0
 */
public class KeywordsFuzzy {
	/**
	 * 使 英文+数字 的词 具有模糊查询的功能
	 * @param searchKeywords
	 * @return
	 */
    public static String getKeywords(String searchKeywords) {
    	StringBuilder re = new StringBuilder();
    	String[] ks = searchKeywords.split(" ");
    	int i =0;
    	for(String k:ks){
    		re.append(k);
    		if(!isContainsChinese(k)){
    			//如果不包含中文,则加上 *
    			re.append("*");
    		}
    		if(i<ks.length-1)
    		re.append(" ");
    		i+=1;
    	}
		return re.toString();
	}

	private static boolean isContainsChinese(String k) {
		String regEx = "[\u4e00-\u9fa5]"; 
    	Pattern pat = Pattern.compile(regEx); 
    	Matcher matcher = pat.matcher(k);
    	boolean flg = false; if (matcher.find())    { 
    		flg = true;
    	} 
    	return flg;
	}
}
