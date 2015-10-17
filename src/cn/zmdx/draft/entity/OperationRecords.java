package cn.zmdx.draft.entity;

import java.util.Date;

public class OperationRecords {
	private int id;
	private int informerId;//操作人id
	private int operationType;//操作类型：1：踩，2：举报，3：投票，4：评论，5：回复，6：@，7：赞，8：关注
	private int pictureSetId;//被操作的图集id
	private int beingInformerId;//被举报的用户id
	private int type;//举报类型，0：举报图集，1举报用户,2其他操作
	private Date datetime;//举报时间
	private String isRead;//是否已读：0：未读，1：已读
	private String ip;//操作请求ip
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOperationType() {
		return operationType;
	}
	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}
	public int getPictureSetId() {
		return pictureSetId;
	}
	public void setPictureSetId(int pictureSetId) {
		this.pictureSetId = pictureSetId;
	}
	public int getInformerId() {
		return informerId;
	}
	public void setInformerId(int informerId) {
		this.informerId = informerId;
	}
	public int getBeingInformerId() {
		return beingInformerId;
	}
	public void setBeingInformerId(int beingInformerId) {
		this.beingInformerId = beingInformerId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Date getDatetime() {
		return datetime;
	}
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	public String getIsRead() {
		return isRead;
	}
	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
}
