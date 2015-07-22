package cn.zmdx.draft.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import cn.zmdx.draft.entity.User;
import cn.zmdx.draft.service.impl.UserServiceImpl;
import cn.zmdx.draft.util.Encrypter;
import cn.zmdx.draft.util.UploadPhoto;
import com.opensymphony.xwork2.ActionSupport;

public class UserAction extends ActionSupport {
	private Logger logger=Logger.getLogger(UserAction.class);
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
	public void register(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		PrintWriter out =null;
		try{
			out = ServletActionContext.getResponse().getWriter();
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
				out.print("{\"state\":\"failed\",\"errorMsg\":\"loginname already exist\"}");
			}
		} catch (IOException ie) {
			out.print("{\"state\":\"error\"}");
			logger.error(ie);
			ie.printStackTrace();
		}catch (Exception e) {
			out.print("{\"state\":\"error\"}");
			logger.error(e);
			e.printStackTrace();
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 登录
	 * @author louxiaojian
	 * @date： 日期：2015-7-6 时间：下午5:24:15
	 * @throws IOException
	 */
	public void login(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		PrintWriter out =null;
		try{
			out = ServletActionContext.getResponse().getWriter();
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
					out.print("{\"state\":\"failed\",\"errorMsg\":\"password error\"}");
				}
			}
		} catch (IOException ie) {
			out.print("{\"state\":\"error\"}");
			logger.error(ie);
			ie.printStackTrace();
		}catch (Exception e) {
			out.print("{\"state\":\"error\"}");
			logger.error(e);
			e.printStackTrace();
		}finally{
			out.flush();
			out.close();
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
		} catch (IOException ie) {
			out.print("{\"state\":\"error\"}");
			logger.error(ie);
			ie.printStackTrace();
		}catch (Exception e) {
			out.print("{\"state\":\"error\"}");
			logger.error(e);
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
	public void perfectInformation(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		PrintWriter out =null;
		try{
			out = ServletActionContext.getResponse().getWriter();
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
		} catch (IOException ie) {
			out.print("{\"state\":\"error\"}");
			logger.error(ie);
			ie.printStackTrace();
		}catch (Exception e) {
			out.print("{\"state\":\"error\"}");
			logger.error(e);
			e.printStackTrace();
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 修改密码
	 * @author louxiaojian
	 * @date： 日期：2015-7-21 时间：上午11:41:41
	 */
	public void updatePassword(){
		HttpServletRequest request=ServletActionContext.getRequest();
		HttpServletResponse response =ServletActionContext.getResponse();
		PrintWriter out= null;
		try {
			out= response.getWriter();
			String oldPassowrd =request.getParameter("oldPassword");
			String newPassowrd =request.getParameter("newPassword");
			String userName=request.getParameter("userName");
			List<?> list=userService.login(userName);
			if(list.size()>0&&!list.isEmpty()){
				User user=(User)list.get(0);
				if(Encrypter.md5(oldPassowrd).equals(user.getPassword())){
					user.setPassword(Encrypter.md5(newPassowrd));
					this.userService.updateUser(user);
					out.print("{\"state\":\"success\"}");
				}else{
					out.print("{\"state\":\"failed\",\"errorMsg\":\"original password error\"}");
				}
			}else{
				out.print("{\"state\":\"failed\",\"errorMsg\":\"username does not exist\"}");
			}
		} catch (IOException ie) {
			out.print("{\"state\":\"error\"}");
			logger.error(ie);
			ie.printStackTrace();
		}catch (Exception e) {
			out.print("{\"state\":\"error\"}");
			logger.error(e);
			e.printStackTrace();
		}finally{
			out.flush();
			out.close();
		}
	}
}
