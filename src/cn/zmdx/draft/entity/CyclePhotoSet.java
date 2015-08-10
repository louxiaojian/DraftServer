package cn.zmdx.draft.entity;

public class CyclePhotoSet {
	private int id;
	private int photoSetId;
	private int themeCycleId;
	private String themeTitle;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPhotoSetId() {
		return photoSetId;
	}
	public void setPhotoSetId(int photoSetId) {
		this.photoSetId = photoSetId;
	}
	public String getThemeTitle() {
		return themeTitle;
	}
	public void setThemeTitle(String themeTitle) {
		this.themeTitle = themeTitle;
	}
	public int getThemeCycleId() {
		return themeCycleId;
	}
	public void setThemeCycleId(int themeCycleId) {
		this.themeCycleId = themeCycleId;
	}
}
