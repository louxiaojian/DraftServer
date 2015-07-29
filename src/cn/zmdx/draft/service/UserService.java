package cn.zmdx.draft.service;

import cn.zmdx.draft.entity.Captcha;
import cn.zmdx.draft.entity.User;

public interface UserService {

	/**
	 * 用户登录
	 * @author louxiaojian
	 * @date： 日期：2015-7-6 时间：下午5:06:21
	 * @param loginname
	 * @return
	 */
	public User findByName(String loginname);

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
	/**
	 * 生成验证码
	 * @author louxiaojian
	 * @date： 日期：2015-7-29 时间：上午10:58:56
	 */
	public Captcha createCaptcha(String loginname, String code);

	/**
	 * 验证该手机号今日是否能获取
	 * @author louxiaojian
	 * @date： 日期：2015-7-29 时间：上午11:36:58
	 * @param telephone
	 */
	public int qualificationByTelephone(String telephone);

	/**
	 * 获取当前可用的验证码
	 * @author louxiaojian
	 * @date： 日期：2015-7-29 时间：上午11:55:27
	 * @param loginname
	 * @return
	 */
	public Captcha queryUsableCaptcha(String loginname);
}
