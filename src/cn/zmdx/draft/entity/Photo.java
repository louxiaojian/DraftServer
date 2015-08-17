package cn.zmdx.draft.entity;

import java.util.Date;

public class Photo {
	
	private int id;
	private String photoUrl;//图片地址
	private Date uploadDate;//上传时间
//	private String descs;//描述
	private int type;//0图集，1真人验证
//	private String status;//审核状态，0:未审核，1:审核通过，2:审核未通过
//	private int praise;//赞
//	private int tread;//踩
//	private Date auditingDate;//审核时间
	private int userid;
//	private int report;//举报次数
//	private int view;//浏览量
	private String fileid;//万象fileid
	private int pictureSetId;//图集id
	
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
//	public String getDescs() {
//		return descs;
//	}
//	public void setDescs(String descs) {
//		this.descs = descs;
//	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
//	public String getStatus() {
//		return status;
//	}
//	public void setStatus(String status) {
//		this.status = status;
//	}
//	public int getPraise() {
//		return praise;
//	}
//	public void setPraise(int praise) {
//		this.praise = praise;
//	}
//	public int getTread() {
//		return tread;
//	}
//	public void setTread(int tread) {
//		this.tread = tread;
//	}
//	public Date getAuditingDate() {
//		return auditingDate;
//	}
//	public void setAuditingDate(Date auditingDate) {
//		this.auditingDate = auditingDate;
//	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
//	public int getReport() {
//		return report;
//	}
//	public void setReport(int report) {
//		this.report = report;
//	}
//	public int getView() {
//		return view;
//	}
//	public void setView(int view) {
//		this.view = view;
//	}
//	public int getVotes() {
//		return votes;
//	}
//	public void setVotes(int votes) {
//		this.votes = votes;
//	}
	public int getPictureSetId() {
		return pictureSetId;
	}
	public void setPictureSetId(int pictureSetId) {
		this.pictureSetId = pictureSetId;
	}
	public String getFileid() {
		return fileid;
	}
	public void setFileid(String fileid) {
		this.fileid = fileid;
	}
	
}
