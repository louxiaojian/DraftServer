package cn.zmdx.draft.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.zmdx.draft.entity.Comment;
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
	 * @param filterMap
	 */
	public List queryCycleRanking(Map<String, String> filterMap);
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
	/**
	 * 真人验证申请
	 * @author louxiaojian
	 * @date： 日期：2015-7-31 时间：上午11:20:43
	 * @param photo
	 * @param userId
	 */
	public void realityVerification(Photo photo, String userId);
	/**
	 * 加载审批记录
	 * @author louxiaojian
	 * @date： 日期：2015-7-31 时间：下午2:21:50
	 * @param filterMap
	 * @return
	 */
	public List queryReviewRecords(Map<String, String> filterMap);
	/**
	 * 举报用户
	 * @author louxiaojian
	 * @date： 日期：2015-8-10 时间：下午3:33:21
	 * @param filterMap
	 */
	public void reportUser(Map<String, String> filterMap);
	/**
	 * 根据选秀主题周期id查看选秀用户排名
	 * @author louxiaojian
	 * @date： 日期：2015-8-13 时间：下午8:29:25
	 * @param filterMap
	 * @return
	 */
	public List queryUserCycleRanking(Map<String, String> filterMap);
	/**
	 * 获取图集评论数
	 * @author louxiaojian
	 * @date： 日期：2015-8-14 时间：上午11:40:38
	 * @param id
	 * @return
	 */
	public int queryCommentByPictureSetId(int id);
	/**
	 * 发现照片集
	 * @author louxiaojian
	 * @date： 日期：2015-8-14 时间：下午12:04:58
	 * @param filterMap
	 * @return
	 */
	public List discoverPictureSet(Map<String, String> filterMap);
	/**
	 * 加载图集点赞人
	 * @author louxiaojian
	 * @date： 日期：2015-8-21 时间：下午12:28:41
	 * @param praiseFilterMap
	 * @return
	 */
	public List queryPraiseUsers(Map<String, String> praiseFilterMap);
	/**
	 * 验证用户是否已点赞
	 * @author louxiaojian
	 * @date： 日期：2015-8-21 时间：下午8:08:59
	 * @param currentUserId
	 * @param pictureSetId
	 */
	public int isPraisedPictureSet(String currentUserId,String pictureSetId);
	/**
	 * 删除图集
	 * @author louxiaojian
	 * @date： 日期：2015-9-10 时间：下午5:43:11
	 * @param filterMap
	 * @return
	 */
	public void deletePictureSet(Map<String, String> filterMap);
	/**
	 * 发表评论
	 * @author louxiaojian
	 * @date： 日期：2015-9-19 时间：下午2:31:17
	 * @param comment
	 */
	public void saveComment(Comment comment);
	/**
	 * 加载通知
	 * @author louxiaojian
	 * @date： 日期：2015-9-19 时间：下午2:51:36
	 * @param filterMap
	 * @return
	 */
	public List queryNotify(Map<String, String> filterMap);
	/**
	 * 将通知改为已读状态
	 * @author louxiaojian
	 * @date： 日期：2015-9-19 时间：下午4:31:35
	 * @param filterMap
	 * @return
	 */
	public int readNotify(Map<String, String> filterMap);
	/**
	 * 删除评论
	 * @author louxiaojian
	 * @date： 日期：2015-9-19 时间：下午8:40:08
	 * @param filterMap
	 * @return
	 */
	public int deleteComment(Map<String, String> filterMap);
}
