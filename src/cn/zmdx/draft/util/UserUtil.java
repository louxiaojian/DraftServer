package cn.zmdx.draft.util;

import cn.zmdx.draft.entity.User;

public class UserUtil {
	/**
	 * 转换用户信息
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-8-8 时间：下午4:24:26
	 * @param user
	 * @return
	 */
	public static User getUser(User user) {
		User newUser = new User();
		newUser.setId(user.getId());
		newUser.setAge(user.getAge());
		newUser.setAddress(user.getAddress());
		newUser.setIntroduction(user.getIntroduction());
		newUser.setHeadPortrait(user.getHeadPortrait());
		newUser.setUsername(user.getUsername());
		newUser.setTelephone(user.getTelephone());
		newUser.setLoginname(user.getLoginname());
		newUser.setGender(user.getGender());
		newUser.setIsvalidate(user.getIsvalidate());
		newUser.setArea(user.getArea());
		newUser.setName(user.getName());
		newUser.setRongCloudToken(user.getRongCloudToken());
		return newUser;
	}
	public static User getUser2(User user) {
		User newUser = new User();
		newUser.setId(user.getId());
		newUser.setAge(user.getAge());
		newUser.setAddress(user.getAddress());
		newUser.setIntroduction(user.getIntroduction());
		newUser.setHeadPortrait(user.getHeadPortrait());
		newUser.setUsername(user.getUsername());
		newUser.setTelephone(user.getTelephone());
		newUser.setLoginname(user.getLoginname());
		newUser.setGender(user.getGender());
		newUser.setIsvalidate(user.getIsvalidate());
		newUser.setArea(user.getArea());
		newUser.setName(user.getName());
		newUser.setRongCloudToken(user.getRongCloudToken());
		newUser.setIsAttention(user.getIsAttention());
		return newUser;
	}
}
