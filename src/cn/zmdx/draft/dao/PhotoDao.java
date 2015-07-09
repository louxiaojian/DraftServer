package cn.zmdx.draft.dao;

import java.util.List;
import java.util.Map;

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

}
