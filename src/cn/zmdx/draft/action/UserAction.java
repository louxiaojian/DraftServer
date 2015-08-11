package cn.zmdx.draft.action;

import java.io.File;
import java.io.FileInputStream;
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
import cn.zmdx.draft.entity.Captcha;
import cn.zmdx.draft.entity.Photo;
import cn.zmdx.draft.entity.PictureSet;
import cn.zmdx.draft.entity.User;
import cn.zmdx.draft.entity.UserAttentionFans;
import cn.zmdx.draft.service.PhotoService;
import cn.zmdx.draft.service.impl.UserServiceImpl;
import cn.zmdx.draft.util.Sha1;
import cn.zmdx.draft.util.UploadPhoto;
import cn.zmdx.draft.util.UserCookieUtil;
import com.alibaba.fastjson.JSON;
import com.bcloud.msg.http.HttpSender;
import com.opensymphony.xwork2.ActionSupport;

public class UserAction extends ActionSupport {
	private Logger logger=Logger.getLogger(UserAction.class);
	private UserServiceImpl userService;
	private PhotoService photoService;
	// 上传文件域
	private File image;
	// 上传文件类型
	private String imageContentType;
	// 封装上传文件名
	private String imageFileName;

	private final static String uri = "http://222.73.117.158/msg/";//应用地址
	private final static String account = "Zmdx888";//账号
	private final static String pswd = "Zmdx888888";//密码
//	private final static String account = "jiekou-clcs-04";//账号
//	private final static String pswd = "Tch147369";//密码
//	private final static String mobiles = "15010118286";//手机号码，多个号码使用","分割
	private final static String contentLeft = "亲爱的用户，您的验证码是";//短信内容
	private final static String contentRight = "，5分钟内有效。";//短信内容
	private final static boolean needstatus = true;//是否需要状态报告，需要true，不需要false
	private final static String product = null;//产品ID
	private final static String extno = null;//扩展码
	
	public UserServiceImpl getUserService() {
		return userService;
	}
	public void setUserService(UserServiceImpl userService) {
		this.userService = userService;
	}
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
			String code=request.getParameter("captcha");
			//获取当前可用的验证码
			Captcha captcha=this.userService.queryUsableCaptcha(loginname);
			if(captcha!=null){
				if(code.equals(captcha.getCode())){
					if(captcha.getDeadline().getTime()>new Date().getTime()){
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
							newUser.setOrgId(0);
							this.userService.saveUser(newUser);
							out.print("{\"state\":0,\"userInfo\":"+JSON.toJSONString(this.getUser(newUser))+"}");
						}else{//用户名已存在
							out.print("{\"state\":1,\"errorMsg\":\"loginname already exist\"}");
						}
					}else{//验证码失效
						out.print("{\"state\":1,\"errorMsg\":\"verification code has expired\"}");
					}
				}else{//验证码错误
					out.print("{\"state\":1,\"errorMsg\":\"verification code error\"}");
				}
			}else{//未获取验证码
//				out.print("{\"state\":1,\"errorMsg\":\"no available code\"}");
				out.print("{\"state\":1,\"errorMsg\":\"verification code error\"}");
			}
		} catch (IOException ie) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+ie.getMessage()+"\",\"errorMsg\":\"system error\"}");
			logger.error(ie);
			ie.printStackTrace();
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"system error\"}");
			logger.error(e);
			e.printStackTrace();
		}finally{
			out.flush();
			out.close();
		}
	}

	/**
	 * 生成验证码
	 * @author louxiaojian
	 * @date： 日期：2015-7-29 时间：上午10:58:56
	 */
	public void createCaptcha(){
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		PrintWriter out =null;
		try{
			out = ServletActionContext.getResponse().getWriter();
			HttpServletRequest request=ServletActionContext.getRequest();
			String telephone=request.getParameter("loginname");
			//验证该手机号今日是否能获取
			int count=this.userService.qualificationByTelephone(telephone);
			if(count>30){
				out.print("{\"state\":1,\"errorMsg\":\"can't get today\"}");
			}else{
				String code=String.valueOf((int)((Math.random()*9+1)*100000));
				String returnString = HttpSender.batchSend(uri, account, pswd, telephone, contentLeft+code+contentRight, needstatus, product, extno);
				String returnCode= returnString.split("\n")[0].split(",")[1];
				System.out.println(returnCode);
				if("0".equals(returnCode)){
					Captcha captcha=this.userService.createCaptcha(telephone,code);
					out.print("{\"state\":0,\"result\":"+JSON.toJSONString(captcha)+"}");
				}else if("107".equals(returnCode)){
					out.print("{\"state\":1,\"errorMsg\":\"telephone error\"}");
				}else {
					out.print("{\"state\":1,\"errorMsg\":\"system error\"}");
				}
			}
		} catch (IOException ie) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+ie.getMessage()+"\",\"errorMsg\":\"system error\"}");
			logger.error(ie);
			ie.printStackTrace();
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"system error\"}");
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
				out.print("{\"state\":1,\"errorMsg\":\"loginname does not exist\"}");
			}else{
				Sha1 sha1=new Sha1();
				pwd=sha1.Digest(pwd);
				if(user.getPassword().equals(pwd)){
					UserCookieUtil.saveCookie(user, ServletActionContext.getResponse());
					out.print("{\"state\":0,\"result\":"+JSON.toJSONString(this.getUser(user))+"}");
				}else{
					out.print("{\"state\":1,\"errorMsg\":\"password error\"}");
				}
			}
		} catch (IOException ie) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+ie.getMessage()+"\",\"errorMsg\":\"system error\"}");
			logger.error(ie);
			ie.printStackTrace();
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"system error\"}");
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
			out.print("{\"state\":0}");
		} catch (IOException ie) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+ie.getMessage()+"\",\"errorMsg\":\"system error\"}");
			logger.error(ie);
			ie.printStackTrace();
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"system error\"}");
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
			int id = Integer.parseInt(request.getParameter("userId"));
			User user=this.userService.getById(id);
			FileInputStream fis= new FileInputStream(getImage());
			String fileName=UploadPhoto.uploadPhoto(fis,imageFileName);
			if(fileName!=null&&!"".equals(fileName)){
				user.setHeadPortrait(fileName);
				this.userService.updateUser(user);
				out.print("{\"state\":0,\"imgUrl\":\""+fileName+"\"}");
			}else{
				out.print("{\"state\":1,\"errorMsg\":\"upload failed\"}");
			}
		} catch (IOException ie) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+ie.getMessage()+"\",\"errorMsg\":\"system error\"}");
			logger.error(ie);
			ie.printStackTrace();
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"system error\"}");
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
			int id=Integer.parseInt(request.getParameter("userId"));
			String username=request.getParameter("username");//昵称
			String address=request.getParameter("address");//地址
			String telephone=request.getParameter("telephone");//联系电话
			String name=request.getParameter("name");//真实姓名
			String ageStr=request.getParameter("age");//年龄
			String gender=request.getParameter("gender");//年龄
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
			user.setGender(Integer.parseInt(gender));
			user.setIntroduction(introduction);
			this.userService.updateUser(user);
			out.print("{\"state\":0,\"userInfo\":"+JSON.toJSON(this.getUser(user))+"}");
		} catch (IOException ie) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+ie.getMessage()+"\",\"errorMsg\":\"system error\"}");
			logger.error(ie);
			ie.printStackTrace();
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"system error\"}");
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
			String userName=request.getParameter("loginName");
			User user=userService.findByName(userName);
			if(user!=null){
				Sha1 sha1=new Sha1();
				oldPassowrd=sha1.Digest(oldPassowrd);
				if(oldPassowrd.equals(user.getPassword())){
					user.setPassword(sha1.Digest(newPassowrd));
					this.userService.updateUser(user);
					out.print("{\"state\":0,\"userInfo\":"+JSON.toJSON(this.getUser(user))+"}");
				}else{//原密码错误
					out.print("{\"state\":1,\"errorMsg\":\"original password error\"}");
				}
			}else{
				out.print("{\"state\":1,\"errorMsg\":\"username does not exist\"}");
			}
		} catch (IOException ie) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+ie.getMessage()+"\",\"errorMsg\":\"system error\"}");
			logger.error(ie);
			ie.printStackTrace();
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"system error\"}");
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
			String userId=request.getParameter("userId");//要查看的用户
			String currentUserId=request.getParameter("currentUserId");//当前用户
			User user=userService.getById(Integer.parseInt(userId));
			//获取用户图集
			Map<String, String> filterMap = new HashMap();
			filterMap.put("userid", userId);
			filterMap.put("currentUserId", currentUserId);
			filterMap.put("limit", "20");
			List photoSet=new ArrayList();
			List<PictureSet> list = photoService.queryPersonalPhotos(filterMap);
			for (int i = 0; i < list.size(); i++) {
				PictureSet ps=list.get(i);
				List<Photo> pList=photoService.queryPhotoByPictureSetId(ps.getId());
				ps.setPhotoList(pList);
				photoSet.add(ps);
			}
			//获取要查看的用户的关注
			Map<String, String> filterMap1 = new HashMap();
			if (userId != null && !"".equals(userId)) {
				filterMap1.put("fansUserId", userId);
			}
			List attentionList=userService.queryAttentions(filterMap1);
			//获取要查看的用户的粉丝
			Map<String, String> filterMap2 = new HashMap();
			if (userId != null && !"".equals(userId)) {
				filterMap2.put("attentionUserId", userId);
			}
			List fansList=userService.queryFans(filterMap2);
			
			out.print("{\"state\":0,\"userInfo\":"+JSON.toJSONString(this.getUser(user))+",\"photoSet\":"+JSON.toJSONString(photoSet, true)+",\"attentionList\":"+JSON.toJSONString(attentionList, true)+",\"fansList\":"+JSON.toJSONString(fansList, true)+"}");
		} catch (IOException ie) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+ie.getMessage()+"\",\"errorMsg\":\"system error\"}");
			logger.error(ie);
			ie.printStackTrace();
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"system error\"}");
			logger.error(e);
			e.printStackTrace();
		}finally{
			out.flush();
			out.close();
		}
	}
	/**
	 * 关注
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午12:51:44
	 */
	public void attention(){
		HttpServletRequest request=ServletActionContext.getRequest();
		HttpServletResponse response =ServletActionContext.getResponse();
		PrintWriter out= null;
		try {
			out= response.getWriter();
			String fansUserId=request.getParameter("currentUserId");
			String attentionUserId=request.getParameter("attentionUserId");
			//是否关注过
			UserAttentionFans u=this.userService.isAttention(fansUserId,attentionUserId);
			if(u!=null){
				out.print("{\"state\":1,\"errorMsg\":\"is attentioned\"}");
			}else{
				UserAttentionFans uaf=new UserAttentionFans();
				uaf.setAttentionUserId(Integer.parseInt(attentionUserId));
				uaf.setFansUserId(Integer.parseInt(fansUserId));
				uaf.setAttentionTime(new Date());
				this.userService.saveObject(uaf);
				out.print("{\"state\":0}");
			}
		} catch (IOException ie) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+ie.getMessage()+"\",\"errorMsg\":\"system error\"}");
			logger.error(ie);
			ie.printStackTrace();
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"system error\"}");
			logger.error(e);
			e.printStackTrace();
		}finally{
			out.flush();
			out.close();
		}
	}
	
	/**
	 * 验证是否已关注
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午2:23:52
	 */
	public void isAttention(){
		HttpServletRequest request=ServletActionContext.getRequest();
		HttpServletResponse response =ServletActionContext.getResponse();
		PrintWriter out= null;
		try {
			out= response.getWriter();
			String fansUserId=request.getParameter("currentUserId");
			String attentionUserId=request.getParameter("attentionUserId");
			//是否关注过
			UserAttentionFans u=this.userService.isAttention(fansUserId,attentionUserId);
			if(u!=null){//已关注
				out.print("{\"state\":0}");
			}else{//未关注
				out.print("{\"state\":1,\"errorMsg\":\"not attentioned\"}");
			}
		} catch (IOException ie) {
			out.print("{\"state\":\"error\"}");
			logger.error(ie);
			ie.printStackTrace();
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"system error\"}");
			logger.error(e);
			e.printStackTrace();
		}finally{
			out.flush();
			out.close();
		}
	}
	
	/**
	 * 取消关注
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午2:25:23
	 */
	public void cancelAttention(){
		HttpServletRequest request=ServletActionContext.getRequest();
		HttpServletResponse response =ServletActionContext.getResponse();
		PrintWriter out= null;
		try {
			out= response.getWriter();
			String fansUserId=request.getParameter("currentUserId");
			String attentionUserId=request.getParameter("attentionUserId");
			//是否关注过
			this.userService.cancelAttention(fansUserId,attentionUserId);
			out.print("{\"state\":0}");
		} catch (IOException ie) {
			out.print("{\"state\":\"error\"}");
			logger.error(ie);
			ie.printStackTrace();
		}catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"system error\"}");
			logger.error(e);
			e.printStackTrace();
		}finally{
			out.flush();
			out.close();
		}
	}
	
	/**
	 * 查看我关注的人
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午2:34:42
	 * @throws IOException
	 */
	public void queryAttentions() throws IOException {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		PrintWriter out = response.getWriter();
		try {
			response.setContentType("text/json; charset=utf-8");
			String fansUserId=request.getParameter("currentUserId");
			Map<String, String> filterMap = new HashMap();
			if (fansUserId != null && !"".equals(fansUserId)) {
				filterMap.put("fansUserId", fansUserId);
			}
			List list=userService.queryAttentions(filterMap);
			out.print("{\"state\":0,\"result\":"+JSON.toJSONString(list, true)+"}");
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"system error\"}");
			logger.error(e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}
	
	/**
	 * 查看我的粉丝
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午2:34:42
	 * @throws IOException
	 */
	public void queryFans() throws IOException {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		PrintWriter out = response.getWriter();
		try {
			response.setContentType("text/json; charset=utf-8");
			String attentionUserId=request.getParameter("currentUserId");
			Map<String, String> filterMap = new HashMap();
			if (attentionUserId != null && !"".equals(attentionUserId)) {
				filterMap.put("attentionUserId", attentionUserId);
			}
			List list=userService.queryFans(filterMap);
			out.print("{\"state\":0,\"result\":"+JSON.toJSONString(list, true)+"}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage()+"\",\"errorMsg\":\"system error\"}");
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 转换用户信息
	 * @author louxiaojian
	 * @date： 日期：2015-8-8 时间：下午4:24:26
	 * @param user
	 * @return
	 */
	public User getUser(User user){
		User newUser=new User();
		newUser.setId(user.getId());
		newUser.setAge(user.getAge());
		newUser.setAddress(user.getAddress());
		newUser.setIntroduction(user.getIntroduction());
		newUser.setHeadPortrait(user.getHeadPortrait());
		newUser.setUsername(user.getUsername());
		newUser.setTelephone(user.getTelephone());
		newUser.setLoginname(user.getLoginname());
		newUser.setGender(user.getGender());
		newUser.setValidateDate(user.getValidateDate());
		return newUser;
	}
}
