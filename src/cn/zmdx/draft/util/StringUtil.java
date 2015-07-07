package cn.zmdx.draft.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class StringUtil {

	private static Logger logger = Logger.getLogger(StringUtil.class);

	/**
	 * replace all regex by replacement in orig string
	 * 
	 * @param orig
	 * @param regex
	 * @param replacement
	 * @return String
	 */
	public static String replaceAll(String orig, String regex,
			String replacement) {
		StringBuffer sb = new StringBuffer();
		int ti = 0;
		int index = orig.indexOf(regex);
		while (index >= 0) {
			sb.append(orig.substring(ti, index));
			sb.append(replacement);
			ti = index + regex.length();
			if (ti == orig.length()) {
				index = -1;
			} else {
				index = orig.indexOf(regex, ti);
			}
		}
		sb.append(orig.substring(ti));
		return sb.toString();
	}

	/**
	 * replace first regex by replacement in orig string
	 * 
	 * @param orig
	 * @param regex
	 * @param replacement
	 * @return String
	 */
	public static String replaceFirst(String orig, String regex,
			String replacement) {
		StringBuffer sb = new StringBuffer();
		int ti = 0;
		int index = orig.indexOf(regex);
		if (index >= 0) {
			sb.append(orig.substring(ti, index));
			sb.append(replacement);
			ti = index + regex.length();
		}
		sb.append(orig.substring(ti));
		return sb.toString();
	}

	/**
	 * split orig string by regex example: "abc,dee,ghi,j" split by "," result
	 * is {"abc","dee","ghi","j"} split by "e" result is {"abc,d","",",ghi,j"}
	 * 
	 * @param orig
	 * @param regex
	 * @return String[]
	 */
	@SuppressWarnings("unchecked")
	public static String[] split(String orig, String regex) {
		List tmpList = new ArrayList();
		int ti = 0;
		int index = orig.indexOf(regex);
		while (index >= 0) {
			tmpList.add(orig.substring(ti, index));
			ti = index + regex.length();
			if (ti == orig.length()) {
				index = -1;
			} else {
				index = orig.indexOf(regex, ti);
			}
		}
		tmpList.add(orig.substring(ti));
		String[] result = new String[tmpList.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (String) tmpList.get(i);
		}
		return result;
	}

	/**
	 * split orig string by two regex
	 * 
	 * @param orig
	 * @param oneregex
	 * @param tworegex
	 * @return String[][]
	 */
	public static String[][] split(String orig, String oneregex, String tworegex) {
		String[] oneArray = split(orig, oneregex);
		String[][] result = new String[oneArray.length][];
		for (int i = 0; i < oneArray.length; i++) {
			String[] twoArray = split(oneArray[i], tworegex);
			result[i] = new String[twoArray.length];
			for (int j = 0; j < twoArray.length; j++) {
				result[i][j] = new String(twoArray[j]);
			}
		}
		return result;
	}

	/**
	 * 判断一个字符串是否是空串
	 * 
	 * 
	 * @param str
	 * @return boolean
	 */
	public static boolean isEmpty(String str) {
		return (str == null) || (str.equals("") || str.equals("null"));
	}

	/**
	 * 处理字符串 如果是null则返回空，否则返回原来的值 String 2011-5-11下午12:12:44 author：李数
	 */
	public static String dealNull(String str) {
		if (isNull(str)) {
			return "";
		} else {
			return str;
		}
	}

	/**
	 * 判断一个字符串时否为空 空串的条件：对象为null，"null",或者空串组合，除空白字符外，没有任何有效字符，如""," "
	 * 
	 * @param str
	 *            String 待判断的字符串
	 * 
	 * @return boolean 是否为空串
	 */
	public static boolean isNull(String str) {
		if (str == null) {
			// 参数对象为null
			return true;
		} else if (str.trim().length() == 0
				|| str.trim().equalsIgnoreCase("null")) {
			// 去掉参数两边的空字符串后长度为0或者为"null"
			return true;
		}
		return false;
	}

	/**
	 * 判断是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isMissing(String str) {
		if (str == null || str.trim().equals("")
				|| str.trim().equalsIgnoreCase("null")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 参数字符转换 String 2011-4-27下午04:54:27 author：李数
	 */
	public static String encodingUrl(String str) {
		try {
			if (str != null) {
				str = str.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
				str = str.replaceAll("\\+", "%2B");
				return java.net.URLDecoder.decode(str, "UTF-8").trim();
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 判断是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isMissing(Long value) {
		if (value == null || value.equals(0L)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 过滤空串，使null字符串显示为"" add date: 2005-08-02
	 * 
	 * @param str1
	 *            String 需要过滤得字符串
	 * 
	 * @return String 过滤后的字符串，即把null字符串处理
	 */
	public static String filterNull(String str1) {
		if (isNull(str1)) {
			return "";
		} else {
			return str1;
		}
	}

	/**
	 * 获取一个字符串左边的n个字符，作为一个串返回，当串不够n个字符时候，返回全部串 add date: 2005-08-02
	 * 
	 * @param str1
	 *            String 原字符符串
	 * 
	 * @param n
	 *            int 需要截取的字符数量
	 * @return String 截取之后的字符串
	 */
	public static String leftString(String str1, int n) {
		String retString = "";
		// 字符串不为空时后处理
		if (!isNull(str1)) {
			if (str1.length() > n) {
				// 当长度>n时进行处理
				retString = str1.substring(0, n);
			} else {
				// 长度不够n，返回全部字符
				retString = str1;
			}
		}
		return retString;
	}

	/**
	 * 获取一个字符串右边的n个字符，作为一个串返回，当串不够n个字符时候，返回全部串 add date: 2006-06-25
	 * 
	 * @param str1
	 *            String 原字符符串
	 * 
	 * @param n
	 *            int 需要截取的字符数量
	 * @return String 截取之后的字符串
	 */
	public static String rightString(String str1, int n) {
		String retString = "";
		// 字符串不为空时后处理
		if (!isNull(str1)) {
			if (str1.length() > n) {
				// 当长度>n时进行处理
				retString = str1.substring(str1.length() - n);
			} else {
				// 长度不够n，返回全部字符
				retString = str1;
			}
		}
		return retString;
	}

	/**
	 * 用指定字符，指定长度，填充字符串
	 * 
	 * @param c
	 *            char 填充的字符
	 * 
	 * @param len
	 *            int 字符串长度
	 * 
	 * @return String 失败返回""
	 */
	public static String fillString(char c, int len) {
		String retString = "";
		while (len > 0) {
			retString += c;
			len--;
		}
		return retString;
	}

	/**
	 * 用指定的格式话字符，格式化指定长度字符串
	 * 
	 * @param oldStr
	 *            String 原有的字符串
	 * @param preChar
	 *            char 前补字符
	 * @param newLen
	 *            int 格式化后的字符
	 * 
	 * @return String 失败返回""
	 */
	public static String formatString(String oldStr, char preChar, int newLen) {
		String retString = "";
		int oldLen = oldStr.length();
		if (oldLen <= newLen) {
			retString = fillString(preChar, newLen - oldLen) + oldStr;
		}
		return retString;
	}

	/**
	 * 用指定格式,格式化当前日期,以字符串的形式返回
	 * 
	 * 
	 * @param date
	 * @param pattern
	 * @return String
	 */
	public static String getDateString(Date date, String pattern) {
		String formatString = "";
		if (isNull(pattern)) {
			// 默认格式
			pattern = "yyyy-MM-dd";
		}
		if (date != null) {
			// 日期不为空
			try {
				// 格式化日期工具类
				DateFormat df = new SimpleDateFormat(pattern);
				formatString = df.format(date);
			} catch (Throwable t) {
				// pattern不符合规定
				logger.error("pattern of format date is invalid", t);
			}
		}
		// 返回格式化的字符串
		return formatString;
	}

	/**
	 * 如果字符串含有',则转化为SQL查询所需的字符串 在出入境表中使用
	 * 
	 * @param str
	 * @return String
	 */
	public static String getStringForSpecial(String str) {
		if (str == null) {
			return "";
		}
		String result = "";
		String[] strArray = null;
		strArray = (str.trim()).split("'");
		int i;
		for (i = 0; i < strArray.length - 1; i++) {
			result += strArray[i] + "''";
		}
		if (i >= 0) {
			result += strArray[i];
		}
		return result;
	}

	/**
	 * 过滤所有非字母的字符，且小写字母转化为大写字母，返回由大写字母组成的字符串
	 * 
	 * @param str
	 * @return String
	 */
	public static String filterNotCharacter(String str) {
		if (str == null) {
			return "";
		}
		String result = str.toUpperCase();
		result = result.replaceAll("[^A-Z]", "");
		return result;
	}

	/**
	 * 过滤所有非字母的字符，且小写字母转化为大写字母，返回由大写字母组成的字符串
	 * 
	 * @param str
	 * @return String
	 */
	public static String filterNotDigit(String str) {
		if (str == null) {
			return "";
		}
		String result = str.toUpperCase();
		result = result.replaceAll("[^0-9]", "");
		return result;
	}

	/**
	 * trim掉所有的whitespace 字符
	 * 
	 * @param str
	 * @return String
	 */
	public static String filterWhiteSpaceCharacter(String str) {
		if (str == null) {
			return "";
		}
		// A whitespace character: [ \t\n\x0B\f\r]
		String result = str.replaceAll("\\s", "");
		// 如果是去除前后的whitespace，则可用trim即可
		return result;
	}

	/**
	 * 去除字符串前后的whitespace
	 * 
	 * @param str
	 * @return String
	 */
	public static String filterSpace(String str) {
		String result = null;
		if (str != null) {
			result = str.trim();
		}
		return result;
	}

	/**
	 * 过虑字符串，只保留字母和数字
	 * 
	 * @param str
	 * @return String
	 */
	public static String filterAllExceptWord(String str) {
		String result = null;
		if (str != null) {
			// [a-zA-Z0-9]
			result = str.replaceAll("[^a-zA-Z0-9]", "");
		}
		return result;
	}

	/**
	 * 过虑字符串，只保留字母和数字,且将小写字母转换为大写
	 * 
	 * 
	 * @param str
	 * @return String
	 */
	public static String filterAllExceptWords(String str) {
		String result = null;
		if (str != null) {
			str = str.toUpperCase();
			// [A-Z0-9]
			result = str.replaceAll("[^A-Z0-9]", "");
		}
		return result;
	}

	/**
	 * 排除"_"之外的Punctuation
	 * 
	 * @param str
	 * @return String
	 */
	public static String filterPunctuationExceptUnderLine(String str) {
		String result = null;
		if (str != null) {
			// [a-zA-Z0-9]
			result = str.replaceAll("[\\p{Punct}&&[^_]]*", "");
		}
		return result;
	}

	/**
	 * 排除Punctuation
	 * 
	 * @param str
	 * @return String
	 */
	public static String filterPunctuation(String str) {
		String result = null;
		if (str != null) {
			// [a-zA-Z0-9]
			result = str.replaceAll("[\\p{Punct}]*", "");
		}
		return result;
	}

	/**
	 * 取代字符串的汉字自造字 当汉字为自造字时，该汉字用全角?来表示
	 * 
	 * 
	 * @param str
	 * @return String
	 * @serial 2006-6-23 Lee S 更改算法
	 */
	public static String filterSelfMadeCHChar(String str) {
		if (str == null) {
			return null;
		}
		StringBuffer strBuf = new StringBuffer(str);
		for (int i = 0; i < str.length(); i++) {
			boolean selfMade = false;
			byte[] tmp = str.substring(i, i + 1).getBytes();
			if (tmp.length == 2) {
				int t1 = tmp[0] & 0xFF;
				int t2 = tmp[1] & 0xFF;
				if ((t1 >= 0xAA && t1 <= 0xAF) && (t2 >= 0xA1 && t2 <= 0xFE)) {
					selfMade = true;
				} else if ((t1 >= 0xF8 && t1 <= 0xFE)
						&& (t2 >= 0xA1 && t2 <= 0xFE)) {
					selfMade = true;
				} else if ((t1 >= 0xA1 && t1 <= 0xA7)
						&& (t2 >= 0x40 && t2 <= 0xA0) && (t2 != 0x7F)) {
					selfMade = true;
				}
				if (selfMade) {
					strBuf.replace(i, i + 1, "？");
				}
			}
		}
		return strBuf.toString();
	}

	/**
	 * 根据类型针对日期后补字符
	 * 
	 * @param oldStr
	 * @param type
	 * @param newLen
	 * @return String
	 */
	public static String backFormatString(String oldStr, int type, int newLen) {
		String retString = "";
		int oldLen = oldStr.length();
		if (type == 1 && oldLen <= newLen) {
			retString = oldStr + "000000";
		} else if (type == 2 && oldLen <= newLen) {
			retString = oldStr + "235959";
		} else {
			retString = oldStr;
		}
		return retString;
	}

	/**
	 * 将字符串转换为Double
	 * 
	 */
	public static Double string2Double(String value) {
		Double result = null;
		if (value == null || value.equals("")) {
			value = "0";
		}
		try {
			double temp = Double.parseDouble(value);
			result = new Double(temp);
		} catch (NumberFormatException e) {
			logger.debug("double parse execption!");
		}
		return result;
	}

	/**
	 * 将字符串转换为Integer
	 * 
	 */
	public static Integer string2Integer(String value) {
		Integer result = null;
		if (value == null || value.equals("")) {
			value = "0";
		}
		try {
			int temp = Integer.parseInt(value);
			result = new Integer(temp);
		} catch (NumberFormatException e) {
			logger.debug("double parse execption!");
		}
		return result;
	}

	/**
	 * MD5 Hash Method, jdk1.4 needed
	 */
	public static String hashMD5(String inputStr) {
		if (inputStr == null || inputStr.length() == 0) {
			return "00000000000000000000000000000000";
		} else {
			StringBuffer str = new StringBuffer();
			// try
			// {
			// KeyGenerator kg = KeyGenerator.getInstance("HmacMD5");
			// SecretKey sk = kg.generateKey();
			// Mac md5 = Mac.getInstance("HmacMD5");
			// md5.init(sk);
			// byte[] hcode = md5.doFinal(inputStr.toString().getBytes());
			// if (hcode != null)
			// {
			// for (int i = 0; i < hcode.length; i++)
			// {
			// if ((hcode[i] & 0xFF) < 0x10)
			// {
			// str.append(0);
			// }
			// str.append(Integer.toHexString(hcode[i] & 0xFF).toUpperCase());
			// }
			// }
			// }
			// catch (NoSuchAlgorithmException e)
			// {
			// logger.error("no 'HmacMD5' algorithm.", e);
			// str.append(System.identityHashCode(inputStr));
			// }
			// catch (InvalidKeyException e)
			// {
			// logger.error("'HmacMD5' key invalid.", e);
			// str.append(System.identityHashCode(inputStr));
			// }
			// return str.toString();
			try {
				MessageDigest m = MessageDigest.getInstance("MD5");
				byte[] hcode = m.digest(inputStr.getBytes());
				if (hcode != null) {
					for (int i = 0; i < hcode.length; i++) {
						if ((hcode[i] & 0xFF) < 0x10) {
							str.append(0);
						}
						str.append(Integer.toHexString(hcode[i] & 0xFF)
								.toUpperCase());
					}
				}
			} catch (NoSuchAlgorithmException e) {
				logger.error("no 'MD5' algorithm.", e);
				str.append(System.identityHashCode(inputStr));
			}
			return str.toString();
		}
	}

	/**
	 * 将年龄段转换成起始年限
	 */
	public static String getFirstYear(String age) {
		String a1 = null;
		try {
			// int b=age.indexOf("-");
			// String fir=age.substring(0,b);
			String fir = age.substring(0, 2);
			int fir1 = Integer.parseInt(fir);
			String pattern = "yyyy";
			String year = StringUtil.getDateString(new Date(), pattern);
			int year1 = Integer.parseInt(year);
			a1 = (year1 - fir1 + 1) + "0000";
		} catch (NumberFormatException e) {
			logger.debug("first year execption!");
		}
		return a1;
	}

	/**
	 * 将年龄段转换成截止年限
	 */
	public static String getLastYear(String age) {
		String a1 = null;
		try {
			String las = age.substring(3, 5);
			int las1 = Integer.parseInt(las);
			String pattern = "yyyy";
			String year = StringUtil.getDateString(new Date(), pattern);
			int year1 = Integer.parseInt(year);
			a1 = (year1 - las1) + "0000";
		} catch (NumberFormatException e) {
			logger.debug("last year execption!");
		}
		return a1;
	}

	/**
	 * 字符串的中英文判断
	 * 
	 * 
	 */
	public static int judgeCE(String name) {
		int result = 0;
		try {
			String a = name.substring(0, 1);
			char a1 = a.charAt(0);
			if (a1 >= 65 && a1 <= 90) {
				result = 0;
			} else if (a1 >= 97 && a1 <= 122) {
				result = 0;
			} else {
				result = 1;
			}
		} catch (NumberFormatException e) {
			logger.debug("charAt parse execption!");
		}
		return result;
	}

	/**
	 * 将模糊查询的语句过滤(GBK)
	 * 
	 * @throws SQLException
	 */
	public static String getBlurGBK(String str) throws SQLException {
		String a1 = null;
		try {
			String mark;
			String temp;
			StringBuffer name2 = new StringBuffer();
			char[] name1 = str.toCharArray();
			for (int i = 0; i < str.length(); i++) {
				if (name1[i] != '%' && name1[i] != '_') {
					if (name1[i] > 128) {
						mark = String.valueOf(name1[i]);
						temp = filterSelfMadeCHChar(mark);
						name2.append(temp);
					}
				} else {
					name2.append(String.valueOf(name1[i]));
				}
			}
			a1 = name2.toString();
		} catch (NumberFormatException e) {
			logger.debug("last year execption!");
		}
		return a1;
	}

	/**
	 * 用指定字符，指定长度，向前填充字符串
	 * 
	 * @param c
	 *            char 填充的字符
	 * 
	 * @param len
	 *            int 字符串长度
	 * 
	 * @return String 失败返回""
	 */
	public static String fillStringLeft(String str) {
		String retString = "";
		if (str != null) {
			int len = 5 - str.length();
			while (len > 0) {
				retString = "0" + str;
				str = retString;
				len--;
			}
		}
		return retString;
	}

	/**
	 * 过滤所有非字母的字符，只保留数字
	 * 
	 * 
	 * @param str
	 * @return String
	 */
	public static String filterToDigit(String str) {
		if (str == null) {
			return "";
		}
		String result = str.replaceAll("[^0-9]", "");
		return result;
	}

	/**
	 * 检查输入的的字符串是否是int类型
	 * 
	 * @author 张加宁
	 * @return 如果正确返回true，错误返false
	 */
	public static boolean checkInteger(String number) {
		boolean flag = true;
		if (number == null || number.trim().equals("")) {
			flag = false;
			return flag;
		}
		try {
			Integer.parseInt(number.trim());
		} catch (Exception e) {
			flag = false;
			return flag;
		}
		return flag;
	}

	/**
	 * 获取字符串的长度，汉字算2个
	 * 
	 * @author 张加宁
	 * @return 返回数值，如果为空或null，返回0
	 */
	public static int getCNLength(String str) {
		if (isEmpty(str)) {
			return 0;
		}
		try {
			str = new String(str.getBytes("gb2312"), "iso-8859-1");
			return str.length();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取字符串的长度，汉字算3个
	 * 
	 * @param str
	 * @return int
	 * @author 张加宁
	 */
	public static int getCNLengthByUTF(String str) {
		if (isEmpty(str)) {
			return 0;
		}
		try {
			str = new String(str.getBytes("gb2312"), "iso-8859-1");
			return str.length();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 过滤字符串中的特殊字符
	 * 
	 * @param str
	 *            待过滤的字符串，rule 自定义的规则
	 * @author 张加宁
	 * @return 返回过滤后的字符串，如果为空或null，返回"",异常返回null
	 */
	public static String replace(String str, String rule) {
		if (isEmpty(str) || isEmpty(rule)) {
			return "";
		}
		try {
			StringBuffer b = new StringBuffer();
			char[] cc = rule.toCharArray();
			for (int i = 0; i < str.length(); i++) {
				for (int j = 0; j < cc.length; j++) {
					if (str.charAt(i) == cc[j]) {
						break;
					}
					if (j == cc.length - 1) {
						b.append(str.charAt(i));
					}
				}
			}
			return b.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断字符串的内容是否是整形
	 * 
	 * @param str
	 * @return boolean
	 * @author 张加宁
	 */
	public static boolean isLong(String str) {
		String regEx = "^[0-9]*$";
		Pattern pat = Pattern.compile(regEx);
		Matcher mat = pat.matcher(str);
		return mat.find();
	}

	/**
	 * 功能：验证字符串长度是否符合要求，一个汉字等于三个字符
	 * 
	 * @param strParameter
	 *            要验证的字符串
	 * @param limitLength
	 *            验证的长度
	 * @return 符合长度ture 超出范围false
	 */
	public static boolean validateStrByLength(String strParameter,
			int limitLength) {
		int temp_int = 0;
		byte[] b = strParameter.getBytes();
		for (int i = 0; i < b.length; i++) {
			if (b[i] >= 0) {
				temp_int = temp_int + 1;
			} else {
				temp_int = temp_int + 3;
				i++;
			}
		}
		if (temp_int > limitLength) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断str是否在数组strArr中
	 * 
	 * @param str
	 * @param strArr
	 * @return 2011-6-16 下午01:53:11 作者：夏明涛
	 */
	public static boolean inStringArr(String str, String[] strArr) {
		if (str == null || strArr == null) {
			return false;
		}
		for (String strInArr : strArr) {
			if (str.equals(strInArr)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 过滤单引号字符
	 * 
	 * @param str
	 * @return String
	 * @author 张加宁
	 * @date 2012-9-19 下午02:42:59
	 */
	public static String escapeString(String str) {
		try {
			return str.replaceAll("'", "''");
		} catch (Exception e) {
			e.printStackTrace();
			return str;
		}
	}

	public static void main(String args[]) throws SQLException {
		System.out.println(StringUtil
				.encodingUrl("德特乐福斯旅行车有限公司(Dethleffs GmbH)"));
		System.out.println(1 / 3 + 1);
		System.out.println(6 % 3);
	}

	/**
	 * @author 张加宁 单词首字母小写
	 */
	public static String toFirstLow(String str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}

	/**
	 * @author 张加宁 单词首字母大写
	 */
	public static String toFirstUp(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

}
