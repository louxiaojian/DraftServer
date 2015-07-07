package cn.zmdx.draft.util;

import java.security.MessageDigest;

public class Encrypter
{
	/**
	 * 进行MD5加密
	 * @param s
	 * @return
	 * @author 张加宁
	 */
	public static String md5(String s)
	{
		try
		{
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			sun.misc.BASE64Encoder base64Encoder = new sun.misc.BASE64Encoder();
			return base64Encoder.encode(messageDigest
					.digest(s.getBytes("utf8")));
		}
		catch (Exception e)
		{
			return s;
		}

	}

}
