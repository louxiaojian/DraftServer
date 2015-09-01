package cn.zmdx.draft.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import cn.zmdx.draft.entity.Captcha;
import cn.zmdx.draft.entity.PictureSet;
import cn.zmdx.draft.entity.User;
import cn.zmdx.draft.entity.UserAttentionFans;
import cn.zmdx.draft.service.PhotoService;
import cn.zmdx.draft.service.impl.UserServiceImpl;
import cn.zmdx.draft.util.Sha1;
import cn.zmdx.draft.util.StringUtil;
import cn.zmdx.draft.util.UserCookieUtil;
import cn.zmdx.draft.util.UserUtil;
import cn.zmdx.draft.util.picCloud.PicCloud;
import cn.zmdx.draft.weibo.Users;
import cn.zmdx.draft.weibo.model.WeiboUser;

import com.alibaba.fastjson.JSON;
import com.bcloud.msg.http.HttpSender;
import com.opensymphony.xwork2.ActionSupport;
import com.qcloud.UploadResult;

public class UserAction extends ActionSupport {
	private Logger logger = Logger.getLogger(UserAction.class);
	private UserServiceImpl userService;
	private PhotoService photoService;
	// 上传文件域
	private File image;
	// 上传文件类型
	private String imageContentType;
	// 封装上传文件名
	private String imageFileName;
	private final static String uri = "http://222.73.117.158/msg/";// 应用地址
	private final static String account = "Zmdx888";// 账号
	private final static String pswd = "Zmdx888888";// 密码
	// private final static String account = "jiekou-clcs-04";//账号
	// private final static String pswd = "Tch147369";//密码
	// private final static String mobiles = "15010118286";//手机号码，多个号码使用","分割
	private final static String contentLeft = "亲爱的用户，您的验证码是";// 短信内容
	private final static String contentRight = "，5分钟内有效。";// 短信内容
	private final static boolean needstatus = true;// 是否需要状态报告，需要true，不需要false
	private final static String product = null;// 产品ID
	private final static String extno = null;// 扩展码
	public static final int APP_ID_V2 = 10002468;
	public static final String SECRET_ID_V2 = "AKIDo26nbKDLWZA6xpPXzRUaYVPgf5wqqlp6";
	public static final String SECRET_KEY_V2 = "upfmsUJgzOitvj0pCzSy4tV9ihdGeZMV";
	public static final String HEADPICBUCKET = "headpic"; // 空间名

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
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-7 时间：下午12:37:15
	 * @throws IOException
	 */
	public void register() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			HttpServletRequest request = ServletActionContext.getRequest();
			String loginname = request.getParameter("loginname");
			String pwd = request.getParameter("password");
			String code = request.getParameter("captcha");

			if ("".equals(loginname) || loginname == null || "".equals(pwd)
					|| pwd == null) {
				out.print("{\"state\":1,\"errorMsg\":\"用户名、密码不能为空\"}");
			} else {
				// 获取当前可用的验证码
				Captcha captcha = this.userService
						.queryUsableCaptcha(loginname);
				if (captcha != null) {
					if (code.equals(captcha.getCode())) {
						if (captcha.getDeadline().getTime() > new Date()
								.getTime()) {
							Sha1 sha1 = new Sha1();
							pwd = sha1.Digest(pwd);
							User user = userService.findByName(loginname);
							if (user == null) {
								User newUser = new User();
								newUser.setLoginname(loginname);
								newUser.setUsername("手机用户"
										+ loginname.substring(7));
								newUser.setPassword(pwd);
								newUser.setFlag("1");
								newUser.setIsvalidate("0");
								newUser.setAge(0);
								newUser.setRegistrationDate(new Date());
								newUser.setOrgId(0);
								// 设置默认头像
								newUser.setHeadPortrait("http://headpic-10002468.image.myqcloud.com/d4fa3046-b2dc-49d1-9cf6-62d3c7fc9bc0");
								newUser.setThirdParty("vshow");// 默认本产品
								this.userService.register(newUser, captcha);
								out.print("{\"state\":0}");
							} else {// 用户名已存在
								out.print("{\"state\":1,\"errorMsg\":\"用户名已存在\"}");
							}
						} else {// 验证码失效
							captcha.setStatus("1");
							// this.userService.updateObject(captcha);
							out.print("{\"state\":1,\"errorMsg\":\"验证码已失效\"}");
						}
					} else {// 验证码错误
						out.print("{\"state\":1,\"errorMsg\":\"验证码错误\"}");
					}
				} else {// 未获取验证码
					out.print("{\"state\":1,\"errorMsg\":\"请先获取验证码\"}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error(e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 生成验证码
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-29 时间：上午10:58:56
	 */
	public void createCaptcha() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			HttpServletRequest request = ServletActionContext.getRequest();
			String telephone = request.getParameter("phoneNumber");
			if ("".equals(telephone) || telephone == null) {
				out.print("{\"state\":1,\"errorMsg\":\"请先填写手机号\"}");
			} else {
				// 验证该手机号今日是否能获取
				int count = this.userService
						.qualificationByTelephone(telephone);
				if (count > 30) {
					out.print("{\"state\":1,\"errorMsg\":\"今日已无发送资格\"}");
				} else {
					String code = String
							.valueOf((int) ((Math.random() * 9 + 1) * 100000));
					String returnString = HttpSender.batchSend(uri, account,
							pswd, telephone, contentLeft + code + contentRight,
							needstatus, product, extno);
					String returnCode = returnString.split("\n")[0].split(",")[1];
					if ("0".equals(returnCode)) {
						Captcha captcha = this.userService.createCaptcha(
								telephone, code);
						out.print("{\"state\":0,\"result\":{\"captcha\":"
								+ JSON.toJSONString(captcha) + "}}");
					} else if ("107".equals(returnCode)) {// 手机号错误
						out.print("{\"state\":1,\"errorMsg\":\"填写手机号有误，请检查\"}");
					} else if ("109".equals(returnCode)) {// 短信额度不足
						out.print("{\"state\":1,\"errorCode\":\"短信额度不足\",\"errorMsg\":\"系统异常\"}");
					} else {
						out.print("{\"state\":1,\"errorMsg\":\"系统异常\"}");
					}
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error(e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 登录
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-6 时间：下午5:24:15
	 * @throws IOException
	 */
	public void login() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			HttpServletRequest request = ServletActionContext.getRequest();
			String loginname = request.getParameter("loginname");
			String pwd = request.getParameter("password");
			if ("".equals(loginname) || loginname == null || "".equals(pwd)
					|| pwd == null) {
				out.print("{\"state\":1,\"errorMsg\":\"用户名或密码不能为空\"}");
			} else {
				User user = userService.findByName(loginname);
				if (user == null) {
					out.print("{\"state\":1,\"errorMsg\":\"用户名不存在\"}");
				} else {
					Sha1 sha1 = new Sha1();
					pwd = sha1.Digest(pwd);
					if (user.getPassword().equals(pwd)) {
						Cookie cookie = UserCookieUtil.saveCookie(user,
								ServletActionContext.getResponse());
						User user2 = UserUtil.getUser(user);
						user2.setCookie(cookie.getValue());
						out.print("{\"state\":0,\"result\":{\"user\":"
								+ JSON.toJSONString(user2) + "}}");
					} else {
						out.print("{\"state\":1,\"errorMsg\":\"密码错误\"}");
					}
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error(e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 注销登录
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-24 时间：下午12:05:37
	 */
	public void logout() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			HttpServletRequest request = ServletActionContext.getRequest();
			// String loginname=request.getParameter("loginname");
			UserCookieUtil.clearCookie(ServletActionContext.getResponse());
			out.print("{\"state\":0}");
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error(e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 上传头像
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-6 时间：下午4:59:19
	 * @throws IOException
	 */
	public void uploadPhoto() throws IOException {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		PrintWriter out = ServletActionContext.getResponse().getWriter();
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			String id = request.getParameter("currentUserId");
			if (id == null || "".equals(id)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
			} else {
				User user = this.userService.getById(Integer.parseInt(id));
				if (user != null) {
					PicCloud pc = new PicCloud(APP_ID_V2, SECRET_ID_V2,
							SECRET_KEY_V2, HEADPICBUCKET);
					UploadResult result = new UploadResult();
					int ret = pc.Upload(getImage(), result);
					if (ret != 0) {
						out.print("{\"state\":\"1\",\"errorMsg\":\"上传失败，请重试\"}");
					} else {
						if (!"http://headpic-10002468.image.myqcloud.com/d4fa3046-b2dc-49d1-9cf6-62d3c7fc9bc0"
								.equals(user.getHeadPortrait())) {
							// 删除原有头像图片
							if (!"".equals(user.getFileid())
									&& user.getFileid() != null) {
								ret = pc.Delete(user.getFileid());
							}
						}
						user.setHeadPortrait(result.download_url);
						user.setFileid(result.fileid);
						this.userService.updateUser(user);
						out.print("{\"state\":0,\"result\":{\"user\":"
								+ JSON.toJSON(UserUtil.getUser(user)) + "}}");
					}
				} else {
					out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error(e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 完善个人信息
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-7 时间：下午12:53:03
	 * @throws IOException
	 */
	public void perfectInformation() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			HttpServletRequest request = ServletActionContext.getRequest();
			String id = request.getParameter("currentUserId");
			String username = StringUtil.encodingUrl(request
					.getParameter("username"));// 昵称
			String address = StringUtil.encodingUrl(request
					.getParameter("address"));// 地址
			String telephone = request.getParameter("telephone");// 联系电话
			String name = request.getParameter("name");// 真实姓名
			String ageStr = request.getParameter("age");// 年龄
			String gender = request.getParameter("gender");// 性别
			String introduction = StringUtil.encodingUrl(request
					.getParameter("introduction"));// 个人介绍
			int age = 0;
			if (!"".equals(ageStr) && ageStr != null) {
				age = Integer.parseInt(ageStr);
			}
			if (id == null || "".equals(id)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
			} else {
				User user = this.userService.getById(Integer.parseInt(id));
				if (user != null) {
					if (!"".equals(username) && username != null) {
						user.setUsername(username);
					}
					if (!"".equals(address) && address != null) {
						user.setAddress(address);
					}
					if (!"".equals(telephone) && telephone != null) {
						user.setTelephone(telephone);
					}
					if (!"".equals(name) && name != null) {
						user.setName(name);
					}
					if (age != 0) {
						user.setAge(age);
					}
					if (!"".equals(gender) && gender != null) {
						user.setGender(Integer.parseInt(gender));
					}
					if (!"".equals(introduction) && introduction != null) {
						user.setIntroduction(introduction);
					}
					this.userService.updateUser(user);
					out.print("{\"state\":0,\"result\":{\"user\":"
							+ JSON.toJSON(UserUtil.getUser(user)) + "}}");
				} else {
					out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error(e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 修改密码
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-21 时间：上午11:41:41
	 */
	public void updatePassword() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String oldPassowrd = request.getParameter("oldPassword");
			String newPassowrd = request.getParameter("newPassword");
			String userName = request.getParameter("loginname");
			if ("".equals(userName) || userName == null) {
				out.print("{\"state\":1,\"errorMsg\":\"用户名不能为空\"}");
			} else {
				User user = userService.findByName(userName);
				if (user != null) {
					Sha1 sha1 = new Sha1();
					oldPassowrd = sha1.Digest(oldPassowrd);
					if (oldPassowrd.equals(user.getPassword())) {
						user.setPassword(sha1.Digest(newPassowrd));
						this.userService.updateUser(user);
						out.print("{\"state\":0}");
					} else {// 原密码错误
						out.print("{\"state\":1,\"errorMsg\":\"原始密码错误\"}");
					}
				} else {
					out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error(e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 查看用户信息
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-27 时间：下午4:51:02
	 */
	public void viewUserInfo() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String userId = request.getParameter("userId");// 要查看的用户
			String currentUserId = request.getParameter("currentUserId");// 当前用户
			User user = userService.getById(Integer.parseInt(userId));
			// 获取用户图集
			Map<String, String> filterMap = new HashMap();
			filterMap.put("userid", userId);
			filterMap.put("currentUserId", currentUserId);
			filterMap.put("limit", "20");
			if (userId == null || "".equals(userId) || user == null) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
			} else {
				// 验证是否已经关注
				UserAttentionFans u = this.userService.isAttention(
						currentUserId, userId);
				if (u != null) {// 已关注
					user.setIsAttention("0");
				} else {// 未关注
					user.setIsAttention("1");
				}
				List photoSet = new ArrayList();
				List<PictureSet> list = photoService
						.queryPersonalPhotos(filterMap);
				// for (int i = 0; i < list.size(); i++) {
				// PictureSet ps=list.get(i);
				// List<Photo>
				// pList=photoService.queryPhotoByPictureSetId(ps.getId());
				// ps.setPhotoList(pList);
				// photoSet.add(ps);
				// }
				// 获取要查看的用户的关注
				Map<String, String> filterMap1 = new HashMap();
				if (userId != null && !"".equals(userId)) {
					filterMap1.put("fansUserId", userId);
				}
				List attentionList = userService.queryAttentions(filterMap1);
				// 获取要查看的用户的粉丝
				Map<String, String> filterMap2 = new HashMap();
				if (userId != null && !"".equals(userId)) {
					filterMap2.put("attentionUserId", userId);
				}
				List fansList = userService.queryFans(filterMap2);
				if (user != null) {
					User newUser = UserUtil.getUser(user);
					newUser.setIsAttention(user.getIsAttention());
					out.print("{\"state\":0,\"result\":{\"user\":"
							+ JSON.toJSONString(newUser) + ",\"photoSet\":"
							+ JSON.toJSONString(list, true)
							+ ",\"attentionUserList\":"
							+ JSON.toJSONString(attentionList, true)
							+ ",\"fansUserList\":"
							+ JSON.toJSONString(fansList, true) + "}}");
				} else {
					out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error(e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 关注
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午12:51:44
	 */
	public void attention() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String fansUserId = request.getParameter("currentUserId");
			String attentionUserId = request.getParameter("userId");
			if (fansUserId == null || "".equals(fansUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请先登录\"}");
			} else {
				if (attentionUserId == null || "".equals(attentionUserId)) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				} else {
					// 是否关注过
					UserAttentionFans u = this.userService.isAttention(
							fansUserId, attentionUserId);
					if (u != null) {
						out.print("{\"state\":1,\"errorMsg\":\"已关注\"}");
					} else {
						UserAttentionFans uaf = new UserAttentionFans();
						uaf.setAttentionUserId(Integer
								.parseInt(attentionUserId));
						uaf.setFansUserId(Integer.parseInt(fansUserId));
						uaf.setAttentionTime(new Date());
						this.userService.saveObject(uaf);
						out.print("{\"state\":0}");
					}
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error(e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 验证是否已关注
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午2:23:52
	 */
	public void isAttention() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String fansUserId = request.getParameter("currentUserId");
			String attentionUserId = request.getParameter("userId");
			if (fansUserId == null || "".equals(fansUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请先登录\"}");
			} else {
				if (attentionUserId == null || "".equals(attentionUserId)) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				} else {
					// 是否关注过
					UserAttentionFans u = this.userService.isAttention(
							fansUserId, attentionUserId);
					if (u != null) {// 已关注
						out.print("{\"state\":0}");
					} else {// 未关注
						out.print("{\"state\":1,\"errorMsg\":\"未关注\"}");
					}
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error(e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 取消关注
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午2:25:23
	 */
	public void cancelAttention() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String fansUserId = request.getParameter("currentUserId");
			String attentionUserId = request.getParameter("userId");
			if (fansUserId == null || "".equals(fansUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请先登录\"}");
			} else {
				if (attentionUserId == null || "".equals(attentionUserId)) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				} else {
					// 是否关注过
					this.userService.cancelAttention(fansUserId,
							attentionUserId);
					out.print("{\"state\":0}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error(e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 查看我关注的人
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午2:34:42
	 * @throws IOException
	 */
	public void queryAttentions() throws IOException {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		try {
			String fansUserId = request.getParameter("currentUserId");
			if (fansUserId == null || "".equals(fansUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请先登录\"}");
			} else {
				Map<String, String> filterMap = new HashMap();
				if (fansUserId != null && !"".equals(fansUserId)) {
					filterMap.put("fansUserId", fansUserId);
				}
				List list = userService.queryAttentions(filterMap);
				out.print("{\"state\":0,\"result\":{\"user\":"
						+ JSON.toJSONString(list, true) + "}}");
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error(e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 查看我的粉丝
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-8-6 时间：下午2:34:42
	 * @throws IOException
	 */
	public void queryFans() throws IOException {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		try {
			String attentionUserId = request.getParameter("currentUserId");
			if (attentionUserId == null || "".equals(attentionUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请先登录\"}");
			} else {
				Map<String, String> filterMap = new HashMap();
				if (attentionUserId != null && !"".equals(attentionUserId)) {
					filterMap.put("attentionUserId", attentionUserId);
				}
				List list = userService.queryFans(filterMap);
				out.print("{\"state\":0,\"result\":{\"user\":"
						+ JSON.toJSONString(list, true) + "}}");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 忘记密码，重置密码
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-8-15 时间：下午4:59:11
	 */
	public void resetPassword() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			HttpServletRequest request = ServletActionContext.getRequest();
			String loginname = request.getParameter("loginname");
			String pwd = request.getParameter("password");
			String code = request.getParameter("captcha");
			if ("".equals(loginname) || loginname == null || pwd == null
					|| "".equals(pwd)) {
				out.print("{\"state\":1,\"errorMsg\":\"用户名或密码不能为空\"}");
			} else {
				// 获取当前可用的验证码
				Captcha captcha = this.userService
						.queryUsableCaptcha(loginname);
				if (captcha != null) {
					if (code.equals(captcha.getCode())) {
						if (captcha.getDeadline().getTime() > new Date()
								.getTime()) {
							Sha1 sha1 = new Sha1();
							pwd = sha1.Digest(pwd);
							User user = userService.findByName(loginname);
							if (user != null) {
								user.setPassword(pwd);
								this.userService.resetPassword(user, captcha);
								out.print("{\"state\":0,\"result\":{\"user\":"
										+ JSON.toJSONString(UserUtil
												.getUser(user)) + "}}");
							} else {// 用户名不存在
								out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
							}
						} else {// 验证码失效
							captcha.setStatus("1");
							this.userService.updateObject(captcha);
							out.print("{\"state\":1,\"errorMsg\":\"验证码已失效\"}");
						}
					} else {// 验证码错误
						out.print("{\"state\":1,\"errorMsg\":\"验证码错误\"}");
					}
				} else {// 未获取验证码
					// out.print("{\"state\":1,\"errorMsg\":\"no available code\"}");
					out.print("{\"state\":1,\"errorMsg\":\"请先获取验证码\"}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error(e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 第三方登录
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-8-29 时间：下午4:21:44
	 */
	public void thirdPartyLogin() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			HttpServletRequest request = ServletActionContext.getRequest();
			String access_token = request.getParameter("token");// token
			String thirdParty = request.getParameter("thirdParty");// 登录平台
			String userId = request.getParameter("userId");// 第三方uid
			String expiresIn = request.getParameter("expiresIn");// 过期时间
			// 验证是否已注册
			User user = this.userService.validateThirdPartyUser(userId,
					thirdParty);
			if (user != null) {// 已存在用户信息
				UserCookieUtil.saveCookie(user,
						ServletActionContext.getResponse());
				out.print("{\"state\":0,\"result\":{\"user\":"
						+ JSON.toJSONString(UserUtil.getUser(user)) + "}}");
			} else {// 先注册该用户
				if ("weibo".equals(thirdParty)) {// 新浪微博登录
					Users users = new Users(access_token);
					WeiboUser wbUser = users.showUserById(userId);
					User newUser = new User();
					if ("m".equals(wbUser.getGender())) {// 男
						newUser.setGender(1);
					} else if ("f".equals(wbUser.getGender())) {// 女
						newUser.setGender(2);
					} else {// 未知
						newUser.setGender(0);
					}
					newUser.setIntroduction(wbUser.getDescription());
					newUser.setLoginname("");
					newUser.setUsername(wbUser.getScreenName());
					newUser.setPassword("");
					newUser.setFlag("1");
					newUser.setIsvalidate("0");
					newUser.setAge(0);
					newUser.setRegistrationDate(new Date());
					newUser.setOrgId(0);
					// 设置默认头像
					newUser.setHeadPortrait(wbUser.getAvatarLarge());
					newUser.setThirdParty(thirdParty);
					newUser.setUid(userId);
					this.userService.saveUser(newUser);

					Cookie cookie = UserCookieUtil.saveCookie(newUser,
							ServletActionContext.getResponse(),
							Long.parseLong(expiresIn));
					User user2 = UserUtil.getUser(newUser);
					user2.setCookie(cookie.getValue());
					out.print("{\"state\":0,\"result\":{\"user\":"
							+ JSON.toJSONString(user2) + "}}");
				} else if ("weixin".equals(thirdParty)) {// 微信登录

				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error(e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

}
