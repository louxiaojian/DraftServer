package cn.zmdx.draft.service;

import java.io.IOException;
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
	 * 修改object
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:47:39
	 * @param photo
	 */
	public void updateObject(Object object);
	/**
	 * 根据id获取Object对象
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:48:32
	 * @param id
	 * @return
	 */
	public Object getObjectById(Class clazz,String id);
	/**
	 * 获取相应周期的排名
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：下午4:34:21
	 * @param cycleId
	 */
	public List queryCycleRanking(String cycleId);
	/**
	 * 获取所有主题
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：下午5:29:49
	 * @param filterMap
	 * @return
	 */
	public List queryThemes(Map<String, Object> filterMap);
	/**
	 * 根据主题id获取相关选秀记录
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：下午5:29:08
	 * @throws IOException
	 */
	public List queryCycleByThemesId(Map<String, Object> filterMap);
	/**
	 * 验证用户是否参与此次选秀
	 * @author louxiaojian
	 * @date： 日期：2015-7-20 时间：下午12:26:33
	 * @param filterMap
	 * @return
	 */
	public List validateIsAttend(Map<String, Object> filterMap);
	/**
	 * 保存对象
	 * @author louxiaojian
	 * @date： 日期：2015-7-7 时间：上午11:31:29
	 * @param obj
	 * @return
	 */
	public void saveEntity(Object obj);
	/**
	 * 查询相应的评论信息
	 * @author louxiaojian
	 * @date： 日期：2015-7-22 时间：下午12:27:59
	 * @param filterMap
	 * @return
	 */
	public List queryComment(Map<String, Object> filterMap);
	/**
	 * 查看选秀照片墙
	 * @author louxiaojian
	 * @date： 日期：2015-7-27 时间：上午11:21:56
	 * @param filterMap
	 * @return
	 */
	public List queryDraftPhotosWall(Map<String, String> filterMap);
	/**
	 * 查看热门照片墙
	 * @author louxiaojian
	 * @date： 日期：2015-7-27 时间：下午12:02:18
	 * @param filterMap
	 * @return
	 */
	public List queryHotPhotosWall(Map<String, String> filterMap);
	/**
	 * 获取相应图片集的图片信息
	 * @author louxiaojian
	 * @date： 日期：2015-7-27 时间：下午2:45:46
	 * @param id
	 */
	public List queryPhotoByPictureSetId(int id);
	/**
	 * 根据userid、pictureSetId、operationType查询操作记录
	 * @author louxiaojian
	 * @date： 日期：2015-7-27 时间：下午3:43:15
	 * @param userid
	 * @param pictureSetId
	 * @param operationType
	 * @return
	 */
	public String OperationPictureSet(String userid, String pictureSetId,
			int operationType);
}
