/**
 * <p>文件名: LoginFilter.java</p>
 * <p>版权声明: Copyright &copy; 2014-2015 智美点心科技</p>
 * <p>创建者: 张加宁</p>
 * <p>创建时间: 2014-11-03  下午03:08:22</p>
 */
package cn.zmdx.draft.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import cn.zmdx.draft.entity.User;
import cn.zmdx.draft.service.impl.UserServiceImpl;
import cn.zmdx.draft.util.Sha1;
import cn.zmdx.draft.util.UserCookieUtil;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * 验证是否登录
 * 
 * @author 张加宁
 */
public class LoginFilter extends HttpServlet implements Filter {

	private static final long serialVersionUID = 1L;
	private String encoding; // 字符编码
	private String ignore; // 验证开关
	private String[] ignoreList; // 验证url数组
	private final static String cookieKey = "draftServer";// 项目名称，保存cookie时的KEY值
	

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding(encoding);
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();

		String reqURI = httpRequest.getRequestURI();
		String reqContextPath = httpRequest.getContextPath();

		User user = (User) session.getAttribute("user");
		// 如果需要验证
		if (ignore != null && ignore.equalsIgnoreCase("false")) {
			if (ignoreList != null && ignoreList.length > 0
					&& isHave(reqURI, reqContextPath, ignoreList)) {
				// 免过滤，放行
				chain.doFilter(request, response);
			} else {
				// if(user==null){
//				UserCookieUtil.clearCookie(httpResponse);
				Cookie[] cookies = httpRequest.getCookies();// 取cookie值
				if (cookies != null) {
					String cookieValue = null;
					// 下面是找到本项目的cookie
					for (int i = 0; i < cookies.length; i++) {
						if (cookieKey.equals(cookies[i].getName())) {
							cookieValue = cookies[i].getValue();
							break;
						}
					}
					// 如果cookieValue为空 说明用户上次没有选择“记住下次登录”执行其他
					if (cookieValue == null) {
						//httpResponse.sendRedirect(reqContextPath + "/");// 跳转至登录页面
						httpResponse.getWriter().print("{\"state\":\"failed\",\"errorMsg\":\"please log in\"}");
					} else {
						// 先得到的CookieValue进行Base64解码
						String cookieValueAfterDecode = new String(
								Base64.decode(cookieValue), "utf-8");
						// 对解码后的值进行分拆,得到一个数组,如果数组长度不为3,就是非法登陆
						String cookieValues[] = cookieValueAfterDecode
								.split(":");
						if (cookieValues.length != 3) {
							//httpResponse.sendRedirect(reqContextPath + "/");// 跳转至登录页面
							httpResponse.getWriter().print("{\"state\":\"failed\",\"errorMsg\":\"please log in\"}");
						}
						// 判断是否在有效期内,过期就删除Cookie
						long validTimeInCookie = new Long(cookieValues[1]);
						if (validTimeInCookie < System.currentTimeMillis()) {
							// 删除Cookie
							UserCookieUtil.clearCookie(httpResponse);
							//httpResponse.sendRedirect(reqContextPath + "/");// 跳转至登录页面
							httpResponse.getWriter().print("{\"state\":\"failed\",\"errorMsg\":\"please log in\"}");
						}
						// 取出cookie中的用户名,并到数据库中检查这个用户名,
						String loginname = cookieValues[0];
						
						BeanFactory beans = WebApplicationContextUtils.getWebApplicationContext(httpRequest.getSession().getServletContext());
						UserServiceImpl userService=(UserServiceImpl)beans.getBean("userService");
//						UserServiceImpl userService=(UserServiceImpl)factory.getBean("userService");
						
						User temp = userService.findByName(loginname);
						// 如果user返回不为空,就取出密码,使用用户名+密码+有效时间+
						// webSiteKey进行MD5加密。与前面设置的进行比较，看是否是同一个用户
						if (temp != null) {
							String sha1ValueInCookie = cookieValues[2];
							Sha1 sha1=new Sha1();
							String sha1ValueFromUser = sha1.Digest("qwer1234"+temp
									.getLoginname()
									+ ":"
									+ temp.getPassword()
									+ ":"
									+ temp.getIsvalidate()
									+ ":"
									+ temp.getFlag()+"/.,mnb");
							// 将结果与Cookie中的MD5码相比较,如果相同,写入Session,自动登陆成功,并继续用户请求
							if (sha1ValueFromUser.equals(sha1ValueInCookie)) {
								session.setAttribute("user", temp);
								chain.doFilter(request, response);
							}else{
								//httpResponse.sendRedirect(reqContextPath + "/");// 跳转至登录页面
								httpResponse.getWriter().print("{\"state\":\"failed\",\"errorMsg\":\"please log in\"}");
							}
						}
					}
				} else {
					//httpResponse.sendRedirect(reqContextPath + "/");// 跳转至登录页面
					httpResponse.getWriter().print("{\"state\":\"failed\",\"errorMsg\":\"please log in\"}");
				}
			}
		}
		// 如果需要验证
		// if (ignore != null && ignore.equalsIgnoreCase("false")) {
		// if (ignoreList != null && ignoreList.length > 0 && isHave(reqURI,
		// reqContextPath, ignoreList)) {
		// //免过滤，放行
		// chain.doFilter(request, response);
		// }
		// else {
		// //如果没有正常登录
		// if (session == null || session.getAttribute("USER_ID") == null) {
		// String path = httpRequest.getContextPath();
		// //没有登录跳转到登录页面
		// response.setContentType("text/html;charset=UTF-8");
		// response
		// .getWriter()
		// .write(
		// "<script>window.parent.parent.location.href='"+ path
		// +"/login.jsp';alert('您的身份验证已失效，请重新登陆!');</script>");
		//
		// }
		// // 正常登录处理
		// else {
		// chain.doFilter(request, response);
		// }
		// }
		// }
		// // 验证关闭状态，放行
		// else {
		// chain.doFilter(request, response);
		// }
	}

	/**
	 * 判断url是否在该数组列表中
	 * 
	 * @param url
	 * @param reqContextPath
	 * @param ignores
	 * @return boolean
	 * @author 张加宁
	 */
	public boolean isHave(String url, String reqContextPath, String[] ignores) {
		for (int i = 0; i < ignores.length; i++) {
			if (url.equals(reqContextPath + ignores[i])) {
				return true;
			}
		}
		return false;
	}

	public void init(FilterConfig config) throws ServletException {
		this.encoding = config.getInitParameter("encoding");
		ignore = config.getInitParameter("ignore");
		String tmpList = config.getInitParameter("ignoreList");
		if (tmpList != null && !tmpList.trim().equals("")) {
			ignoreList = tmpList.split(",");
		}
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getIgnore() {
		return ignore;
	}

	public void setIgnore(String ignore) {
		this.ignore = ignore;
	}

	public String[] getIgnoreList() {
		return ignoreList;
	}

	public void setIgnoreList(String[] ignoreList) {
		this.ignoreList = ignoreList;
	}
}
