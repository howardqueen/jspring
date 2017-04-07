package com.jspring.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.jspring.Exceptions;

/**
 * @author HowardQian(howard.queen@qq.com), 2012-05-04 12:28
 */
public final class DateFormats {
	private DateFormats() {
	}

	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final DateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * yyyy-MM-dd
	 */
	public static final DateFormat date = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final DateFormat shortDate = new SimpleDateFormat("yyyyMMdd");

	/**
	 * yyyyMMdd
	 */
	public static final DateFormat time = new SimpleDateFormat("HH:mm:ss");

	/**
	 * HH:mm:ss.SSS
	 */
	public static final DateFormat milliTime = new SimpleDateFormat("HH:mm:ss.SSS");

	public static final int getMonth(String monthName) {
		if ("Jan".equals(monthName)) {
			return 1;
		} else if ("Feb".equals(monthName)) {
			return 2;
		} else if ("Mar".equals(monthName)) {
			return 3;
		} else if ("Apr".equals(monthName)) {
			return 4;
		} else if ("May".equals(monthName)) {
			return 5;
		} else if ("Jun".equals(monthName)) {
			return 6;
		} else if ("Jul".equals(monthName)) {
			return 7;
		} else if ("Aug".equals(monthName)) {
			return 8;
		} else if ("Sep".equals(monthName)) {
			return 9;
		} else if ("Oct".equals(monthName)) {
			return 10;
		} else if ("Nov".equals(monthName)) {
			return 11;
		} else if ("Dec".equals(monthName)) {
			return 11;
		} else {
			throw Exceptions.newInstance("Illegal month name " + monthName);
		}
	}

}
