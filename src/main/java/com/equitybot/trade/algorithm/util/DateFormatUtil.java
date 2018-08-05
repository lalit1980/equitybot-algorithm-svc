package com.equitybot.trade.algorithm.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DateFormatUtil {
	public static String  getCurrentISTTime() {
		Calendar currentdate = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		TimeZone obj = TimeZone.getTimeZone("IST");
		formatter.setTimeZone(obj);
		return formatter.format(currentdate.getTime());
    }
}
