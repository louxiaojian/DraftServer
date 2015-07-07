package cn.zmdx.draft.entity;

import java.util.Date;

public class Photo {
	
	private int id;
	private String photoUrl;//图片地址
	private Date uploadDate;//上传时间
	private String descs;//描述
	private String type;//分类，0:个人，1:选秀，2:秀场
	private String flag;//审核状态，0:未审核，1:审核通过，2:审核未通过
	private String praise;//赞
	private String tread;//踩
	private Date auditingDate;//审核时间
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	public Date getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}
	public String getDescs() {
		return descs;
	}
	public void setDescs(String descs) {
		this.descs = descs;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getPraise() {
		return praise;
	}
	public void setPraise(String praise) {
		this.praise = praise;
	}
	public String getTread() {
		return tread;
	}
	public void setTread(String tread) {
		this.tread = tread;
	}
	public Date getAuditingDate() {
		return auditingDate;
	}
	public void setAuditingDate(Date auditingDate) {
		this.auditingDate = auditingDate;
	}
	
}
