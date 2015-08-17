package cn.zmdx.draft.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import cn.zmdx.draft.entity.Comment;
import cn.zmdx.draft.entity.CyclePhotoSet;
import cn.zmdx.draft.entity.Photo;
import cn.zmdx.draft.entity.PictureSet;
import cn.zmdx.draft.entity.RankPictureSet;
import cn.zmdx.draft.entity.User;
import cn.zmdx.draft.service.PhotoService;
import cn.zmdx.draft.util.SensitivewordFilter;
import cn.zmdx.draft.util.picCloud.PicCloud;
import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ActionSupport;
import com.qcloud.UploadResult;

public class PhotoAction extends ActionSupport{
	Logger logger = Logger.getLogger(PhotoAction.class);
	private PhotoService photoService;
	// 上传文件域
	private File [] image;
	// 上传文件类型
	private String [] imageContentType;
	// 封装上传文件名 
	private String [] imageFileName;
    public static final int APP_ID_V2 = 10002468;
	public static final String SECRET_ID_V2 = "AKIDo26nbKDLWZA6xpPXzRUaYVPgf5wqqlp6";
	public static final String SECRET_KEY_V2 = "upfmsUJgzOitvj0pCzSy4tV9ihdGeZMV";
    public static final String BUCKET = "headpic";        //空间名
    
	public PhotoService getPhotoService() {
		return photoService;
	}
	public void setPhotoService(PhotoService photoService) {
		this.photoService = photoService;
	}
	public File[] getImage() {
		return image;
	}
	public void setImage(File[] image) {
		this.image = image;
	}
	public String[] getImageContentType() {
		return imageContentType;
	}
	public void setImageContentType(String[] imageContentType) {
		this.imageContentType = imageContentType;
	}
	public String[] getImageFileName() {
		return imageFileName;
	}
	public void setImageFileName(String[] imageFileName) {
		this.imageFileName = imageFileName;
	}
	/**
	 * 查看个人图集
	 * @author louxiaojian
	 * @date： 日期：2015-7-8 时间：上午10:51:54
	 */
	public void queryPersonalPhotos(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
//		ServletActionContext.getResponse().setHeader("Cache-Control", "max-age=300");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
			//lastModified
			String lastid = request.getParameter("lastId");
			//查询数据数量
			String limit=request.getParameter("limit");
			//标示，0查询lastid之后的数据，1查询lastid之前的数据
			String flag=request.getParameter("flag");
			String userid = request.getParameter("userId");
			String currentUserId=request.getParameter("currentUserId");
			if ("".equals(limit)||limit==null|| "0".equals(limit)){
				limit = "10";
			}
			if("".equals(flag)||flag==null){
				flag="0";
			}
			
			Map<String, String> filterMap = new HashMap();
			filterMap.put("limit", limit);
			filterMap.put("lastid", lastid);
			filterMap.put("flag", flag);
			filterMap.put("userid", userid);
			filterMap.put("currentUserId", currentUserId);
//			List result=new ArrayList();
			List<PictureSet> list = photoService.queryPersonalPhotos(filterMap);
//			for (int i = 0; i < list.size(); i++) {
//				PictureSet ps=list.get(i);
//				List<Photo> pList=photoService.queryPhotoByPictureSetId(ps.getId());
//				ps.setPhotoList(pList);
//				result.add(ps);
//			}
			//图集所属用户信息
//			for (int i = 0; i < list.size(); i++) {
//				PictureSet ps=list.get(i);
//				User user=(User)this.photoService.getObjectById(User.class, ps.getUserid()+"");
//				User u=new User();
//				u.setId(user.getId());
//				u.setHeadPortrait(user.getHeadPortrait());
//				u.setUsername(user.getUsername());
//				ps.setUser(u);
//				result.add(ps);
//			}
			
			out.print("{\"state\":0,\"result\":"+JSON.toJSONString(list, true)+"}");
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 查看最新、热门照片集
	 * @author louxiaojian
	 * @date： 日期：2015-7-8 时间：上午10:51:54
	 */
	public void queryPhotosWall(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
//		ServletActionContext.getResponse().setHeader("Cache-Control", "max-age=300");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
			//lastid
			String lastid = request.getParameter("lastId");
			//查询数据数量
			String limit=request.getParameter("limit");
			//标示，0查询lastModified之后的数据，1查询lastModified之前的数据
			String flag=request.getParameter("flag");
			String category = request.getParameter("category");//0 新，1 热门  
			if ("".equals(limit)||limit==null|| "0".equals(limit)){
				limit = "10";
			}
			if ("".equals(lastid)||lastid==null){
				lastid = "0";
			}
			if ("".equals(flag)||flag==null){
				flag = "0";
			}
			
			Map<String, String> filterMap = new HashMap();
			filterMap.put("limit", limit);
			filterMap.put("lastid", lastid);
			filterMap.put("flag", flag);
			filterMap.put("category", category);
			List list =null;
			List result=new ArrayList();
			if("1".equals(category)){//热门
				list = photoService.queryHotPhotosWall(filterMap);
//				for (int i = 0; i < list.size(); i++) {
//					RankPictureSet ps=(RankPictureSet)list.get(i);
//					List<Photo> pList=photoService.queryPhotoByPictureSetId(ps.getPictureSetId());
//					ps.setPhotoList(pList);
//					result.add(ps);
//				}
				//图集所属用户信息
				for (int i = 0; i < list.size(); i++) {
					RankPictureSet ps=(RankPictureSet)list.get(i);
					User user=(User)this.photoService.getObjectById(User.class, ps.getUserid()+"");
					User u=new User();
					u.setId(user.getId());
					u.setHeadPortrait(user.getHeadPortrait());
					u.setUsername(user.getUsername());
					ps.setUser(u);
					result.add(ps);
				}
			}else if("0".equals(category)){//新
				list = photoService.queryPhotosWall(filterMap);
//				for (int i = 0; i < list.size(); i++) {
//					PictureSet ps=(PictureSet)list.get(i);
//					List<Photo> pList=photoService.queryPhotoByPictureSetId(ps.getId());
//					ps.setPhotoList(pList);
//					result.add(ps);
//				}
				//图集所属用户信息
				for (int i = 0; i < list.size(); i++) {
					PictureSet ps=(PictureSet)list.get(i);
					User user=(User)this.photoService.getObjectById(User.class, ps.getUserid()+"");
					User u=new User();
					u.setId(user.getId());
					u.setHeadPortrait(user.getHeadPortrait());
					u.setUsername(user.getUsername());
					ps.setUser(u);
					result.add(ps);
				}
			}
			out.print("{\"state\":0,\"result\":"+JSON.toJSONString(result, true)+"}");
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 查看选秀照片墙（最新）
	 * @author louxiaojian
	 * @date： 日期：2015-7-8 时间：上午10:51:54
	 */
	public void queryDraftPhotosWall(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
//		ServletActionContext.getResponse().setHeader("Cache-Control", "max-age=300");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
			//lastid
			String lastid = request.getParameter("lastId");
			//查询数据数量
			String limit=request.getParameter("limit");
			//标示，0查询lastModified之后的数据，1查询lastModified之前的数据
			String flag=request.getParameter("flag");
			//选秀主题周期id
			String themeCycleId=request.getParameter("themeId");
			if ("".equals(limit)||limit==null|| "0".equals(limit)){
				limit = "10";
			}
			if ("".equals(flag)||flag==null){
				flag = "0";
			}
			Map<String, String> filterMap = new HashMap();
			filterMap.put("limit", limit);
			filterMap.put("lastid", lastid);
			filterMap.put("flag", flag);
			filterMap.put("themeCycleId", themeCycleId);
			List<PictureSet> list = photoService.queryDraftPhotosWall(filterMap);
			
			List result=new ArrayList();
			for (int i = 0; i < list.size(); i++) {
				PictureSet ps=list.get(i);
				//图集所有照片
//				List<Photo> pList=photoService.queryPhotoByPictureSetId(ps.getId());
//				ps.setPhotoList(pList);
				//图集评论数
				int comments=photoService.queryCommentByPictureSetId(ps.getId());
				ps.setComments(comments);
				//图集所属用户信息
				User user=(User)this.photoService.getObjectById(User.class, ps.getUserid()+"");
				User u=new User();
				u.setId(user.getId());
				u.setHeadPortrait(user.getHeadPortrait());
				u.setUsername(user.getUsername());
				ps.setUser(u);
				result.add(ps);
			
			}
			
			out.print("{\"state\":0,\"result\":"+JSON.toJSONString(result, true)+"}");
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 用户上传图片
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:35:13
	 */
	public void uploadPhoto(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
			String type=request.getParameter("type");//分类，0:个人，1:秀场
			String userid=request.getParameter("userId");
			String descs=request.getParameter("descs");
			String themeCycleId=request.getParameter("themeCycleId");//选秀主题周期id
			String themeTitle=request.getParameter("themeTitle");// 选秀主题标题

			Map<String, Object> filterMap = new HashMap();
			
			//创建图集
			PictureSet ps=new PictureSet();
			ps.setType(type);
			ps.setDescs(descs);
			ps.setUserid(Integer.parseInt(userid));
			ps.setUploadDate(new Date());
			ps.setStatus("1");
			ps.setThemeCycleId(Integer.parseInt(themeCycleId));
			filterMap.put("pictureSet", ps);
			
			File [] files=getImage();
			int errorcount=0;
			String [] fileids = new String[]{};
			PicCloud pc = new PicCloud(APP_ID_V2, SECRET_ID_V2, SECRET_KEY_V2, BUCKET);
			for (int i = 0; i < files.length; i++) {
				UploadResult result = new UploadResult();
				int ret = pc.Upload(files[i], result);
//				int ret=1;
				if(ret!=0){
					errorcount++;
					break;
				}else{
					Photo photo=new Photo();
					photo.setPhotoUrl(result.download_url);
					photo.setUploadDate(new Date());
					photo.setUserid(Integer.parseInt(userid));
					photo.setType(0);//图集
					photo.setFileid(result.fileid);
					filterMap.put("photo"+i, photo);
					fileids[fileids.length]=result.fileid;
				}
			}
			if(errorcount==0){
				filterMap.put("count", files.length);
				if("1".equals(type)){
					//图片选秀信息
					CyclePhotoSet cyclePhoto=new CyclePhotoSet();
					cyclePhoto.setThemeCycleId(Integer.parseInt(themeCycleId));
					cyclePhoto.setThemeTitle(themeTitle);
					filterMap.put("cyclePhoto", cyclePhoto);
				}
				photoService.uploadPhoto(filterMap);
				out.print("{\"state\":0}");
			}else{//失败删除本次所有上传照片
				for (int i = 0; i < fileids.length; i++) {
					pc.Delete(fileids[i]);
				}
				out.print("{\"state\":\"1\",\"errorMsg\":\"上传失败，请重试\"}");
			}
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	
	/**
	 * 上传真人验证照片
	 * @author louxiaojian
	 * @date： 日期：2015-7-30 时间：下午2:16:43
	 */
	public void realityVerification(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
			String userId=request.getParameter("userId");
			File [] files=getImage();
			//上传至万象空间
			PicCloud pc = new PicCloud(APP_ID_V2, SECRET_ID_V2, SECRET_KEY_V2, BUCKET);
			UploadResult result = new UploadResult();
			int ret = pc.Upload(files[0], result);
			if(ret!=0){
				out.print("{\"state\":\"1\",\"errorMsg\":\"上传失败，请重试\"}");
			}else{
				//图片链接
				Photo photo=new Photo();
				photo.setPhotoUrl(result.download_url);
				photo.setUploadDate(new Date());
				photo.setUserid(Integer.parseInt(userId));
				photo.setPictureSetId(0);
				photo.setType(1);//图集
				this.photoService.realityVerification(photo,userId);
				out.print("{\"state\":0}");
			}
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 点赞
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:46:21
	 */
	public void praisePhoto(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
			String userid=request.getParameter("userId");
			String pictureSetId=request.getParameter("pictureSetId");
			String result=photoService.OperationPictureSet(userid,pictureSetId,0);
			if("failed".equals(result)){//已操作
				out.print("{\"state\":1,\"result\":{\"state\":\"0\"}}");
			}else{
				out.print("{\"state\":0,\"result\":{\"state\":\"1\"}}");
			}
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 踩
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:46:21
	 */
	public void treadPhoto(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
			String userid=request.getParameter("userId");
			String pictureSetId=request.getParameter("pictureSetId");
			String result=photoService.OperationPictureSet(userid,pictureSetId,1);
			if("failed".equals(result)){//已操作过
				out.print("{\"state\":1,\"result\":{\"state\":\"0\"}}");
			}else{
				out.print("{\"state\":0,\"result\":{\"state\":\"1\"}}");
			}
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 举报图集
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:46:21
	 */
	public void reportPhoto(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
			String userid=request.getParameter("userId");
			String pictureSetId=request.getParameter("pictureSetId");
			String result=photoService.OperationPictureSet(userid,pictureSetId,2);
			if("failed".equals(result)){//已操作过
				out.print("{\"state\":1,\"result\":{\"state\":\"0\"}}");
			}else{
				out.print("{\"state\":0,\"result\":{\"state\":\"1\"}}");
			}
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 增加浏览量
	 * @author louxiaojian
	 * @date： 日期：2015-7-21 时间：下午3:58:36
	 */
	public void viewPhoto(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
			String pictureSetId=request.getParameter("pictureSetId");
			
			PictureSet ps=(PictureSet)photoService.getObjectById(PictureSet.class,pictureSetId);
			ps.setView(ps.getView()+1);
			
			photoService.updateObject(ps);

			out.print("{\"state\":0}");
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 选秀投票
	 * @author louxiaojian
	 * @date： 日期：2015-7-27 时间：上午11:17:18
	 */
	public void vote(){//operation_records
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
			String userid=request.getParameter("userId");
			String pictureSetId=request.getParameter("pictureSetId");
			String result=photoService.OperationPictureSet(userid,pictureSetId,3);
			if("failed".equals(result)){//已操作过
				out.print("{\"state\":1,\"result\":{\"state\":\"0\"}}");
			}else{
				out.print("{\"state\":0,\"result\":{\"state\":\"1\"}}");
			}
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 根据选秀主题周期id查看选秀图集排名
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:46:21
	 */
	public void queryCycleRanking(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
			//lastid
			String lastid = request.getParameter("lastId");
			//查询数据数量
			String limit=request.getParameter("limit");
			//标示，0查询lastModified之后的数据，1查询lastModified之前的数据
			String flag=request.getParameter("flag");
			//选秀主题周期id
			String themeCycleId=request.getParameter("themeCycleId");
			if ("".equals(limit)||limit==null|| "0".equals(limit)){
				limit = "10";
			}
			if ("".equals(flag)||flag==null){
				flag = "0";
			}
			Map<String, String> filterMap = new HashMap();
			filterMap.put("themeCycleId", themeCycleId);
			filterMap.put("lastid", lastid);
			filterMap.put("limit", limit);
			filterMap.put("flag", flag);
			List list=photoService.queryCycleRanking(filterMap);
			
			List result=new ArrayList();
			for (int i = 0; i < list.size(); i++) {
				RankPictureSet ps=(RankPictureSet)list.get(i);
				//图集所有图片
//				List<Photo> pList=photoService.queryPhotoByPictureSetId(ps.getPictureSetId());
//				ps.setPhotoList(pList);
				//图集评论数
				int comments=photoService.queryCommentByPictureSetId(ps.getId());
				ps.setComments(comments);
				//图集所属用户信息
				User user=(User)this.photoService.getObjectById(User.class, ps.getUserid()+"");
				User u=new User();
				u.setId(user.getId());
				u.setHeadPortrait(user.getHeadPortrait());
				u.setUsername(user.getUsername());
				ps.setUser(u);
				result.add(ps);
			}

			//图集所属用户信息
//			for (int i = 0; i < list.size(); i++) {
//				PictureSet ps=(PictureSet)list.get(i);
//				User user=(User)this.photoService.getObjectById(User.class, ps.getUserid()+"");
//				User u=new User();
//				u.setId(user.getId());
//				u.setHeadPortrait(user.getHeadPortrait());
//				u.setUsername(user.getUsername());
//				ps.setUser(u);
//				result.add(ps);
//			}
			out.print("{\"state\":0,\"result\":"+JSON.toJSONString(result, true)+"}");
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	
	/**
	 * 根据选秀主题周期id查看选秀用户排名
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:46:21
	 */
	public void queryUserCycleRanking(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
			//lastid
			String lastid = request.getParameter("lastId");
			//查询数据数量
			String limit=request.getParameter("limit");
			//标示，0查询lastModified之后的数据，1查询lastModified之前的数据
			String flag=request.getParameter("flag");
			//选秀主题周期id
			String themeCycleId=request.getParameter("themeCycleId");
			if ("".equals(limit)||limit==null|| "0".equals(limit)){
				limit = "10";
			}
			if ("".equals(flag)||flag==null){
				flag = "0";
			}
			Map<String, String> filterMap = new HashMap();
			filterMap.put("themeCycleId", themeCycleId);
			filterMap.put("lastid", lastid);
			filterMap.put("limit", limit);
			filterMap.put("flag", flag);
			List list=photoService.queryUserCycleRanking(filterMap);
			
//			List result=new ArrayList();
//			for (int i = 0; i < list.size(); i++) {
//				RankPictureSet ps=(RankPictureSet)list.get(i);
//				List<Photo> pList=photoService.queryPhotoByPictureSetId(ps.getPictureSetId());
//				ps.setPhotoList(pList);
//				result.add(ps);
//			}
			out.print("{\"state\":0,\"result\":"+JSON.toJSONString(list, true)+"}");
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	
	/**
	 * 获取所有选秀主题
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：下午5:29:08
	 */
	public void queryThemes(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
			Map<String, Object> filterMap = new HashMap();
			List list=photoService.queryThemes(filterMap);
			
			out.print("{\"state\":0,\"result\":"+JSON.toJSONString(list, true)+"}");
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 根据主题id获取相关选秀周期
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：下午5:29:08
	 */
	public void queryCycleByThemesId(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
			
			String themeId=request.getParameter("themeId");
			String flag=request.getParameter("flag");//0:未开始，1:进行中，2:已结束
			Map<String, Object> filterMap = new HashMap();
			filterMap.put("themeId", themeId);
			filterMap.put("flag", flag);
			List list=photoService.queryCycleByThemesId(filterMap);
			
			out.print("{\"state\":0,\"result\":"+JSON.toJSONString(list, true)+"}");
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 验证用户是否参与此次选秀
	 * @author louxiaojian
	 * @date： 日期：2015-7-20 时间：下午12:26:33
	 * @param filterMap
	 * @return
	 */
	public void validateQualification(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
//			int i =Integer.parseInt("s");
			String themeCycleId=request.getParameter("themeCycleId");//周期id
			String userId=request.getParameter("userId");//用户id
			User user=(User)this.photoService.getObjectById(User.class, userId);
			if(!"1".equals(user.getIsvalidate())){//未通过真人验证
				out.print("{\"state\":1,\"errorMsg\":\"not verified\"}");
			}else{
				Map<String, Object> filterMap = new HashMap<String, Object>();
				filterMap.put("themeCycleId", themeCycleId);
				filterMap.put("userId", userId);
				List<?> list=photoService.validateIsAttend(filterMap);
				if(list!=null&&list.size()>0){//参与过
					out.print("{\"state\":0,\"result\":{\"state\":\"0\"}}");
				}else{//未参与
					out.print("{\"state\":0,\"result\":{\"state\":\"1\"}}");
				}
			}
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 发表评论
	 * @author louxiaojian
	 * @date： 日期：2015-7-22 时间：上午11:49:09
	 */
	public void replyComment(){
		HttpServletRequest request= ServletActionContext.getRequest();
		HttpServletResponse response= ServletActionContext.getResponse();
		PrintWriter out=null;
		try {
			response.setContentType("text/json; charset=utf-8");
			out=response.getWriter();
			String pictureSetId=request.getParameter("pictureSetId");
			String content=request.getParameter("content");
			//敏感词过滤
			SensitivewordFilter sf=new SensitivewordFilter();
			content=sf.replaceSensitiveWord(content, 1, "*");
			String parentUserId=request.getParameter("parentUserId");
			String userId=request.getParameter("userId");
			Comment comment=new Comment();
			comment.setContent(content);
			if(!"".equals(parentUserId)&&parentUserId!=null){
				comment.setParentUserId(Integer.parseInt(parentUserId));
			}
			comment.setPictureSetId(Integer.parseInt(pictureSetId));
			comment.setUserId(Integer.parseInt(userId));
			comment.setDatetime(new Date());
			this.photoService.saveEntity(comment);
			out.print("{\"state\":0,\"result\":"+comment.getId()+"}");
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 加载图片评论
	 * @author louxiaojian
	 * @date： 日期：2015-7-22 时间：下午12:50:14
	 */
	public void loadComment(){
		HttpServletRequest request= ServletActionContext.getRequest();
		HttpServletResponse response= ServletActionContext.getResponse();
		PrintWriter out=null;
		try {
			response.setContentType("text/json; charset=utf-8");
			out=response.getWriter();
			String pictureSetId=request.getParameter("pictureSetId");
			String lastId=request.getParameter("lastId");
			String flag=request.getParameter("flag");
			String limit=request.getParameter("limit");
			if("".equals(limit)||limit==null){
				limit="10";
			}
			Map<String, Object> filterMap = new HashMap();
			filterMap.put("pictureSetId", pictureSetId);
			filterMap.put("lastId", lastId);
			filterMap.put("flag", flag);
			filterMap.put("limit", limit);
			List list=this.photoService.queryComment(filterMap);
			out.print("{\"state\":0,\"result\":"+JSON.toJSONString(list, true)+"}");
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 加载审批记录
	 * @author louxiaojian
	 * @date： 日期：2015-7-31 时间：下午12:33:42
	 * @throws IOException
	 */
	public void queryReviewRecords() throws IOException {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		PrintWriter out = response.getWriter();
		try {
			response.setContentType("text/json; charset=utf-8");
//			String type = request.getParameter("type");
			String type = "1";
			String userId = request.getParameter("userId");
			String pictureSetId = request.getParameter("pictureSetId");
			Map<String, String> filterMap = new HashMap();
			if (type != null && !"".equals(type)) {
				filterMap.put("type", type);
			}
			if (userId != null && !"".equals(userId)) {
				filterMap.put("userId", userId);
			}
			if (pictureSetId != null && !"".equals(pictureSetId)) {
				filterMap.put("pictureSetId", pictureSetId);
			}
			List list=photoService.queryReviewRecords(filterMap);
			out.print("{\"state\":0,\"result\":"+JSON.toJSONString(list, true)+"}");
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			logger.error(e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}
	
	/**
	 * 举报用户
	 * @author louxiaojian
	 * @date： 日期：2015-8-10 时间：下午3:09:50
	 */
	public void reportUser(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
			String currentUserId=request.getParameter("currentUserId");//当前用户
			String beingInformerId=request.getParameter("beingInformerId");//被举报用户id
			Map<String, String> filterMap = new HashMap();
			filterMap.put("currentUserId", currentUserId);
			filterMap.put("beingInformerId", beingInformerId);
			photoService.reportUser(filterMap);
			out.print("{\"state\":0}");
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	
	/**
	 * 发现照片集
	 * @author louxiaojian
	 * @date： 日期：2015-8-14 时间：下午12:04:17
	 */
	public void discoverPictureSet(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
			//查询数据数量
			String limit=request.getParameter("limit");
			if ("".equals(limit)||limit==null|| "0".equals(limit)){
				limit = "10";
			}
			Map<String, String> filterMap = new HashMap();
			filterMap.put("limit", limit);
			List list=photoService.discoverPictureSet(filterMap);
			
//			List result=new ArrayList();
//			for (int i = 0; i < list.size(); i++) {
//				PictureSet ps=(PictureSet)list.get(i);
//				List<Photo> pList=photoService.queryPhotoByPictureSetId(ps.getId());
//				ps.setPhotoList(pList);
//				result.add(ps);
//			}
			out.print("{\"state\":0,\"result\":"+JSON.toJSONString(list, true)+"}");
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	
	/**
	 * 查看图集及加载评论
	 * @author louxiaojian
	 * @date： 日期：2015-8-17 时间：上午10:52:05
	 */
	public void viewPictureSet(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
			//图集id
			String id=request.getParameter("id");
			
			PictureSet ps=(PictureSet)this.photoService.getObjectById(PictureSet.class,id);
			
			List result=new ArrayList();
			if(ps!=null){
				List<Photo> pList=photoService.queryPhotoByPictureSetId(ps.getId());
				ps.setPhotoList(pList);
				result.add(ps);
			}
			Map<String, Object> filterMap = new HashMap();
			filterMap.put("pictureSetId", id);
			filterMap.put("limit", 20);
			List list=this.photoService.queryComment(filterMap);
			out.print("{\"state\":0,\"result\":{\"photoSet\":"+JSON.toJSONString(result, true)+",\"comments\":"+JSON.toJSONString(list, true)+"}}");
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
}
