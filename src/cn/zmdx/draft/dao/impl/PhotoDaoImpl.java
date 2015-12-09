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
		this.getSession().createSQLQuery("set NAMES utf8mb4").executeUpdate();
		this.template.save(obj);
	}

	@Override
	public void updateEntity(Object obj) {
		this.getSession().createSQLQuery("set NAMES utf8mb4").executeUpdate();
		this.getHibernateTemplate().update(obj);
	}

	@Override
	public List queryPersonalPhotos(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id as orderId,id,uploadDate,descs,type,status,praise,userid,CONCAT(coverUrl,'?imageView2/0/w/"
				+ filterMap.get("width")
				+ "/h/"
				+ filterMap.get("width")
				+ "') as coverUrl,photoCount from picture_set where 1=1 and display=0 ");
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
				sql.append(" and status =1 ");// 非本人只能看审核通过并且举报未限制的 and
												// report<50
			}
			sql.append(" order by uploadDate desc ");
			if (!"0".equals(filterMap.get("limit"))
					&& !"".equals(filterMap.get("limit"))
					&& filterMap.get("limit") != null) {
				sql.append("  limit :limit");
			}
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
		if (!"0".equals(filterMap.get("limit"))
				&& !"".equals(filterMap.get("limit"))
				&& filterMap.get("limit") != null) {
			query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
		}
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
		sql.append("select id as orderId,id,uploadDate,descs,type,praise,userid,CONCAT(coverUrl,'?imageView2/0/w/"
				+ filterMap.get("width")
				+ "/h/"
				+ filterMap.get("width")
				+ "') as coverUrl,photoCount from picture_set where status =1 and userid>0 and report<10 and display=0 ");// 个人、选秀图片全部显示在照片墙
		// and
		// type=0
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"0".equals(filterMap.get("lastid"))
					&& !"".equals(filterMap.get("lastid"))
					&& filterMap.get("lastid") != null) {
				sql.append(" and id < :lastid");
			}
			sql.append(" order by uploadDate desc,id desc ");

			if (!"0".equals(filterMap.get("limit"))
					&& !"".equals(filterMap.get("limit"))
					&& filterMap.get("limit") != null) {
				sql.append(" limit :limit");
			}
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
		sql.append("select id as orderId,picture_set_id as id,uploadDate,descs,type,praise,userid,theme_cycle_id as themeCycleId,CONCAT(coverUrl,'?imageView2/0/w/"
				+ filterMap.get("width")
				+ "/h/"
				+ filterMap.get("width")
				+ "') as coverUrl,rank,photoCount,votes from draft_rank_picture_set where status =1 and type=1 and userid>0  and display=0 ");
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
			sql.append(" order by votes desc,orderId desc ");
			if (!"".equals(filterMap.get("limit"))
					&& filterMap.get("limit") != null
					&& !"0".equals(filterMap.get("limit"))) {
				sql.append(" limit :limit");
			}
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
		if (!"".equals(filterMap.get("limit"))
				&& filterMap.get("limit") != null
				&& !"0".equals(filterMap.get("limit"))) {
			query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
		}
		return query.list();
	}

	@Override
	public List queryThemes(Map<String, Object> filterMap) {
		StringBuffer sql = new StringBuffer();
		if (Integer.parseInt(String.valueOf(filterMap.get("middleNum"))) >= 1
				|| "Android".equals(filterMap.get("pf"))) {// 1.1.0···9
															// 版本或者Android版本
			sql.append("select id,theme_title as themeTitle,tag_url as tag,starttime,endtime,status,new_bg_url as bgUrl,descs,detail_image_url as detailImageUrl,isNeedValidate,inside_detail_image_url as insideDetailImageUrl,descs,role,award_setting as awardSetting,notice,inside_bg_url as insideBgUrl from theme_cycle order by status asc,starttime asc");
		} else {
			sql.append("select id,theme_title as themeTitle,tag_url as tag,starttime,endtime,status,bg_url as bgUrl,descs,detail_image_url as detailImageUrl,isNeedValidate,inside_detail_image_url as insideDetailImageUrl,descs,role,award_setting as awardSetting,inside_bg_url as insideBgUrl from theme_cycle order by status asc,starttime asc");
		}
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
		sql.append("SELECT ps.id from cycle_photo_set cp LEFT join picture_set ps on cp.photo_set_id =ps.id where cp.theme_cycle_id=? and ps.userid=? and ps.display=0");

		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Photo.class));
		query.setInteger(0,
				Integer.parseInt(filterMap.get("themeCycleId").toString()));
		if (filterMap.get("userId") != null
				&& !"".equals(filterMap.get("userId"))) {
			query.setInteger(1,
					Integer.parseInt(filterMap.get("userId").toString()));
		} else {
			query.setInteger(1, 0);
		}
		return query.list();
	}

	@Override
	public List queryComment(Map<String, Object> filterMap) {
		StringBuffer sql = new StringBuffer();
		// sql.append("select id,content,datetime,parent_user_id as parentUserId ,picture_set_id as pictureSetId,user_id as userId,username,parentusername from (SELECT c.id,content,datetime,parent_user_id,picture_set_id,user_id,u.loginname as username,uu.loginname as parentusername FROM comment c left join users u on c.user_id=u.id left join users uu on uu.id =c.parent_user_id where 1=1 ");
		sql.append("SELECT c.id as id,c.id as orderId,c.content,c.datetime,c.parent_user_id as parentUserId,c.picture_set_id as pictureSetId,c.user_id as userId,u.loginname as username,uu.loginname as parentusername FROM comment c left join users u on c.user_id=u.id left join users uu on uu.id =c.parent_user_id where 1=1 and c.user_id>0 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("pictureSetId"))
					&& filterMap.get("pictureSetId") != null
					&& !"''".equals(filterMap.get("pictureSetId"))
					&& !"null".equals(filterMap.get("pictureSetId"))) {
				sql.append(" and picture_set_id = :pictureSetId");
			}
			if (!"".equals(filterMap.get("lastId"))
					&& filterMap.get("lastId") != null
					&& !"0".equals(filterMap.get("lastId"))) {
				sql.append(" and c.id < :lastid");
			}
		}
		sql.append(" order by datetime desc,id desc ");
		if (!"0".equals(filterMap.get("limit"))
				&& !"".equals(filterMap.get("limit"))
				&& filterMap.get("limit") != null) {
			sql.append(" limit :limit");
		}
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
				&& filterMap.get("lastId") != null
				&& !"0".equals(filterMap.get("lastId"))) {
			query.setInteger("lastid",
					Integer.parseInt(filterMap.get("lastId").toString()));
		}
		if (!"0".equals(filterMap.get("limit"))
				&& !"".equals(filterMap.get("limit"))
				&& filterMap.get("limit") != null) {
			query.setInteger("limit",
					Integer.parseInt(filterMap.get("limit").toString()));
		}
		return query.list();
	}

	@Override
	public List<Photo> queryDraftPhotosWall(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id as orderId,id,uploadDate,descs,type,praise,userid,theme_cycle_id as themeCycleId,CONCAT(coverUrl,'?imageView2/0/w/"
				+ filterMap.get("width")
				+ "/h/"
				+ filterMap.get("width")
				+ "') as coverUrl,photoCount,votes from picture_set where status =1 and type=1 and userid>0  and display=0 ");
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
			sql.append(" order by uploadDate desc,id desc");// limit :limit
			if (!"".equals(filterMap.get("limit"))
					&& filterMap.get("limit") != null
					&& !"0".equals(filterMap.get("limit"))) {
				sql.append(" limit :limit");
			}
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
				&& filterMap.get("limit") != null
				&& !"0".equals(filterMap.get("limit"))) {
			query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
		}
		return query.list();
	}

	@Override
	public List<Photo> queryHotPhotosWall(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id as orderId,picture_set_id as id,uploadDate,descs,type,praise,userid,CONCAT(coverUrl,'?imageView2/0/w/"
				+ filterMap.get("width")
				+ "/h/"
				+ filterMap.get("width")
				+ "') as coverUrl,rank,photoCount from rank_picture_set where status =1 and userid>0 and report<10  and display=0 ");// 个人、选秀图片全部显示在照片墙
		// and
		// type=0
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"0".equals(filterMap.get("lastid"))
					&& !"".equals(filterMap.get("lastid"))
					&& filterMap.get("lastid") != null) {
				sql.append(" and id < :lastid");
			}
			sql.append(" order by rank desc,orderId desc ");
			if (!"0".equals(filterMap.get("limit"))
					&& !"".equals(filterMap.get("limit"))
					&& filterMap.get("limit") != null) {
				sql.append(" limit :limit");
			}
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
		if (!"0".equals(filterMap.get("limit"))
				&& !"".equals(filterMap.get("limit"))
				&& filterMap.get("limit") != null) {
			query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
		}
		return query.list();
	}

	@Override
	public List queryPhotoByPictureSetId(String userid, String pictureSetId,
			int operationType) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id from operation_records where operation_type=:operationType");
		if (pictureSetId != null && !"".equals(pictureSetId)) {
			sql.append(" and picture_set_id=:pictureSetId");
		}
		// if (userid != null && !"".equals(userid)) {
		sql.append(" and informer_id =:userid");
		// }
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString());
		if (userid != null && !"".equals(userid)) {
			query.setInteger("userid", Integer.parseInt(userid));
		} else {
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
		sql.append("select rps.id as orderId,u.id,u.loginname,u.age,u.gender,u.username,u.headPortrait,u.introduction,u.area,rps.praise,rps.votes from users u left join draft_rank_picture_set rps on rps.userid=u.id where rps.status =1 and rps.userid>0 and rps.type=1 and rps.display=0 ");
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
			sql.append(" order by rps.votes desc,rps.id desc ");
			if (!"".equals(filterMap.get("limit"))
					&& filterMap.get("limit") != null
					&& !"0".equals(filterMap.get("limit"))) {
				sql.append(" limit :limit");
			}
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
		if (!"".equals(filterMap.get("limit"))
				&& filterMap.get("limit") != null
				&& !"0".equals(filterMap.get("limit"))) {
			query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
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
		sql.append("select id,uploadDate,descs,type,praise,userid,theme_cycle_id as themeCycleId,CONCAT(coverUrl,'?imageView2/0/w/"
				+ filterMap.get("width")
				+ "/h/"
				+ filterMap.get("width")
				+ "') as coverUrl,photoCount from picture_set where status =1 and userid>0 and report<10 and type=0  and display=0 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			sql.append(" order by rand() ");
			if (!"0".equals(filterMap.get("limit"))
					&& !"".equals(filterMap.get("limit"))
					&& filterMap.get("limit") != null) {
				sql.append(" limit :limit");
			}
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
		sql.append("select o.id as orderId,u.id,u.loginname,u.age,u.gender,u.username,u.headPortrait,u.introduction,u.area from users u left join operation_records o on informer_id=u.id where operation_type= 7");
		if (praiseFilterMap != null && !praiseFilterMap.isEmpty()) {
			if (!"".equals(praiseFilterMap.get("pictureSetId"))
					&& praiseFilterMap.get("pictureSetId") != null) {
				sql.append(" and picture_set_id=:pictureSetId ");
			}
			if (!"".equals(praiseFilterMap.get("lastId"))
					&& praiseFilterMap.get("lastId") != null
					&& !"0".equals(praiseFilterMap.get("lastId"))) {
				sql.append(" and o.id<:lastId ");
			}
			sql.append(" order by o.id desc ");
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
				&& praiseFilterMap.get("lastId") != null
				&& !"0".equals(praiseFilterMap.get("lastId"))) {
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
		Query query = getSession().createSQLQuery(sql);
		query.executeUpdate();
	}

	@Override
	public int deleteOperationRecords(int userId, int pictureSetId) {
		Query query = getSession()
				.createQuery(
						"delete OperationRecords where pictureSetId=? and informerId=? and operationType=7");
		query.setInteger(0, pictureSetId);
		query.setInteger(1, userId);
		return query.executeUpdate();
	}

	@Override
	public List queryPhotoByPictureSetIds(String pictureSetIds) {
		StringBuffer sql = new StringBuffer();
		sql.append("select fileid from photo where picture_set_id in (?) ");
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Photo.class));
		query.setString(0, pictureSetIds);
		return query.list();
	}

	@Override
	public List queryNotify(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select o.id as id,o.id as orderId,u.id as userId,u.username as userName,u.gender as gender,u.headPortrait as headPortrait,u.introduction as introduction,u.area as area,o.operation_type as type,o.datetime as dateTime,ps.coverUrl as coverUrl,ps.id as pictureSetId,o.isRead,com.content as content from picture_set ps  left join operation_records o on ps.id=o.picture_set_id  left join  users u  on informer_id=u.id left join comment com on com.id=o.comment_id where operation_type>3  and u.id>0 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("currentUserId"))
					&& filterMap.get("currentUserId") != null) {
				sql.append(" and (ps.userid=:psUserId or o.being_informer_id=:currentUserId)");
			}
			if (!"".equals(filterMap.get("lastId"))
					&& filterMap.get("lastId") != null
					&& !"0".equals(filterMap.get("lastId"))) {
				sql.append(" and o.id<:lastId ");
			}
			if (!"".equals(filterMap.get("status"))
					&& filterMap.get("status") != null) {
				sql.append(" and o.isRead=:status ");
			}
			sql.append(" order by o.id desc ");
			if (!"".equals(filterMap.get("limit"))
					&& filterMap.get("limit") != null) {
				sql.append(" limit :limit");
			}
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Notify.class));
		if (!"".equals(filterMap.get("currentUserId"))
				&& filterMap.get("currentUserId") != null) {
			query.setInteger("psUserId",
					Integer.parseInt(filterMap.get("currentUserId")));
			query.setInteger("currentUserId",
					Integer.parseInt(filterMap.get("currentUserId")));
		}
		if (!"".equals(filterMap.get("lastId"))
				&& filterMap.get("lastId") != null
				&& !"0".equals(filterMap.get("lastId"))) {
			query.setInteger("lastId",
					Integer.parseInt(filterMap.get("lastId")));
		}
		if (!"".equals(filterMap.get("status"))
				&& filterMap.get("status") != null) {
			query.setInteger("status",
					Integer.parseInt(filterMap.get("status")));
		}
		if (!"".equals(filterMap.get("limit"))
				&& filterMap.get("limit") != null) {
			query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
		}
		return query.list();
	}

	@Override
	public int readNotify(Map<String, String> filterMap) {
		StringBuffer updateSql = new StringBuffer(
				"update operation_records o left join picture_set ps on ps.id=o.picture_set_id set isRead=1 where isRead=0 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("currentUserId"))
					&& filterMap.get("currentUserId") != null) {
				updateSql
						.append(" and (ps.userid=:psUserId or o.being_informer_id=:currentUserId)");
			}
		}
		Query query = getSession().createSQLQuery(updateSql.toString());
		if (!"".equals(filterMap.get("currentUserId"))
				&& filterMap.get("currentUserId") != null) {
			query.setInteger("psUserId",
					Integer.parseInt(filterMap.get("currentUserId")));
			query.setInteger("currentUserId",
					Integer.parseInt(filterMap.get("currentUserId")));
		}
		return query.executeUpdate();
	}

	@Override
	public int deleteComment(Map<String, String> filterMap) {
		String deleteSqlString = "delete from comment where id=:id and user_id=:userId";
		Query query = getSession().createSQLQuery(deleteSqlString);
		query.setInteger("id", Integer.parseInt(filterMap.get("id")));
		query.setInteger("userId",
				Integer.parseInt(filterMap.get("currentUserId")));
		return query.executeUpdate();
	}

	@Override
	public int queryUserSurplusVote(Map<String, String> surplusVotesFilterMap) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		StringBuffer sql = new StringBuffer(
				"select ors.id,ors.informer_id from operation_records ors left join picture_set ps on ps.id=ors.picture_set_id where ors.operation_type=3 ");
		sql.append("and ors.datetime like '%" + sdf.format(date) + "%'");
		if (surplusVotesFilterMap != null && !surplusVotesFilterMap.isEmpty()) {
			if (!"".equals(surplusVotesFilterMap.get("userId"))
					&& surplusVotesFilterMap.get("userId") != null) {
				sql.append("  and ors.informer_id=:userId");
			}
			if (!"".equals(surplusVotesFilterMap.get("themeId"))
					&& surplusVotesFilterMap.get("themeId") != null) {
				sql.append(" and ps.theme_cycle_id=:themeId");
			}
		}
		Query query = getSession().createSQLQuery(sql.toString());
		if (surplusVotesFilterMap != null && !surplusVotesFilterMap.isEmpty()) {
			if (!"".equals(surplusVotesFilterMap.get("userId"))
					&& surplusVotesFilterMap.get("userId") != null) {
				query.setInteger("userId",
						Integer.parseInt(surplusVotesFilterMap.get("userId")));
			}
			if (!"".equals(surplusVotesFilterMap.get("themeId"))
					&& surplusVotesFilterMap.get("themeId") != null) {
				query.setInteger("themeId",
						Integer.parseInt(surplusVotesFilterMap.get("themeId")));
			}
		}
		return query.list().size();
	}

	@Override
	public List queryOperations(Map<String, String> filterMap) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		StringBuffer sql = new StringBuffer(
				"select ors.id,ors.informer_id from operation_records ors left join picture_set ps on ps.id=ors.picture_set_id where ors.operation_type=3 ");
		sql.append("and ors.datetime like '%" + sdf.format(date) + "%'");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("operate_type"))
					&& filterMap.get("operate_type") != null) {
				sql.append("  and ors.operation_type=:operate_type");
			}
			if (!"".equals(filterMap.get("ip")) && filterMap.get("ip") != null) {
				sql.append(" and ors.ip=:ip");
			}
		}
		Query query = getSession().createSQLQuery(sql.toString());
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("operate_type"))
					&& filterMap.get("operate_type") != null) {
				query.setInteger("operate_type",
						Integer.parseInt(filterMap.get("operate_type")));
			}
			if (!"".equals(filterMap.get("ip")) && filterMap.get("ip") != null) {
				query.setString("ip", filterMap.get("ip"));
			}
		}
		return query.list();
	}

	@Override
	public List queryPhotoSetByAttentedUser(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id as orderId,id,uploadDate,descs,type,praise,userid,CONCAT(coverUrl,'?imageView2/0/w/"
				+ filterMap.get("width")
				+ "/h/"
				+ filterMap.get("width")
				+ "') as coverUrl,rank,photoCount from picture_set where status =1 and userid>0 and report<10  and display=0 ");// 个人、选秀图片全部显示在照片墙
		// and
		// type=0
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"0".equals(filterMap.get("lastid"))
					&& !"".equals(filterMap.get("lastid"))
					&& filterMap.get("lastid") != null) {
				sql.append(" and id < :lastid ");
			}
			if (!"0".equals(filterMap.get("currentUserId"))
					&& !"".equals(filterMap.get("currentUserId"))
					&& filterMap.get("currentUserId") != null) {
				sql.append(" and userid in (select attention_user_id from user_attention_fans uaf where uaf.fans_user_id=:userId )");
			}
			sql.append(" order by uploadDate desc,orderId desc ");
			if (!"0".equals(filterMap.get("limit"))
					&& !"".equals(filterMap.get("limit"))
					&& filterMap.get("limit") != null) {
				sql.append(" limit :limit");
			}
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
		if (!"0".equals(filterMap.get("currentUserId"))
				&& !"".equals(filterMap.get("currentUserId"))
				&& filterMap.get("currentUserId") != null) {
			query.setInteger("userId",
					Integer.parseInt(filterMap.get("currentUserId")));
		}
		if (!"0".equals(filterMap.get("limit"))
				&& !"".equals(filterMap.get("limit"))
				&& filterMap.get("limit") != null) {
			query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
		}
		return query.list();
	}

	@Override
	public List queryBulletinBoard(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id,image_url as imageUrl,url,display from bulletin_board where display='0' order by id desc");
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(
						Transformers.aliasToBean(BulletinBoard.class));
		return query.list();
	}

	@Override
	public List loadPraiseNotify(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select o.id as id,o.id as orderId,u.id as userId,u.username as userName,u.gender as gender,u.headPortrait as headPortrait,u.introduction as introduction,u.area as area,o.operation_type as type,o.datetime as dateTime,ps.coverUrl as coverUrl,ps.id as pictureSetId,o.isRead from picture_set ps  left join operation_records o on ps.id=o.picture_set_id  left join  users u  on informer_id=u.id where u.id>0 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("type"))
					&& filterMap.get("type") != null) {
				if ("0".equals(filterMap.get("type"))) {// 点赞
					// operation_type 4：评论，5：回复，6：@，7：赞，8：关注
					sql.append(" and operation_type=7 ");
				}
			}
			if (!"".equals(filterMap.get("currentUserId"))
					&& filterMap.get("currentUserId") != null) {
				sql.append(" and ps.userid=:psUserId");
			}
			if (!"".equals(filterMap.get("lastId"))
					&& filterMap.get("lastId") != null
					&& !"0".equals(filterMap.get("lastId"))) {
				sql.append(" and o.id<:lastId ");
			}
			if (!"".equals(filterMap.get("status"))
					&& filterMap.get("status") != null) {
				sql.append(" and o.isRead=:status ");
			}
			sql.append(" order by o.id desc ");
			if (!"".equals(filterMap.get("limit"))
					&& filterMap.get("limit") != null) {
				sql.append(" limit :limit");
			}
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Notify.class));
		if (!"".equals(filterMap.get("currentUserId"))
				&& filterMap.get("currentUserId") != null) {
			query.setInteger("psUserId",
					Integer.parseInt(filterMap.get("currentUserId")));
		}
		if (!"".equals(filterMap.get("lastId"))
				&& filterMap.get("lastId") != null
				&& !"0".equals(filterMap.get("lastId"))) {
			query.setInteger("lastId",
					Integer.parseInt(filterMap.get("lastId")));
		}
		if (!"".equals(filterMap.get("status"))
				&& filterMap.get("status") != null) {
			query.setInteger("status",
					Integer.parseInt(filterMap.get("status")));
		}
		if (!"".equals(filterMap.get("limit"))
				&& filterMap.get("limit") != null) {
			query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
		}
		return query.list();
	}

	@Override
	public List loadReplyNotify(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select o.id as id,o.id as orderId,u.id as userId,u.username as userName,u.gender as gender,u.headPortrait as headPortrait,u.introduction as introduction,u.area as area,o.operation_type as type,o.datetime as dateTime,ps.coverUrl as coverUrl,ps.id as pictureSetId,o.isRead,com.content as content from picture_set ps  left join operation_records o on ps.id=o.picture_set_id  left join  users u  on informer_id=u.id left join comment com on com.id=o.comment_id where u.id>0 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("type"))
					&& filterMap.get("type") != null) {
				if ("1".equals(filterMap.get("type"))) {// 评论
					sql.append(" and operation_type >3 and operation_type<6");
				}
			}
			if (!"".equals(filterMap.get("currentUserId"))
					&& filterMap.get("currentUserId") != null) {
				sql.append(" and (ps.userid=:psUserId or o.being_informer_id=:currentUserId)");
			}
			if (!"".equals(filterMap.get("lastId"))
					&& filterMap.get("lastId") != null
					&& !"0".equals(filterMap.get("lastId"))) {
				sql.append(" and o.id<:lastId ");
			}
			if (!"".equals(filterMap.get("status"))
					&& filterMap.get("status") != null) {
				sql.append(" and o.isRead=:status ");
			}
			sql.append(" order by o.id desc ");
			if (!"".equals(filterMap.get("limit"))
					&& filterMap.get("limit") != null) {
				sql.append(" limit :limit");
			}
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Notify.class));
		if (!"".equals(filterMap.get("currentUserId"))
				&& filterMap.get("currentUserId") != null) {
			query.setInteger("psUserId",
					Integer.parseInt(filterMap.get("currentUserId")));
			query.setInteger("currentUserId",
					Integer.parseInt(filterMap.get("currentUserId")));
		}
		if (!"".equals(filterMap.get("lastId"))
				&& filterMap.get("lastId") != null
				&& !"0".equals(filterMap.get("lastId"))) {
			query.setInteger("lastId",
					Integer.parseInt(filterMap.get("lastId")));
		}
		if (!"".equals(filterMap.get("status"))
				&& filterMap.get("status") != null) {
			query.setInteger("status",
					Integer.parseInt(filterMap.get("status")));
		}
		if (!"".equals(filterMap.get("limit"))
				&& filterMap.get("limit") != null) {
			query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
		}
		return query.list();
	}

	@Override
	public List loadAttentedNotify(Map<String, String> filterMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("select o.id as id,o.id as orderId,u.id as userId,u.username as userName,u.gender as gender,u.headPortrait as headPortrait,u.introduction as introduction,u.area as area,o.operation_type as type,o.datetime as dateTime,o.isRead from operation_records o left join  users u  on informer_id=u.id where u.id>0 ");
		if (filterMap != null && !filterMap.isEmpty()) {
			if (!"".equals(filterMap.get("type"))
					&& filterMap.get("type") != null) {
				if ("2".equals(filterMap.get("type"))) {// 关注
					sql.append(" and operation_type=8 ");
				}
			}
			if (!"".equals(filterMap.get("currentUserId"))
					&& filterMap.get("currentUserId") != null) {
				sql.append(" and o.being_informer_id=:currentUserId");
			}
			if (!"".equals(filterMap.get("lastId"))
					&& filterMap.get("lastId") != null
					&& !"0".equals(filterMap.get("lastId"))) {
				sql.append(" and o.id<:lastId ");
			}
			if (!"".equals(filterMap.get("status"))
					&& filterMap.get("status") != null) {
				sql.append(" and o.isRead=:status ");
			}
			sql.append(" order by o.id desc ");
			if (!"".equals(filterMap.get("limit"))
					&& filterMap.get("limit") != null) {
				sql.append(" limit :limit");
			}
		}
		// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
		Query query = getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.aliasToBean(Notify.class));
		if (!"".equals(filterMap.get("currentUserId"))
				&& filterMap.get("currentUserId") != null) {
			query.setInteger("currentUserId",
					Integer.parseInt(filterMap.get("currentUserId")));
		}
		if (!"".equals(filterMap.get("lastId"))
				&& filterMap.get("lastId") != null
				&& !"0".equals(filterMap.get("lastId"))) {
			query.setInteger("lastId",
					Integer.parseInt(filterMap.get("lastId")));
		}
		if (!"".equals(filterMap.get("status"))
				&& filterMap.get("status") != null) {
			query.setInteger("status",
					Integer.parseInt(filterMap.get("status")));
		}
		if (!"".equals(filterMap.get("limit"))
				&& filterMap.get("limit") != null) {
			query.setInteger("limit", Integer.parseInt(filterMap.get("limit")));
		}
		return query.list();
	}

	@Override
	public List queryVotingResults(Map<String, String> filterMap) {
		if (filterMap != null && !filterMap.isEmpty()) {
			StringBuffer sql = new StringBuffer();
			if (!"".equals(filterMap.get("pictureSetId"))
					&& filterMap.get("pictureSetId") != null) {
				sql.append("select u.id as id,u.id as orderId,u.username as username,u.gender as gender,u.headPortrait as headPortrait,u.introduction as introduction,u.area as area,CONVERT(count(1),SIGNED)" +
						" as voteNum from users u left join operation_records o on informer_id=u.id where o.operation_type=3 and o.picture_set_id=:pictureSetId ");
				if (!"".equals(filterMap.get("lastId"))
						&& filterMap.get("lastId") != null
						&& !"0".equals(filterMap.get("lastId"))) {
					sql.append(" and u.id<:lastId ");
				}
				sql.append(" group by o.informer_id order by u.id desc");
				if (!"".equals(filterMap.get("limit"))
						&& filterMap.get("limit") != null) {
					sql.append(" limit :limit");
				}
			}
			// 将返回结果映射到具体的类。可以是实体类，也可以是普通的pojo类
			Query query = getSession().createSQLQuery(sql.toString())
					.setResultTransformer(Transformers.aliasToBean(User.class));
			if (!"".equals(filterMap.get("pictureSetId"))
					&& filterMap.get("pictureSetId") != null) {
				query.setInteger("pictureSetId",
						Integer.parseInt(filterMap.get("pictureSetId")));
			}
			if (!"".equals(filterMap.get("lastId"))
					&& filterMap.get("lastId") != null
					&& !"0".equals(filterMap.get("lastId"))) {
				query.setInteger("lastId",
						Integer.parseInt(filterMap.get("lastId")));
			}
			if (!"".equals(filterMap.get("limit"))
					&& filterMap.get("limit") != null) {
				query.setInteger("limit",
						Integer.parseInt(filterMap.get("limit")));
			}
			return query.list();
		} else {
			return null;
		}
	}

}
