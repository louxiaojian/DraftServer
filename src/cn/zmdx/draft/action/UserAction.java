package cn.zmdx.draft.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import cn.zmdx.draft.entity.User;
import cn.zmdx.draft.service.impl.UserServiceImpl;
import cn.zmdx.draft.util.Encrypter;
import cn.zmdx.draft.util.UploadPhoto;

import com.opensymphony.xwork2.ActionSupport;

public class UserAction extends ActionSupport {

	private UserServiceImpl userService;
	// 上传文件域
	private File image;
	// 上传文件类型
	private String imageContentType;
	// 封装上传文件名
	private String imageFileName;

	public UserServiceImpl getUserService() {
		return userService;
	}
	public void setUserService(UserServiceImpl userService) {
		this.userService = userService;
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
	 * 用户注册
	 * @author louxiaojian
	 * @date： 日期：2015-7-7 时间：下午12:37:15
	 * @throws IOException
	 */
	public void register() throws IOException{
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		PrintWriter out = ServletActionContext.getResponse().getWriter();
		try{
			HttpServletRequest request=ServletActionContext.getRequest();
			String loginname=request.getParameter("loginname");
			String pwd=request.getParameter("pwd");
			pwd=Encrypter.md5(pwd);
			List<?> list=userService.login(loginname);
			if(list.size()==0){
//				out.print("{\"state\":\"success\"}");
				User user =new User();
				user.setLoginname(loginname);
				user.setPassword(pwd);
				user.setFlag("1");
				user.setIsvalidate("0");
				user.setAge(0);
//				this.userService.saveUser(user);
			}else{
				out.print("{\"state\":\"loginname already exist\"}");
			}
		}catch (Exception e) {
			out.print("{\"state\":\"error\"}");
			e.printStackTrace();
		}
	}
	/**
	 * 登录
	 * @author louxiaojian
	 * @date： 日期：2015-7-6 时间：下午5:24:15
	 * @throws IOException
	 */
	public void login() throws IOException{
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		PrintWriter out = ServletActionContext.getResponse().getWriter();
		try{
			HttpServletRequest request=ServletActionContext.getRequest();
			String loginname=request.getParameter("loginname");
			String pwd=request.getParameter("pwd");
			List<?> list=userService.login(loginname);
			if(list.size()==0){
				out.print("{\"state\":\"loginname does not exist\"}");
			}else{
				User user=(User)list.get(0);
				if(user.getPassword().equals(Encrypter.md5(pwd))){
					out.print("{\"state\":\"success\"}");
				}else{
					out.print("{\"state\":\"password error\"}");
				}
			}
		}catch (Exception e) {
			out.print("{\"state\":\"error\"}");
			e.printStackTrace();
		}
	}
	/**
	 * 上传头像
	 * @author louxiaojian
	 * @date： 日期：2015-7-6 时间：下午4:59:19
	 * @throws IOException
	 */
	public void uploadPhoto() throws IOException{
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		PrintWriter out = ServletActionContext.getResponse().getWriter();
		try {
			HttpServletRequest request=ServletActionContext.getRequest();
			int id = Integer.parseInt(request.getParameter("id"));
			User user=this.userService.getById(id);
			FileInputStream fis= new FileInputStream(getImage());
			String fileName=UploadPhoto.uploadPhoto(fis,imageFileName);
			if(fileName!=null&&!"".equals(fileName)){
				user.setHeadPortrait(fileName);
				this.userService.updateUser(user);
			}
			out.print("{\"state\":\"success\"}");
		} catch (Exception e) {
			out.print("{\"state\":\"error\"}");
//			logger.error(e);
			e.printStackTrace();
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 完善个人信息
	 * @author louxiaojian
	 * @date： 日期：2015-7-7 时间：下午12:53:03
	 * @throws IOException
	 */
	public void perfectInformation() throws IOException{
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		PrintWriter out = ServletActionContext.getResponse().getWriter();
		try {
			HttpServletRequest request=ServletActionContext.getRequest();
			int id=Integer.parseInt(request.getParameter("id"));
			String username=request.getParameter("username");//昵称
			String address=request.getParameter("address");//地址
			String telephone=request.getParameter("telephone");//联系电话
			String name=request.getParameter("name");//真实姓名
			String ageStr=request.getParameter("age");//年龄
			String introduction=request.getParameter("introduction");//个人介绍
			int age=0;
			if(!"".equals(ageStr)&&ageStr!=null){
				age=Integer.parseInt(ageStr);
			}
			User user=this.userService.getById(id);
			user.setUsername(username);
			user.setAddress(address);
			user.setTelephone(telephone);
			user.setName(name);
			user.setAge(age);
			user.setIntroduction(introduction);
			this.userService.updateUser(user);
			out.print("{\"state\":\"success\"}");
		} catch (Exception e) {
			out.print("{\"state\":\"error\"}");
			e.printStackTrace();
		}
	}
	
	public void queryUserPhoto() throws IOException{
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		ServletActionContext.getResponse().setHeader("Cache-Control", "max-age=300");
		PrintWriter out = ServletActionContext.getResponse().getWriter();
		//lastModified
		String lastModified = ServletActionContext.getRequest().getParameter("lastModified");
		//查询数据数量
		String limit=ServletActionContext.getRequest().getParameter("limit");
		//标示，0查询lastModified之后的数据，1查询lastModified之前的数据
		String flag=ServletActionContext.getRequest().getParameter("flag");
		if (null == lastModified || "".equals(lastModified)
				|| "null".equals(lastModified)) {
			lastModified = "0";
		}
		if ("".equals(limit)||limit==null|| "0".equals(limit)){
			limit = "20";
		}
		Map<String, String> filterMap = new HashMap();
		filterMap.put("limit", limit);
		filterMap.put("lastModified", lastModified);
		filterMap.put("flag", flag);
		try {
//			List<UserPhoto> list = userService.queryUserPhoto(filterMap);
//			out.print("{\"state\":\"success\",\"data\":"+JSON.toJSONString(list, true)+"}");
		} catch (Exception e) {
			out.print("{\"state\":\"error\"}");
//			logger.error(e);
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}
}
