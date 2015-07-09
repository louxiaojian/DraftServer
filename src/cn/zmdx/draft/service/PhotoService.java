package cn.zmdx.draft.service;

import java.util.List;
import java.util.Map;

import cn.zmdx.draft.entity.Photo;

public interface PhotoService {
	/**
	 * 查看个人照片
	 * @author louxiaojian
	 * @date： 日期：2015-7-8 时间：上午10:17:01
	 * @param filterMap
	 * @return
	 */
	public List queryPersonalPhotos(Map<String, String> filterMap);
	/**
	 * 查看照片墙
	 * @author louxiaojian
	 * @date： 日期：2015-7-8 时间：上午10:20:10
	 * @param filterMap
	 * @return
	 */
	public List queryPhotosWall(Map<String, String> filterMap);
	/**
	 * 用户上传图片
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:37:36
	 * @param filterMap
	 */
	public void uploadPhoto(Map<String, Object> filterMap);
	/**
	 * 修改Photo
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:47:39
	 * @param photo
	 */
	public void updatePhoto(Photo photo);
	/**
	 * 根据id获取Photo对象
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:48:32
	 * @param id
	 * @return
	 */
	public Photo getPhotoById(String id);
}