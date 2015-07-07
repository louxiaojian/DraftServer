package cn.zmdx.draft.service;

import java.util.List;

import cn.zmdx.draft.entity.User;

public interface UserService {

	/**
	 * 用户登录
	 * @author louxiaojian
	 * @date： 日期：2015-7-6 时间：下午5:06:21
	 * @param loginname
	 * @return
	 */
	public List<?> login(String loginname);

	/**
	 * 用户注册
	 * @author louxiaojian
	 * @date： 日期：2015-7-7 时间：上午11:27:29
	 * @param user
	 * @return
	 */
	public void saveUser(User user);
	/**
	 * 根据id获取user对象
	 * @author louxiaojian
	 * @date： 日期：2015-7-7 时间：下午12:17:08
	 * @param id
	 * @return
	 */
	public User getById(int id);
	/**
	 * 修改用户信息
	 * @author louxiaojian
	 * @date： 日期：2015-7-7 时间：上午11:28:05
	 * @param user
	 * @return
	 */
	public void updateUser(User user);
}
