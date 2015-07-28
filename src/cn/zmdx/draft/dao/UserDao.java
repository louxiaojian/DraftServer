package cn.zmdx.draft.dao;

import cn.zmdx.draft.entity.User;

public interface UserDao extends BaseDao{

	/**
	 * 用户登录
	 * @author louxiaojian
	 * @date： 日期：2015-7-6 时间：下午5:10:52
	 * @param loginname
	 * @return
	 */
	public User findByName(String loginname);
}
