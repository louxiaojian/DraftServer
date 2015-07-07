package cn.zmdx.draft.dao;

import java.io.Serializable;

public interface BaseDao {
	/**
	 * 根据id获取对象
	 * @author louxiaojian
	 * @date： 日期：2015-7-7 时间：上午11:31:10
	 * @param entityClass
	 * @param id
	 * @return
	 */
	public Object getEntity(Class entityClass, Serializable id);
	/**
	 * 保存对象
	 * @author louxiaojian
	 * @date： 日期：2015-7-7 时间：上午11:31:29
	 * @param obj
	 * @return
	 */
	public void saveEntity(Object obj);
	/**
	 * 修改对象
	 * @author louxiaojian
	 * @date： 日期：2015-7-7 时间：上午11:31:41
	 * @param obj
	 * @return
	 */
	public void updateEntity(Object obj);
	/**
	 * 删除对象
	 * @author louxiaojian
	 * @date： 日期：2015-7-7 时间：上午11:32:55
	 * @param obj
	 * @return
	 */
	public void deleteEntity(Object obj);
}
