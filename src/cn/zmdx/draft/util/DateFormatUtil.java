package cn.zmdx.draft.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

public class DateFormatUtil {
	private static final long dayTime = 24 * 60 * 60 * 1000;
	 public static void main(String[] args) {
		 String dd = new Date().toString();
		
//		 toStrDate(dd);
	    }



	 /**
	  * 将时区格式 Thu Nov 04 23:10:48 CST 2010 转成 日期格式
	  * @param str
	  * @return
	  */
	public static String strToDateStr(String str){
		 String tmp="";
		 Date d = new Date(str); //输出格式：Thu Nov 04 23:10:48 CST 2010 
		 java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd"); 
		 tmp= format1.format(d); //输出格式：2010-11-04 
		 return tmp;
	}
	public static String getCurrTime() {
		Date now = new Date();
		String s = getCurrTime(now);
		return s;
	}
	
	public static String getCurrTimeString(){
		Date now = new Date();
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String s = outFormat.format(now);
		return s;
	}

	public static String getCurrTime(Date date){
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String s = outFormat.format(date);
		return s;
	}
	/**
	 * Formats a Date object to return a date using the global locale.
	 */
	public static String formatDate(Date date) {
		if (date == null) {
			return null;
		} else {
			SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");
			return outFormat.format(date);
		}
	}

	/**
	 * Formats a Date object to return a date and time using the global locale.
	 */
	public static String formatDateTime(Date date) {
		if (date == null) {
			return null;
		} else {
			SimpleDateFormat outFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			return outFormat.format(date);
		}
	}

	public static String formatDate2(Date myDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		String strDate = formatter.format(myDate);
		return strDate;
	}

	public static String formatDate3(Date myDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
		String strDate = formatter.format(myDate);
		return strDate;
	}

	public static String formatDate4(Date myDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String strDate = formatter.format(myDate);
		return strDate;
	}

	public static String formatDate5(Date myDate) {
		String strDate = getYear(myDate) + "-" + getMonth(myDate) + "-"
				+ getDay(myDate);
		return strDate;
	}

	public static String formatDate6(Date myDate) {
		if (myDate == null) {
			return null;
		} else {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			String strDate = formatter.format(myDate);
			return strDate;
		}
	}

	public static long Date2Long(int year, int month, int date) {
		Calendar cld = Calendar.getInstance();
		month = month - 1;
		cld.set(year, month, date);
		return cld.getTime().getTime();
	}

	public static long Time2Long(int year, int month, int date, int hour,
			int minute, int second) {
		Calendar cld = Calendar.getInstance();
		month = month - 1;
		cld.set(year, month, date, hour, minute, second);
		return cld.getTime().getTime();
	}

	public static int getYear(long t) {
		Calendar cld = Calendar.getInstance();
		if (t > 0) {
			cld.setTime(new java.util.Date(t));
		}
		return cld.get(Calendar.YEAR);
	}

	public static int getMonth(long t) {
		Calendar cld = Calendar.getInstance();
		if (t > 0) {
			cld.setTime(new java.util.Date(t));
		}
		return cld.get(Calendar.MONTH) + 1;
	}

	public static int getDay(long t) {
		Calendar cld = Calendar.getInstance();
		if (t > 0) {
			cld.setTime(new java.util.Date(t));
		}
		return cld.get(Calendar.DAY_OF_MONTH);
	}

	public static int getHour(long t) {
		Calendar cld = Calendar.getInstance();
		if (t > 0) {
			cld.setTime(new java.util.Date(t));
		}
		return cld.get(Calendar.HOUR_OF_DAY);
	}

	public static int getMinute(long t) {
		Calendar cld = Calendar.getInstance();
		if (t > 0) {
			cld.setTime(new java.util.Date(t));
		}
		return cld.get(Calendar.MINUTE);
	}

	public static int getSecond(long t) {
		Calendar cld = Calendar.getInstance();
		if (t > 0) {
			cld.setTime(new java.util.Date(t));
		}
		return cld.get(Calendar.SECOND);
	}

	public static int getYear(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(Calendar.YEAR);
	}

	public static int getMonth(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(Calendar.MONTH) + 1;
	}

	public static int getDay(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(Calendar.DAY_OF_MONTH);
	}

	public static int getHour(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(Calendar.HOUR_OF_DAY);
	}

	public static int getMinute(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(Calendar.MINUTE);
	}

	public static int getSecond(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(Calendar.SECOND);
	}

	public static int getYear() {
		Calendar cld = Calendar.getInstance();
		cld.setTime(new java.util.Date());
		return cld.get(Calendar.YEAR);
	}

	public static int getMonth() {
		Calendar cld = Calendar.getInstance();
		cld.setTime(new java.util.Date());
		return cld.get(Calendar.MONTH) + 1;
	}

	public static int getDay() {
		Calendar cld = Calendar.getInstance();
		cld.setTime(new java.util.Date());
		return cld.get(Calendar.DAY_OF_MONTH);
	}

	public static Date getDate(String dateStr) throws Exception {
		return getDate(dateStr,"yyyy-MM-dd");
	}

	public static Date getDate(String dateStr,String formatText) throws Exception {
		Date d = null;
		if (dateStr != null && !dateStr.equals("")) {
			SimpleDateFormat outFormat = new SimpleDateFormat(formatText);
			d = outFormat.parse(dateStr);
		}
		return d;
	}

	public static Date getNextDate(String dateStr) throws Exception {
		Date d = null;
		if (dateStr != null && !dateStr.equals("")) {
			SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");
			d = outFormat.parse(dateStr);
		}
		if (d != null) {
			return new Date(d.getTime() + dayTime);
		}
		return null;
	}
	
	public static Vector<String> doFormatDate(String startMonth, String endMonth) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		Vector<String> vector = new Vector<String>();
		vector.addElement(startMonth);
		if(startMonth.equals(endMonth)){
			return vector;
		}
		try {
		Date a= sdf.parse(startMonth);
			while(true){
				a.setMonth(a.getMonth()+1);
				String fDate = sdf.format(a);
				vector.addElement(fDate);
				if(fDate.equals(endMonth))
				break;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return vector;
	}
	/**
	 * 指定日期加n天
	 * 
	 * @param gc
	 * @return
	 */
	public static  Date getNextNDay(Date date, int n) {
		/**
		 * 详细设计： 1.指定日期加n天
		 */
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.add(Calendar.DATE, n);
		return gc.getTime();
	}	
	/**  
	    * 得到二个日期间的间隔天数  
	    */  

		public static long countDay(Date start, Date end) {   
	        long day = 0;   
	        try {   
	         day = (end.getTime() - start.getTime()) / (24 * 60 * 60 * 1000);   
	        } catch (Exception e) {   
	         return 0l;   
	        }   
	        return day  ;   
	    }  
		
		public static Timestamp StringtoTimestamp(String date){
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
			format.setLenient(false); 
			//要转换字符串 str_test  
			try {   
				Timestamp ts = new Timestamp(format.parse(date).getTime());   
				return ts;
			} catch (ParseException e) {   
					// TODO Auto-generated catch block   
					e.printStackTrace();  
					return null;
			}
		}
		
		public static String TimestampToStringLong(Timestamp time){
			try {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒
				String str = df.format(time);
				return str;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return null;
			}
		}
		public static String TimestampToStringShort(Timestamp time){
			try {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//定义格式，不显示毫秒
				if (time!=null)
				{
					String str = df.format(time);
					return str;
				}
				return null;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return null;
			}
		}
}
