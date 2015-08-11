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
				sql.append(" and userid = :userid");
			}
			if ("0".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
					sql.append(" and id > :lastid");
				}
			} else if ("1".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
					sql.append(" and id < :lastid");
				}
			}
			if(!filterMap.get("currentUserId").equals(filterMap.get("userid"))){
				sql.append(" and status =:status ");//非本人只能看审核通过的
			}
			sql.append(" order by uploadDate desc limit :limit");
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(PictureSet.class));
		if (!"".equals(filterMap.get("userid"))
				&& filterMap.get("userid") != null
				&& !"''".equals(filterMap.get("userid"))
				&& !"null".equals(filterMap.get("userid"))) {
			query.setInteger("userid", Integer.parseInt(filterMap.get("userid")));
		}
		if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
			query.setInteger("lastid", Integer.parseInt(filterMap.get("lastid")));
		}
		if(!filterMap.get("currentUserId").equals(filterMap.get("userid"))){
			query.setString("status", "1");
		}
		query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
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
					sql.append(" and id > :lastid");
				}
			} else if ("1".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
					sql.append(" and id < :lastid");
				}
			}
			sql.append(" order by auditingDate desc limit :limit ");
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(PictureSet.class));
		if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
			query.setInteger("lastid", Integer.parseInt(filterMap.get("lastid")));
		}
		if(!"0".equals(filterMap.get("limit"))&&!"".equals(filterMap.get("limit"))&&filterMap.get("limit")!=null) {
			query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
		}
		return query.list();
	}

	@Override
	public List queryCycleRanking(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id,picture_set_id as pictureSetId,uploadDate,descs,type,status,praise,tread,auditingDate,userid,report,view,votes,theme_cycle_id as themeCycleId from rank_picture_set where status =1 and type=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if ("0".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
					sql.append(" and id > :lastid");
				}
			} else if ("1".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
					sql.append(" and id < :lastid");
				}
			}
			if(!"".equals(filterMap.get("themeCycleId"))&&filterMap.get("themeCycleId")!=null){
				sql.append(" and theme_cycle_id=:themeCycleId");
			}
			sql.append(" order by praise desc ");
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(RankPictureSet.class));
		if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
			query.setInteger("lastid", Integer.parseInt(filterMap.get("lastid")));
		}
		if(!"".equals(filterMap.get("themeCycleId"))&&filterMap.get("themeCycleId")!=null){
			query.setInteger("themeCycleId", Integer.parseInt(filterMap.get("themeCycleId")));
		}
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
		sql.append("select p.id from photo p left join cycle_photo_set cp on p.id=cp.photo_set_id  where cp.theme_cycle_id=? and p.userid=? ");

		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Photo.class));
		query.setInteger(0, Integer.parseInt(filterMap.get("themeCycleId").toString()));
		query.setInteger(1, Integer.parseInt(filterMap.get("userId").toString()));
		return query.list();
	}

	@Override
	public List queryComment(Map<String, Object> filterMap) {
		StringBuffer sql = new StringBuffer();
//		sql.append("select id,content,datetime,parent_user_id as parentUserId ,picture_set_id as pictureSetId,user_id as userId,username,parentusername from (SELECT c.id,content,datetime,parent_user_id,picture_set_id,user_id,u.loginname as username,uu.loginname as parentusername FROM comment c left join users u on c.user_id=u.id left join users uu on uu.id =c.parent_user_id where 1=1 ");
		sql.append("SELECT c.id as id,content,datetime,parent_user_id as parentUserId,picture_set_id as pictureSetId,user_id as userId,u.loginname as username,uu.loginname as parentusername FROM comment c left join users u on c.user_id=u.id left join users uu on uu.id =c.parent_user_id where 1=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("pictureSetId"))
					&& filterMap.get("pictureSetId") != null
					&& !"''".equals(filterMap.get("pictureSetId"))
					&& !"null".equals(filterMap.get("pictureSetId"))) {
				sql.append(" and picture_set_id = :pictureSetId");
			}
			if("0".equals(filterMap.get("flag"))){
				if(!"".equals(filterMap.get("lastId"))&&filterMap.get("lastId")!=null){
					sql.append(" and c.id > :lastid");
				}
			}else if("1".equals(filterMap.get("flag"))){
				if(!"".equals(filterMap.get("lastId"))&&filterMap.get("lastId")!=null){
					sql.append(" and c.id < :lastid");
				}
			}
		}
		sql.append(" order by datetime desc limit :limit");
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Comment.class));
		if (!"".equals(filterMap.get("pictureSetId"))
				&& filterMap.get("pictureSetId") != null
				&& !"''".equals(filterMap.get("pictureSetId"))
				&& !"null".equals(filterMap.get("pictureSetId"))) {
			query.setInteger("pictureSetId", Integer.parseInt(filterMap.get("pictureSetId").toString()));
		}
		if(!"".equals(filterMap.get("lastId"))&&filterMap.get("lastId")!=null){
			query.setInteger("lastid", Integer.parseInt(filterMap.get("lastId").toString()));
		}
		query.setInteger("limit", Integer.parseInt(filterMap.get("limit").toString()));
		return query.list();
	}

	@Override
	public List<Photo> queryDraftPhotosWall(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id,uploadDate,descs,type,status,praise,tread,auditingDate,userid,report,view,votes,theme_cycle_id as themeCycleId from picture_set where status =1 and type=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if ("0".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
					sql.append(" and id > :lastid");
				}
			} else if ("1".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
					sql.append(" and id < :lastid");
				}
			}
			if(!"".equals(filterMap.get("themeCycleId"))&&filterMap.get("themeCycleId")!=null){
				sql.append(" and theme_cycle_id=:themeCycleId");
			}
			sql.append(" order by auditingDate desc limit :limit");
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(PictureSet.class));
		if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
			query.setInteger("lastid", Integer.parseInt(filterMap.get("lastid")));
		}
		if(!"".equals(filterMap.get("themeCycleId"))&&filterMap.get("themeCycleId")!=null){
			query.setInteger("themeCycleId", Integer.parseInt(filterMap.get("themeCycleId")));
		}
		if(!"".equals(filterMap.get("limit"))&&filterMap.get("limit")!=null){
			query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
		}
		return query.list();
	}

	@Override
	public List<Photo> queryHotPhotosWall(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id,picture_set_id as pictureSetId,uploadDate,descs,type,status,praise,tread,auditingDate,userid,report,view,votes from rank_picture_set where status =1 and type=0 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if ("0".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
					sql.append(" and id > :lastid");
				}
			} else if ("1".equals(filterMap.get("flag"))) {
				if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
					sql.append(" and id < :lastid");
				}
			}
			sql.append(" order by praise desc limit :limit");
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(RankPictureSet.class));
		if (!"0".equals(filterMap.get("lastid"))&&!"".equals(filterMap.get("lastid"))&&filterMap.get("lastid")!=null) {
			query.setInteger("lastid", Integer.parseInt(filterMap.get("lastid")));
		}
		query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
		return query.list();
	}

	@Override
	public List queryPhotoByPictureSetId(String userid, String pictureSetId,
			int operationType) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id from operation_records where informer_id =? and picture_set_id=? and operation_type=?");
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString());
		query.setInteger(0, Integer.parseInt(userid));
		query.setInteger(1, Integer.parseInt(pictureSetId));
		query.setInteger(2, operationType);
		return query.list();
	}

	@Override
	public List queryReviewRecords(Map<String, String> filterMap) {
//		StringBuffer sql=new StringBuffer("select id,status,photo_set_id as photoSetId,descs,datetime,operator_id as operatorId,operator_name as operatorName,user_id as userId,type from (SELECT rr.id,rr.status,photo_set_id,rr.descs,rr.datetime,rr.operator_id,u.loginname as operator_name,rr.user_id,rr.type FROM review_records rr left join users u on u.id=rr.operator_id where 1=1 ");
		StringBuffer sql=new StringBuffer("SELECT rr.id,rr.status,photo_set_id as photoSetId,rr.descs,rr.datetime,rr.operator_id as operatorId,u.loginname as operatorName,rr.user_id as userId,rr.type FROM review_records rr left join users u on u.id=rr.operator_id where 1=1 ");
		if(filterMap!=null&&!filterMap.isEmpty()){
			if("0".equals(filterMap.get("type"))){
				if(!"".equals(filterMap.get("pictureSetId"))&&filterMap.get("pictureSetId")!=null){
					sql.append(" and photo_set_id =?");
				}
			}else if("1".equals(filterMap.get("type"))){
				if(!"".equals(filterMap.get("userId"))&&filterMap.get("userId")!=null){
					sql.append(" and user_id =?");
				}
			}
		}
		sql.append(" order by datetime desc");
		Query query = getSession().createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(ReviewRecords.class));
		if("0".equals(filterMap.get("type"))){
			query.setInteger(0, Integer.parseInt(filterMap.get("pictureSetId")));
		}else if("1".equals(filterMap.get("type"))){
			query.setInteger(0, Integer.parseInt(filterMap.get("userId")));
		}
		return query.list();
	}

}
