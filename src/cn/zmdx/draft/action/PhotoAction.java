package cn.zmdx.draft.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import cn.zmdx.draft.entity.Comment;
import cn.zmdx.draft.entity.CyclePhoto;
import cn.zmdx.draft.entity.Photo;
import cn.zmdx.draft.service.PhotoService;
import cn.zmdx.draft.util.UploadPhoto;
import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ActionSupport;

public class PhotoAction extends ActionSupport{
	Logger logger = Logger.getLogger(PhotoAction.class);
	private PhotoService photoService;
	// 上传文件域
	private File [] image;
	// 上传文件类型
	private String [] imageContentType;
	// 封装上传文件名 
	private String [] imageFileName;
	
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
	 * 查看个人照片
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
			String lastModified = request.getParameter("lastModified");
			//查询数据数量
			String limit=request.getParameter("limit");
			//标示，0查询lastModified之后的数据，1查询lastModified之前的数据
			String flag=request.getParameter("flag");
			String userid = request.getParameter("userid");
			if (null == lastModified || "".equals(lastModified)
					|| "null".equals(lastModified)) {
				lastModified = "0";
			}
			if ("".equals(limit)||limit==null|| "0".equals(limit)){
				limit = "10";
			}
			
			Map<String, String> filterMap = new HashMap();
			filterMap.put("limit", limit);
			filterMap.put("lastModified", lastModified);
			filterMap.put("flag", flag);
			filterMap.put("userid", userid);
			List<Photo> list = photoService.queryPersonalPhotos(filterMap);
			
			out.print("{\"state\":\"success\",\"result\":"+JSON.toJSONString(list, true)+"}");
		}catch (Exception e) {
			out.print("{\"state\":\"error\",\"errorCode\":\""+e.getClass().getName()+"\",\"errorMsg\":\""+e.getMessage()+"\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 查看照片墙
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
			//lastModified
			String lastModified = request.getParameter("lastModified");
			//查询数据数量
			String limit=request.getParameter("limit");
			//标示，0查询lastModified之后的数据，1查询lastModified之前的数据
			String flag=request.getParameter("flag");
			String category = request.getParameter("category");//0 新，1 热门  
			if (null == lastModified || "".equals(lastModified)
					|| "null".equals(lastModified)) {
				lastModified = "0";
			}
			if ("".equals(limit)||limit==null|| "0".equals(limit)){
				limit = "10";
			}
			
			Map<String, String> filterMap = new HashMap();
			filterMap.put("limit", limit);
			filterMap.put("lastModified", lastModified);
			filterMap.put("flag", flag);
			filterMap.put("category", category);
			List<Photo> list = photoService.queryPhotosWall(filterMap);
			
			out.print("{\"state\":\"success\",\"result\":"+JSON.toJSONString(list, true)+"}");
		}catch (Exception e) {
			out.print("{\"state\":\"error\",\"errorCode\":\""+e.getClass().getName()+"\",\"errorMsg\":\""+e.getMessage()+"\"}");
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
			String userid=request.getParameter("userid");
			String descs=request.getParameter("descs");
			String cycleId=request.getParameter("cycleId");
			String cycleNo=request.getParameter("cycleNo");
			
//			FileInputStream fis= new FileInputStream(getImage());
//			String fileName=UploadPhoto.uploadPhoto(fis,imageFileName);

			Map<String, Object> filterMap = new HashMap();
			//图片信息
//			Photo photo=new Photo();
//			photo.setPhotoUrl(fileName);
//			photo.setUploadDate(new Date());
//			photo.setDescs(descs);
//			photo.setType(type);
//			photo.setPraise(0);
//			photo.setTread(0);
//			photo.setReport(0);
//			photo.setStatus("0");
//			photo.setUserid(Integer.parseInt(userid));
//			filterMap.put("photo", photo);
			
			File [] files=getImage();
			for (int i = 0; i < files.length; i++) {
				FileInputStream fis= new FileInputStream(files[i]);
				String fileName=UploadPhoto.uploadPhoto(fis, imageFileName[i]);
				Photo photo=new Photo();
				photo.setPhotoUrl(fileName);
				photo.setUploadDate(new Date());
				photo.setDescs(descs);
				photo.setType(type);
				photo.setPraise(0);
				photo.setTread(0);
				photo.setReport(0);
				photo.setStatus("0");
				photo.setView(0);
				photo.setUserid(Integer.parseInt(userid));
				filterMap.put("photo"+i, photo);
			}
			filterMap.put("count", files.length);
			if("1".equals(type)){
				//图片选秀信息
				CyclePhoto cyclePhoto=new CyclePhoto();
				cyclePhoto.setCycleId(Integer.parseInt(cycleId));
				cyclePhoto.setCycleNo(cycleNo);
				filterMap.put("cyclePhoto", cyclePhoto);
			}
			
			photoService.uploadPhoto(filterMap);
			out.print("{\"state\":\"success\"}");
		}catch (Exception e) {
			out.print("{\"state\":\"error\",\"errorCode\":\""+e.getClass().getName()+"\",\"errorMsg\":\""+e.getMessage()+"\"}");
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
			String id=request.getParameter("id");
			
			Photo photo=photoService.getPhotoById(id);
			photo.setPraise(photo.getPraise()+1);
			
			photoService.updatePhoto(photo);
			
			out.print("{\"state\":\"success\"}");
		}catch (Exception e) {
			out.print("{\"state\":\"error\",\"errorCode\":\""+e.getClass().getName()+"\",\"errorMsg\":\""+e.getMessage()+"\"}");
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
			String id=request.getParameter("id");
			
			Photo photo=photoService.getPhotoById(id);
			photo.setTread(photo.getTread()-1);
			
			photoService.updatePhoto(photo);

			out.print("{\"state\":\"success\"}");
		}catch (Exception e) {
			out.print("{\"state\":\"error\",\"errorCode\":\""+e.getClass().getName()+"\",\"errorMsg\":\""+e.getMessage()+"\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 举报
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
			String id=request.getParameter("id");
			
			Photo photo=photoService.getPhotoById(id);
			photo.setReport(photo.getReport()+1);
			
			photoService.updatePhoto(photo);

			out.print("{\"state\":\"success\"}");
		}catch (Exception e) {
			out.print("{\"state\":\"error\",\"errorCode\":\""+e.getClass().getName()+"\",\"errorMsg\":\""+e.getMessage()+"\"}");
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
			String id=request.getParameter("id");
			
			Photo photo=photoService.getPhotoById(id);
			photo.setView(photo.getView()+1);
			
			photoService.updatePhoto(photo);

			out.print("{\"state\":\"success\"}");
		}catch (Exception e) {
			out.print("{\"state\":\"error\",\"errorCode\":\""+e.getClass().getName()+"\",\"errorMsg\":\""+e.getMessage()+"\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 根据选秀周期id查看选秀排名
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
			String id=request.getParameter("id");
			
			List list=photoService.queryCycleRanking(id);
			
			out.print("{\"state\":\"success\",\"result\":"+JSON.toJSONString(list, true)+"}");
		}catch (Exception e) {
			out.print("{\"state\":\"error\",\"errorCode\":\""+e.getClass().getName()+"\",\"errorMsg\":\""+e.getMessage()+"\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 获取所有主题
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
			
			out.print("{\"state\":\"success\",\"result\":"+JSON.toJSONString(list, true)+"}");
		}catch (Exception e) {
			out.print("{\"state\":\"error\",\"errorCode\":\""+e.getClass().getName()+"\",\"errorMsg\":\""+e.getMessage()+"\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 根据主题id获取相关选秀记录
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
			
			out.print("{\"state\":\"success\",\"result\":"+JSON.toJSONString(list, true)+"}");
		}catch (Exception e) {
			out.print("{\"state\":\"error\",\"errorCode\":\""+e.getClass().getName()+"\",\"errorMsg\":\""+e.getMessage()+"\"}");
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
	public void validateIsAttend(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = null ;
		try{
			out = ServletActionContext.getResponse().getWriter();
//			int i =Integer.parseInt("s");
			String cycleId=request.getParameter("cycleId");//周期id
			String userId=request.getParameter("userId");//用户id
			Map<String, Object> filterMap = new HashMap<String, Object>();
			filterMap.put("cycleId", cycleId);
			filterMap.put("userId", userId);
			List<?> list=photoService.validateIsAttend(filterMap);
			if(list!=null&&list.size()>0){//参与过
				out.print("{\"state\":\"success\",\"result\":\"yes\"}");
			}else{//未参与
				out.print("{\"state\":\"success\",\"result\":\"no\"}");
			}
		}catch (Exception e) {
			out.print("{\"state\":\"error\",\"errorCode\":\""+e.getClass().getName()+"\",\"errorMsg\":\""+e.getMessage()+"\"}");
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
	public void comment(){
		HttpServletRequest request= ServletActionContext.getRequest();
		HttpServletResponse response= ServletActionContext.getResponse();
		PrintWriter out=null;
		try {
			response.setContentType("text/json; charset=utf-8");
			out=response.getWriter();
			String photoId=request.getParameter("photoId");
			String content=request.getParameter("content");
			String parentId=request.getParameter("parentId");
			String userId=request.getParameter("userId");
			Comment comment=new Comment();
			comment.setContent(content);
			comment.setParentId(Integer.parseInt(parentId));
			comment.setPhotoId(Integer.parseInt(photoId));
			comment.setUserId(Integer.parseInt(userId));
			comment.setDatetime(new Date());
			this.photoService.saveEntity(comment);
			out.print("{\"state\":\"success\"}");
		}catch (Exception e) {
			out.print("{\"state\":\"error\",\"errorCode\":\""+e.getClass().getName()+"\",\"errorMsg\":\""+e.getLocalizedMessage()+"\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 加载评论
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
			String photoId=request.getParameter("photoId");
			String lastId=request.getParameter("lastId");
			String flag=request.getParameter("flag");
			Map<String, Object> filterMap = new HashMap();
			filterMap.put("photoId", photoId);
			filterMap.put("lastId", lastId);
			filterMap.put("flag", flag);
			List list=this.photoService.queryComment(filterMap);
			out.print("{\"state\":\"success\",\"result\":"+JSON.toJSONString(list, true)+"}");
		}catch (Exception e) {
			out.print("{\"state\":\"error\",\"errorCode\":\""+e.getClass().getName()+"\",\"errorMsg\":\""+e.getLocalizedMessage()+"\"}");
			e.printStackTrace();
			logger.error(e);
		}finally{
			out.flush();
			out.close();
		}
	}
}
