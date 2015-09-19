package cn.zmdx.draft.entity;

import java.util.Date;

public class Notify {
	//id,u.id as userId,u.username as username,u.gender as gender,u.headPortrait as headPortrait,u.introduction as introduction,
	//o.operation_type as type,o.datetime as datetime,ps.coverUrl as coverUrl,ps.id as pictureSetId
	private int id;
	private int userId;
	private String userName;
	private int gender;
	private String headPortrait;
	private String introduction;
	private int type;
	private Date dateTime;
	private String coverUrl;
	private int pictureSetId;
	private String isRead;//是否已读：0：未读，1：已读
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public String getHeadPortrait() {
		return headPortrait;
	}
	public void setHeadPortrait(String headPortrait) {
		this.headPortrait = headPortrait;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	public String getCoverUrl() {
		return coverUrl;
	}
	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}
	public int getPictureSetId() {
		return pictureSetId;
	}
	public void setPictureSetId(int pictureSetId) {
		this.pictureSetId = pictureSetId;
	}
	public String getIsRead() {
		return isRead;
	}
	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}
}
