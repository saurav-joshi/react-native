package com.iaasimov.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.5 $ 
 */
public class StringUtils {

	private static final int PASSWD_MIN_LEN = 6;
	
	public static boolean isEmpty(String string) {
		if (string == null) {
			return true;
		}
		if ("".equals(string.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param dateStr
	 * @return
	 */	
	public static Date stringToDate(String dateStr) {
		return stringToDate(dateStr, StringConstants.DATE_FORMAT);
	}
	
	public static Date stringToDate(String dateStr, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date ret = null;
		try {
			ret = sdf.parse(dateStr);
		} catch (Exception e) {
		}
		return ret;
	}
	
	public static String dateToString(Date date) {
		return dateToString(date, StringConstants.DATE_FORMAT);
	}
	
	public static String dateToString(Date date, String format) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String ret="";
		
		try {
			ret =sdf.format(date); 
		} catch (Exception e) {
			ret="";
		}
		return ret;
	}
	
	public static boolean isLong(String source) {
		boolean ret = true;
		
		try {
			Long.parseLong(source);
		} catch (Exception ex) {
			ret= false;
		}
		return ret;
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public static  boolean validateTimeString(String input) {
		if (input.indexOf(":")<0) {
			return false;
		}
		String min= input.substring(input.indexOf(":")+1);
		String hour= input.substring(0,input.indexOf(":"));
		if (!isLong(min) || !isLong(hour)) {
			return false;
		}
		if (Long.valueOf(hour).longValue() >23) {
			return false;
		}
		if (Long.valueOf(min).longValue() >59) {
			return false;
		}
		return isLong(min) && isLong(hour);
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isAllDegit(String input) {
		if (input !=null) {
			input=input.trim();
		}
		char[] charArray = input.toCharArray();
		boolean valid= true;
		if (charArray !=null && charArray .length>0) {
			for (int i=0; i<charArray.length;i++) {
				if (!Character.isDigit(charArray[i])) {
					valid=false;
					break;
				}
			}
		}
		return valid;
	}
	
	/**
	 * Get duration in minutes
	 * 
	 * @param starttime
	 * @param endTime
	 * @param defaultVal
	 * @return
	 */
	public static int durationMinute(Date starttime, Date endTime, int defaultVal) {
		
		if (endTime == null) {
			return defaultVal;
		}
		long duration = endTime.getTime() - starttime.getTime();
		duration = duration / 1000; //second
		return (int)duration / 60;
	}
	
	/**
	 * Validate password
	 * @param password
	 */
	public static boolean validatePasswordContainName(String password, String name) {
		password =password.toLowerCase();
		String[] words = name.toLowerCase().split(" ");
		
		if (words!=null) {
			for (int i= 0;i <words.length; i++) {
				if (words[i] !=null && words[i].length()>1 && password.indexOf(words[i])>=0) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Validate password length
	 * @param password
	 */
	public static boolean validatePasswordLength(String password) {
		password= password.trim();
		if (password.length() < PASSWD_MIN_LEN) {
			return false;
		}
		boolean hasDigit =false;
		for (int i=0 ; i <password.length();i++) {
			if (Character.isDigit(password.charAt(i))) {
				hasDigit =true;
			}
		}
		return hasDigit;
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public static String withoutHttp(String input) {
		if (input == null) return "";
		String ret = input.toLowerCase();
		if (ret.startsWith("http://")) {
			ret = ret.substring(7);
		} else if (ret.startsWith("https://")) {
			ret = ret.substring(8);
		}
		return ret;
	}
	
	/**
	 * Parse value from duplicate param name to array.
	 * @param params
	 * @return an array of param's value
	 */
	public static String[] parseParamsToArray(String params) {
		if (StringUtils.isEmpty(params)) {
			return null;
		}
		StringTokenizer token = new StringTokenizer(params, "&");
		String[] ret = new String[token.countTokens()];
		int i = 0;
		while (token.hasMoreTokens()) {
			String val = token.nextToken();
			ret[i++] = val.substring(val.indexOf("=")+1, val.length());
		}
		return ret;
	}
	
	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (int i=0; i< bytes.length; i++) {
		    sb.append(String.format("%02x", bytes[i]));
		}
		return sb.toString().toUpperCase();
	}
	
	/**
	 * Get the application full context url
	 * @param request
	 * @param secured
	 * @return
	 */
	public static String getApplicationUrl(String serverName, String contextPath, boolean secured) {
		String url = "";
		if (secured) {
			url="https://";
		} else {
			url="http://";
		}
		url += serverName;
		if (!isEmpty(contextPath)) {
			url += contextPath;
		}
		if (!url.endsWith("/")) {
			url +="/";
		}
		url.replaceAll("//", "/");
		return url;
	}
	
	public static String redirectURL(String host, String uri, boolean secured) {
		String url = host + uri;
		if (secured) {
			url = "https://" + url;
		} else {
			url = "http://" + url;
		}
		return url;
	}
}
