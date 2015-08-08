package cn.zmdx.draft.entity;

import java.util.Date;

public class UserAttentionFans {
	
	private int id;
	private int attentionUserId;//被关注的人id
	private int fansUserId;//粉丝id
	private Date attentionTime;//关注时间
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAttentionUserId() {
		return attentionUserId;
	}
	public void setAttentionUserId(int attentionUserId) {
		this.attentionUserId = attentionUserId;
	}
	public int getFansUserId() {
		return fansUserId;
	}
	public void setFansUserId(int fansUserId) {
		this.fansUserId = fansUserId;
	}
	public Date getAttentionTime() {
		return attentionTime;
	}
	public void setAttentionTime(Date attentionTime) {
		this.attentionTime = attentionTime;
	}
}
