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
		sql.append("select id as orderId,id,uploadDate,descs,type,status,praise,userid,CONCAT(coverUrl,'?imageView2/0/w/"+filterMap.get("width")+"/h/"+filterMap.get("width")+"') as coverUrl from picture_set where 1=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("userid"))
					&& filterMap.get("userid") != null
					&& !"''".equals(filterMap.get("userid"))
					&& !"null".equals(filterMap.get("userid"))) {
				sql.append(" and userid = :userid");
			}
			if (!"0".equals(filterMap.get("lastid"))
					&& !"".equals(filterMap.get("lastid"))
					&& filterMap.get("lastid") != null) {
				sql.append(" and id < :lastid");
			}
			if (!filterMap.get("currentUserId").equals(filterMap.get("userid"))) {
				sql.append(" and status =1 ");// 非本人只能看审核通过并且举报未限制的 and report<50
			}
			sql.append(" order by uploadDate desc limit :limit");
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(
						Transformers.aliasToBean(PictureSet.class));
		if (!"".equals(filterMap.get("userid"))
				&& filterMap.get("userid") != null
				&& !"''".equals(filterMap.get("userid"))
				&& !"null".equals(filterMap.get("userid"))) {
			query.setInteger("userid",
					Integer.parseInt(filterMap.get("userid")));
		}
		if (!"0".equals(filterMap.get("lastid"))
				&& !"".equals(filterMap.get("lastid"))
				&& filterMap.get("lastid") != null) {
			query.setInteger("lastid",
					Integer.parseInt(filterMap.get("lastid")));
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
		sql.append("select id as orderId,id,uploadDate,descs,type,praise,userid,CONCAT(coverUrl,'?imageView2/0/w/"+filterMap.get("width")+"/h/"+filterMap.get("width")+"') as coverUrl from picture_set where status =1 and type=0 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"0".equals(filterMap.get("lastid"))
					&& !"".equals(filterMap.get("lastid"))
					&& filterMap.get("lastid") != null) {
				sql.append(" and id < :lastid");
			}
			sql.append(" order by uploadDate desc,id desc limit :limit ");
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(
						Transformers.aliasToBean(PictureSet.class));
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
	public List queryCycleRanking(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id as orderId,picture_set_id as id,uploadDate,descs,type,praise,userid,theme_cycle_id as themeCycleId,CONCAT(coverUrl,'?imageView2/0/w/"+filterMap.get("width")+"/h/"+filterMap.get("width")+"') as coverUrl,rank from rank_picture_set where status =1 and type=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"0".equals(filterMap.get("lastid"))
					&& !"".equals(filterMap.get("lastid"))
					&& filterMap.get("lastid") != null) {
				sql.append(" and id < :lastid");
			}
			if (!"".equals(filterMap.get("themeCycleId"))
					&& filterMap.get("themeCycleId") != null) {
				sql.append(" and theme_cycle_id=:themeCycleId");
			}
			sql.append(" order by praise desc,id desc ");
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(
						Transformers.aliasToBean(RankPictureSet.class));
		if (!"0".equals(filterMap.get("lastid"))
				&& !"".equals(filterMap.get("lastid"))
				&& filterMap.get("lastid") != null) {
			query.setInteger("lastid",
					Integer.parseInt(filterMap.get("lastid")));
		}
		if (!"".equals(filterMap.get("themeCycleId"))
				&& filterMap.get("themeCycleId") != null) {
			query.setInteger("themeCycleId",
					Integer.parseInt(filterMap.get("themeCycleId")));
		}
		return query.list();
	}

	@Override
	public List queryThemes(Map<String, Object> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id,theme_title as themeTitle,tag_url as tag,starttime,endtime,status,bg_url as bgUrl,descs,detail_image_url as detailImageUrl from theme_cycle order by status,starttime asc");

		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Cycle.class));
		return query.list();
	}

	@Override
	public List queryCycleByThemesId(Map<String, Object> filterMap) {
		/**
		 * StringBuffer sql = new StringBuffer(); sql.append(
		 * "select id,cycle_no as cycleNo,starttime,signup_endtime as signupEndtime,endtime,status,theme_id as themeId from (select id,cycle_no,starttime,signup_endtime,endtime,status,theme_id from cycle where 1=1 "
		 * ); if (filterMap != null && !filterMap.isEmpty()) { if
		 * (!"".equals(filterMap.get("themeId")) && filterMap.get("themeId") !=
		 * null && !"''".equals(filterMap.get("themeId")) &&
		 * !"null".equals(filterMap.get("themeId"))) {
		 * sql.append(" and theme_id = " + filterMap.get("themeId") + " "); }
		 * if("0".equals(filterMap.get("flag"))){//未开始
		 * sql.append(" and status = 0"); }else
		 * if("1".equals(filterMap.get("flag"))){//进行中
		 * sql.append(" and status = 1"); }else
		 * if("2".equals(filterMap.get("flag"))){//已结束
		 * sql.append(" and status = 2"); } }
		 * sql.append(" order by starttime desc limit 10 ) t  "); //
		 * 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类 Query query =
		 * getSession().createSQLQuery(sql.toString())
		 * .setResultTransformer(Transformers.aliasToBean(Cycle.class)); return
		 * query.list();
		 */
		return null;
	}

	@Override
	public List validateIsAttend(Map<String, Object> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select p.id from photo p left join cycle_photo_set cp on p.id=cp.photo_set_id  where cp.theme_cycle_id=? and p.userid=? ");

		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Photo.class));
		query.setInteger(0,
				Integer.parseInt(filterMap.get("themeCycleId").toString()));
		if(filterMap.get("userId")!=null&&!"".equals(filterMap.get("userId"))){
			query.setInteger(1,
					Integer.parseInt(filterMap.get("userId").toString()));
		}else{
			query.setInteger(1,0);
		}
		return query.list();
	}

	@Override
	public List queryComment(Map<String, Object> filterMap) {
		StringBuffer sql = new StringBuffer();
		// sql.append("select id,content,datetime,parent_user_id as parentUserId ,picture_set_id as pictureSetId,user_id as userId,username,parentusername from (SELECT c.id,content,datetime,parent_user_id,picture_set_id,user_id,u.loginname as username,uu.loginname as parentusername FROM comment c left join users u on c.user_id=u.id left join users uu on uu.id =c.parent_user_id where 1=1 ");
		sql.append("SELECT c.id as id,content,datetime,parent_user_id as parentUserId,picture_set_id as pictureSetId,user_id as userId,u.loginname as username,uu.loginname as parentusername FROM comment c left join users u on c.user_id=u.id left join users uu on uu.id =c.parent_user_id where 1=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("pictureSetId"))
					&& filterMap.get("pictureSetId") != null
					&& !"''".equals(filterMap.get("pictureSetId"))
					&& !"null".equals(filterMap.get("pictureSetId"))) {
				sql.append(" and picture_set_id = :pictureSetId");
			}
			if (!"".equals(filterMap.get("lastId"))
					&& filterMap.get("lastId") != null) {
				sql.append(" and c.id < :lastid");
			}
		}
		sql.append(" order by datetime desc,id desc limit :limit");
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Comment.class));
		if (!"".equals(filterMap.get("pictureSetId"))
				&& filterMap.get("pictureSetId") != null
				&& !"''".equals(filterMap.get("pictureSetId"))
				&& !"null".equals(filterMap.get("pictureSetId"))) {
			query.setInteger("pictureSetId",
					Integer.parseInt(filterMap.get("pictureSetId").toString()));
		}
		if (!"".equals(filterMap.get("lastId"))
				&& filterMap.get("lastId") != null) {
			query.setInteger("lastid",
					Integer.parseInt(filterMap.get("lastId").toString()));
		}
		query.setInteger("limit",
				Integer.parseInt(filterMap.get("limit").toString()));
		return query.list();
	}

	@Override
	public List<Photo> queryDraftPhotosWall(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id as orderId,id,uploadDate,descs,type,praise,userid,theme_cycle_id as themeCycleId,CONCAT(coverUrl,'?imageView2/0/w/"+filterMap.get("width")+"/h/"+filterMap.get("width")+"') as coverUrl from picture_set where status =1 and type=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"0".equals(filterMap.get("lastid"))
					&& !"".equals(filterMap.get("lastid"))
					&& filterMap.get("lastid") != null) {
				sql.append(" and id < :lastid");
			}
			if (!"".equals(filterMap.get("themeCycleId"))
					&& filterMap.get("themeCycleId") != null) {
				sql.append(" and theme_cycle_id=:themeCycleId");
			}
			sql.append(" order by uploadDate desc,id desc limit :limit");
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(
						Transformers.aliasToBean(PictureSet.class));
		if (!"0".equals(filterMap.get("lastid"))
				&& !"".equals(filterMap.get("lastid"))
				&& filterMap.get("lastid") != null) {
			query.setInteger("lastid",
					Integer.parseInt(filterMap.get("lastid")));
		}
		if (!"".equals(filterMap.get("themeCycleId"))
				&& filterMap.get("themeCycleId") != null) {
			query.setInteger("themeCycleId",
					Integer.parseInt(filterMap.get("themeCycleId")));
		}
		if (!"".equals(filterMap.get("limit"))
				&& filterMap.get("limit") != null) {
			query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
		}
		return query.list();
	}

	@Override
	public List<Photo> queryHotPhotosWall(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id as orderId,picture_set_id as id,uploadDate,descs,type,praise,userid,CONCAT(coverUrl,'?imageView2/0/w/"+filterMap.get("width")+"/h/"+filterMap.get("width")+"') as coverUrl,rank from rank_picture_set where status =1 and type=0 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"0".equals(filterMap.get("lastid"))
					&& !"".equals(filterMap.get("lastid"))
					&& filterMap.get("lastid") != null) {
				sql.append(" and id < :lastid");
			}
			sql.append(" order by rank desc,id desc limit :limit");
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(
						Transformers.aliasToBean(RankPictureSet.class));
		if (!"0".equals(filterMap.get("lastid"))
				&& !"".equals(filterMap.get("lastid"))
				&& filterMap.get("lastid") != null) {
			query.setInteger("lastid",
					Integer.parseInt(filterMap.get("lastid")));
		}
		query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
		return query.list();
	}

	@Override
	public List queryPhotoByPictureSetId(String userid, String pictureSetId,
			int operationType) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id from operation_records where operation_type=:operationType");
//		if (userid != null && !"".equals(userid)) {
			sql.append(" and informer_id =:userid");
//		}
		if (pictureSetId != null && !"".equals(pictureSetId)) {
			sql.append(" and picture_set_id=:pictureSetId");
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString());
		if (userid != null && !"".equals(userid)) {
			query.setInteger("userid", Integer.parseInt(userid));
		}else{
			query.setInteger("userid", 0);
		}
		if (pictureSetId != null && !"".equals(pictureSetId)) {
			query.setInteger("pictureSetId", Integer.parseInt(pictureSetId));
		}
		query.setInteger("operationType", operationType);
		return query.list();
	}

	@Override
	public List queryReviewRecords(Map<String, String> filterMap) {
		// StringBuffer sql=new
		// StringBuffer("select id,status,photo_set_id as photoSetId,descs,datetime,operator_id as operatorId,operator_name as operatorName,user_id as userId,type from (SELECT rr.id,rr.status,photo_set_id,rr.descs,rr.datetime,rr.operator_id,u.loginname as operator_name,rr.user_id,rr.type FROM review_records rr left join users u on u.id=rr.operator_id where 1=1 ");
		StringBuffer sql = new StringBuffer(
				"SELECT rr.id,rr.status,photo_set_id as photoSetId,rr.descs,rr.datetime,rr.operator_id as operatorId,u.loginname as operatorName,rr.user_id as userId,rr.type FROM review_records rr left join users u on u.id=rr.operator_id where 1=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if ("0".equals(filterMap.get("type"))) {
				if (!"".equals(filterMap.get("pictureSetId"))
						&& filterMap.get("pictureSetId") != null) {
					sql.append(" and photo_set_id =?");
				}
			} else if ("1".equals(filterMap.get("type"))) {
				if (!"".equals(filterMap.get("userId"))
						&& filterMap.get("userId") != null) {
					sql.append(" and user_id =?");
				}
			}
		}
		sql.append(" order by datetime desc");
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(
						Transformers.aliasToBean(ReviewRecords.class));
		if ("0".equals(filterMap.get("type"))) {
			query.setInteger(0, Integer.parseInt(filterMap.get("pictureSetId")));
		} else if ("1".equals(filterMap.get("type"))) {
			query.setInteger(0, Integer.parseInt(filterMap.get("userId")));
		}
		return query.list();
	}

	@Override
	public List queryUserCycleRanking(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select u.id,u.loginname,u.age,u.gender,u.username,u.headPortrait,u.introduction,rps.praise from users u left join rank_picture_set rps on rps.userid=u.id where rps.status =1 and rps.type=1 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"0".equals(filterMap.get("lastid"))
					&& !"".equals(filterMap.get("lastid"))
					&& filterMap.get("lastid") != null) {
				sql.append(" and rps.id < :lastid");
			}
			if (!"".equals(filterMap.get("themeCycleId"))
					&& filterMap.get("themeCycleId") != null) {
				sql.append(" and rps.theme_cycle_id=:themeCycleId");
			}
			sql.append(" order by rps.praise desc,rps.id desc ");
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(User.class));
		if (!"0".equals(filterMap.get("lastid"))
				&& !"".equals(filterMap.get("lastid"))
				&& filterMap.get("lastid") != null) {
			query.setInteger("lastid",
					Integer.parseInt(filterMap.get("lastid")));
		}
		if (!"".equals(filterMap.get("themeCycleId"))
				&& filterMap.get("themeCycleId") != null) {
			query.setInteger("themeCycleId",
					Integer.parseInt(filterMap.get("themeCycleId")));
		}
		return query.list();
	}

	@Override
	public int queryCommentByPictureSetId(int id) {
		List list = this.template.find("from Comment where pictureSetId=?", id);
		return list.size();
	}

	@Override
	public List discoverPictureSet(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id,uploadDate,descs,type,praise,userid,theme_cycle_id as themeCycleId,coverUrl from picture_set where status =1 and type=0 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			sql.append(" order by rand() limit :limit");
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(
						Transformers.aliasToBean(PictureSet.class));
		if (!"".equals(filterMap.get("limit"))
				&& filterMap.get("limit") != null) {
			query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
		}
		return query.list();
	}

	@Override
	public List queryPraiseUsers(Map<String, String> praiseFilterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select u.id,u.loginname,u.age,u.gender,u.username,u.headPortrait,u.introduction from users u left join operation_records o on informer_id=u.id where operation_type=0 ");
		if (praiseFilterMap != null && !praiseFilterMap.isEmpty()) {
			if (!"".equals(praiseFilterMap.get("pictureSetId"))
					&& praiseFilterMap.get("pictureSetId") != null) {
				sql.append(" and picture_set_id=:pictureSetId ");
			}
			if (!"".equals(praiseFilterMap.get("lastId"))
					&& praiseFilterMap.get("lastId") != null) {
				sql.append(" and u.id<:lastId ");
			}
			sql.append(" order by o.datetime desc ");
			if (!"".equals(praiseFilterMap.get("limit"))
					&& praiseFilterMap.get("limit") != null) {
				sql.append(" limit :limit");
			}
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(User.class));
		if (!"".equals(praiseFilterMap.get("pictureSetId"))
				&& praiseFilterMap.get("pictureSetId") != null) {
			query.setInteger("pictureSetId",
					Integer.parseInt(praiseFilterMap.get("pictureSetId")));
		}
		if (!"".equals(praiseFilterMap.get("lastId"))
				&& praiseFilterMap.get("lastId") != null) {
			query.setInteger("lastId",
					Integer.parseInt(praiseFilterMap.get("lastId")));
		}
		if (!"".equals(praiseFilterMap.get("limit"))
				&& praiseFilterMap.get("limit") != null) {
			query.setInteger("limit",
					Integer.parseInt(praiseFilterMap.get("limit")));
		}
		return query.list();
	}

	@Override
	public void executeSql(String sql) {
		Query query=getSession().createSQLQuery(sql);
		query.executeUpdate();
	}

	@Override
	public int deleteOperationRecords(int userId, int pictureSetId) {
		Query query=getSession().createQuery("delete OperationRecords where pictureSetId=? and informerId=? and operationType=0");
		query.setInteger(0, pictureSetId);
		query.setInteger(1, userId);
		return query.executeUpdate();
	}

}
