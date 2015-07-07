package cn.zmdx.draft.dao.impl;

import java.io.Serializable;
import java.util.List;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import cn.zmdx.draft.dao.UserDao;

public class UserDaoImpl extends HibernateDaoSupport implements UserDao {
	HibernateTemplate template;
	public UserDaoImpl(HibernateTemplate template) {
		this.template = template;
	}

	@Override
	public List<?> login(String loginname) {
		List<?> list=this.template.find("from User where loginname=?",loginname);
		return list;
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

}
