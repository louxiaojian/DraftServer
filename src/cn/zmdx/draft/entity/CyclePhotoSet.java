package cn.zmdx.draft.entity;

public class CyclePhotoSet {
	private int id;
	private int photoSetId;
	private int cycleId;
	private String cycleNo;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCycleId() {
		return cycleId;
	}
	public void setCycleId(int cycleId) {
		this.cycleId = cycleId;
	}
	public String getCycleNo() {
		return cycleNo;
	}
	public void setCycleNo(String cycleNo) {
		this.cycleNo = cycleNo;
	}
	public int getPhotoSetId() {
		return photoSetId;
	}
	public void setPhotoSetId(int photoSetId) {
		this.photoSetId = photoSetId;
	}
}
