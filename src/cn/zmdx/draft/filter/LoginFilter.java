/**
 * <p>文件名: LoginFilter.java</p>
 * <p>版权声明: Copyright &copy; 2014-2015 智美点心科技</p>
 * <p>创建者: 张加宁</p>
 * <p>创建时间: 2014-11-03  下午03:08:22</p>
 */
package cn.zmdx.draft.filter;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 验证是否登录
 * @author 张加宁
 */
public class LoginFilter extends HttpServlet implements Filter  {

	private static final long serialVersionUID = 1L;
	private String encoding; // 字符编码
	private String ignore; // 验证开关
	private String[] ignoreList; // 验证url数组

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		request.setCharacterEncoding(encoding);
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();
		String reqURI = httpRequest.getRequestURI();
		String reqContextPath = httpRequest.getContextPath();
		// 如果需要验证
		if (ignore != null && ignore.equalsIgnoreCase("false")) {
			if (ignoreList != null && ignoreList.length > 0 && isHave(reqURI, reqContextPath, ignoreList)) {
				//免过滤，放行
				chain.doFilter(request, response);
			}
			else {
				//如果没有正常登录
				if (session == null || session.getAttribute("USER_ID") == null) {
					String path = httpRequest.getContextPath();
					//没有登录跳转到登录页面
					response.setContentType("text/html;charset=UTF-8");
					response
					.getWriter()
					.write(
							"<script>window.parent.parent.location.href='"+ path +"/login.jsp';alert('您的身份验证已失效，请重新登陆!');</script>");
					
				}
				// 正常登录处理
				else {
					chain.doFilter(request, response);
				}
			}
		}
		// 验证关闭状态，放行
		else {
			chain.doFilter(request, response);
		}
	}

	/**
	 * 判断url是否在该数组列表中
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
