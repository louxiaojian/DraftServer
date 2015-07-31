package cn.zmdx.draft.dao;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.zmdx.draft.entity.Photo;

public interface PhotoDao extends BaseDao {
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
	 * 获取相应周期的排名
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：下午4:34:21
	 * @param cycleId
	 */
	public List queryCycleRanking(String cycleId);
	/**
	 * 获取所有主题
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：下午5:31:12
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
	 * @date： 日期：2015-7-27 时间：上午11:22:56
	 * @param filterMap
	 * @return
	 */
	public List<Photo> queryDraftPhotosWall(Map<String, String> filterMap);
	/**
	 * 查看热门照片墙
	 * @author louxiaojian
	 * @date： 日期：2015-7-27 时间：下午12:03:02
	 * @param filterMap
	 * @return
	 */
	public List<Photo> queryHotPhotosWall(Map<String, String> filterMap);
	/**
	 * 获取相应图片集的图片信息
	 * @author louxiaojian
	 * @date： 日期：2015-7-27 时间：下午2:47:52
	 * @param id
	 * @return
	 */
	public List queryPhotoByPictureSetId(int id);
	/**
	 * 根据userid、pictureSetId、operationType查询操作记录
	 * @author louxiaojian
	 * @date： 日期：2015-7-27 时间：下午3:44:51
	 * @param userid
	 * @param pictureSetId
	 * @param operationType
	 * @return
	 */
	public List queryPhotoByPictureSetId(String userid, String pictureSetId,
			int operationType);
	/**
	 * 加载审批记录
	 * @author louxiaojian
	 * @date： 日期：2015-7-31 时间：下午2:22:47
	 * @param filterMap
	 * @return
	 */
	public List queryReviewRecords(Map<String, String> filterMap);

}
