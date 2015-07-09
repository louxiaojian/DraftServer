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
import cn.zmdx.draft.entity.Photo;

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
		sql.append("select id,photoUrl,uploadDate,descs,type,flag,praise,tread,auditingDate,userid from (select id,photoUrl,uploadDate,descs,type,flag,praise,tread,auditingDate,userid from photo where 1=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("userid"))
					&& filterMap.get("userid") != null
					&& !"''".equals(filterMap.get("userid"))
					&& !"null".equals(filterMap.get("userid"))) {
				sql.append(" and userid = " + filterMap.get("userid") + " ");
			}
			if ("0".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastModified"))) {
					sql.append(" and uploadDate > '"
							+ dfl.format(lastModified) + "'  ");
					sql.append(" and uploadDate < '" + dfl.format(date)
							+ "'  ");
				}
			} else if ("1".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastModified"))) {
					sql.append(" and uploadDate < '"
							+ dfl.format(lastModified) + "'  ");
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
		sql.append("select id,photoUrl,uploadDate,descs,type,flag,praise,tread,auditingDate,userid from (select id,photoUrl,uploadDate,descs,type,flag,praise,tread,auditingDate,userid from photo where flag =1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if ("0".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastModified"))) {
					sql.append(" and uploadDate > '"
							+ dfl.format(lastModified) + "'  ");
					sql.append(" and uploadDate < '" + dfl.format(date)
							+ "'  ");
				}
			} else if ("1".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastModified"))) {
					sql.append(" and uploadDate < '"
							+ dfl.format(lastModified) + "'  ");
				}
			}
			if ("0".equals(filterMap.get("lastModified"))) {
				sql.append(" and uploadDate < '" + dfl.format(date) + "' ");
			}
			if("0".equals(filterMap.get("category"))){//新
				sql.append(" order by auditingDate desc limit "
						+ Integer.parseInt(filterMap.get("limit")) + " ) t  ");
			}else if("1".equals(filterMap.get("category"))){//热门
				sql.append(" order by praise desc limit "
						+ Integer.parseInt(filterMap.get("limit")) + " ) t  ");
			}
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Photo.class));
		return query.list();
	}

}
