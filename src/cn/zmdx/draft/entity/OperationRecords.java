package cn.zmdx.draft.entity;

import java.util.Date;

public class OperationRecords {
	private int id;
	private int informerId;//操作人id
	private int operationType;//操作类型：0：赞，1：踩，2：举报，3：投票
	private int pictureSetId;//被操作的图集id
	private int beingInformerId;//被举报的用户id
	private int type;//举报类型，0：举报图集，1举报用户,2其他操作
	private Date datetime;//举报时间
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
}
