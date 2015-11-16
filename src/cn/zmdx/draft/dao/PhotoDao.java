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
	 * @param filterMap
	 */
	public List queryCycleRanking(Map<String, String> filterMap);
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
	/**
	 * 根据选秀主题周期id查看选秀用户排名
	 * @author louxiaojian
	 * @date： 日期：2015-8-13 时间：下午8:30:47
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
	 * @date： 日期：2015-8-14 时间：下午12:05:46
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
	 * 执行sql语句
	 * @author louxiaojian
	 * @date： 日期：2015-8-31 时间：下午5:30:59
	 * @param sql
	 */
	public void executeSql(String sql);
	/**
	 * 删除操作记录
	 * @author louxiaojian
	 * @date： 日期：2015-8-31 时间：下午5:43:40
	 * @param userId
	 * @param pictureSetId
	 * @return 
	 */
	public int deleteOperationRecords(int userId,int pictureSetId);
	/**
	 * 根据id获取图片
	 * @author louxiaojian
	 * @date： 日期：2015-9-15 时间：下午3:36:40
	 * @return
	 */
	public List queryPhotoByPictureSetIds(String pictureSetIds);
	/**
	 * 加载通知
	 * @author louxiaojian
	 * @date： 日期：2015-9-19 时间：下午2:52:52
	 * @param filterMap
	 * @return
	 */
	public List queryNotify(Map<String, String> filterMap);
	/**
	 * 将通知改为已读状态
	 * @author louxiaojian
	 * @date： 日期：2015-9-19 时间：下午4:32:23
	 * @param filterMap
	 * @return
	 */
	public int readNotify(Map<String, String> filterMap);
	/**
	 * 删除评论
	 * @author louxiaojian
	 * @date： 日期：2015-9-19 时间：下午8:40:59
	 * @param id
	 * @return
	 */
	public int deleteComment(Map<String, String> filterMap);
	/**
	 * 用户当前主题剩余票数
	 * @author louxiaojian
	 * @date： 日期：2015-9-25 时间：下午3:49:25
	 * @param surplusVotesFilterMap
	 * @return
	 */
	public int queryUserSurplusVote(Map<String, String> surplusVotesFilterMap);
	/**
	 * 检查当前ip投票次数
	 * @author louxiaojian
	 * @date： 日期：2015-10-16 时间：下午12:11:59
	 * @param filterMap
	 * @return
	 */
	public List queryOperations(Map<String, String> filterMap);
	/**
	 * 查询我关注的人的最新图集
	 * @author louxiaojian
	 * @date： 日期：2015-11-10 时间：上午10:52:52
	 * @param filterMap
	 * @return
	 */
	public List queryPhotoSetByAttentedUser(Map<String, String> filterMap);
	/**
	 * 获取公告栏
	 * @author louxiaojian
	 * @date： 日期：2015-11-16 时间：上午11:37:30
	 * @param filterMap
	 * @return
	 */
	public List queryBulletinBoard(Map<String, String> filterMap);
}
