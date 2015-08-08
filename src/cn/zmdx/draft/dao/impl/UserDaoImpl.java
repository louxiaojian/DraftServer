package cn.zmdx.draft.dao.impl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import cn.zmdx.draft.dao.UserDao;
import cn.zmdx.draft.entity.Captcha;
import cn.zmdx.draft.entity.ReviewRecords;
import cn.zmdx.draft.entity.User;
import cn.zmdx.draft.entity.UserAttentionFans;

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

	@Override
	public UserAttentionFans isAttention(String fansUserId,
			String attentionUserId) {
		List list=this.template.find("from UserAttentionFans where attentionUserId=? and fansUserId=?",new Integer []{Integer.parseInt(attentionUserId),Integer.parseInt(fansUserId)});
		if(list.size()>0){
			return (UserAttentionFans)list.get(0);
		}else{
			return null;
		}
	}

	@Override
	public void cancelAttention(String fansUserId, String attentionUserId) {
		Query query=getSession().createQuery("delete UserAttentionFans where attentionUserId=? and fansUserId=?");
		query.setInteger(0, Integer.parseInt(attentionUserId));
		query.setInteger(1, Integer.parseInt(fansUserId));
		query.executeUpdate();
	}

	@Override
	public List queryAttentions(Map<String, String> filterMap) {
		StringBuffer sql=new StringBuffer("select u.id,u.loginname,u.age,u.gender,u.username,u.headPortrait,u.introduction from users u left join user_attention_fans uaf on uaf.attention_user_id=u.id where 1=1 ");
		if(filterMap!=null&&!filterMap.isEmpty()){
			if(!"".equals(filterMap.get("fansUserId"))&&filterMap.get("fansUserId")!=null){
				sql.append(" and fans_user_id =?");
			}
		}
		sql.append(" order by uaf.attention_time desc");
//		sql.append(") t");
		Query query = getSession().createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(User.class));
		if(!"".equals(filterMap.get("fansUserId"))&&filterMap.get("fansUserId")!=null){
			query.setInteger(0, Integer.parseInt(filterMap.get("fansUserId")));
		}
		return query.list();
	}

	@Override
	public List queryFans(Map<String, String> filterMap) {
		StringBuffer sql=new StringBuffer("select  u.id,u.loginname,u.age,u.gender,u.username,u.headPortrait,u.introduction from users u left join user_attention_fans uaf on uaf.fans_user_id=u.id where 1=1 ");
		if(filterMap!=null&&!filterMap.isEmpty()){
			if(!"".equals(filterMap.get("attentionUserId"))&&filterMap.get("attentionUserId")!=null){
				sql.append(" and attention_user_id =?");
			}
		}
		sql.append(" order by uaf.attention_time desc");
//		sql.append(") t");
		Query query = getSession().createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(User.class));
		if(!"".equals(filterMap.get("attentionUserId"))&&filterMap.get("attentionUserId")!=null){
			query.setInteger(0, Integer.parseInt(filterMap.get("attentionUserId")));
		}
		return query.list();
	}

}
