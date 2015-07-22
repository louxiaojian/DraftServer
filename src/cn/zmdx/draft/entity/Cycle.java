package cn.zmdx.draft.entity;

import java.util.Date;

public class Cycle {
	
	private int id;
	private String cycleNo;//选秀周期号
	private Date starttime;//选秀开始时间
	private Date signupEndtime;//报名结束时间
	private Date endtime;//选秀结束时间
	private String status;//选秀状态，0:未开始，1:进行中，2:结束
	private int themeId;//选秀主题id
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCycleNo() {
		return cycleNo;
	}
	public void setCycleNo(String cycleNo) {
		this.cycleNo = cycleNo;
	}
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
	public Date getSignupEndtime() {
		return signupEndtime;
	}
	public void setSignupEndtime(Date signupEndtime) {
		this.signupEndtime = signupEndtime;
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
	public int getThemeId() {
		return themeId;
	}
	public void setThemeId(int themeId) {
		this.themeId = themeId;
	}
}
