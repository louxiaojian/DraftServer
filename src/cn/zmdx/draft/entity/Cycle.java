package cn.zmdx.draft.entity;

import java.util.Date;

public class Cycle {
	
	private int id;
	private String themeTitle;//主题标题
	private Date starttime;//选秀开始时间
	private Date endtime;//选秀结束时间
	private String status;//选秀状态，0:结束，1:进行中，2:未开始
	private String bgUrl;//背景图
	private String descs;//主题描述
	private String tag;//标签图片路径
	private String detailImageUrl;//描述图片
	private String insideDetailImageUrl;//选秀查看详情页面内
	private String webDetailUrl;//web页面显示主题活动详情
	private String webTitleUrl;//web页面标题图片
	private String role;//主题规则
	private String awardSetting;//奖项设置
	private String isNeedValidate;//是否需要真人验证，0：需要，1：不需要
	private Date voteStartTime;//投票开始时间
	private Date voteEndTime;//投票结束时间
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
	public Date getEndtime() {
		return endtime;
	}
	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getThemeTitle() {
		return themeTitle;
	}
	public void setThemeTitle(String themeTitle) {
		this.themeTitle = themeTitle;
	}
	public String getBgUrl() {
		return bgUrl;
	}
	public void setBgUrl(String bgUrl) {
		this.bgUrl = bgUrl;
	}
	public String getDescs() {
		return descs;
	}
	public void setDescs(String descs) {
		this.descs = descs;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getDetailImageUrl() {
		return detailImageUrl;
	}
	public void setDetailImageUrl(String detailImageUrl) {
		this.detailImageUrl = detailImageUrl;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getAwardSetting() {
		return awardSetting;
	}
	public void setAwardSetting(String awardSetting) {
		this.awardSetting = awardSetting;
	}
	public String getIsNeedValidate() {
		return isNeedValidate;
	}
	public void setIsNeedValidate(String isNeedValidate) {
		this.isNeedValidate = isNeedValidate;
	}
	public String getInsideDetailImageUrl() {
		return insideDetailImageUrl;
	}
	public void setInsideDetailImageUrl(String insideDetailImageUrl) {
		this.insideDetailImageUrl = insideDetailImageUrl;
	}
	public Date getVoteStartTime() {
		return voteStartTime;
	}
	public void setVoteStartTime(Date voteStartTime) {
		this.voteStartTime = voteStartTime;
	}
	public Date getVoteEndTime() {
		return voteEndTime;
	}
	public void setVoteEndTime(Date voteEndTime) {
		this.voteEndTime = voteEndTime;
	}
	public String getWebDetailUrl() {
		return webDetailUrl;
	}
	public void setWebDetailUrl(String webDetailUrl) {
		this.webDetailUrl = webDetailUrl;
	}
	public String getWebTitleUrl() {
		return webTitleUrl;
	}
	public void setWebTitleUrl(String webTitleUrl) {
		this.webTitleUrl = webTitleUrl;
	}
}
