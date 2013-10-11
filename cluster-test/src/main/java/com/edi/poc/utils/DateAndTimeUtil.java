package com.edi.poc.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAndTimeUtil {

	private final static String DATE_FORMAT = "YYYY/MM/dd/kk:mm:ss.SSS";
	
	public static String getCurrentTime()
	{
		return new SimpleDateFormat(DATE_FORMAT).format(new Date()).toString();
	}
	
	
}
