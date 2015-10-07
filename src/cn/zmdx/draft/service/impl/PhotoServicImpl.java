package cn.zmdx.draft.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zmdx.draft.dao.PhotoDao;
import cn.zmdx.draft.entity.Comment;
import cn.zmdx.draft.entity.CyclePhotoSet;
import cn.zmdx.draft.entity.OperationRecords;
import cn.zmdx.draft.entity.Photo;
import cn.zmdx.draft.entity.PictureSet;
import cn.zmdx.draft.entity.User;
import cn.zmdx.draft.service.PhotoService;
import cn.zmdx.draft.util.picCloud.PicCloud;

public class PhotoServicImpl implements PhotoService {
	public static final int APP_ID_V2 = 10002468;
	public static final String SECRET_ID_V2 = "AKIDo26nbKDLWZA6xpPXzRUaYVPgf5wqqlp6";
	public static final String SECRET_KEY_V2 = "upfmsUJgzOitvj0pCzSy4tV9ihdGeZMV";
	public static final String ALBUMBUCKET = "album"; // 空间名
	private PhotoDao photoDao;
	public PhotoServicImpl(PhotoDao photoDao){
		this.photoDao=photoDao;
	}

	@Override
	public List queryPersonalPhotos(Map<String, String> filterMap) {
		return this.photoDao.queryPersonalPhotos(filterMap);
	}

	@Override
	public List queryPhotosWall(Map<String, String> filterMap) {
		return this.photoDao.queryPhotosWall(filterMap);
	}

	@Override
	public void uploadPhoto(Map<String, Object> filterMap) {
		PictureSet ps=(PictureSet)filterMap.get("pictureSet");
		for(int i=0;i<(Integer)filterMap.get("count");i++){
			Photo photo =(Photo)filterMap.get("photo"+i);
			if(i==0){
				ps.setCoverUrl(photo.getPhotoUrl());
			}
			ps.setPhotoCount((Integer)filterMap.get("count"));
			this.photoDao.saveEntity(ps);
			photo.setPictureSetId(ps.getId());
			this.photoDao.saveEntity(photo);
			if(filterMap.get("cyclePhoto")!=null){
				CyclePhotoSet cyclePhoto=(CyclePhotoSet)filterMap.get("cyclePhoto");
				cyclePhoto.setPhotoSetId(ps.getId());
				this.photoDao.saveEntity(cyclePhoto);
			}
		}
		this.photoDao.updateEntity(ps);
	}

	@Override
	public void updateObject(Object object) {
		this.photoDao.updateEntity(object);
	}

	@Override
	public Object getObjectById(Class clazz,String id) {
		return this.photoDao.getEntity(clazz, Integer.parseInt(id));
	}

	@Override
	public List queryCycleRanking(Map<String, String> filterMap) {
		return this.photoDao.queryCycleRanking(filterMap);
	}

	@Override
	public List queryThemes(Map<String, Object> filterMap) {
		return this.photoDao.queryThemes(filterMap);
	}

	@Override
	public List queryCycleByThemesId(Map<String, Object> filterMap) {
		return this.photoDao.queryCycleByThemesId(filterMap);
	}

	@Override
	public List validateIsAttend(Map<String, Object> filterMap) {
		return this.photoDao.validateIsAttend(filterMap);
	}

	@Override
	public void saveEntity(Object obj) {
		this.photoDao.saveEntity(obj);
	}

	@Override
	public List queryComment(Map<String, Object> filterMap) {
		return this.photoDao.queryComment(filterMap);
	}

	@Override
	public List<Photo> queryDraftPhotosWall(Map<String, String> filterMap) {
		return this.photoDao.queryDraftPhotosWall(filterMap);
	}

	@Override
	public List<Photo> queryHotPhotosWall(Map<String, String> filterMap) {
		return this.photoDao.queryHotPhotosWall(filterMap);
	}

	@Override
	public List queryPhotoByPictureSetId(int id) {
		return this.photoDao.queryPhotoByPictureSetId(id);
		
	}

	@Override
	public String OperationPictureSet(String userid, String pictureSetId,
			int operationType) {
		List list=this.photoDao.queryPhotoByPictureSetId(userid,pictureSetId,operationType);
		if(operationType!=3){
			if(list.size()>0){
				return "failed";
			}
		} 
		PictureSet ps=(PictureSet)this.photoDao.getEntity(PictureSet.class,Integer.parseInt(pictureSetId));
//		if(!String.valueOf(ps.getUserid()).equals(userid)){
			//操作类型：7：赞，1：踩，2：举报，3：投票，4取消赞
			if(operationType==7){
				if("0".equals(ps.getType())){//个人
					ps.setPraise(ps.getPraise()+1);
					long time=new Date().getTime()-ps.getUploadDate().getTime();
				    long hour=time/(24*60*60*1000);
					double rank=ps.getPraise()/Math.pow(hour+2, 1.8);
					ps.setRank(rank);
				}else{
					ps.setPraise(ps.getPraise()+1);
				}
			}else if(operationType==1){
				ps.setTread(ps.getTread()+1);
			}else if(operationType==2){
				ps.setReport(ps.getReport()+1);
			}else if(operationType==3){//投票
				ps.setVotes(ps.getVotes()+1);
			}else if(operationType==4){
				if("0".equals(ps.getType())){//个人
					ps.setPraise(ps.getPraise()-1);
					long time=new Date().getTime()-ps.getUploadDate().getTime();
			    	long hour=time/(24*60*60*1000);
					double rank=ps.getPraise()/Math.pow(hour+2, 1.8);
					ps.setRank(rank);
				}else{
					ps.setPraise(ps.getPraise()-1);
				}
				int count=this.photoDao.deleteOperationRecords(Integer.parseInt(userid), Integer.parseInt(pictureSetId));
				if(count<1){
					return "failed";
				}
			}
			this.photoDao.updateEntity(ps);
			if(operationType!=4){
				OperationRecords or =new OperationRecords();
				if(operationType==2){//举报图集
					or.setType(0);
				}else{//其他操作
					or.setType(2);
				}
				or.setDatetime(new Date());
				or.setOperationType(operationType);
				or.setPictureSetId(Integer.parseInt(pictureSetId));
				or.setInformerId(Integer.parseInt(userid));
				or.setIsRead("0");//未读
				this.photoDao.saveEntity(or);
			}
			return "success";
//		}else {
//			return "failed";
//		}
	}

	@Override
	public void realityVerification(Photo photo, String userId) {
		User user=(User)this.photoDao.getEntity(User.class, Integer.parseInt(userId));
		user.setIsvalidate("3");//待审核
		user.setValidateUrl(photo.getPhotoUrl());//真人验证图片地址
		this.photoDao.updateEntity(user);
		this.photoDao.saveEntity(photo);
	}

	@Override
	public List queryReviewRecords(Map<String, String> filterMap) {
		return this.photoDao.queryReviewRecords(filterMap);
	}

	@Override
	public void reportUser(Map<String, String> filterMap) {
		OperationRecords or =new OperationRecords();
		or.setOperationType(2);
		or.setInformerId(Integer.parseInt(filterMap.get("currentUserId")));//举报人id
		or.setBeingInformerId(Integer.parseInt(filterMap.get("beingInformerId")));//被举报人id
		or.setType(1);
		or.setDatetime(new Date());
		or.setIsRead("0");//未读
		User user=(User)this.photoDao.getEntity(User.class, Integer.parseInt(filterMap.get("beingInformerId")));
		user.setReport(user.getReport()+1);
		this.photoDao.updateEntity(user);
		this.photoDao.saveEntity(or);
	}

	@Override
	public List queryUserCycleRanking(Map<String, String> filterMap) {
		return this.photoDao.queryUserCycleRanking(filterMap);
	}

	@Override
	public int queryCommentByPictureSetId(int id) {
		return this.photoDao.queryCommentByPictureSetId(id);
	}

	@Override
	public List discoverPictureSet(Map<String, String> filterMap) {
		return this.photoDao.discoverPictureSet(filterMap);
	}

	@Override
	public List queryPraiseUsers(Map<String, String> praiseFilterMap) {
		return this.photoDao.queryPraiseUsers(praiseFilterMap);
	}

	@Override
	public int isPraisedPictureSet(String currentUserId,String pictureSetId) {
		List list=this.photoDao.queryPhotoByPictureSetId(currentUserId,pictureSetId,7);
		return list.size();
	}

	@Override
	public void deletePictureSet(Map<String, String> filterMap) {
		String ids=filterMap.get("pictureSetIds");
		//删除图片源文件
		List fileidList=this.photoDao.queryPhotoByPictureSetIds(ids);
		PicCloud pc = new PicCloud(APP_ID_V2, SECRET_ID_V2, SECRET_KEY_V2,
				ALBUMBUCKET);
		for (int i = 0; i < fileidList.size(); i++) {
			Photo photo=(Photo)fileidList.get(i);
			pc.Delete(photo.getFileid());
		}
		//删除图集所有评论
		this.photoDao.executeSql("DELETE from comment where picture_set_id in ("+ids+")");
		//删除图集点赞记录
		this.photoDao.executeSql("DELETE from operation_records where picture_set_id in ("+ids+")");
		//删除图集所有照片
		this.photoDao.executeSql("DELETE from photo where picture_set_id in ("+ids+")");
		//删除选秀记录
		this.photoDao.executeSql("DELETE from cycle_photo_set where photo_set_id in ("+ids+")");
		//删除图集
		this.photoDao.executeSql("DELETE from picture_set where id in ("+ids+")");
		//删除排名
		this.photoDao.executeSql("DELETE from rank_picture_set where picture_set_id in ("+ids+")");
	}

	@Override
	public void saveComment(Comment comment) {
		this.photoDao.saveEntity(comment);
		OperationRecords or =new OperationRecords();
		or.setOperationType(4);//评论
		or.setInformerId(comment.getUserId());//评论人id
		if(comment.getParentUserId()!=null&&!"".equals(comment.getParentUserId())){
			or.setBeingInformerId(comment.getParentUserId());//回复人id
		}
		or.setPictureSetId(comment.getPictureSetId());//评论图集id
		or.setType(2);
		or.setDatetime(new Date());
		or.setIsRead("0");//未读
		this.photoDao.saveEntity(or);
	}

	@Override
	public List queryNotify(Map<String, String> filterMap) {
		return this.photoDao.queryNotify(filterMap);
	}

	@Override
	public int readNotify(Map<String, String> filterMap) {
		return this.photoDao.readNotify(filterMap);
	}

	@Override
	public int deleteComment(Map<String, String> filterMap) {
		return this.photoDao.deleteComment(filterMap);
	}

	@Override
	public int queryUserSurplusVote(Map<String, String> surplusVotesFilterMap) {
		return this.photoDao.queryUserSurplusVote(surplusVotesFilterMap);
	}
	
}
