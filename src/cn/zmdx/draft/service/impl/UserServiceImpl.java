package cn.zmdx.draft.service.impl;

import java.util.List;

import cn.zmdx.draft.dao.UserDao;
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
}
