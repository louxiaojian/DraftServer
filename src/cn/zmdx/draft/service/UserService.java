package cn.zmdx.draft.service;

import java.util.List;
import java.util.Map;

import cn.zmdx.draft.entity.Captcha;
import cn.zmdx.draft.entity.User;
import cn.zmdx.draft.entity.UserAttentionFans;

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

	/**
	 * 添加对象
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午12:50:33
	 * @param obj
	 */
	public void saveObject(Object obj);
	
	/**
	 * 验证是否已关注
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午1:59:24
	 * @param fansUserId
	 * @param attentionUserId
	 * @return
	 */
	public UserAttentionFans isAttention(String fansUserId, String attentionUserId);

	/**
	 * 取消关注
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午2:26:38
	 * @param fansUserId
	 * @param attentionUserId
	 */
	public void cancelAttention(String fansUserId, String attentionUserId);

	/**
	 * 查看我关注的人
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午2:37:06
	 * @param filterMap
	 * @return
	 */
	public List queryAttentions(Map<String, String> filterMap);

	/**
	 * 查看我的粉丝
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午2:37:06
	 * @param filterMap
	 * @return
	 */
	public List queryFans(Map<String, String> filterMap);

	/**
	 * 用户注册
	 * @author louxiaojian
	 * @date： 日期：2015-8-15 时间：下午4:51:53
	 * @param newUser
	 */
	public void register(User newUser, Captcha captch);

	/**
	 * 忘记密码，重置密码
	 * @author louxiaojian
	 * @date： 日期：2015-8-15 时间：下午5:07:38
	 * @param user
	 * @param captcha
	 */
	public void resetPassword(User user, Captcha captcha);

	/**
	 * 修改object对象
	 * @author louxiaojian
	 * @date： 日期：2015-8-15 时间：下午5:13:44
	 * @param object
	 */
	public void updateObject(Object object);

	/**
	 * 验证第三方用户信息是否存在
	 * @author louxiaojian
	 * @date： 日期：2015-8-29 时间：下午4:24:50
	 * @param userId
	 * @param thirdParty
	 * @return
	 */
	public User validateThirdPartyUser(String userId, String thirdParty);

	/**
	 * 验证用户昵称是否被占用
	 * @author louxiaojian
	 * @date： 日期：2015-9-11 时间：上午11:08:36
	 * @param username
	 * @return
	 */
	public int nickNameUsed(String username,int id);
}
