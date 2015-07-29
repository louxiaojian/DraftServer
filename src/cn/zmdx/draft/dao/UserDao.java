package cn.zmdx.draft.dao;

import cn.zmdx.draft.entity.Captcha;
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
	/**
	 * 使loginname之前验证码失效
	 * @author louxiaojian
	 * @date： 日期：2015-7-29 时间：上午11:06:17
	 * @param loginname
	 */
	public void updateCaptchaByLoginname(String loginname);
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
