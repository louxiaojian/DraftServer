package cn.zmdx.draft.action;

import io.rong.ApiHttpClient;
import io.rong.models.FormatType;
import io.rong.models.SdkHttpResult;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.PushPayload;
import cn.zmdx.draft.entity.Captcha;
import cn.zmdx.draft.entity.Notify;
import cn.zmdx.draft.entity.PictureSet;
import cn.zmdx.draft.entity.User;
import cn.zmdx.draft.entity.UserAttentionFans;
import cn.zmdx.draft.jpush.PushExample;
import cn.zmdx.draft.service.PhotoService;
import cn.zmdx.draft.service.impl.UserServiceImpl;
import cn.zmdx.draft.util.Sha1;
import cn.zmdx.draft.util.StringUtil;
import cn.zmdx.draft.util.UserCookieUtil;
import cn.zmdx.draft.util.UserUtil;
import cn.zmdx.draft.util.picCloud.PicCloud;
import cn.zmdx.draft.weibo.Users;
import cn.zmdx.draft.weibo.model.WeiboUser;
import cn.zmdx.draft.weixin.api.SnsAPI;
import cn.zmdx.draft.weixin.entity.WeiXinUser;

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
	private static final String jpushAppKey = "b1d281203f8f4d8b2d7f2993";
	private static final String jpushMasterSecret = "acc4ade2f7b4b5757f9bd5d8";
	private static String rongCloudAppKey = "sfci50a7cb0gi";
	private static String rongCloudSecret = "ZZK2E56947b";
	private static final boolean rongCloudFlag = true;
	static {
		if (rongCloudFlag) {// 正式 即 生产环境
			rongCloudAppKey = "y745wfm84bfgv";
			rongCloudSecret = "JC1cZU0cwst";
		} else {// 测试 即 开发环境
			rongCloudAppKey = "sfci50a7cb0gi";
			rongCloudSecret = "ZZK2E56947b";
		}
	}
	private JPushClient jPushClient = new JPushClient(jpushMasterSecret,
			jpushAppKey, 3);

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
				logger.error("{\"state\":1,\"errorMsg\":\"用户名、密码不能为空\"}");
			} else {
				// 获取当前可用的验证码
				Captcha captcha = this.userService
						.queryUsableCaptcha(loginname);
				User user = userService.findByName(loginname);
				if (user == null) {
					if (captcha != null) {
						if (code.equals(captcha.getCode())) {
							if (captcha.getDeadline().getTime() > new Date()
									.getTime()) {
								Sha1 sha1 = new Sha1();
								pwd = sha1.Digest(pwd);
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

								// 默认关注官方账号
								UserAttentionFans uaf = new UserAttentionFans();
								uaf.setAttentionUserId(78434);
								uaf.setFansUserId(newUser.getId());
								uaf.setAttentionTime(new Date());
								this.userService.attentionUser(uaf);

								out.print("{\"state\":0,\"result\":{\"state\":0}}");
								logger.error("{\"state\":0,\"result\":{\"state\":0}}");
							} else {// 验证码失效
								captcha.setStatus("1");
								// this.userService.updateObject(captcha);
								out.print("{\"state\":1,\"errorMsg\":\"验证码已失效\"}");
								logger.error("{\"state\":1,\"errorMsg\":\"验证码已失效\"}");
							}
						} else {// 验证码错误
							out.print("{\"state\":1,\"errorMsg\":\"验证码错误\"}");
							logger.error("{\"state\":1,\"errorMsg\":\"验证码错误\"}");
						}
					} else {// 未获取验证码
						out.print("{\"state\":1,\"errorMsg\":\"请先获取验证码\"}");
						logger.error("{\"state\":1,\"errorMsg\":\"请先获取验证码\"}");
					}
				} else {// 用户名已存在
					out.print("{\"state\":1,\"errorMsg\":\"用户名已存在\"}");
					logger.error("{\"state\":1,\"errorMsg\":\"用户名已存在\"}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("用户注册register报错：" + e);
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
				logger.error("{\"state\":1,\"errorMsg\":\"请先填写手机号\"}");
			} else {
				// 验证该手机号今日是否能获取
				int count = this.userService
						.qualificationByTelephone(telephone);
				if (count > 9) {// 每天最多获取10条验证码
					out.print("{\"state\":1,\"errorMsg\":\"今日已无发送资格\"}");
					logger.error("{\"state\":1,\"errorMsg\":\"今日已无发送资格\"}");
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
						logger.error("{\"state\":0,\"result\":{\"captcha\":"
								+ JSON.toJSONString(captcha) + "}}");
					} else if ("107".equals(returnCode)) {// 手机号错误
						out.print("{\"state\":1,\"errorMsg\":\"填写手机号有误，请检查\"}");
						logger.error("{\"state\":1,\"errorMsg\":\"填写手机号有误，请检查\"}");
					} else if ("109".equals(returnCode)) {// 短信额度不足
						out.print("{\"state\":1,\"errorCode\":\"短信额度不足\",\"errorMsg\":\"系统异常\"}");
						logger.error("{\"state\":1,\"errorCode\":\"短信额度不足\",\"errorMsg\":\"系统异常\"}");
					} else {
						out.print("{\"state\":1,\"errorMsg\":\"系统异常\"}");
						logger.error("{\"state\":1,\"errorMsg\":\"系统异常\"}");
					}
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("生成验证码createCaptcha报错：" + e);
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
			String alias = request.getParameter("alias");// 用户登录设备别名
			String pf = request.getParameter("pf");// 当前登录的平台：iPhone、Android
			// app版本号 1.0.1
			String appVersion = request.getParameter("appversion");
			String[] appversion = appVersion.split("\\.");
			if ("".equals(loginname) || loginname == null || "".equals(pwd)
					|| pwd == null) {
				out.print("{\"state\":1,\"errorMsg\":\"用户名或密码不能为空\"}");
				logger.error("{\"state\":1,\"errorMsg\":\"用户名或密码不能为空\"}");
			} else {
				User user = userService.findByName(loginname);
				if (user == null) {
					out.print("{\"state\":1,\"errorMsg\":\"用户名不存在\"}");
					logger.error("{\"state\":1,\"errorMsg\":\"用户名不存在\"}");
				} else {
					if (alias != null && !"".equals(alias)
							&& !"null".equals(alias)) {
						user.setAlias(alias);// 更新用户登录设备别名
						user.setPf(pf);// 更新当前登录的平台：iPhone、Android
					} else {
						alias = "null";
					}
					String token = "";
					if (Integer.parseInt(appversion[1]) > 1
							&& Integer.parseInt(appversion[2]) >= 1) {
						if ("".equals(user.getRongCloudToken())
								|| user.getRongCloudToken() == null) {
							// 绑定用户融云token
							SdkHttpResult result = null;
							try {
								result = ApiHttpClient
										.getToken(rongCloudAppKey,
												rongCloudSecret,
												String.valueOf(user.getId()),
												user.getUsername(),
												user.getHeadPortrait(),
												FormatType.json);
								JSONObject resultJson = new JSONObject(
										result.toString());
								String code = resultJson.get("code").toString();
								if ("200".equals(code)) {
									JSONObject jsonObject = new JSONObject(
											resultJson.get("result").toString());
									token = jsonObject.get("token").toString();
									user.setRongCloudToken(token);
								} else if ("403".equals(code)) {
									logger.error("测试人数已满");
								}
							} catch (Exception e) {
								e.printStackTrace();
								logger.error("第三方登录获取融云token报错：" + e);
							}
						}
					}
					// 更新用户信息
					if (alias.equals(user.getAlias())
							|| pf.equals(user.getPf())
							|| token.equals(user.getRongCloudToken())) {
						this.userService.updateUser(user);
					}
					Sha1 sha1 = new Sha1();
					pwd = sha1.Digest(pwd);
					if (user.getPassword().equals(pwd)) {
						Cookie cookie = UserCookieUtil.saveCookie(user,
								ServletActionContext.getResponse());
						User user2 = UserUtil.getUser(user);
						user2.setCookie(cookie.getValue());
						out.print("{\"state\":0,\"result\":{\"user\":"
								+ JSON.toJSONString(user2) + "}}");
						logger.error("{\"state\":0,\"result\":{\"user\":"
								+ JSON.toJSONString(user2) + "}}");
					} else {
						out.print("{\"state\":1,\"errorMsg\":\"密码错误\"}");
						logger.error("{\"state\":1,\"errorMsg\":\"密码错误\"}");
					}
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("登录login报错：" + e);
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
			out.print("{\"state\":0,\"result\":{\"state\":0}}");
			logger.error("{\"state\":0,\"result\":{\"state\":0}}");
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("注销logout报错：" + e);
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
			// app版本号 1.0.1
			String appVersion = request.getParameter("appversion");
			String[] appversion = appVersion.split("\\.");
			if (id == null || "".equals(id) || "null".equals(id)
					|| "0".equals(id)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
			} else {
				User user = this.userService.getById(Integer.parseInt(id));
				if (user != null) {
					PicCloud pc = new PicCloud(APP_ID_V2, SECRET_ID_V2,
							SECRET_KEY_V2, HEADPICBUCKET);
					UploadResult result = new UploadResult();
					int ret = pc.Upload(getImage(), result);
					if (ret != 0) {
						System.out.println(pc.GetError());
						out.print("{\"state\":\"1\",\"errorMsg\":\"上传失败，请重试\"}");
						logger.error("{\"state\":\"1\",\"errorMsg\":\"上传失败，请重试\"}");
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
						if (Integer.parseInt(appversion[1]) > 1
								&& Integer.parseInt(appversion[2]) >= 1) {
							try {
								// 刷新融云用户信息
								ApiHttpClient
										.refreshUser(rongCloudAppKey,
												rongCloudSecret,
												String.valueOf(user.getId()),
												user.getUsername(),
												user.getHeadPortrait(),
												FormatType.json);
							} catch (Exception e) {
								e.printStackTrace();
								logger.error("上传用户头像更新融云token报错：" + e);
							}
						}
						out.print("{\"state\":0,\"result\":{\"user\":"
								+ JSON.toJSON(UserUtil.getUser(user)) + "}}");
						logger.error("{\"state\":0,\"result\":{\"user\":"
								+ JSON.toJSON(UserUtil.getUser(user)) + "}}");
					}
				} else {
					out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
					logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("上传头像logout报错：" + e);
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
			request.setCharacterEncoding("utf-8");
			String id = request.getParameter("currentUserId");
			String username = StringUtil.encodingUrl(request
					.getParameter("username"));// 昵称
			String address = StringUtil.encodingUrl(request
					.getParameter("address"));// 地址
			String telephone = request.getParameter("telephone");// 联系电话
			String name = StringUtil.encodingUrl(request.getParameter("name"));// 真实姓名
			String ageStr = request.getParameter("age");// 年龄
			String gender = request.getParameter("gender");// 性别
			String area = StringUtil.encodingUrl(request.getParameter("area"));// 地区
			String introduction = StringUtil.encodingUrl(request
					.getParameter("introduction"));// 个人介绍
			// app版本号 1.0.1
			String appVersion = request.getParameter("appversion");
			String[] appversion = appVersion.split("\\.");
			int age = 0;
			if (!"".equals(ageStr) && ageStr != null) {
				age = Integer.parseInt(ageStr);
			}
			if (id == null || "".equals(id) || "null".equals(id)
					|| "0".equals(id)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
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
					if (!"".equals(area) && area != null) {
						user.setArea(area);
					}
					// if (!"".equals(username) && username != null) {
					// int
					// count=this.userService.nickNameUsed(username,Integer.parseInt(id));
					// if(count>0){
					// out.print("{\"state\":1,\"errorMsg\":\"昵称已被占用\"}");
					// }else{
					// this.userService.updateUser(user);
					// out.print("{\"state\":0,\"result\":{\"user\":"
					// + JSON.toJSON(UserUtil.getUser(user)) + "}}");
					// // }
					// }else{
					this.userService.updateUser(user);
					if (!"".equals(username) && username != null) {
						if (Integer.parseInt(appversion[1]) > 1
								&& Integer.parseInt(appversion[2]) >= 1) {
							try {
								// 刷新融云用户信息
								ApiHttpClient
										.refreshUser(rongCloudAppKey,
												rongCloudSecret,
												String.valueOf(user.getId()),
												user.getUsername(),
												user.getHeadPortrait(),
												FormatType.json);
							} catch (Exception e) {
								e.printStackTrace();
								logger.error("第三方登录获取融云token报错：" + e);
							}
						}
					}
					out.print("{\"state\":0,\"result\":{\"user\":"
							+ JSON.toJSON(UserUtil.getUser(user)) + "}}");
					logger.error("{\"state\":0,\"result\":{\"user\":"
							+ JSON.toJSON(UserUtil.getUser(user)) + "}}");
					// }
				} else {
					out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
					logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("修改个人信息perfectInformation报错：" + e);
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
				logger.error("{\"state\":1,\"errorMsg\":\"用户名不能为空\"}");
			} else {
				User user = userService.findByName(userName);
				if (user != null) {
					Sha1 sha1 = new Sha1();
					oldPassowrd = sha1.Digest(oldPassowrd);
					if (oldPassowrd.equals(user.getPassword())) {
						user.setPassword(sha1.Digest(newPassowrd));
						this.userService.updateUser(user);
						out.print("{\"state\":0,\"result\":{\"state\":0}}");
						logger.error("{\"state\":0,\"result\":{\"state\":0}}");
					} else {// 原密码错误
						out.print("{\"state\":1,\"errorMsg\":\"原始密码错误\"}");
						logger.error("{\"state\":1,\"errorMsg\":\"原始密码错误\"}");
					}
				} else {
					out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
					logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("修改密码updatePassword报错：" + e);
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
			String width = request.getParameter("w");// 缩放宽度
			User user = userService.getById(Integer.parseInt(userId));
			if (userId == null || "".equals(userId) || user == null
					|| "null".equals(userId) || "0".equals(userId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
			} else {
				// 验证是否已经关注
				UserAttentionFans u = this.userService.isAttention(
						currentUserId, userId);
				if (u != null) {// 已关注
					user.setIsAttention("1");
				} else {// 未关注
					user.setIsAttention("0");
				}
				// 获取用户图集
				Map<String, String> filterMap = new HashMap();
				filterMap.put("userid", userId);
				filterMap.put("currentUserId", currentUserId);
				// filterMap.put("limit", "20");
				filterMap.put("width", width);
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
				// filterMap1.put("limit", "20");
				if (userId != null && !"".equals(userId)) {
					filterMap1.put("fansUserId", userId);
				}
				List attentionList = userService.queryAttentions(filterMap1);
				// 获取要查看的用户的粉丝
				Map<String, String> filterMap2 = new HashMap();
				if("78434".equals(userId)){
					filterMap2.put("limit", "30");
				}
				if (userId != null && !"".equals(userId)) {
					filterMap2.put("attentionUserId", userId);
				}
				List fansList = userService.queryFans(filterMap2);
				List notifyList = null;
				int isRead = 1;// 0：未读 ，1：已读
				if (currentUserId.equals(userId)) {
					// 通知
					Map<String, String> notifyFilterMap = new HashMap();
					notifyFilterMap.put("currentUserId", currentUserId);
					// notifyFilterMap.put("limit", "20");
					notifyList = this.photoService.queryNotify(notifyFilterMap);
					for (int i = 0; i < notifyList.size(); i++) {
						Notify notify = (Notify) notifyList.get(i);
						if ("0".equals(notify.getIsRead())) {
							isRead = 0;
							break;
						}
					}
				}
				if (user != null) {
					User newUser = UserUtil.getUser(user);
					newUser.setIsAttention(user.getIsAttention());
					out.print("{\"state\":0,\"result\":{\"user\":"
							+ JSON.toJSONString(newUser) + ",\"photoSet\":"
							+ JSON.toJSONString(list, true)
							+ ",\"attentionUserList\":"
							+ JSON.toJSONString(attentionList, true)
							+ ",\"fansUserList\":"
							+ JSON.toJSONString(fansList, true)
							+ ",\"notifyList\":"
							+ JSON.toJSONString(notifyList, true)
							+ ",\"isRead\":" + isRead + "}}");
					logger.error("{\"state\":0,\"result\":{\"user\":"
							+ JSON.toJSONString(newUser) + ",\"photoSet\":"
							+ JSON.toJSONString(list, true)
							+ ",\"attentionUserList\":"
							+ JSON.toJSONString(attentionList, true)
							+ ",\"fansUserList\":"
							+ JSON.toJSONString(fansList, true)
							+ ",\"notifyList\":"
							+ JSON.toJSONString(notifyList, true)
							+ ",\"isRead\":" + isRead + "}}");
				} else {
					out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
					logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("查看用户信息viewUserInfo报错：" + e);
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
			if (fansUserId == null || "".equals(fansUserId)
					|| "null".equals(fansUserId) || "0".equals(fansUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			} else {
				if (attentionUserId == null || "".equals(attentionUserId)) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
					logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				} else {
					// 是否关注过
					UserAttentionFans u = this.userService.isAttention(
							fansUserId, attentionUserId);
					if (u != null) {
						out.print("{\"state\":0,\"result\":{\"state\":1}}");
						logger.error("{\"state\":0,\"result\":{\"state\":1}}");
					} else {
						User attentionUser = this.userService.getById(Integer
								.parseInt(attentionUserId));
						User currentUser = this.userService.getById(Integer
								.parseInt(fansUserId));
						if (!"".equals(attentionUser.getAlias())
								&& attentionUser.getAlias() != null) {
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("scheme", "vshow://vshow.com/notification");
							PushResult result;
							PushPayload pushPayload;
							try {
								if ("iPhone".equals(attentionUser.getPf())) {
									pushPayload = PushExample
											.buildPushObject_ios_tagAnd_alertWithExtrasAndMessage(
													currentUser.getUsername()
															+ " 关注了您", map,
													attentionUser.getAlias());
									result = jPushClient.sendPush(pushPayload);
									System.out
											.println("jpush result：" + result);
									logger.error("发送通知：" + result);
								} else if ("Android".equals(attentionUser
										.getPf())) {
									pushPayload = PushExample
											.buildPushObject_android_tagAnd_alertWithExtrasAndMessage(
													"享秀",
													currentUser.getUsername()
															+ " 关注了您", map,
													attentionUser.getAlias());
									result = jPushClient.sendPush(pushPayload);
									System.out
											.println("jpush result：" + result);
									logger.error("发送通知：" + result);
								}
							} catch (APIConnectionException e) {
								logger.error("发送通知：" + e);
								e.printStackTrace();
							} catch (APIRequestException e) {
								logger.error("发送通知：" + e);
								e.printStackTrace();
							}
							// IosAlert alert = IosAlert.newBuilder()
							// .setTitleAndBody("测试标题", "你被关注了")
							// .setActionLocKey("PLAY")
							// .build();
							// PushPayload pushPayload
							// =PushPayload.newBuilder();
							// PushResult result =
							// jPushClient.sendIosNotificationWithAlias(alert,
							// new
							// HashMap<String, String>(), "91");
							// logger.info("jpush result："+result );
						}
						UserAttentionFans uaf = new UserAttentionFans();
						uaf.setAttentionUserId(Integer
								.parseInt(attentionUserId));
						uaf.setFansUserId(Integer.parseInt(fansUserId));
						uaf.setAttentionTime(new Date());
						this.userService.attentionUser(uaf);
						out.print("{\"state\":0,\"result\":{\"state\":0}}");
						logger.error("{\"state\":0,\"result\":{\"state\":0}}");
					}
				}
			}
			// }catch (APIConnectionException e) {
			// out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
			// + "\",\"errorMsg\":\"系统异常\"}");
			// logger.error("Connection error, should retry later", e);
			// } catch (APIRequestException e) {
			// out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
			// + "\",\"errorMsg\":\"系统异常\"}");
			// logger.error("Should review the error, and fix the request", e);
			// logger.info("HTTP Status: " + e.getStatus());
			// logger.info("Error Code: " + e.getErrorCode());
			// logger.info("Error Message: " + e.getErrorMessage());
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("关注attention报错：" + e);
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
			if (fansUserId == null || "".equals(fansUserId)
					|| "null".equals(fansUserId) || "0".equals(fansUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			} else {
				if (attentionUserId == null || "".equals(attentionUserId)) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
					logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				} else {
					// 是否关注过
					UserAttentionFans u = this.userService.isAttention(
							fansUserId, attentionUserId);
					if (u != null) {// 已关注
						out.print("{\"state\":0,\"result\":{\"state\":1}}");
						logger.error("{\"state\":0,\"result\":{\"state\":1}}");
					} else {// 未关注
						out.print("{\"state\":0,\"result\":{\"state\":0}}");
						logger.error("{\"state\":0,\"result\":{\"state\":0}}");
					}
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("验证是否已经关注isAttention报错：" + e);
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
			if (fansUserId == null || "".equals(fansUserId)
					|| "null".equals(fansUserId) || "0".equals(fansUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			} else {
				if (attentionUserId == null || "".equals(attentionUserId)) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
					logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				} else {
					// 取消关注
					// if(!"78434".equals(attentionUserId)){
					this.userService.cancelAttention(fansUserId,
							attentionUserId);
					out.print("{\"state\":0,\"result\":{\"state\":0}}");
					logger.error("{\"state\":0,\"result\":{\"state\":0}}");
					// }else{
					// out.print("{\"state\":1,\"errorMsg\":\"不能取消关注官方账号\"}");
					// logger.error("{\"state\":1,\"errorMsg\":\"不能取消关注官方账\"}");
					// }
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("取消关注cancelAttention报错：" + e);
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
			// lastModified
			String lastid = request.getParameter("lastId");
			String limit = request.getParameter("limit");
			if ("".equals(limit) || limit == null || "0".equals(limit)) {
				limit = "20";
			}
			if (fansUserId == null || "".equals(fansUserId)
					|| "null".equals(fansUserId) || "0".equals(fansUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			} else {
				Map<String, String> filterMap = new HashMap();
				if (fansUserId != null && !"".equals(fansUserId)) {
					filterMap.put("fansUserId", fansUserId);
				}
				filterMap.put("lastid", lastid);
				filterMap.put("limit", limit);
				List list = userService.queryAttentions(filterMap);
				out.print("{\"state\":0,\"result\":{\"user\":"
						+ JSON.toJSONString(list, true) + "}}");
				logger.error("{\"state\":0,\"result\":{\"user\":"
						+ JSON.toJSONString(list, true) + "}}");
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("加载查看用户的关注的人queryAttentions报错：" + e);
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
			// lastModified
			String lastid = request.getParameter("lastId");
			String limit = request.getParameter("limit");
			if ("".equals(limit) || limit == null || "0".equals(limit)) {
				limit = "20";
			}
			if (attentionUserId == null || "".equals(attentionUserId)
					|| "null".equals(attentionUserId)
					|| "0".equals(attentionUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			} else {
				Map<String, String> filterMap = new HashMap();
				filterMap.put("lastid", lastid);
				if (attentionUserId != null && !"".equals(attentionUserId)) {
					filterMap.put("attentionUserId", attentionUserId);
				}
				filterMap.put("limit", limit);
				List list = userService.queryFans(filterMap);
				out.print("{\"state\":0,\"result\":{\"user\":"
						+ JSON.toJSONString(list, true) + "}}");
				logger.error("{\"state\":0,\"result\":{\"user\":"
						+ JSON.toJSONString(list, true) + "}}");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("加载查看用户的粉丝queryFans报错：" + e);
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
				logger.error("{\"state\":1,\"errorMsg\":\"用户名或密码不能为空\"}");
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
								logger.error("{\"state\":0,\"result\":{\"user\":"
										+ JSON.toJSONString(UserUtil
												.getUser(user)) + "}}");
							} else {// 用户名不存在
								out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
								logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
							}
						} else {// 验证码失效
							captcha.setStatus("1");
							this.userService.updateObject(captcha);
							out.print("{\"state\":1,\"errorMsg\":\"验证码已失效\"}");
							logger.error("{\"state\":1,\"errorMsg\":\"验证码已失效\"}");
						}
					} else {// 验证码错误
						out.print("{\"state\":1,\"errorMsg\":\"验证码错误\"}");
						logger.error("{\"state\":1,\"errorMsg\":\"验证码错误\"}");
					}
				} else {// 未获取验证码
					// out.print("{\"state\":1,\"errorMsg\":\"no available code\"}");
					out.print("{\"state\":1,\"errorMsg\":\"请先获取验证码\"}");
					logger.error("{\"state\":1,\"errorMsg\":\"请先获取验证码\"}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("重置密码resetPassword报错：" + e);
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
			request.setCharacterEncoding("utf-8");
			String access_token = request.getParameter("token");// token
			String thirdParty = request.getParameter("thirdParty");// 登录平台vshow、weibo、weixin
			String userId = request.getParameter("userId");// 第三方uid
			String expiresIn = request.getParameter("expiresIn");// 过期时间
			String alias = request.getParameter("alias");// 用户登录设备别名
			String pf = request.getParameter("pf");// 平台Android、iOS
			// app版本号 1.0.1
			String appVersion = request.getParameter("appversion");
			String[] appversion = appVersion.split("\\.");
			// 验证是否已注册
			User user = this.userService.validateThirdPartyUser(userId,
					thirdParty);
			if (user != null) {// 已存在用户信息
				if (alias != null && !"".equals(alias) && !"null".equals(alias)) {
					user.setAlias(alias);// 更新用户登录设备别名
					user.setPf(pf);// 更新当前登录的平台：iPhone、Android
				} else {
					alias = "null";
				}
				String token = "";
				if (Integer.parseInt(appversion[1]) > 1
						&& Integer.parseInt(appversion[2]) >= 1) {
					if ("".equals(user.getRongCloudToken())
							|| user.getRongCloudToken() == null) {
						// 绑定用户融云token
						SdkHttpResult result = null;
						try {
							result = ApiHttpClient.getToken(rongCloudAppKey,
									rongCloudSecret,
									String.valueOf(user.getId()),
									user.getUsername(), user.getHeadPortrait(),
									FormatType.json);
							JSONObject resultJson = new JSONObject(
									result.toString());
							String code = resultJson.get("code").toString();
							if ("200".equals(code)) {
								JSONObject jsonObject = new JSONObject(
										resultJson.get("result").toString());
								token = jsonObject.get("token").toString();
								user.setRongCloudToken(token);
							} else if ("403".equals(code)) {
								logger.error("测试人数已满");
							}
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("第三方登录获取融云token报错：" + e);
						}
					}
				}
				// 更新用户信息
				if (alias.equals(user.getAlias()) || pf.equals(user.getPf())
						|| token.equals(user.getRongCloudToken())) {
					this.userService.updateUser(user);
				}
				UserCookieUtil.saveCookie(user,
						ServletActionContext.getResponse());
				out.print("{\"state\":0,\"result\":{\"user\":"
						+ JSON.toJSONString(UserUtil.getUser(user)) + "}}");
			} else {// 先注册该用户
				User newUser = new User();
				if ("weibo".equals(thirdParty)) {// 新浪微博登录
					logger.error("***********************开始***********************");
					Users users = new Users(access_token);
					logger.error("***********************获取Users帮助类***********************");
					WeiboUser wbUser = users.showUserById(userId);
					logger.error("***********************获取微博用户对象WeiBoUser***********************");
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
					if (alias != null && !"".equals(alias)
							&& !"null".equals(alias)) {
						newUser.setAlias(alias);// 保存用户登录设备别名
						newUser.setPf(pf);// 更新当前登录的平台：iPhone、Android
					}
					if (!"".equals(wbUser.getAvatarLarge())
							&& wbUser.getAvatarLarge() != null) {
						// 设置默认头像
						newUser.setHeadPortrait(wbUser.getAvatarLarge());
					} else {
						newUser.setHeadPortrait("http://headpic-10002468.image.myqcloud.com/d4fa3046-b2dc-49d1-9cf6-62d3c7fc9bc0");
					}
					newUser.setThirdParty(thirdParty);
					newUser.setUid(userId);
					logger.error("***********************保存对象到项目中***********************");
					this.userService.saveUser(newUser);
				} else if ("weixin".equals(thirdParty)) {// 微信登录
					WeiXinUser weixinUser = SnsAPI.userinfo(access_token,
							userId, "zh_CN");
					newUser.setUsername(weixinUser.getNickname());
					if (!"".equals(weixinUser.getHeadimgurl())
							&& weixinUser.getHeadimgurl() != null) {
						newUser.setHeadPortrait(weixinUser.getHeadimgurl());
					} else {
						newUser.setHeadPortrait("http://headpic-10002468.image.myqcloud.com/d4fa3046-b2dc-49d1-9cf6-62d3c7fc9bc0");
					}
					newUser.setGender(weixinUser.getSex());
					newUser.setIntroduction("");
					newUser.setLoginname("");
					newUser.setPassword("");
					newUser.setFlag("1");
					newUser.setIsvalidate("0");
					newUser.setAge(0);
					newUser.setRegistrationDate(new Date());
					newUser.setOrgId(0);
					newUser.setThirdParty(thirdParty);
					newUser.setUid(userId);
					if (alias != null && !"".equals(alias)
							&& !"null".equals(alias)) {
						newUser.setAlias(alias);// 保存用户登录设备别名
						newUser.setPf(pf);// 更新当前登录的平台：iPhone、Android
					}
					this.userService.saveUser(newUser);
				}
				String token = "";
				if (Integer.parseInt(appversion[1]) > 1
						&& Integer.parseInt(appversion[2]) >= 1) {
					// 绑定用户融云token
					SdkHttpResult result = null;
					try {
						result = ApiHttpClient.getToken(rongCloudAppKey,
								rongCloudSecret,
								String.valueOf(newUser.getId()),
								newUser.getUsername(),
								newUser.getHeadPortrait(), FormatType.json);
						JSONObject resultJson = new JSONObject(
								result.toString());
						String code = resultJson.get("code").toString();
						if ("200".equals(code)) {
							JSONObject jsonObject = new JSONObject(resultJson
									.get("result").toString());
							token = jsonObject.get("token").toString();
							newUser.setRongCloudToken(token);
							this.photoService.updateObject(newUser);
						} else if ("403".equals(code)) {
							logger.error("测试人数已满");
						}
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("第三方登录获取融云token报错：" + e);
					}
				}
				logger.error("***********************获取cookie信息***********************");
				Cookie cookie = UserCookieUtil.saveCookie(newUser,
						ServletActionContext.getResponse(),
						Long.parseLong(expiresIn));
				User user2 = UserUtil.getUser(newUser);
				user2.setCookie(cookie.getValue());
				logger.error("***********************返回数据***********************");
				out.print("{\"state\":0,\"result\":{\"user\":"
						+ JSON.toJSONString(user2) + "}}");
				logger.error("{\"state\":0,\"result\":{\"user\":"
						+ JSON.toJSONString(user2) + "}}");
				logger.error("***********************结束***********************");
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("第三方登录thirdPartyLogin报错：" + e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * @ 自动提示加载人员
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-9-22 时间：下午4:58:11
	 * @throws IOException
	 */
	public void automaticPrompt() throws IOException {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		try {
			String attentionUserId = request.getParameter("currentUserId");
			String nickName = StringUtil.encodingUrl(request
					.getParameter("nickName"));// 用户昵称
			if (attentionUserId == null || "".equals(attentionUserId)
					|| "null".equals(attentionUserId)
					|| "0".equals(attentionUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			} else {
				Map<String, String> filterMap = new HashMap();
				if (nickName != null && !"".equals(nickName)) {
					filterMap.put("nickName", nickName);
				}
				List list = userService.automaticPrompt(filterMap);
				out.print("{\"state\":0,\"result\":{\"user\":"
						+ JSON.toJSONString(list, true) + "}}");
				logger.error("{\"state\":0,\"result\":{\"user\":"
						+ JSON.toJSONString(list, true) + "}}");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("@自动提示加载人员automaticPrompt报错：" + e);
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 绑定用户发送通知的设备别名（divaceToken）
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-10-17 时间：下午12:04:54
	 * @throws IOException
	 */
	public void bindingUserAlias() throws IOException {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		try {
			String userId = request.getParameter("currentUserId");
			String aliasStr = request.getParameter("alias");
			if (userId != null && !"".equals(userId) && !"null".equals(userId)
					&& !"0".equals(userId) && aliasStr != null
					&& !"".equals(aliasStr) && !"null".equals(aliasStr)) {
				User user = this.userService.getById(Integer.parseInt(userId));
				if (user != null) {
					user.setAlias(aliasStr);
					this.userService.updateUser(user);
					out.print("{\"state\":0}");
					logger.error("{\"state\":0}");
				} else {
					out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
					logger.error("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
				}
			} else {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("绑定用户发送通知的设备别名（divaceToken）bindingUserAlias报错：" + e);
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 加载个人中心通知状态
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-10-23 时间：上午11:15:02
	 */
	public void loadNotifyStatus() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String currentUserId = request.getParameter("currentUserId");// 当前用户
			User user = userService.getById(Integer.parseInt(currentUserId));
			if (currentUserId == null || "".equals(currentUserId)
					|| user == null || "null".equals(currentUserId)
					|| "0".equals(currentUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
			} else {
				// 通知 0：未读 ，1：已读
				Map<String, String> notifyFilterMap = new HashMap();
				notifyFilterMap.put("currentUserId", currentUserId);
				notifyFilterMap.put("status", "0");
				List notifyList = this.photoService
						.queryNotify(notifyFilterMap);
				if (notifyList.size() > 0) {// 有未读通知
					out.print("{\"state\":0,\"result\":{\"isRead\":0}}");
					logger.error("{\"state\":0,\"result\":{\"isRead\":0}}");
				} else {// 没有未读通知
					out.print("{\"state\":0,\"result\":{\"isRead\":1}}");
					logger.error("{\"state\":0,\"result\":{\"isRead\":1}}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("查看用户信息viewUserInfo报错：" + e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 个人中心-加载通知
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-10-30 时间：上午10:41:25
	 */
	public void loadNotify() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String currentUserId = request.getParameter("currentUserId");// 当前用户
			// lastModified
			String lastId = request.getParameter("lastId");
			// 查询数据数量
			String limit = request.getParameter("limit");
			if ("".equals(limit) || limit == null || "0".equals(limit)) {
				limit = "20";
			}
			if (currentUserId == null || "".equals(currentUserId)
					|| "null".equals(currentUserId)
					|| "0".equals(currentUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
			} else {
				User user = userService
						.getById(Integer.parseInt(currentUserId));
				if (user == null) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
					logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				} else {
					// 通知 0：未读 ，1：已读
					Map<String, String> filterMap = new HashMap();
					filterMap.put("limit", limit);
					filterMap.put("lastId", lastId);
					filterMap.put("currentUserId", currentUserId);
					List notifyList = this.photoService.queryNotify(filterMap);
					out.print("{\"state\":0,\"result\":{\"notifyList\":"
							+ JSON.toJSONString(notifyList, true) + "}}");
					logger.error("{\"state\":0,\"result\":{\"notifyList\":"
							+ JSON.toJSONString(notifyList, true) + "}}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("查看用户信息viewUserInfo报错：" + e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 加载用户信息
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-11-10 时间：下午5:29:10
	 */
	public void loadUserInfo() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String userId = request.getParameter("userId");// 要查看的用户
			String currentUserId = request.getParameter("currentUserId");// 要查看的用户
			User user = userService.getById(Integer.parseInt(userId));
			if (userId == null || "".equals(userId) || user == null
					|| "null".equals(userId) || "0".equals(userId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
			} else {
				if (!currentUserId.equals(userId)) {
					// 验证是否已经关注
					UserAttentionFans u = this.userService.isAttention(
							currentUserId, userId);
					if (u != null) {// 已关注
						user.setIsAttention("1");
					} else {// 未关注
						user.setIsAttention("0");
					}
				}
				out.print("{\"state\":0,\"result\":{\"user\":"
						+ JSON.toJSONString(UserUtil.getUser2(user)) + "}}");
				logger.error("{\"state\":0,\"result\":{\"user\":"
						+ JSON.toJSONString(UserUtil.getUser2(user)) + "}}");
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("加载用户信息loadUserInfo报错：" + e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 重新绑定用户别名及登录平台
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-11-20 时间：下午2:06:52
	 */
	public void resetAliasAndPf() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String currentUserId = request.getParameter("currentUserId");// 当前用户
			String alias = request.getParameter("alias");// 推送时的用户别名
			String pf = request.getParameter("pf");// 当前登录平台
			User user = userService.getById(Integer.parseInt(currentUserId));
			if (currentUserId == null || "".equals(currentUserId)
					|| user == null || "null".equals(currentUserId)
					|| "0".equals(currentUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
			} else {
				user.setAlias(alias);
				user.setPf(pf);
				this.userService.updateUser(user);
				out.print("{\"state\":0,\"result\":{\"state\":0}}");
				logger.error("{\"state\":0,\"result\":{\"state\":0}}");
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("重新绑定用户别名及登录平台resetAliasAndPf报错：" + e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 用户获取融云token
	 */
	public void getRongCloudToken() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String currentUserId = request.getParameter("currentUserId");// 当前用户
			User user = userService.getById(Integer.parseInt(currentUserId));
			if (currentUserId == null || "".equals(currentUserId)
					|| user == null || "null".equals(currentUserId)
					|| "0".equals(currentUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
			} else {
				SdkHttpResult result = null;
				try {
					result = ApiHttpClient.getToken(rongCloudAppKey,
							rongCloudSecret, String.valueOf(user.getId()),
							user.getUsername(), user.getHeadPortrait(),
							FormatType.json);
					JSONObject resultJson = new JSONObject(result.toString());
					String code = resultJson.get("code").toString();
					if ("200".equals(code)) {
						JSONObject jsonObject = new JSONObject(resultJson.get(
								"result").toString());
						String token = jsonObject.get("token").toString();
						user.setRongCloudToken(token);
						this.photoService.updateObject(user);
						out.print("{\"state\":0,\"result\":{\"state\":0,\"token\":\""
								+ token + "\"}}");
						logger.error("{\"state\":0,\"result\":{\"state\":0,\"token\":\""
								+ token + "\"}}");
					} else {
						out.print("{\"state\":0,\"result\":{\"state\":1}}");
						logger.error("{\"state\":0,\"result\":{\"state\":1}}");
					}
				} catch (Exception e) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"用户获取融云token异常\"}");
					logger.error("{\"state\":\"1\",\"errorMsg\":\"用户获取融云token异常\"}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("用户获取融云token getRongCloudToken报错：" + e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 检查用户在线状态
	 */
	public void checkOnline() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String userId = request.getParameter("userId");// 要检查的用户Id
			if (userId == null || "".equals(userId) || "null".equals(userId)
					|| "0".equals(userId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
			} else {
				SdkHttpResult result = null;
				try {
					result = ApiHttpClient.checkOnline(rongCloudAppKey,
							rongCloudSecret, userId, FormatType.json);
					JSONObject resultJson = new JSONObject(result.toString());
					String code = resultJson.get("code").toString();
					if ("200".equals(code)) {
						JSONObject jsonObject = new JSONObject(resultJson.get(
								"result").toString());
						String status = jsonObject.get("status").toString();// 在线状态，1为在线，0为不在线
						out.print("{\"state\":0,\"result\":{\"state\":0,\"status\":"
								+ status + "}}");
						logger.error("{\"state\":0,\"result\":{\"state\":0,\"status\":"
								+ status + "}}");
					} else {
						out.print("{\"state\":0,\"result\":{\"state\":1}}");
						logger.error("{\"state\":0,\"result\":{\"state\":1}}");
					}
				} catch (Exception e) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"检查用户在线状态异常\"}");
					logger.error("{\"state\":\"1\",\"errorMsg\":\"检查用户在线状态异常\"}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("用户获取融云token getRongCloudToken报错：" + e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 刷新融云用户信息
	 */
	public void refreshUser() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String currentUserId = request.getParameter("currentUserId");// 当前用户Id
			if (currentUserId == null || "".equals(currentUserId)
					|| "null".equals(currentUserId)
					|| "0".equals(currentUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
			} else {
				User user = userService
						.getById(Integer.parseInt(currentUserId));
				SdkHttpResult result = null;
				try {
					result = ApiHttpClient.refreshUser(rongCloudAppKey,
							rongCloudSecret, String.valueOf(user.getId()),
							user.getUsername(), user.getHeadPortrait(),
							FormatType.json);
					JSONObject resultJson = new JSONObject(result.toString());
					String code = resultJson.get("code").toString();
					if ("200".equals(code)) {
						out.print("{\"state\":0,\"result\":{\"state\":0}}");
						logger.error("{\"state\":0,\"result\":{\"state\":0}}");
					} else {
						out.print("{\"state\":0,\"result\":{\"state\":1}}");
						logger.error("{\"state\":0,\"result\":{\"state\":1}}");
					}
				} catch (Exception e) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"刷新融云用户异常\"}");
					logger.error("{\"state\":\"1\",\"errorMsg\":\"刷新融云用户异常\"}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("用户获取融云token getRongCloudToken报错：" + e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 个人中心-加载点赞、评论、关注通知
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-10-30 时间：上午10:41:25
	 */
	public void loadPraiseOrAttentedOrReplyNotify() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String currentUserId = request.getParameter("currentUserId");// 当前用户
			// lastModified
			String lastId = request.getParameter("lastId");
			// 查询数据数量
			String limit = request.getParameter("limit");
			String type = request.getParameter("type");// 0：点赞，1：评论，2：关注

			if ("".equals(limit) || limit == null || "0".equals(limit)) {
				limit = "20";
			}
			if (currentUserId == null || "".equals(currentUserId)
					|| "null".equals(currentUserId)
					|| "0".equals(currentUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
			} else {
				User user = userService
						.getById(Integer.parseInt(currentUserId));
				if (user == null) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
					logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				} else {
					// 通知 0：未读 ，1：已读
					Map<String, String> filterMap = new HashMap();
					filterMap.put("limit", limit);
					filterMap.put("lastId", lastId);
					filterMap.put("currentUserId", currentUserId);
					filterMap.put("type", type);
					List notifyList = this.photoService
							.loadPraiseOrAttentedOrReplyNotify(filterMap);
					out.print("{\"state\":0,\"result\":{\"notifyList\":"
							+ JSON.toJSONString(notifyList, true) + "}}");
					logger.error("{\"state\":0}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("查看用户信息viewUserInfo报错：" + e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 会话列表----根据id数组获取user对象集合
	 */
	public void loadUsers() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String ids = request.getParameter("ids");// 获取的id
			// lastModified
			String lastId = request.getParameter("lastId");
			// 查询数据数量
			String limit = request.getParameter("limit");

			if ("".equals(limit) || limit == null || "0".equals(limit)) {
				limit = "20";
			}
			if (ids == null || "".equals(ids) || "null".equals(ids)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"用户不存在\"}");
			} else {
				Map<String, String> filterMap = new HashMap();
				filterMap.put("limit", limit);
				filterMap.put("lastId", lastId);
				filterMap.put("ids", ids);
				List userList = this.userService.loadUsers(filterMap);
				out.print("{\"state\":0,\"result\":{\"userList\":"
						+ JSON.toJSONString(userList, true) + "}}");
				logger.error("{\"state\":0}");
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("查看用户信息viewUserInfo报错：" + e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 搜索用户
	 */
	public void searchUser() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String userName = StringUtil.encodingUrl(request.getParameter("userName"));// 用户昵称

			if (userName == null || "".equals(userName)
					|| "null".equals(userName)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请输入用户昵称\"}");
				logger.error("{\"state\":\"1\",\"errorMsg\":\"请输入用户昵称\"}");
			} else {
				Map<String, String> filterMap = new HashMap();
				filterMap.put("userName", userName);
				List userList = this.userService.searchUser(filterMap);
				out.print("{\"state\":0,\"result\":{\"userList\":"
						+ JSON.toJSONString(userList, true) + "}}");
				logger.error("{\"state\":0}");
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			logger.error("搜索用户searchUser报错：" + e);
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

}
