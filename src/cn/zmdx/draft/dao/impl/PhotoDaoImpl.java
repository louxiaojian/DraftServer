package cn.zmdx.draft.dao.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import cn.zmdx.draft.dao.PhotoDao;
import cn.zmdx.draft.entity.*;

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
		sql.append("select id,uploadDate,descs,type,status,praise,tread,auditingDate,userid,report,view from picture_set where 1=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("userid"))
					&& filterMap.get("userid") != null
					&& !"''".equals(filterMap.get("userid"))
					&& !"null".equals(filterMap.get("userid"))) {
				sql.append(" and userid = " + filterMap.get("userid") + " ");
			}
			if ("0".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
					sql.append(" and id > " + filterMap.get("lastid"));
				}
			} else if ("1".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
					sql.append(" and id < " + filterMap.get("lastid"));
				}
			}
			if(!filterMap.get("currentUserId").equals(filterMap.get("userid"))){
				sql.append(" and status ='1' ");//非本人只能看审核通过的
			}
			sql.append(" order by uploadDate desc limit "
					+ Integer.parseInt(filterMap.get("limit")) );
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(PictureSet.class));
		return query.list();
	}

	@Override
	public List queryPhotoByPictureSetId(int id) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id,photoUrl,uploadDate,userid from photo where picture_set_id=? ");
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Photo.class));
		query.setInteger(0, id);
		return query.list();
	}

	@Override
	public List queryPhotosWall(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id,uploadDate,descs,type,status,praise,tread,auditingDate,userid,report,view from picture_set where status =1 and type=0 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if ("0".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
					sql.append(" and id > " +filterMap.get("lastid"));
				}
			} else if ("1".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
					sql.append(" and id < " +filterMap.get("lastid"));
				}
			}
			sql.append(" order by auditingDate desc limit "
					+ Integer.parseInt(filterMap.get("limit")));
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(PictureSet.class));
		return query.list();
	}

	@Override
	public List queryCycleRanking(String cycleId) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id,uploadDate,descs,type,status,praise,tread,auditingDate,userid,report,view from (" +
				"select p.id,p.uploadDate,p.descs,p.type,p.status,p.praise,p.tread,p.auditingDate,p.userid,p.report,p.view from picture_set p " +
				"left join cycle_photo_set cps on p.id=cps.photo_set_id  where cps.cycle_id=? and p.type=1 and p.status=1 order by praise desc ) t");

		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(PictureSet.class));
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
		sql.append("select id,content,datetime,parent_user_id as parentUserId ,picture_set_id as pictureSetId,user_id as userId,username,parentusername from (SELECT c.id,content,datetime,parent_user_id,picture_set_id,user_id,u.loginname as username,uu.loginname as parentusername FROM comment c left join users u on c.user_id=u.id left join users uu on uu.id =c.parent_user_id where 1=1 ");
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

	@Override
	public List<Photo> queryDraftPhotosWall(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id,uploadDate,descs,type,status,praise,tread,auditingDate,userid,report,view,votes from rank_picture_set where status =1 and type=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if ("0".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
					sql.append(" and id > " +filterMap.get("lastid"));
				}
			} else if ("1".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
					sql.append(" and id < "+filterMap.get("lastid"));
				}
			}
			sql.append(" order by votes desc limit "
					+ Integer.parseInt(filterMap.get("limit")) );
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(PictureSet.class));
		return query.list();
	}

	@Override
	public List<Photo> queryHotPhotosWall(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id,uploadDate,descs,type,status,praise,tread,auditingDate,userid,report,view,votes from rank_picture_set where status =1 and type=0 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if ("0".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
					sql.append(" and id > " +filterMap.get("lastid"));
				}
			} else if ("1".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastModified"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
					sql.append(" and id < "+filterMap.get("lastid"));
				}
			}
			sql.append(" order by praise desc limit "
					+ Integer.parseInt(filterMap.get("limit")) );
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(PictureSet.class));
		return query.list();
	}

	@Override
	public List queryPhotoByPictureSetId(String userid, String pictureSetId,
			int operationType) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id from operation_records where userid =? and picture_set_id=? and operation_type=?");
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString());
		query.setInteger(0, Integer.parseInt(userid));
		query.setInteger(1, Integer.parseInt(pictureSetId));
		query.setInteger(2, operationType);
		return query.list();
	}

}
