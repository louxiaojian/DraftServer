package cn.zmdx.draft.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import cn.zmdx.draft.entity.User;
import cn.zmdx.draft.service.impl.UserServiceImpl;
import cn.zmdx.draft.util.Sha1;
import cn.zmdx.draft.util.UploadPhoto;
import cn.zmdx.draft.util.UserCookieUtil;

import com.alibaba.fastjson.JSON;
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
			Sha1 sha1=new Sha1();
			pwd=sha1.Digest(pwd);
			User user=userService.findByName(loginname);
			if(user==null){
				User newUser =new User();
				newUser.setLoginname(loginname);
				newUser.setPassword(pwd);
				newUser.setFlag("1");
				newUser.setIsvalidate("0");
				newUser.setAge(0);
				newUser.setRegistrationDate(new Date());
				this.userService.saveUser(newUser);
				out.print("{\"state\":\"success\"}");
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
			User user=userService.findByName(loginname);
			if(user==null){
				out.print("{\"state\":\"loginname does not exist\"}");
			}else{
				Sha1 sha1=new Sha1();
				pwd=sha1.Digest(pwd);
				if(user.getPassword().equals(pwd)){
					UserCookieUtil.saveCookie(user, ServletActionContext.getResponse());
					User u=new User();
					u.setId(user.getId());
					u.setAge(user.getAge());
					u.setAddress(user.getAddress());
					u.setIntroduction(user.getIntroduction());
					u.setHeadPortrait(user.getHeadPortrait());
					u.setUsername(user.getUsername());
					u.setTelephone(user.getTelephone());
					u.setLoginname(user.getLoginname());
					u.setGender(user.getGender());
					out.print("{\"state\":\"success\",\"result\":"+JSON.toJSONString(u)+"}");
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
	 * 注销登录
	 * @author louxiaojian
	 * @date： 日期：2015-7-24 时间：下午12:05:37
	 */
	public void logout(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		PrintWriter out =null;
		try{
			out = ServletActionContext.getResponse().getWriter();
			HttpServletRequest request=ServletActionContext.getRequest();
//			String loginname=request.getParameter("loginname");
			UserCookieUtil.clearCookie(ServletActionContext.getResponse());
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
			User user=userService.findByName(userName);
			if(user!=null){
				Sha1 sha1=new Sha1();
				oldPassowrd=sha1.Digest(oldPassowrd);
				if(oldPassowrd.equals(user.getPassword())){
					user.setPassword(sha1.Digest(newPassowrd));
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
	/**
	 * 查看用户信息
	 * @author louxiaojian
	 * @date： 日期：2015-7-27 时间：下午4:51:02
	 */
	public void viewUserInfo(){
		HttpServletRequest request=ServletActionContext.getRequest();
		HttpServletResponse response =ServletActionContext.getResponse();
		PrintWriter out= null;
		try {
			out= response.getWriter();
			String userId=request.getParameter("userId");
			User user=userService.getById(Integer.parseInt(userId));
			User u=new User();
			u.setId(user.getId());
			u.setAge(user.getAge());
			u.setAddress(user.getAddress());
			u.setIntroduction(user.getIntroduction());
			u.setHeadPortrait(user.getHeadPortrait());
			u.setUsername(user.getUsername());
			u.setTelephone(user.getTelephone());
			out.print("{\"state\":\"success\",\"result\":"+JSON.toJSONString(u)+"}");
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
