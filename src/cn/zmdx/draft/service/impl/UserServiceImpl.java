package cn.zmdx.draft.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.zmdx.draft.dao.UserDao;
import cn.zmdx.draft.entity.Captcha;
import cn.zmdx.draft.entity.OperationRecords;
import cn.zmdx.draft.entity.User;
import cn.zmdx.draft.entity.UserAttentionFans;
import cn.zmdx.draft.service.UserService;

public class UserServiceImpl implements UserService {
	private UserDao userDao;

	public UserServiceImpl(UserDao userDao){
		this.userDao=userDao; 
	}

	@Override
	public User findByName(String loginname) {
		return this.userDao.findByName(loginname);
	}

	@Override
	public void saveUser(User user) {
		this.userDao.saveEntity(user);
	}
	@Override
	public void saveObject(Object obj) {
		this.userDao.saveEntity(obj);
	}
	@Override
	public void updateUser(User user) {
		this.userDao.updateEntity(user);
	}

	@Override
	public User getById(int id) {
		return (User)this.userDao.getEntity(User.class, id);
	}
	
	@Override
	public Captcha createCaptcha(String loginname, String code) {
		//将loginname之前的验证码设置为已用
		this.userDao.updateCaptchaByLoginname(loginname);
		Captcha captcha=new Captcha();
		captcha.setCode(code);
		Date createtime =new Date();
		captcha.setCreatetime(createtime);
		createtime.getTime();
		captcha.setDeadline(new Date(createtime.getTime()+2*60*60*1000));
		captcha.setStatus("0");
		captcha.setTelephone(loginname);
		this.userDao.saveEntity(captcha);
		return captcha;
	}

	@Override
	public int qualificationByTelephone(String telephone) {
		return this.userDao.qualificationByTelephone(telephone);
	}

	@Override
	public Captcha queryUsableCaptcha(String loginname) {
		return this.userDao.queryUsableCaptcha(loginname);
	}

	@Override
	public UserAttentionFans isAttention(String fansUserId,
			String attentionUserId) {
		return this.userDao.isAttention(fansUserId,attentionUserId);
	}
	
	@Override
	public void cancelAttention(String fansUserId, String attentionUserId) {
		this.userDao.cancelAttention(fansUserId,attentionUserId);
	}

	@Override
	public List queryAttentions(Map<String, String> filterMap) {
		return this.userDao.queryAttentions(filterMap);
	}

	@Override
	public List queryFans(Map<String, String> filterMap) {
		return this.userDao.queryFans(filterMap);
	}

	@Override
	public void register(User newUser, Captcha captcha) {
		this.userDao.saveEntity(newUser);
		newUser.setUid(String.valueOf(newUser.getId()));
		this.userDao.updateEntity(newUser);
		captcha.setStatus("2");//验证码已用
		this.userDao.updateEntity(captcha);
	}
	
	@Override
	public void resetPassword(User user, Captcha captcha) {
		this.userDao.updateEntity(user);
		captcha.setStatus("2");//验证码已用
		this.userDao.updateEntity(captcha);
	}

	@Override
	public void updateObject(Object object) {
		this.userDao.updateEntity(object);
	}

	@Override
	public User validateThirdPartyUser(String userId, String thirdParty) {
		return this.userDao.validateThirdPartyUser(userId,thirdParty);
	}

	@Override
	public int nickNameUsed(String username,int id) {
		return this.userDao.nickNameUsed(username,id);
	}

	@Override
	public List automaticPrompt(Map<String, String> filterMap) {
		return this.userDao.automaticPrompt(filterMap);
	}

	@Override
	public void attentionUser(UserAttentionFans uaf) {
		OperationRecords or =new OperationRecords();
		or.setDatetime(new Date());
		or.setOperationType(8);
		or.setInformerId(uaf.getFansUserId());
		or.setBeingInformerId(uaf.getAttentionUserId());
		or.setIsRead("0");//未读
		this.userDao.saveEntity(or);
		this.userDao.saveEntity(uaf);
	}
	
	@Override
	public List loadUsers(Map<String, String> filterMap) {
		return this.userDao.loadUsers(filterMap);
	}
}
