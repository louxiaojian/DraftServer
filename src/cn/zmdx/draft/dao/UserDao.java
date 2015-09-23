package cn.zmdx.draft.dao;

import java.util.List;
import java.util.Map;

import cn.zmdx.draft.entity.Captcha;
import cn.zmdx.draft.entity.User;
import cn.zmdx.draft.entity.UserAttentionFans;

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
	/**
	 * 验证是否已关注
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午2:00:11
	 * @param fansUserId
	 * @param attentionUserId
	 * @return
	 */
	public UserAttentionFans isAttention(String fansUserId,
			String attentionUserId);
	/**
	 * 取消关注
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午2:26:51
	 * @param fansUserId
	 * @param attentionUserId
	 */
	public void cancelAttention(String fansUserId, String attentionUserId);
	/**
	 * 查看我关注的人
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午2:38:55
	 * @param filterMap
	 * @return
	 */
	public List queryAttentions(Map<String, String> filterMap);
	/**
	 * 查看我的粉丝
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午2:38:55
	 * @param filterMap
	 * @return
	 */
	public List queryFans(Map<String, String> filterMap);
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
	/**
	 * @ 自动提示加载人员
	 * @author louxiaojian
	 * @date： 日期：2015-9-22 时间：下午5:02:10
	 * @param filterMap
	 * @return
	 */
	public List automaticPrompt(Map<String, String> filterMap);
}
