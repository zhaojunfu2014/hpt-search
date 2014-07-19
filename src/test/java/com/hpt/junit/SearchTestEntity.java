package com.hpt.junit;



import com.hpt.search.annotation.SearchColum;
import com.hpt.search.annotation.SearchEntity;

@SearchEntity(searchFields={"author","content"},idFiled="name")
public class SearchTestEntity {
	
	@SearchColum(name="author", index = true, store = true)
	private String author;
	
	@SearchColum(index =true, store = true)
	private String content;
	
	@SearchColum(index =true,name="name", store = true)
	private String name;
	
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SearchTestEntity(String author, String content, String name) {
		super();
		this.author = author;
		this.content = content;
		this.name = name;
	}
	public SearchTestEntity() {
		super();
	}
	
	
	@Override
	public String toString() {
		return "SearchTestEntity [author=" + author + ", content=" + content
				+ ", name=" + name + "]";
	}
}
