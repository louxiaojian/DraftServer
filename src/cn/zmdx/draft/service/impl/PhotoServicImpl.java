package cn.zmdx.draft.service.impl;

import java.util.List;
import java.util.Map;

import cn.zmdx.draft.dao.PhotoDao;
import cn.zmdx.draft.entity.Photo;
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
		Photo photo =(Photo)filterMap.get("photo");
		this.photoDao.saveEntity(photo);
	}

	@Override
	public void updatePhoto(Photo photo) {
		this.photoDao.updateEntity(photo);
	}

	@Override
	public Photo getPhotoById(String id) {
		return (Photo)this.photoDao.getEntity(Photo.class, id);
	}

}
