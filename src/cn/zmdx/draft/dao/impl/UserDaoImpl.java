package cn.zmdx.draft.dao.impl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import cn.zmdx.draft.dao.UserDao;
import cn.zmdx.draft.entity.Captcha;
import cn.zmdx.draft.entity.User;

public class UserDaoImpl extends HibernateDaoSupport implements UserDao {
	HibernateTemplate template;
	public UserDaoImpl(HibernateTemplate template) {
		this.template = template;
	}

	@Override
	public User findByName(String loginname) {
		List<?> list=this.template.find("from User where loginname=?",loginname);
		if(list.size()>0){
			return (User)list.get(0);
		}else{
			return null;
		}
	}

	@Override
	public void deleteEntity(Object obj) {
		this.template.delete(obj);
	}

	@Override
	public Object getEntity(Class entityClass, Serializable id) {
		return this.template.get(entityClass, id);
	}

	@Override
	public void saveEntity(Object obj) {
		this.template.save(obj);
	}

	@Override
	public void updateEntity(Object obj) {
		this.getHibernateTemplate().update(obj);
	}

	@Override
	public void updateCaptchaByLoginname(String loginname) {
		Query query=getSession().createSQLQuery("update captcha set status=1 where telephone=? and status=0");
		query.setString(0, loginname);
		query.executeUpdate();
	}

	@Override
	public int qualificationByTelephone(String telephone) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Query query=getSession().createSQLQuery("select id from captcha where telephone=? and createtime like '%"+sdf.format(new Date())+"%'");
		query.setString(0, telephone);
		return query.list().size();
	}

	@Override
	public Captcha queryUsableCaptcha(String loginname) {
		List list=this.template.find("from Captcha where telephone=? and status=0",loginname);
		if(list.size()>0){
			Captcha captcha=(Captcha) list.get(0);
			return captcha;
		}else{
			return null;
		}
	}

}
