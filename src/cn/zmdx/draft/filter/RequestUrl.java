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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import cn.zmdx.draft.action.UserAction;

/**
 * 验证是否登录
 * @author 张加宁
 */
public class RequestUrl extends HttpServlet implements Filter  {

	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(RequestUrl.class);

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();
		String reqURI = httpRequest.getRequestURI()+"?"+httpRequest.getQueryString();
		String reqContextPath = httpRequest.getContextPath();
//		System.out.println("1***"+reqURI);
		logger.error(reqURI);
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
