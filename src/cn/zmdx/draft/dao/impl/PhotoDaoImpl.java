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

import cn.zmdx.draft.dao.PhotoDao;
import cn.zmdx.draft.entity.Comment;
import cn.zmdx.draft.entity.Cycle;
import cn.zmdx.draft.entity.Photo;
import cn.zmdx.draft.entity.Themes;

public class PhotoDaoImpl extends HibernateDaoSupport implements PhotoDao {
	HibernateTemplate template;

	public PhotoDaoImpl(HibernateTemplate template) {
		this.template = template;
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
	public List queryPersonalPhotos(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		Date date = new Date();// 取时间
		Date lastModified = new Date(Long.parseLong(filterMap
				.get("lastModified")));// 时间戳转换为时间
		SimpleDateFormat dfl = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sql.append("select id,photoUrl,uploadDate,descs,type,status,praise,tread,auditingDate,userid,report,view from (select id,photoUrl,uploadDate,descs,type,status,praise,tread,auditingDate,userid,report,view from photo where 1=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("userid"))
					&& filterMap.get("userid") != null
					&& !"''".equals(filterMap.get("userid"))
					&& !"null".equals(filterMap.get("userid"))) {
				sql.append(" and userid = " + filterMap.get("userid") + " ");
			}
			if ("0".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastModified"))) {
					sql.append(" and uploadDate > '" + dfl.format(lastModified)
							+ "'  ");
					sql.append(" and uploadDate < '" + dfl.format(date) + "'  ");
				}
			} else if ("1".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastModified"))) {
					sql.append(" and uploadDate < '" + dfl.format(lastModified)
							+ "'  ");
				}
			}
			if ("0".equals(filterMap.get("lastModified"))) {
				sql.append(" and uploadDate < '" + dfl.format(date) + "' ");
			}
			sql.append(" order by uploadDate desc limit "
					+ Integer.parseInt(filterMap.get("limit")) + " ) t  ");
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Photo.class));
		return query.list();
	}

	@Override
	public List queryPhotosWall(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		Date date = new Date();// 取时间
		Date lastModified = new Date(Long.parseLong(filterMap
				.get("lastModified")));// 时间戳转换为时间
		SimpleDateFormat dfl = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sql.append("select id,photoUrl,uploadDate,descs,type,status,praise,tread,auditingDate,userid,report,view from (select id,photoUrl,uploadDate,descs,type,status,praise,tread,auditingDate,userid,report,view from photo where status =1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if ("0".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastModified"))) {
					sql.append(" and uploadDate > '" + dfl.format(lastModified)
							+ "'  ");
					sql.append(" and uploadDate < '" + dfl.format(date) + "'  ");
				}
			} else if ("1".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastModified"))) {
					sql.append(" and uploadDate < '" + dfl.format(lastModified)
							+ "'  ");
				}
			}
			if ("0".equals(filterMap.get("lastModified"))) {
				sql.append(" and uploadDate < '" + dfl.format(date) + "' ");
			}
			if ("0".equals(filterMap.get("category"))) {// 新
				sql.append(" order by auditingDate desc limit "
						+ Integer.parseInt(filterMap.get("limit")) + " ) t  ");
			} else if ("1".equals(filterMap.get("category"))) {// 热门
				sql.append(" order by praise desc limit "
						+ Integer.parseInt(filterMap.get("limit")) + " ) t  ");
			}
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Photo.class));
		return query.list();
	}

	@Override
	public List queryCycleRanking(String cycleId) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id,photoUrl,uploadDate,descs,type,status,praise,tread,auditingDate,userid,report,p.view from (" +
				"select p.id,p.photoUrl,p.uploadDate,p.descs,p.type,p.status,p.praise,p.tread,p.auditingDate,p.userid,p.report,p.view from photo p " +
				"left join cycle_photo cp on p.id=cp.photo_id  where cp.cycle_id=? and p.type=1 and p.status=1 order by praise desc ) t");

		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Photo.class));
		query.setInteger(0, Integer.parseInt(cycleId));
		return query.list();
	}

	@Override
	public List queryThemes(Map<String, Object> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id,name,descs from (select id,name,descs from themes order by id desc ) t");

		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Themes.class));
		return query.list();
	}

	@Override
	public List queryCycleByThemesId(Map<String, Object> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id,cycle_no as cycleNo,starttime,signup_endtime as signupEndtime,endtime,status,theme_id as themeId from (select id,cycle_no,starttime,signup_endtime,endtime,status,theme_id from cycle where 1=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("themeId"))
					&& filterMap.get("themeId") != null
					&& !"''".equals(filterMap.get("themeId"))
					&& !"null".equals(filterMap.get("themeId"))) {
				sql.append(" and theme_id = " + filterMap.get("themeId") + " ");
			}
			if("0".equals(filterMap.get("flag"))){//未开始
				sql.append(" and status = 0");
			}else if("1".equals(filterMap.get("flag"))){//进行中
				sql.append(" and status = 1");
			}else if("2".equals(filterMap.get("flag"))){//已结束
				sql.append(" and status = 2");
			}
		}
		sql.append(" order by starttime desc limit 10 ) t  ");
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Cycle.class));
		return query.list();
	}

	@Override
	public List validateIsAttend(Map<String, Object> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select p.id from photo p left join cycle_photo cp on p.id=cp.photo_id  where cp.cycle_id=? and p.userid=? order by praise desc");

		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Photo.class));
		query.setInteger(0, Integer.parseInt(filterMap.get("cycleId").toString()));
		query.setInteger(1, Integer.parseInt(filterMap.get("userId").toString()));
		return query.list();
	}

	@Override
	public List queryComment(Map<String, Object> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id,content,datetime,parent_id as parentId ,photo_id as photoId,user_id as userId,loginname as username from (SELECT c.id,content,datetime,parent_id,photo_id,user_id,u.loginname FROM comment c left join users u on c.user_id=u.id where 1=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("photoId"))
					&& filterMap.get("photoId") != null
					&& !"''".equals(filterMap.get("photoId"))
					&& !"null".equals(filterMap.get("photoId"))) {
				sql.append(" and photo_id = " + filterMap.get("photoId") + " ");
			}
			if("0".equals(filterMap.get("flag"))){
				if(!"".equals(filterMap.get("lastId"))&&filterMap.get("lastId")!=null){
					sql.append(" and c.id >"+filterMap.get("lastId"));
				}
			}else if("1".equals(filterMap.get("flag"))){
				if(!"".equals(filterMap.get("lastId"))&&filterMap.get("lastId")!=null){
					sql.append(" and c.id <"+filterMap.get("lastId"));
				}
			}
		}
		sql.append(" order by datetime desc ) t  ");
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Comment.class));
		return query.list();
	}

}
