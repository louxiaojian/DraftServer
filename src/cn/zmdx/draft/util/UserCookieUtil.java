package cn.zmdx.draft.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import cn.zmdx.draft.entity.User;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class UserCookieUtil {
	private final static String cookieKey = "draftServer";// 项目名称，保存cookie时的KEY值
	// private final static String
	// protectName="draftServer";//项目名称，保存cookie是的KEY值
	private final static long cookieMaxAge = 60 * 60;// 有效期(秒)

	/**
	 * 保存cookie到response
	 * @author louxiaojian
	 * @date： 日期：2015-7-24 时间：下午12:03:37
	 * @param user
	 * @param response
	 */
	public static void saveCookie(User user, HttpServletResponse response) {
		// cookie的有效期至（到哪一天）
		long validTime = System.currentTimeMillis() + (cookieMaxAge);
		// MD5加密用户详细信息
		Sha1 sha1=new Sha1();
		String sha1CookieValue = sha1.Digest("qwer1234"+user.getLoginname() + ":"
				+ user.getPassword() + ":" + user.getIsvalidate() + ":"
				+ user.getFlag()+"/.,mnb");
		// 被保存的完整的Cookie值
		String cookieValue = user.getLoginname() + ":" + validTime + ":"
				+ sha1CookieValue;
		// 对Cookie的值进行BASE64编码
		String cookieValueBase64 = new String(Base64.encode(cookieValue
				.getBytes()));
		Cookie cookie = new Cookie(cookieKey, cookieValueBase64);
		cookie.setMaxAge(60 * 60);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	/**
	 * 用户注销时，清除cookie
	 * @author louxiaojian
	 * @date： 日期：2015-7-24 时间：下午12:04:00
	 * @param response
	 */
	public static void clearCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie(cookieKey, null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	/**
	 * 获取Cookie组合字符串的MD5码的字符串
	 * @author louxiaojian
	 * @date： 日期：2015-7-24 时间：下午12:07:00
	 * @param value
	 * @return
	 */
	public static String getMD5(String value) {
		String result = null;
		try {
			byte[] valueByte = value.getBytes();
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(valueByte);
			result = toHex(md.digest());
		} catch (NoSuchAlgorithmException e2) {
			e2.printStackTrace();
		}
		return result;
	}

	/**
	 *  将传递进来的字节数组转换成十六进制的字符串形式并返回
	 * @author louxiaojian
	 * @date： 日期：2015-7-24 时间：下午12:06:55
	 * @param buffer
	 * @return
	 */
	private static String toHex(byte[] buffer) {
		StringBuffer sb = new StringBuffer(buffer.length * 2);
		for (int i = 0; i < buffer.length; i++) {
			sb.append(Character.forDigit((buffer[i] & 0xf0) >> 4, 16));
			sb.append(Character.forDigit(buffer[i] & 0x0f, 16));
		}
		return sb.toString();
	}
}
