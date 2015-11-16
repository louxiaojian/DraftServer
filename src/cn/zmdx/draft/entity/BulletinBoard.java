package cn.zmdx.draft.entity;

public class BulletinBoard {
	
	private int id;
	private String imageUrl;//图片url
	private String url;//跳转url
	private int display;//是否显示  0：显示，1：不显示
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getDisplay() {
		return display;
	}
	public void setDisplay(int display) {
		this.display = display;
	}
}
