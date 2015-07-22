package cn.zmdx.draft.entity;

import java.util.Date;

/**
 * 评论
 * @author louxiaojian
 * @date： 日期：2015-7-22 时间：上午11:44:01
 */
public class Comment {
	
	private int id;
	private Date datetime;//发表评论时间
	private String content;//评论内容
	private int parentId;//上级评论id，默认为0
	private int photoId;//评论所属图片id
	private int userId;//评论人id
	private String username;//评论人昵称
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getDatetime() {
		return datetime;
	}
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public int getPhotoId() {
		return photoId;
	}
	public void setPhotoId(int photoId) {
		this.photoId = photoId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

}
