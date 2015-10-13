package cn.zmdx.draft.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
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
import cn.zmdx.draft.entity.Cycle;
import cn.zmdx.draft.entity.CyclePhotoSet;
import cn.zmdx.draft.entity.Photo;
import cn.zmdx.draft.entity.PictureSet;
import cn.zmdx.draft.entity.RankPictureSet;
import cn.zmdx.draft.entity.User;
import cn.zmdx.draft.service.PhotoService;
import cn.zmdx.draft.service.impl.UserServiceImpl;
import cn.zmdx.draft.util.SensitivewordFilter;
import cn.zmdx.draft.util.StringUtil;
import cn.zmdx.draft.util.UserCookieUtil;
import cn.zmdx.draft.util.UserUtil;
import cn.zmdx.draft.util.picCloud.PicCloud;
import cn.zmdx.draft.weixin.api.SnsAPI;
import cn.zmdx.draft.weixin.bean.SnsToken;
import cn.zmdx.draft.weixin.entity.WeiXinUser;

import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ActionSupport;
import com.qcloud.UploadResult;

public class PhotoAction extends ActionSupport {
	Logger logger = Logger.getLogger(PhotoAction.class);
	private PhotoService photoService;
	private UserServiceImpl userService;
	// 上传文件域
	private File[] image;
	// 上传文件类型
	private String[] imageContentType;
	// 封装上传文件名
	private String[] imageFileName;
	public static final int APP_ID_V2 = 10002468;
	public static final String SECRET_ID_V2 = "AKIDo26nbKDLWZA6xpPXzRUaYVPgf5wqqlp6";
	public static final String SECRET_KEY_V2 = "upfmsUJgzOitvj0pCzSy4tV9ihdGeZMV";
	public static final String HEADPICBUCKET = "headpic"; // 空间名
	public static final String ALBUMBUCKET = "album"; // 空间名

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
	public UserServiceImpl getUserService() {
		return userService;
	}
	public void setUserService(UserServiceImpl userService) {
		this.userService = userService;
	}
	/**
	 * 查看个人图集
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-8 时间：上午10:51:54
	 */
	public void queryPersonalPhotos() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		// ServletActionContext.getResponse().setHeader("Cache-Control",
		// "max-age=300");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			// lastModified
			String lastid = request.getParameter("lastId");
			// 查询数据数量
			String limit = request.getParameter("limit");
			String userid = request.getParameter("userId");
			String currentUserId = request.getParameter("currentUserId");
			String width = request.getParameter("w");// 缩放宽度
			if ("".equals(limit) || limit == null || "0".equals(limit)) {
				limit = "10";
			}
			if (userid == null || "".equals(userid) || "null".equals(userid)
					|| "0".equals(userid)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"查看的用户不存在\"}");
			} else {
				Map<String, String> filterMap = new HashMap();
				filterMap.put("limit", limit);
				filterMap.put("lastid", lastid);
				filterMap.put("userid", userid);
				filterMap.put("currentUserId", currentUserId);
				filterMap.put("width", width);
				// List result=new ArrayList();
				List<PictureSet> list = photoService
						.queryPersonalPhotos(filterMap);
				// for (int i = 0; i < list.size(); i++) {
				// PictureSet ps=list.get(i);
				// List<Photo>
				// pList=photoService.queryPhotoByPictureSetId(ps.getId());
				// ps.setPhotoList(pList);
				// result.add(ps);
				// }
				// 图集所属用户信息
				// for (int i = 0; i < list.size(); i++) {
				// PictureSet ps=list.get(i);
				// User user=(User)this.photoService.getObjectById(User.class,
				// ps.getUserid()+"");
				// User u=new User();
				// u.setId(user.getId());
				// u.setHeadPortrait(user.getHeadPortrait());
				// u.setUsername(user.getUsername());
				// ps.setUser(u);
				// result.add(ps);
				// }

				out.print("{\"state\":0,\"result\":{\"photoSet\":"
						+ JSON.toJSONString(list, true) + "}}");
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 查看最新、热门照片集
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-8 时间：上午10:51:54
	 */
	public void queryPhotosWall() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		// ServletActionContext.getResponse().setHeader("Cache-Control",
		// "max-age=300");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			// lastid
			String lastid = request.getParameter("lastId");
			// 查询数据数量
			String limit = request.getParameter("limit");
			// 标示，0查询lastModified之后的数据，1查询lastModified之前的数据
			String category = request.getParameter("category");// 0 新，1 热门
			String currentUserId = request.getParameter("currentUserId");// 当前用户
			String width = request.getParameter("w");// 缩放宽度
			if ("".equals(limit) || limit == null || "0".equals(limit)) {
				limit = "10";
			}
			if ("".equals(lastid) || lastid == null) {
				lastid = "0";
			}

			Map<String, String> filterMap = new HashMap();
			filterMap.put("limit", limit);
			filterMap.put("lastid", lastid);
			filterMap.put("category", category);
			filterMap.put("width", width);
			List list = null;
			List result = new ArrayList();
			if ("1".equals(category)) {// 热门
				list = photoService.queryHotPhotosWall(filterMap);
				// for (int i = 0; i < list.size(); i++) {
				// RankPictureSet ps=(RankPictureSet)list.get(i);
				// List<Photo>
				// pList=photoService.queryPhotoByPictureSetId(ps.getPictureSetId());
				// ps.setPhotoList(pList);
				// result.add(ps);
				// }
				// 图集所属用户信息
				for (int i = 0; i < list.size(); i++) {
					RankPictureSet ps = (RankPictureSet) list.get(i);
					// 验证当前用户是否已赞
					int count = this.photoService.isPraisedPictureSet(
							currentUserId, ps.getId() + "");
					if (count > 0) {// 已赞
						ps.setIsUserPraised("1");
					} else {// 未赞
						ps.setIsUserPraised("0");
					}
					User user = (User) this.photoService.getObjectById(
							User.class, ps.getUserid() + "");
					User u = new User();
					u.setId(user.getId());
					u.setHeadPortrait(user.getHeadPortrait());
					u.setUsername(user.getUsername());
					ps.setUser(u);
					result.add(ps);
				}
			} else if ("0".equals(category)) {// 新
				list = photoService.queryPhotosWall(filterMap);
				// for (int i = 0; i < list.size(); i++) {
				// PictureSet ps=(PictureSet)list.get(i);
				// List<Photo>
				// pList=photoService.queryPhotoByPictureSetId(ps.getId());
				// ps.setPhotoList(pList);
				// result.add(ps);
				// }
				// 图集所属用户信息
				for (int i = 0; i < list.size(); i++) {
					PictureSet ps = (PictureSet) list.get(i);
					// 验证当前用户是否已赞
					int count = this.photoService.isPraisedPictureSet(
							currentUserId, ps.getId() + "");
					if (count > 0) {// 已赞
						ps.setIsUserPraised("1");
					} else {// 未赞
						ps.setIsUserPraised("0");
					}
					User user = (User) this.photoService.getObjectById(
							User.class, ps.getUserid() + "");
					User u = new User();
					u.setId(user.getId());
					u.setHeadPortrait(user.getHeadPortrait());
					u.setUsername(user.getUsername());
					ps.setUser(u);
					result.add(ps);
				}
			}
			out.print("{\"state\":0,\"result\":{\"photoSet\":"
					+ JSON.toJSONString(result, true) + "}}");
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 查看选秀照片墙（最新）
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-8 时间：上午10:51:54
	 */
	public void queryDraftPhotosWall() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		// ServletActionContext.getResponse().setHeader("Cache-Control",
		// "max-age=300");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			// lastid
			String lastid = request.getParameter("lastId");
			// 查询数据数量
			String limit = request.getParameter("limit");
			// 选秀主题周期id
			String themeCycleId = request.getParameter("themeId");
			String currentUserId = request.getParameter("currentUserId");// 当前用户
			String width = request.getParameter("w");// 缩放宽度
			if ("".equals(limit) || limit == null || "0".equals(limit)) {
				limit = "10";
			}
			if (themeCycleId == null || "".equals(themeCycleId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请选择选秀主题\"}");
			} else {
				Map<String, String> filterMap = new HashMap();
				filterMap.put("limit", limit);
				filterMap.put("lastid", lastid);
				filterMap.put("themeCycleId", themeCycleId);
				filterMap.put("width", width);
				List<PictureSet> list = photoService
						.queryDraftPhotosWall(filterMap);

				List result = new ArrayList();
				for (int i = 0; i < list.size(); i++) {
					PictureSet ps = list.get(i);
					// 图集所有照片
					// List<Photo>
					// pList=photoService.queryPhotoByPictureSetId(ps.getId());
					// ps.setPhotoList(pList);
					// 验证当前用户是否已赞
					int count = this.photoService.isPraisedPictureSet(
							currentUserId, ps.getId() + "");
					if (count > 0) {// 已赞
						ps.setIsUserPraised("1");
					} else {// 未赞
						ps.setIsUserPraised("0");
					}
					// 图集评论数
					int comments = photoService.queryCommentByPictureSetId(ps
							.getId());
					ps.setComments(comments);
					// 图集所属用户信息
					User user = (User) this.photoService.getObjectById(
							User.class, ps.getUserid() + "");
					User u = new User();
					u.setId(user.getId());
					u.setHeadPortrait(user.getHeadPortrait());
					u.setUsername(user.getUsername());
					ps.setUser(u);
					result.add(ps);

				}
				// 用户当前主题剩余票数
				Map<String, String> surplusVotesFilterMap = new HashMap<String, String>();
				surplusVotesFilterMap.put("userId", currentUserId);
				surplusVotesFilterMap.put("themeId", themeCycleId);
				int surplusVotes = this.photoService
						.queryUserSurplusVote(surplusVotesFilterMap);
				out.print("{\"state\":0,\"result\":{\"photoSet\":"
						+ JSON.toJSONString(result, true)
						+ ",\"surplusVotes\":\"" + (3 - surplusVotes) + "\"}}");
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 用户上传图片
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:35:13
	 */
	public void uploadPhoto() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		File[] files = getImage();
		int errorcount = 0;
		String[] fileids = new String[9];
		PicCloud pc = new PicCloud(APP_ID_V2, SECRET_ID_V2, SECRET_KEY_V2,
				ALBUMBUCKET);
		try {
			out = ServletActionContext.getResponse().getWriter();
			String type = request.getParameter("type");// 分类，0:个人，1:秀场
			String userid = request.getParameter("currentUserId");
			String descs = request.getParameter("descs");
			descs = StringUtil.encodingUrl(descs);
			String themeCycleId = request.getParameter("themeCycleId");// 选秀主题周期id
			String themeTitle = request.getParameter("themeTitle");// 选秀主题标题

			Map<String, Object> filterMap = new HashMap();
			if (userid == null || "".equals(userid) || "null".equals(userid)
					|| "0".equals(userid)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			} else {
				User user = (User) this.photoService.getObjectById(User.class,
						userid);
				if (themeCycleId != null && !"".equals(themeCycleId)) {
					Map<String, Object> valifilterMap = new HashMap<String, Object>();
					valifilterMap.put("themeCycleId", themeCycleId);
					valifilterMap.put("userId", userid);
					List<?> list = photoService.validateIsAttend(valifilterMap);
					if (list != null && list.size() > 0) {// 参与过
						out.print("{\"state\":\"1\",\"errorMsg\":\"您以参与过此次主题选秀\"}");
					}else{
						Cycle cycle = (Cycle) this.photoService.getObjectById(
								Cycle.class, themeCycleId);
						if ("1".equals(cycle.getIsNeedValidate())
								|| "1".equals(user.getIsvalidate())) {// 不需要真人验证、或者已经通过真人验证
							if (getImage() != null && getImage().length > 0) {
								// 创建图集
								PictureSet ps = new PictureSet();
								ps.setType(type);
								ps.setDescs(descs);
								ps.setUserid(Integer.parseInt(userid));
								ps.setUploadDate(new Date());
								ps.setStatus("1");
								ps.setRank(0);
								if ("1".equals(type)) {
									ps.setThemeCycleId(Integer
											.parseInt(themeCycleId));
								} else {
									ps.setThemeCycleId(Integer.parseInt("0"));
								}
								filterMap.put("pictureSet", ps);

								for (int i = 0; i < files.length; i++) {
									UploadResult result = new UploadResult();
									int ret = pc.Upload(files[i], result);
									if (i > 8) {
										break;
									}
									// int ret=1;
									if (ret != 0) {
										System.out.println(pc.GetError());
										errorcount++;
										break;
									} else {
										Photo photo = new Photo();
										photo.setPhotoUrl(result.download_url);
										photo.setUploadDate(new Date());
										photo.setUserid(Integer
												.parseInt(userid));
										photo.setType(0);// 图集
										photo.setFileid(result.fileid);
										filterMap.put("photo" + i, photo);
										fileids[i] = result.fileid;
									}
								}
								if (errorcount == 0) {
									filterMap.put("count", files.length);
									if (!"1".equals(cycle.getStatus())) {// 选秀非进行中的状态
										// 删除本次所有上传照片
										for (int i = 0; i < fileids.length; i++) {
											if (fileids[i] != null
													&& !"".equals(fileids[i])) {
												int code = pc
														.Delete(fileids[i]);
												System.out.println("删除code："
														+ code);
											}
										}
										out.print("{\"state\":\"1\",\"errorMsg\":\"请选择其它正在进行中的主题活动\"}");
									} else {
										// 图片选秀信息
										CyclePhotoSet cyclePhoto = new CyclePhotoSet();
										cyclePhoto.setThemeCycleId(Integer
												.parseInt(themeCycleId));
										cyclePhoto.setThemeTitle(themeTitle);
										filterMap.put("cyclePhoto", cyclePhoto);
										photoService.uploadPhoto(filterMap);
										out.print("{\"state\":0,\"result\":{\"state\":0}}");
									}
								} else {// 失败删除本次所有上传照片
									for (int i = 0; i < fileids.length; i++) {
										if (fileids[i] != null
												&& !"".equals(fileids[i])) {
											int code = pc.Delete(fileids[i]);
											System.out
													.println("删除code：" + code);
										}
									}
									out.print("{\"state\":\"1\",\"errorMsg\":\"上传失败，请重试\"}");
								}
							} else {
								out.print("{\"state\":\"1\",\"errorMsg\":\"请先选择照片\"}");
							}
						} else {
							out.print("{\"state\":\"1\",\"errorMsg\":\"未通过真人验证\"}");
						}
					}
				} else {
					if (getImage() != null && getImage().length > 0) {
						// 创建图集
						PictureSet ps = new PictureSet();
						ps.setType(type);
						ps.setDescs(descs);
						ps.setUserid(Integer.parseInt(userid));
						ps.setUploadDate(new Date());
						ps.setStatus("1");
						ps.setRank(0);
						if ("1".equals(type)) {
							ps.setThemeCycleId(Integer.parseInt(themeCycleId));
						} else {
							ps.setThemeCycleId(Integer.parseInt("0"));
						}
						filterMap.put("pictureSet", ps);

						for (int i = 0; i < files.length; i++) {
							UploadResult result = new UploadResult();
							int ret = pc.Upload(files[i], result);
							if (i > 8) {
								break;
							}
							// int ret=1;
							if (ret != 0) {
								System.out.println(pc.GetError());
								errorcount++;
								break;
							} else {
								Photo photo = new Photo();
								photo.setPhotoUrl(result.download_url);
								photo.setUploadDate(new Date());
								photo.setUserid(Integer.parseInt(userid));
								photo.setType(0);// 图集
								photo.setFileid(result.fileid);
								filterMap.put("photo" + i, photo);
								fileids[i] = result.fileid;
							}
						}
						if (errorcount == 0) {
							filterMap.put("count", files.length);
							photoService.uploadPhoto(filterMap);
							out.print("{\"state\":0,\"result\":{\"state\":0}}");
						} else {// 失败删除本次所有上传照片
							for (int i = 0; i < fileids.length; i++) {
								if (fileids[i] != null
										&& !"".equals(fileids[i])) {
									int code = pc.Delete(fileids[i]);
									System.out.println("删除code：" + code);
								}
							}
							out.print("{\"state\":\"1\",\"errorMsg\":\"上传失败，请重试\"}");
						}
					} else {
						out.print("{\"state\":\"1\",\"errorMsg\":\"请先选择照片\"}");
					}
				}

			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
			// 删除本次所有上传照片
			for (int i = 0; i < fileids.length; i++) {
				if (fileids[i] != null && !"".equals(fileids[i])) {
					int code = pc.Delete(fileids[i]);
					System.out.println("删除code：" + code);
				}
			}
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 上传真人验证照片
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-30 时间：下午2:16:43
	 */
	public void realityVerification() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			String userId = request.getParameter("currentUserId");
			if (userId == null || "".equals(userId) || "null".equals(userId)
					|| "0".equals(userId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			} else {
				File[] files = getImage();
				if (files != null && files[0] != null) {
					// 上传至万象空间
					PicCloud pc = new PicCloud(APP_ID_V2, SECRET_ID_V2,
							SECRET_KEY_V2, HEADPICBUCKET);
					UploadResult result = new UploadResult();
					int ret = pc.Upload(files[0], result);
					if (ret != 0) {
						System.out.println(pc.GetError());
						out.print("{\"state\":\"1\",\"errorMsg\":\"上传失败，请重试\"}");
					} else {
						// 图片链接
						Photo photo = new Photo();
						photo.setPhotoUrl(result.download_url);
						photo.setUploadDate(new Date());
						photo.setUserid(Integer.parseInt(userId));
						photo.setPictureSetId(0);
						photo.setType(1);// 图集
						photo.setFileid(result.fileid);
						this.photoService.realityVerification(photo, userId);
						out.print("{\"state\":0,\"result\":{\"state\":0}}");
					}
				} else {
					out.print("{\"state\":\"1\",\"errorMsg\":\"请先选择照片\"}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 点赞
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:46:21
	 */
	public void praisePhoto() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			String userid = request.getParameter("currentUserId");
			String pictureSetId = request.getParameter("pictureSetId");
			if (userid == null || "".equals(userid) || "null".equals(userid)
					|| "0".equals(userid)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			} else {
				if (pictureSetId == null || "".equals(pictureSetId)) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"请先选择图集\"}");
				} else {
					String result = photoService.OperationPictureSet(userid,
							pictureSetId, 7);
					if ("failed".equals(result)) {// 已操作
						out.print("{\"state\":0,\"result\":{\"state\":\"1\"}}");
					} else {
						out.print("{\"state\":0,\"result\":{\"state\":\"0\"}}");
					}
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 取消点赞
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:46:21
	 */
	public void cancelPraisePhoto() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			String userid = request.getParameter("currentUserId");
			String pictureSetId = request.getParameter("pictureSetId");
			if (userid == null || "".equals(userid) || "null".equals(userid)
					|| "0".equals(userid)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			} else {
				if (pictureSetId == null || "".equals(pictureSetId)) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"请先选择图集\"}");
				} else {
					String result = photoService.OperationPictureSet(userid,
							pictureSetId, 4);
					if ("failed".equals(result)) {// 已操作
						out.print("{\"state\":0,\"result\":{\"state\":\"1\"}}");
					} else {
						out.print("{\"state\":0,\"result\":{\"state\":\"0\"}}");
					}
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 踩
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:46:21
	 */
	public void treadPhoto() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			String userid = request.getParameter("currentUserId");
			String pictureSetId = request.getParameter("pictureSetId");
			if (userid == null || "".equals(userid) || "null".equals(userid)
					|| "0".equals(userid)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			} else {
				if (pictureSetId == null || "".equals(pictureSetId)) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"请先选择图集\"}");
				} else {
					String result = photoService.OperationPictureSet(userid,
							pictureSetId, 1);
					if ("failed".equals(result)) {// 已操作过
						out.print("{\"state\":0,\"result\":{\"state\":\"1\"}}");
					} else {
						out.print("{\"state\":0,\"result\":{\"state\":\"0\"}}");
					}
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 举报图集
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:46:21
	 */
	public void reportPhoto() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			String userid = request.getParameter("currentUserId");
			String pictureSetId = request.getParameter("pictureSetId");
			if (userid == null || "".equals(userid) || "null".equals(userid)
					|| "0".equals(userid)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			} else {
				if (pictureSetId == null || "".equals(pictureSetId)) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"请先选择图集\"}");
				} else {
					String result = photoService.OperationPictureSet(userid,
							pictureSetId, 2);
					if ("failed".equals(result)) {// 已操作过
						out.print("{\"state\":0,\"result\":{\"state\":\"1\"}}");
					} else {
						out.print("{\"state\":0,\"result\":{\"state\":\"0\"}}");
					}
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 增加浏览量
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-21 时间：下午3:58:36
	 */
	public void viewPhoto() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			String pictureSetId = request.getParameter("pictureSetId");

			if (pictureSetId == null || "".equals(pictureSetId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请先选择图集\"}");
			} else {
				PictureSet ps = (PictureSet) photoService.getObjectById(
						PictureSet.class, pictureSetId);
				ps.setView(ps.getView() + 1);

				photoService.updateObject(ps);

				out.print("{\"state\":0,\"result\":{\"state\":0}}");
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 选秀投票
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-27 时间：上午11:17:18
	 */
	public void vote() {// operation_records
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			String userid = request.getParameter("currentUserId");
			String pictureSetId = request.getParameter("pictureSetId");
			User user = (User) this.photoService.getObjectById(User.class,
					userid);
			if (("".equals(userid) || userid == null) || user == null) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			} else {
				if ("".equals(pictureSetId) || pictureSetId == null) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"请先选择图集\"}");
				} else {
					PictureSet ps = (PictureSet) this.photoService
							.getObjectById(PictureSet.class, pictureSetId);
					int themeId = ps.getThemeCycleId();
					Cycle cycle = (Cycle) this.photoService.getObjectById(
							Cycle.class, String.valueOf(themeId));
					Date currentDate = new Date();
					if (!"1".equals(cycle.getStatus())) {
						out.print("{\"state\":\"1\",\"errorMsg\":\"主题活动结束，投票已停止\"}");
					} else if (cycle.getVoteStartTime().getTime() < currentDate
							.getTime()
							&& cycle.getVoteEndTime().getTime() > currentDate
									.getTime()) {
						Map<String, String> surplusVotesFilterMap = new HashMap<String, String>();
						surplusVotesFilterMap.put("userId", userid);
						surplusVotesFilterMap.put("themeId",
								String.valueOf(ps.getThemeCycleId()));
						int votes = this.photoService
								.queryUserSurplusVote(surplusVotesFilterMap);
						if (votes >= 3) {
							out.print("{\"state\":0,\"result\":{\"state\":\"1\",\"surplusVotes\":\"0\"}}");
						} else {
							String result = photoService.OperationPictureSet(
									userid, pictureSetId, 3);
							out.print("{\"state\":0,\"result\":{\"state\":\"0\",\"surplusVotes\":\""
									+ (3 - votes - 1) + "\"}}");
						}
					} else if (cycle.getVoteStartTime().getTime() > currentDate
							.getTime()) {
						out.print("{\"state\":\"1\",\"errorMsg\":\"投票未开始\"}");
					} else if (cycle.getVoteEndTime().getTime() < currentDate
							.getTime()) {
						out.print("{\"state\":\"1\",\"errorMsg\":\"投票已结束\"}");
					}
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 根据选秀主题周期id查看选秀图集排名
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:46:21
	 */
	public void queryCycleRanking() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			// lastid
			String lastid = request.getParameter("lastId");
			// 查询数据数量
			String limit = request.getParameter("limit");
			// 选秀主题周期id
			String themeCycleId = request.getParameter("themeCycleId");
			String currentUserId = request.getParameter("currentUserId");// 当前用户
			String width = request.getParameter("w");// 缩放宽度
			if ("".equals(limit) || limit == null || "0".equals(limit)) {
				limit = "10";
			}
			Map<String, String> filterMap = new HashMap();
			filterMap.put("themeCycleId", themeCycleId);
			filterMap.put("lastid", lastid);
			filterMap.put("limit", limit);
			filterMap.put("width", width);

			if ("".equals(themeCycleId) || themeCycleId == null) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请先选择选秀周期\"}");
			} else {
				List list = photoService.queryCycleRanking(filterMap);

				List result = new ArrayList();
				for (int i = 0; i < list.size(); i++) {
					RankPictureSet ps = (RankPictureSet) list.get(i);
					// 图集所有图片
					// List<Photo>
					// pList=photoService.queryPhotoByPictureSetId(ps.getPictureSetId());
					// ps.setPhotoList(pList);
					// 验证当前用户是否已赞
					int count = this.photoService.isPraisedPictureSet(
							currentUserId, ps.getPictureSetId() + "");
					if (count > 0) {// 已赞
						ps.setIsUserPraised("1");
					} else {// 未赞
						ps.setIsUserPraised("0");
					}
					// 图集评论数
					int comments = photoService.queryCommentByPictureSetId(ps
							.getId());
					ps.setComments(comments);
					// 图集所属用户信息
					User user = (User) this.photoService.getObjectById(
							User.class, ps.getUserid() + "");
					User u = new User();
					u.setId(user.getId());
					u.setHeadPortrait(user.getHeadPortrait());
					u.setUsername(user.getUsername());
					ps.setUser(u);
					result.add(ps);
				}

				// 图集所属用户信息
				// for (int i = 0; i < list.size(); i++) {
				// PictureSet ps=(PictureSet)list.get(i);
				// User user=(User)this.photoService.getObjectById(User.class,
				// ps.getUserid()+"");
				// User u=new User();
				// u.setId(user.getId());
				// u.setHeadPortrait(user.getHeadPortrait());
				// u.setUsername(user.getUsername());
				// ps.setUser(u);
				// result.add(ps);
				// }
				// 用户当前主题剩余票数
				Map<String, String> surplusVotesFilterMap = new HashMap<String, String>();
				surplusVotesFilterMap.put("userId", currentUserId);
				surplusVotesFilterMap.put("themeId", themeCycleId);
				int surplusVotes = this.photoService
						.queryUserSurplusVote(surplusVotesFilterMap);
				out.print("{\"state\":0,\"result\":{\"photoSet\":"
						+ JSON.toJSONString(result, true)
						+ ",\"surplusVotes\":\"" + (3 - surplusVotes) + "\"}}");
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 根据选秀主题周期id查看选秀用户排名
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：上午10:46:21
	 */
	public void queryUserCycleRanking() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			// lastid
			String lastid = request.getParameter("lastId");
			// 查询数据数量
			String limit = request.getParameter("limit");
			// 选秀主题周期id
			String themeCycleId = request.getParameter("themeCycleId");
			String currentUserId = request.getParameter("currentUserId");// 当前用户
			if ("".equals(limit) || limit == null || "0".equals(limit)) {
				limit = "10";
			}
			if ("".equals(themeCycleId) || themeCycleId == null) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请先选择选秀周期\"}");
			} else {
				Map<String, String> filterMap = new HashMap();
				filterMap.put("themeCycleId", themeCycleId);
				filterMap.put("lastid", lastid);
				filterMap.put("limit", limit);
				List list = photoService.queryUserCycleRanking(filterMap);

				// List result=new ArrayList();
				// for (int i = 0; i < list.size(); i++) {
				// RankPictureSet ps=(RankPictureSet)list.get(i);
				// List<Photo>
				// pList=photoService.queryPhotoByPictureSetId(ps.getPictureSetId());
				// ps.setPhotoList(pList);
				// result.add(ps);
				// }
				// 用户当前主题剩余票数
				Map<String, String> surplusVotesFilterMap = new HashMap<String, String>();
				surplusVotesFilterMap.put("userId", currentUserId);
				surplusVotesFilterMap.put("themeId", themeCycleId);
				int surplusVotes = this.photoService
						.queryUserSurplusVote(surplusVotesFilterMap);
				out.print("{\"state\":0,\"result\":{\"user\":"
						+ JSON.toJSONString(list, true)
						+ ",\"surplusVotes\":\"" + (3 - surplusVotes) + "\"}}");
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 获取所有选秀主题
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：下午5:29:08
	 */
	public void queryThemes() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			Map<String, Object> filterMap = new HashMap();
			List list = photoService.queryThemes(filterMap);

			out.print("{\"state\":0,\"result\":{\"themeCycle\":"
					+ JSON.toJSONString(list, true) + "}}");
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 根据主题id获取相关选秀周期 （作废）
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-9 时间：下午5:29:08
	 */
	/**
	 * public void queryCycleByThemesId(){
	 * ServletActionContext.getResponse().setContentType(
	 * "text/json; charset=utf-8"); HttpServletRequest request=
	 * ServletActionContext.getRequest(); PrintWriter out = null ; try{ out =
	 * ServletActionContext.getResponse().getWriter();
	 * 
	 * String themeId=request.getParameter("themeId"); String
	 * flag=request.getParameter("flag");//0:未开始，1:进行中，2:已结束 Map<String, Object>
	 * filterMap = new HashMap(); filterMap.put("themeId", themeId);
	 * filterMap.put("flag", flag); List
	 * list=photoService.queryCycleByThemesId(filterMap);
	 * 
	 * out.print("{\"state\":0,\"result\":"+JSON.toJSONString(list, true)+"}");
	 * }catch (Exception e) {
	 * out.print("{\"state\":\"2\",\"errorCode\":\""+e.getMessage
	 * ()+"\",\"errorMsg\":\"系统异常\"}"); e.printStackTrace(); logger.error(e);
	 * }finally{ out.flush(); out.close(); } }
	 */
	/**
	 * 验证用户是否参与此次选秀
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-20 时间：下午12:26:33
	 * @param filterMap
	 * @return
	 */
	public void validateQualification() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			// int i =Integer.parseInt("s");
			String themeCycleId = request.getParameter("themeCycleId");// 周期id
			String userId = request.getParameter("currentUserId");// 用户id
			if (userId == null || "".equals(userId) || "null".equals(userId)
					|| "0".equals(userId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			} else {
				if (themeCycleId == null || "".equals(themeCycleId)) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"请先选择选秀主题\"}");
				} else {
					User user = (User) this.photoService.getObjectById(
							User.class, userId);
					if (!"1".equals(user.getIsvalidate())) {// 未通过真人验证
						out.print("{\"state\":0,\"result\":{\"state\":\"2\"}}");
					} else {
						Map<String, Object> filterMap = new HashMap<String, Object>();
						filterMap.put("themeCycleId", themeCycleId);
						filterMap.put("userId", userId);
						List<?> list = photoService.validateIsAttend(filterMap);
						if (list != null && list.size() > 0) {// 参与过
							out.print("{\"state\":0,\"result\":{\"state\":\"1\"}}");
						} else {// 未参与
							out.print("{\"state\":0,\"result\":{\"state\":\"0\"}}");
						}
					}
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 发表评论
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-22 时间：上午11:49:09
	 */
	public void replyComment() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			request.setCharacterEncoding("utf-8");
			String pictureSetId = request.getParameter("pictureSetId");
			String content = StringUtil.encodingUrl(request
					.getParameter("content"));
			// 敏感词过滤
			SensitivewordFilter sf = new SensitivewordFilter();
			content = sf.replaceSensitiveWord(content, 1, "*");
			String parentUserId = request.getParameter("userId");
			String userId = request.getParameter("currentUserId");
			if (pictureSetId == null || "".equals(pictureSetId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请先选择图集\"}");
			} else {
				if (userId == null || "".equals(userId)
						|| "null".equals(userId) || "0".equals(userId)) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
				} else {
					if (content == null || "".equals(content)) {
						out.print("{\"state\":\"1\",\"errorMsg\":\"评论不能为空\"}");
					} else {
						Comment comment = new Comment();
						comment.setContent(content);
						if (!"".equals(parentUserId) && parentUserId != null) {
							comment.setParentUserId(Integer
									.parseInt(parentUserId));
						}
						comment.setPictureSetId(Integer.parseInt(pictureSetId));
						comment.setUserId(Integer.parseInt(userId));
						comment.setDatetime(new Date());
						this.photoService.saveComment(comment);
						out.print("{\"state\":0,\"result\":{\"commentId\":"
								+ comment.getId() + "}}");
					}
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 加载图片评论
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-22 时间：下午12:50:14
	 */
	public void loadComment() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String pictureSetId = request.getParameter("pictureSetId");
			String lastId = request.getParameter("lastId");
			String limit = request.getParameter("limit");
			if (pictureSetId == null || "".equals(pictureSetId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请先选择图集\"}");
			} else {
				if ("".equals(limit) || limit == null) {
					limit = "10";
				}
				Map<String, Object> filterMap = new HashMap();
				filterMap.put("pictureSetId", pictureSetId);
				filterMap.put("lastId", lastId);
				filterMap.put("limit", limit);
				List list = this.photoService.queryComment(filterMap);
				out.print("{\"state\":0,\"result\":{\"comments\":"
						+ JSON.toJSONString(list, true) + "}}");
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}
	/**
	 * 加载审批记录
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-7-31 时间：下午12:33:42
	 * @throws IOException
	 */
	public void queryReviewRecords() throws IOException {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		try {
			// String type = request.getParameter("type");
			String type = "1";
			String userId = request.getParameter("currentUserId");
			String pictureSetId = request.getParameter("pictureSetId");
			if ((userId == null || "".equals(userId))
					&& (pictureSetId == null || "".equals(pictureSetId))) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"数据不存在\"}");
			} else {
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
				List list = photoService.queryReviewRecords(filterMap);
				out.print("{\"state\":0,\"result\":{\"reviewRecords\":"
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
	 * 举报用户
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-8-10 时间：下午3:09:50
	 */
	public void reportUser() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			String currentUserId = request.getParameter("currentUserId");// 当前用户
			String beingInformerId = request.getParameter("userId");// 被举报用户id
			if (currentUserId == null || "".equals(currentUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			} else {
				if (beingInformerId == null || "".equals(beingInformerId)) {
					out.print("{\"state\":\"1\",\"errorMsg\":\"查看的用户不存在\"}");
				} else {
					Map<String, String> filterMap = new HashMap();
					filterMap.put("currentUserId", currentUserId);
					filterMap.put("beingInformerId", beingInformerId);
					photoService.reportUser(filterMap);
					out.print("{\"state\":0,\"result\":{\"state\":0}}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 发现照片集
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-8-14 时间：下午12:04:17
	 */
	public void discoverPictureSet() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			// 查询数据数量
			String limit = request.getParameter("limit");
			String width = request.getParameter("w");// 缩放宽度
			if ("".equals(limit) || limit == null || "0".equals(limit)) {
				limit = "10";
			}
			Map<String, String> filterMap = new HashMap();
			filterMap.put("limit", limit);
			filterMap.put("width", width);
			List list = photoService.discoverPictureSet(filterMap);

			List result = new ArrayList();
			// for (int i = 0; i < list.size(); i++) {
			// PictureSet ps=(PictureSet)list.get(i);
			// List<Photo>
			// pList=photoService.queryPhotoByPictureSetId(ps.getId());
			// ps.setPhotoList(pList);
			// result.add(ps);
			// }
			// 图集所属用户信息
			for (int i = 0; i < list.size(); i++) {
				PictureSet ps = (PictureSet) list.get(i);
				User user = (User) this.photoService.getObjectById(User.class,
						ps.getUserid() + "");
				User u = new User();
				u.setId(user.getId());
				u.setHeadPortrait(user.getHeadPortrait());
				u.setUsername(user.getUsername());
				ps.setUser(u);
				result.add(ps);
			}
			out.print("{\"state\":0,\"result\":{\"photoSet\":"
					+ JSON.toJSONString(result, true) + "}}");
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 查看图集及加载评论
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-8-17 时间：上午10:52:05
	 */
	public void viewPictureSet() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			// 图集id
			String id = request.getParameter("pictureSetId");
			String currentUserId = request.getParameter("currentUserId");

			if (id == null || "".equals(id)) {
				out.print("{\"state\":1,\"errorMsg\":\"请选择图集\"}");
			} else {
				PictureSet ps = (PictureSet) this.photoService.getObjectById(
						PictureSet.class, id);
				if (ps != null) {
					// 图集所有图片
					List pList = photoService.queryPhotoByPictureSetId(ps
							.getId());
					ps.setPhotoList(pList);
					// 验证是否已点赞
					int count = this.photoService.isPraisedPictureSet(
							currentUserId, ps.getId() + "");
					if (count > 0) {// 已赞
						ps.setIsUserPraised("1");
					} else {// 未赞
						ps.setIsUserPraised("0");
					}
					// 加载图集点赞人
					Map<String, String> praiseFilterMap = new HashMap();
					praiseFilterMap.put("pictureSetId", id);
					praiseFilterMap.put("limit", "7");
					List praiseList = this.photoService
							.queryPraiseUsers(praiseFilterMap);
					// 图集所属用户
					User user = (User) this.photoService.getObjectById(
							User.class, String.valueOf(ps.getUserid()));
					ps.setUser(UserUtil.getUser(user));
					ps.setPraiseUserList(praiseList);
					// 加载评论
					Map<String, Object> filterMap = new HashMap();
					filterMap.put("pictureSetId", id);
					filterMap.put("limit", 20);
					List list = this.photoService.queryComment(filterMap);
					List commList = new ArrayList();
					for (int i = 0; i < list.size(); i++) {
						Comment comment = (Comment) list.get(i);
						User user1 = (User) this.photoService
								.getObjectById(User.class,
										String.valueOf(comment.getUserId()));
						comment.setUser(UserUtil.getUser(user1));
						commList.add(comment);
					}
					out.print("{\"state\":0,\"result\":{\"photoSet\":"
							+ JSON.toJSONString(ps) + ",\"comments\":"
							+ JSON.toJSONString(commList, true) + "}}");
				} else {
					out.print("{\"state\":1,\"errorMsg\":\"图集不存在\"}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 选秀详情（最热、全部、排名）
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-8-26 时间：上午10:33:57
	 */
	public void loadCycleInfo() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			// lastid
			String lastid = request.getParameter("lastId");
			// 查询数据数量
			String limit = request.getParameter("limit");
			// 选秀主题周期id
			String themeCycleId = request.getParameter("themeCycleId");
			String currentUserId = request.getParameter("currentUserId");// 当前用户
			String width = request.getParameter("w");// 缩放宽度
			if ("".equals(limit) || limit == null || "0".equals(limit)) {
				limit = "10";
			}
			if ("".equals(themeCycleId) || themeCycleId == null) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请先选择选秀周期\"}");
			} else {
				Map<String, String> filterMap = new HashMap();
				filterMap.put("themeCycleId", themeCycleId);
				filterMap.put("lastid", lastid);
				filterMap.put("limit", limit);
				filterMap.put("width", width);
				// 验证用户是否参加此次主题
				Map<String, Object> vfilterMap = new HashMap<String, Object>();
				vfilterMap.put("themeCycleId", themeCycleId);
				vfilterMap.put("userId", currentUserId);
				List<?> vlist = photoService.validateIsAttend(vfilterMap);
				String isvalidate = "0";
				if (vlist != null && vlist.size() > 0) {// 参与过
					isvalidate = "1";
				} else {// 未参与
					isvalidate = "0";
				}
				// 选秀最新图集
				List<PictureSet> list = photoService
						.queryDraftPhotosWall(filterMap);
				List result = new ArrayList();
				for (int i = 0; i < list.size(); i++) {
					PictureSet ps = list.get(i);
					// 图集所有照片
					// List<Photo>
					// pList=photoService.queryPhotoByPictureSetId(ps.getId());
					// ps.setPhotoList(pList);
					// 验证当前用户是否已赞
					int count = this.photoService.isPraisedPictureSet(
							currentUserId, ps.getId() + "");
					if (count > 0) {// 已赞
						ps.setIsUserPraised("1");
					} else {// 未赞
						ps.setIsUserPraised("0");
					}
					// 图集评论数
					int comments = photoService.queryCommentByPictureSetId(ps
							.getId());
					ps.setComments(comments);
					// 图集所属用户信息
					User user = (User) this.photoService.getObjectById(
							User.class, ps.getUserid() + "");
					User u = new User();
					u.setId(user.getId());
					u.setHeadPortrait(user.getHeadPortrait());
					u.setUsername(user.getUsername());
					ps.setUser(u);
					result.add(ps);
				}
				// 图集排名
				List photoSetList = photoService.queryCycleRanking(filterMap);
				List photoSetResult = new ArrayList();
				for (int i = 0; i < photoSetList.size(); i++) {
					RankPictureSet ps = (RankPictureSet) photoSetList.get(i);
					// 图集所有图片
					// List<Photo>
					// pList=photoService.queryPhotoByPictureSetId(ps.getPictureSetId());
					// ps.setPhotoList(pList);
					// 验证当前用户是否已赞
					int count = this.photoService.isPraisedPictureSet(
							currentUserId, ps.getPictureSetId() + "");
					if (count > 0) {// 已赞
						ps.setIsUserPraised("1");
					} else {// 未赞
						ps.setIsUserPraised("0");
					}
					// 图集评论数
					int comments = photoService.queryCommentByPictureSetId(ps
							.getId());
					ps.setComments(comments);
					// 图集所属用户信息
					User user = (User) this.photoService.getObjectById(
							User.class, ps.getUserid() + "");
					User u = new User();
					u.setId(user.getId());
					u.setHeadPortrait(user.getHeadPortrait());
					u.setUsername(user.getUsername());
					ps.setUser(u);
					photoSetResult.add(ps);
				}
				// 用户排名
				List userlist = photoService.queryUserCycleRanking(filterMap);

				// 用户当前主题剩余票数
				Map<String, String> surplusVotesFilterMap = new HashMap<String, String>();
				surplusVotesFilterMap.put("userId", currentUserId);
				surplusVotesFilterMap.put("themeId", themeCycleId);
				int surplusVotes = this.photoService
						.queryUserSurplusVote(surplusVotesFilterMap);
				out.print("{\"state\":0,\"result\":{\"psList\":"
						+ JSON.toJSONString(photoSetResult, true)
						+ ",\"psRank\":" + JSON.toJSONString(result, true)
						+ ",\"userRank\":" + JSON.toJSONString(userlist, true)
						+ ",\"isUserAttented\":\"" + isvalidate
						+ "\",\"surplusVotes\":\"" + (3 - surplusVotes)
						+ "\"}}");
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 加载图集点赞人员列表
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-9-2 时间：下午3:17:17
	 */
	public void loadPraiseUserList() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			// 图集id
			String id = request.getParameter("pictureSetId");
			String limit = request.getParameter("limit");
			String lastId = request.getParameter("lastId");

			if (id == null || "".equals(id)) {
				out.print("{\"state\":1,\"errorMsg\":\"请选择图集\"}");
			} else {
				if (limit != null && !"".equals(limit)) {
					limit = "20";
				}
				// 加载图集点赞人
				Map<String, String> praiseFilterMap = new HashMap();
				praiseFilterMap.put("pictureSetId", id);
				praiseFilterMap.put("limit", limit);
				praiseFilterMap.put("lastId", lastId);
				List praiseList = this.photoService
						.queryPraiseUsers(praiseFilterMap);
				out.print("{\"state\":0,\"result\":{\"praiseUsers\":"
						+ JSON.toJSONString(praiseList, true) + "}}");
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 删除图集
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-9-10 时间：下午5:34:20
	 */
	public void deletePictureSet() {
		ServletActionContext.getResponse().setContentType(
				"text/json; charset=utf-8");
		HttpServletRequest request = ServletActionContext.getRequest();
		PrintWriter out = null;
		try {
			out = ServletActionContext.getResponse().getWriter();
			// 图集id
			String pictureSetIds = request.getParameter("pictureSetIds");
			String userId = request.getParameter("userId");
			String currentUserId = request.getParameter("currentUserId");

			if (currentUserId == null || "".equals(currentUserId)) {
				out.print("{\"state\":1,\"errorMsg\":\"请重新登录\"}");
			} else {
				// 加载图集点赞人
				Map<String, String> filterMap = new HashMap();
				filterMap.put("pictureSetIds", pictureSetIds);
				this.photoService.deletePictureSet(filterMap);
				out.print("{\"state\":0,\"result\":{\"state\":0}}");
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}

	public String loadThemeCycle() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String themeCycleId = request.getParameter("themeCycleId");
		Cycle cycle = (Cycle) this.photoService.getObjectById(Cycle.class,
				themeCycleId);
		request.setAttribute("cycle", cycle);
		return "toLoadThemeCycle";
	}

	/**
	 * 加载通知
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-9-19 时间：下午2:48:50
	 */
	public void loadNotify() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String currentUserId = request.getParameter("currentUserId");// 当前用户id
			String lastId = request.getParameter("lastId");
			String limit = request.getParameter("limit");
			if (currentUserId == null || "".equals(currentUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			} else {
				if ("".equals(limit) || limit == null) {
					limit = "10";
				}
				Map<String, String> filterMap = new HashMap();
				filterMap.put("currentUserId", currentUserId);
				filterMap.put("lastId", lastId);
				filterMap.put("limit", limit);
				List list = this.photoService.queryNotify(filterMap);
				out.print("{\"state\":0,\"result\":{\"notifyList\":"
						+ JSON.toJSONString(list, true) + "}}");
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 将通知改为已读状态
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-9-19 时间：下午4:11:43
	 */
	public void readNotify() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String currentUserId = request.getParameter("currentUserId");// 当前用户id
			if (currentUserId == null || "".equals(currentUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			} else {
				Map<String, String> filterMap = new HashMap();
				filterMap.put("currentUserId", currentUserId);
				int count = this.photoService.readNotify(filterMap);
				if (count > 0) {
					out.print("{\"state\":0,\"result\":{\"state\":0}}");
				} else {
					out.print("{\"state\":0,\"result\":{\"state\":1}}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 删除评论
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-9-19 时间：下午8:50:42
	 */
	public void deleteComment() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String currentUserId = request.getParameter("currentUserId");// 当前用户id
			String id = request.getParameter("id");// 当前用户id
			if (currentUserId == null || "".equals(currentUserId)) {
				out.print("{\"state\":\"1\",\"errorMsg\":\"请重新登录\"}");
			} else {
				Map<String, String> filterMap = new HashMap();
				filterMap.put("currentUserId", currentUserId);
				filterMap.put("id", id);
				int count = this.photoService.deleteComment(filterMap);
				if (count > 0) {
					out.print("{\"state\":0,\"result\":{\"state\":0}}");
				} else {
					out.print("{\"state\":0,\"result\":{\"state\":1}}");
				}
			}
		} catch (Exception e) {
			out.print("{\"state\":\"2\",\"errorCode\":\"" + e.getMessage()
					+ "\",\"errorMsg\":\"系统异常\"}");
			e.printStackTrace();
			logger.error(e);
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 加载图集信息
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-9-29 时间：上午11:31:20
	 * @return
	 */
	public String loadPictureSet() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String pictureSetId = request.getParameter("pictureSetId");
		String themeId = request.getParameter("themeId");
		String code = request.getParameter("code");
		if (code != null && !"".equals(code)) {
			logger.error("code****" + code + "****");
			String secret = "9d992669fdd0cadc51110e20571be7e9";
			String appid = "wx81b0d978030f90aa";
			SnsToken snsToken = SnsAPI.oauth2AccessToken(appid, secret, code);
			logger.error("Openid****" + snsToken.getOpenid() + "****");
			// 验证是否已注册
			User currentUser = this.userService.validateThirdPartyUser(
					snsToken.getOpenid(), "weixin");
			if (currentUser != null) {// 已存在用户信息
				request.setAttribute("currentUser", currentUser);
				request.getSession().setAttribute("currentUserId",
						currentUser.getId());
			} else {
				logger.error("Access_token****" + snsToken.getAccess_token()
						+ "****");
				WeiXinUser weixinUser = new WeiXinUser();
				weixinUser = SnsAPI.userinfo(snsToken.getAccess_token(),
						snsToken.getOpenid(), "zh_CN");
				logger.error("Openid****" + snsToken.getOpenid() + "****");
				logger.error("weixinUser****" + weixinUser.getNickname()
						+ "****");
				if (weixinUser.getNickname() != null
						&& !"".equals(weixinUser.getNickname())) {
					User newUser = new User();
					newUser.setUsername(weixinUser.getNickname());
					if (!"".equals(weixinUser.getHeadimgurl())
							&& weixinUser.getHeadimgurl() != null) {
						newUser.setHeadPortrait(weixinUser.getHeadimgurl());
					} else {
						newUser.setHeadPortrait("http://headpic-10002468.image.myqcloud.com/d4fa3046-b2dc-49d1-9cf6-62d3c7fc9bc0");
					}
					newUser.setGender(weixinUser.getSex() == null
							? 0
							: weixinUser.getSex());
					newUser.setIntroduction("");
					newUser.setLoginname("");
					newUser.setPassword("");
					newUser.setFlag("1");
					newUser.setIsvalidate("0");
					newUser.setAge(0);
					newUser.setRegistrationDate(new Date());
					newUser.setOrgId(0);
					newUser.setThirdParty("weixin");
					newUser.setUid(snsToken.getOpenid());
					this.photoService.saveEntity(newUser);
					request.setAttribute("currentUser", newUser);
					request.getSession().setAttribute("currentUserId",
							newUser.getId());
				}
			}
		}
		PictureSet pictureSet = null;
		List pList = new ArrayList();
		if (pictureSetId != null && !"".equals(pictureSetId)) {
			pictureSet = (PictureSet) this.photoService.getObjectById(
					PictureSet.class, pictureSetId);
			if (pictureSet != null) {
				// 图集所属用户
				User user = (User) this.photoService.getObjectById(User.class,
						String.valueOf(pictureSet.getUserid()));
				pictureSet.setUser(UserUtil.getUser(user));
				// 图集所有图片
				pList = photoService.queryPhotoByPictureSetId(pictureSet
						.getId());
			}
		}
		request.setAttribute("pictureSet", pictureSet);
		if (themeId != null && !"".equals(themeId) && !"0".equals(themeId)) {
			Cycle cycle = (Cycle) this.photoService.getObjectById(Cycle.class,
					themeId);
			request.setAttribute("themeId", themeId);
			request.setAttribute("cycle", cycle);
		}
		request.setAttribute("photos", pList);
		request.setAttribute("code", code);
		return "toLoadPictureSet";
	}
	/**
	 * 测试上传数据
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-8-28 时间：下午2:39:15
	 */
	/**
	 * public void testUpload(){ HttpServletRequest request =
	 * ServletActionContext.getRequest(); HttpServletResponse response =
	 * ServletActionContext.getResponse();
	 * response.setContentType("text/json; charset=utf-8"); PrintWriter out =
	 * null; try { out = response.getWriter(); String
	 * urlString="/Users/fanpengcheng/Downloads/pic/"; PicCloud pc = new
	 * PicCloud(APP_ID_V2, SECRET_ID_V2, SECRET_KEY_V2, BUCKET); for (int i = 1;
	 * i <= 24; i++) { Map<String, Object> filterMap = new HashMap();// 创建图集
	 * String type =String.valueOf((int) (Math.round(Math.random()))); int
	 * userid=(int) (Math.round(Math.random() * 15 ))+1; int themeCycleId=(int)
	 * (Math.round(Math.random() * 10 ))+1; PictureSet ps = new PictureSet();
	 * ps.setType(String.valueOf((int) (Math.round(Math.random()))));
	 * ps.setDescs(i+"数据"+i+"测试"+i); ps.setUserid(userid);//随机用户id
	 * ps.setUploadDate(new Date()); ps.setStatus("1"); ps.setPraise((int)
	 * (Math.round(Math.random() * 80 ))); if ("1".equals(type)) {
	 * ps.setThemeCycleId(themeCycleId); } else { ps.setThemeCycleId(0); }
	 * filterMap.put("pictureSet", ps); int errorcount = 0; String[] fileids =
	 * new String[9]; int len=0; for (int j = 4; j <= 5; j++) {//图片编号 变化 File
	 * file =new File(urlString+i+"-"+j+".jpg"); if(file.exists()){ len++;
	 * UploadResult result = new UploadResult(); Date starttime=new Date(); int
	 * ret = pc.Upload(file, result); Date endtime=new Date();
	 * System.out.println("上传用时："+(endtime.getTime()-starttime.getTime())+"毫秒");
	 * // int ret=0; // if (i > 8) { // break; // } // int ret=1; if (ret != 0)
	 * { errorcount++; break; } else { Photo photo = new Photo();
	 * photo.setPhotoUrl(result.download_url); photo.setUploadDate(new Date());
	 * photo.setUserid(userid); photo.setType(0);// 图集
	 * photo.setFileid(result.fileid); filterMap.put("photo" + (j-4), photo);
	 * fileids[j-4] = result.fileid; } } } if (errorcount == 0) {
	 * filterMap.put("count", len); if ("1".equals(type)) {// 选秀 Cycle cycle =
	 * (Cycle) this.photoService .getObjectById(Cycle.class,
	 * String.valueOf(themeCycleId)); if (!"1".equals(cycle.getStatus())) {//
	 * 选秀非进行中的状态 // 删除本次所有上传照片 for (int ii = 0; ii < fileids.length; ii++) { if
	 * (fileids[ii] != null && !"".equals(fileids[ii])) { int code =
	 * pc.Delete(fileids[ii]); System.out.println("删除code：" + code); } } //
	 * out.print("{\"state\":\"1\",\"errorMsg\":\"请选择其它正在进行中的主题活动\"}"); } else {
	 * // 图片选秀信息 CyclePhotoSet cyclePhoto = new CyclePhotoSet();
	 * cyclePhoto.setThemeCycleId(themeCycleId); //
	 * cyclePhoto.setThemeTitle(themeTitle); filterMap.put("cyclePhoto",
	 * cyclePhoto); photoService.uploadPhoto(filterMap); //
	 * out.print("{\"state\":0}"); } } else {
	 * photoService.uploadPhoto(filterMap); // out.print("{\"state\":0}"); } }
	 * else {// 失败删除本次所有上传照片 for (int ii = 0; ii < fileids.length; ii++) { if
	 * (fileids[ii] != null && !"".equals(fileids[ii])) { int code =
	 * pc.Delete(fileids[ii]); System.out.println("删除code：" + code); } } //
	 * out.print("{\"state\":\"1\",\"errorMsg\":\"上传失败，请重试\"}"); } }
	 * out.print("{\"state\":0}"); } catch (IOException e) {
	 * e.printStackTrace();
	 * out.print("{\"state\":\"1\",\"errorMsg\":\"上传失败，请重试\"}"); }finally{
	 * out.flush(); out.close(); } }
	 */
}
