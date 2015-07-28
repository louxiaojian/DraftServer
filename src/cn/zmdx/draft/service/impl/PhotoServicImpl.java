package cn.zmdx.draft.service.impl;

import java.util.List;
import java.util.Map;

import cn.zmdx.draft.dao.PhotoDao;
import cn.zmdx.draft.entity.CyclePhotoSet;
import cn.zmdx.draft.entity.OperationRecords;
import cn.zmdx.draft.entity.Photo;
import cn.zmdx.draft.entity.PictureSet;
import cn.zmdx.draft.service.PhotoService;

public class PhotoServicImpl implements PhotoService {
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
		this.photoDao.saveEntity(ps);
		for(int i=0;i<(Integer)filterMap.get("count");i++){
			Photo photo =(Photo)filterMap.get("photo"+i);
			photo.setPictureSetId(ps.getId());
			this.photoDao.saveEntity(photo);
			if(filterMap.get("cyclePhoto")!=null){
				CyclePhotoSet cyclePhoto=(CyclePhotoSet)filterMap.get("cyclePhoto");
				cyclePhoto.setPhotoSetId(ps.getId());
				this.photoDao.saveEntity(cyclePhoto);
			}
		}
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
	public List queryCycleRanking(String cycleId) {
		return this.photoDao.queryCycleRanking(cycleId);
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
		if(list.size()>0){
			return "failed";
		}else{
			PictureSet ps=(PictureSet)this.photoDao.getEntity(PictureSet.class,Integer.parseInt(pictureSetId));
			//操作类型：0：赞，1：踩，2：举报，3：投票
			if(operationType==0){
				ps.setPraise(ps.getPraise()+1);
			}else if(operationType==1){
				ps.setTread(ps.getTread()+1);
			}else if(operationType==2){
				ps.setReport(ps.getReport()+1);
			}else if(operationType==3){
				ps.setVotes(ps.getVotes()+1);
			}
			this.photoDao.updateEntity(ps);
			OperationRecords or =new OperationRecords();
			or.setOperationType(operationType);
			or.setPictureSetId(Integer.parseInt(pictureSetId));
			or.setUserid(Integer.parseInt(userid));
			this.photoDao.saveEntity(or);
			return "success";
		}
	}
	
}
