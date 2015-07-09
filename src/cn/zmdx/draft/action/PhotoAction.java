package cn.zmdx.draft.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import cn.zmdx.draft.entity.Photo;
import cn.zmdx.draft.service.PhotoService;
import cn.zmdx.draft.util.UploadPhoto;

import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ActionSupport;

public class PhotoAction extends ActionSupport{
	private PhotoService photoService;
	// 上传文件域
	private File image;
	// 上传文件类型
	private String imageContentType;
	// 封装上传文件名
	private String imageFileName;
	
	public PhotoService getPhotoService() {
		return photoService;
	}
	public void setPhotoService(PhotoService photoService) {
		this.photoService = photoService;
	}
	public File getImage() {
		return image;
	}
	public void setImage(File image) {
		this.image = image;
	}
	public String getImageContentType() {
		return imageContentType;
	}
	public void setImageContentType(String imageContentType) {
		this.imageContentType = imageContentType;
	}
	public String getImageFileName() {
		return imageFileName;
	}
	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}
	/**
	 * 查看个人照片
	 * @author louxiaojian
	 * @date： 日期：2015-7-8 时间：上午10:51:54
	 * @throws IOException
	 */
	public void queryPersonalPhotos() throws IOException{
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
//		ServletActionContext.getResponse().setHeader("Cache-Control", "max-age=300");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = ServletActionContext.getResponse().getWriter();
		try{
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
			
			out.print("{\"state\":\"success\",\"data\":"+JSON.toJSONString(list, true)+"}");
			out.flush();
			out.close();
		}catch (Exception e) {
			out.print("{\"state\":\"error\"}");
//			logger.error(e);
			e.printStackTrace();
		}
	}
	/**
	 * 查看照片墙
	 * @author louxiaojian
	 * @date： 日期：2015-7-8 时间：上午10:51:54
	 * @throws IOException
	 */
	public void queryPhotosWall() throws IOException{
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
//		ServletActionContext.getResponse().setHeader("Cache-Control", "max-age=300");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = ServletActionContext.getResponse().getWriter();
		try{
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
			
			out.print("{\"state\":\"success\",\"data\":"+JSON.toJSONString(list, true)+"}");
			out.flush();
			out.close();
		}catch (Exception e) {
			out.print("{\"state\":\"error\"}");
//			logger.error(e);
			e.printStackTrace();
		}
	}
	/**
	 * 用户上传图片
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:35:13
	 * @throws IOException
	 */
	public void uploadPhoto() throws IOException{
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = ServletActionContext.getResponse().getWriter();
		try{
			String type=request.getParameter("type");//分类，0:个人，1:选秀，2:秀场
			String userid=request.getParameter("userid");
			FileInputStream fis= new FileInputStream(getImage());
			String fileName=UploadPhoto.uploadPhoto(fis,imageFileName);
			String descs=request.getParameter("descs");
			
			Photo photo=new Photo();
			photo.setPhotoUrl(fileName);
			photo.setUploadDate(new Date());
			photo.setDescs(descs);
			photo.setType(type);
			photo.setPraise(0);
			photo.setTread(0);
			photo.setFlag("0");
			photo.setUserid(Integer.parseInt(userid));
			
			Map<String, Object> filterMap = new HashMap();
			filterMap.put("photo", photo);
			
			photoService.uploadPhoto(filterMap);
		}catch (Exception e) {
			out.print("{\"state\":\"error\"}");
//			logger.error(e);
			e.printStackTrace();
		}
	}
	/**
	 * 点赞
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:46:21
	 * @throws IOException
	 */
	public void praise()throws IOException{
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = ServletActionContext.getResponse().getWriter();
		try{
			String id=request.getParameter("id");
			
			Photo photo=photoService.getPhotoById(id);
			photo.setPraise(photo.getPraise()+1);
			
			photoService.updatePhoto(photo);
			
		}catch (Exception e) {
			out.print("{\"state\":\"error\"}");
//			logger.error(e);
			e.printStackTrace();
		}
	}
	/**
	 * 踩
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:46:21
	 * @throws IOException
	 */
	public void tread()throws IOException{
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request= ServletActionContext.getRequest();
		PrintWriter out = ServletActionContext.getResponse().getWriter();
		try{
			String id=request.getParameter("id");
			
			Photo photo=photoService.getPhotoById(id);
			photo.setTread(photo.getTread()-1);
			
			photoService.updatePhoto(photo);
			
		}catch (Exception e) {
			out.print("{\"state\":\"error\"}");
//			logger.error(e);
			e.printStackTrace();
		}
	}
}
