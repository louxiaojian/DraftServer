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
		List<?> list = this.template.find("from User where loginname=?",
				loginname);
		if (list.size() > 0) {
			return (User) list.get(0);
		} else {
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
		this.getSession().createSQLQuery("set NAMES utf8mb4").executeUpdate();
		this.template.save(obj);
	}

	@Override
	public void updateEntity(Object obj) {
		this.getSession().createSQLQuery("set NAMES utf8mb4").executeUpdate();
		this.getHibernateTemplate().update(obj);
	}

	@Override
	public void updateCaptchaByLoginname(String loginname) {
		Query query = getSession().createSQLQuery(
				"update captcha set status=2 where telephone=? and status=0");
		query.setString(0, loginname);
		query.executeUpdate();
	}

	@Override
	public int qualificationByTelephone(String telephone) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Query query = getSession().createSQLQuery(
				"select id from captcha where telephone=? and createtime like '%"
						+ sdf.format(new Date()) + "%'");
		query.setString(0, telephone);
		return query.list().size();
	}

	@Override
	public Captcha queryUsableCaptcha(String loginname) {
		List list = this.template.find(
				"from Captcha where telephone=? and status=0", loginname);
		if (list.size() > 0) {
			Captcha captcha = (Captcha) list.get(0);
			return captcha;
		} else {
			return null;
		}
	}

	@Override
	public UserAttentionFans isAttention(String fansUserId,
			String attentionUserId) {
		List list = this.template
				.find("from UserAttentionFans where attentionUserId=? and fansUserId=?",
						new Integer[]{Integer.parseInt(attentionUserId),
								Integer.parseInt(fansUserId)});
		if (list.size() > 0) {
			return (UserAttentionFans) list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void cancelAttention(String fansUserId, String attentionUserId) {
		Query query = getSession()
				.createSQLQuery(
						"delete from user_attention_fans where attention_user_id=? and fans_user_id=?");
		Query query1 = getSession()
				.createSQLQuery(
						"delete from operation_records where informer_id=? and being_informer_id=? and operation_type=8");
		query.setInteger(0, Integer.parseInt(attentionUserId));
		query.setInteger(1, Integer.parseInt(fansUserId));
		query1.setInteger(0, Integer.parseInt(fansUserId));
		query1.setInteger(1, Integer.parseInt(attentionUserId));
		query.executeUpdate();
		query1.executeUpdate();
	}

	@Override
	public List queryAttentions(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer(
				"select u.id,u.loginname,u.age,u.gender,u.username,u.headPortrait,u.introduction,u.area,uaf.id as orderId from users u left join user_attention_fans uaf on uaf.attention_user_id=u.id where 1=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("fansUserId"))
					&& filterMap.get("fansUserId") != null) {
				sql.append(" and fans_user_id =?");
			}
			if (!"0".equals(filterMap.get("lastid"))
					&& !"".equals(filterMap.get("lastid"))
					&& filterMap.get("lastid") != null) {
				sql.append(" and uaf.id < :lastid");
			}
		}
		sql.append(" group by u.id order by uaf.id desc ");
		if (!"0".equals(filterMap.get("limit"))
				&& !"".equals(filterMap.get("limit"))
				&& filterMap.get("limit") != null) {
			sql.append(" limit :limit");
		}
		// sql.append(") t");
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(User.class));
		if (!"".equals(filterMap.get("fansUserId"))
				&& filterMap.get("fansUserId") != null) {
			query.setInteger(0, Integer.parseInt(filterMap.get("fansUserId")));
		}
		if (!"0".equals(filterMap.get("lastid"))
				&& !"".equals(filterMap.get("lastid"))
				&& filterMap.get("lastid") != null) {
			query.setInteger("lastid",
					Integer.parseInt(filterMap.get("lastid")));
		}
		if (!"0".equals(filterMap.get("limit"))
				&& !"".equals(filterMap.get("limit"))
				&& filterMap.get("limit") != null) {
			query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
		}
		return query.list();
	}

	@Override
	public List queryFans(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer(
				"select  u.id,u.loginname,u.age,u.gender,u.username,u.headPortrait,u.introduction,u.area,uaf.id as orderId from users u left join user_attention_fans uaf on uaf.fans_user_id=u.id where 1=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("attentionUserId"))
					&& filterMap.get("attentionUserId") != null) {
				sql.append(" and attention_user_id =?");
			}
			if (!"0".equals(filterMap.get("lastid"))
					&& !"".equals(filterMap.get("lastid"))
					&& filterMap.get("lastid") != null) {
				sql.append(" and uaf.id < :lastid");
			}
		}
		sql.append(" group by u.id order by uaf.id desc");
		if (!"0".equals(filterMap.get("limit"))
				&& !"".equals(filterMap.get("limit"))
				&& filterMap.get("limit") != null) {
			sql.append(" limit :limit");
		}
		// sql.append(") t");
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(User.class));
		if (!"".equals(filterMap.get("attentionUserId"))
				&& filterMap.get("attentionUserId") != null) {
			query.setInteger(0,
					Integer.parseInt(filterMap.get("attentionUserId")));
		}
		if (!"0".equals(filterMap.get("lastid"))
				&& !"".equals(filterMap.get("lastid"))
				&& filterMap.get("lastid") != null) {
			query.setInteger("lastid",
					Integer.parseInt(filterMap.get("lastid")));
		}
		if (!"0".equals(filterMap.get("limit"))
				&& !"".equals(filterMap.get("limit"))
				&& filterMap.get("limit") != null) {
			query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
		}
		return query.list();
	}

	@Override
	public User validateThirdPartyUser(String userId, String thirdParty) {
		List list = this.template.find(
				"from User where uid=? and third_party=?", new String[]{userId,
						thirdParty});
		if (list.size() > 0) {
			return (User) list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public int nickNameUsed(String username, int id) {
		List list = this.template.find("from User where username=? and id!=?",
				new Object[]{username, id});
		return list.size();
	}

	@Override
	public List automaticPrompt(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer(
				"select id,username from users where 1=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("nickName"))
					&& filterMap.get("nickName") != null) {
				sql.append(" and username like ?");
			}
		}
		sql.append(" limit 5");
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(User.class));
		if (!"".equals(filterMap.get("nickName"))
				&& filterMap.get("nickName") != null) {
			query.setString(0, filterMap.get("nickName") + "%");
		}
		return query.list();
	}

	@Override
	public List loadUsers(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer(
				"select id,username,headPortrait from users where 1=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("ids"))
					&& filterMap.get("ids") != null) {
				sql.append(" and id in (:ids)");
			}
		}
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(User.class));
		if (!"".equals(filterMap.get("ids")) && filterMap.get("ids") != null) {
			query.setParameterList("ids", filterMap.get("ids").split(","));
		}
		return query.list();
	}

	@Override
	public List searchUser(Map<String, String> filterMap) {
		if (!"".equals(filterMap.get("userName"))
				&& filterMap.get("userName") != null) {
			StringBuffer sql = new StringBuffer(
					"select u.id,u.age,u.gender,u.username,u.headPortrait,u.introduction,u.area from users u where 1=1 ");
			sql.append(" and username like :userName limit 20");
			Query query = getSession().createSQLQuery(sql.toString())
					.setResultTransformer(
							Transformers.aliasToBean(User.class));
			if (!"".equals(filterMap.get("userName"))
					&& filterMap.get("userName") != null) {
				query.setString("userName", "%" + filterMap.get("userName")
						+ "%");
			}
			return query.list();
		}else{
			return null;
		}
	}

}
