package cn.zmdx.draft.entity;

import java.util.Date;

public class User {
	private int id;
	private String username;//昵称
	private String password;//密码
	private String address;//地址
	private String telephone;//联系电话
	private String name;//真实姓名
	private String loginname;//登录名
	private String flag;//用户状态，0:未激活，1:正常，2:冻结
	private String isvalidate;//真人验证，0:未验证，1:验证失败，2:验证成功,3:待审核
	private String validateUrl;//真人验证图片地址
	private String headPortrait;//头像
	private int age;//年龄
	private int gender;//性别：0未知，1男，2女
	private String introduction;//个人介绍
	private Date registrationDate;//注册时间
	private int orgId;//用户权限，0普通用户，1管理员
	private Date validateDate;//真人验证审批时间
	private int report;
	private String thirdParty;//登录平台
	private String uid;
	private String fileid;//头像图片 万象fileid
	private String isAttention;//是否已关注，0已关注，1未关注
	private String cookie;
	
	//选秀排名所用
	private int praise;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLoginname() {
		return loginname;
	}
	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getIsvalidate() {
		return isvalidate;
	}
	public void setIsvalidate(String isvalidate) {
		this.isvalidate = isvalidate;
	}
	public String getHeadPortrait() {
		return headPortrait;
	}
	public void setHeadPortrait(String headPortrait) {
		this.headPortrait = headPortrait;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public Date getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public int getOrgId() {
		return orgId;
	}
	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}
	public Date getValidateDate() {
		return validateDate;
	}
	public void setValidateDate(Date validateDate) {
		this.validateDate = validateDate;
	}
	public int getPraise() {
		return praise;
	}
	public void setPraise(int praise) {
		this.praise = praise;
	}
	public int getReport() {
		return report;
	}
	public void setReport(int report) {
		this.report = report;
	}
	public String getFileid() {
		return fileid;
	}
	public void setFileid(String fileid) {
		this.fileid = fileid;
	}
	public String getIsAttention() {
		return isAttention;
	}
	public void setIsAttention(String isAttention) {
		this.isAttention = isAttention;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getThirdParty() {
		return thirdParty;
	}
	public void setThirdParty(String thirdParty) {
		this.thirdParty = thirdParty;
	}
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public String getValidateUrl() {
		return validateUrl;
	}
	public void setValidateUrl(String validateUrl) {
		this.validateUrl = validateUrl;
	}
	
}
