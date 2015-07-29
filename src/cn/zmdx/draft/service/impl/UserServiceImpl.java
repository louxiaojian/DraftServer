package cn.zmdx.draft.service.impl;

import java.util.Date;

import cn.zmdx.draft.dao.UserDao;
import cn.zmdx.draft.entity.Captcha;
import cn.zmdx.draft.entity.User;
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
	public void updateUser(User user) {
		this.userDao.updateEntity(user);
	}

	@Override
	public User getById(int id) {
		return (User)this.userDao.getEntity(User.class, id);
	}
	
	@Override
	public Captcha createCaptcha(String loginname, String code) {
		//将loginname之前的验证码失效
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
}
