package com.jspring.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.jspring.Exceptions;

public final class DateTime implements Comparable<DateTime> {

	private final long _timeInMillis;

	public long getTimeInMillis() {
		return _timeInMillis;
	}

	public DateTime(long timeInMillis) {
		this._timeInMillis = timeInMillis;
	}

	/**
	 * @param month
	 *            Start with 1.
	 */
	public DateTime(int year, int month, int day, int hour, int minute,
			int second, int milliseconds) {
		Calendar c = getCalendar();
		c.set(year, month - 1, day, hour, minute, second);
		c.set(Calendar.MILLISECOND, milliseconds);
		this._timeInMillis = c.getTimeInMillis();
	}

	/**
	 * @param month
	 *            Start with 1.
	 */
	public DateTime(int year, int month, int day, int hour, int minute,
			int second) {
		this(year, month, day, hour, minute, second, 0);
	}

	/**
	 * @param month
	 *            Start with 1.
	 */
	public DateTime(int year, int month, int day) {
		this(year, month, day, 0, 0, 0, 0);
	}

	public DateTime(Date localTime) {
		this(localTime.getTime());
	}

	public DateTime(Calendar calendar) {
		this(calendar.getTimeInMillis());// Recreate calendar for safety
	}

	private Calendar _calendar;

	private Calendar getCalendar() {
		if (null == _calendar) {
			_calendar = Calendar.getInstance();
			_calendar.setTimeInMillis(getTimeInMillis());
		}
		return _calendar;
	}

	public int getYear() {
		return getCalendar().get(Calendar.YEAR);
	}

	/**
	 * @return Start with 1.
	 */
	public int getMonth() {
		return getCalendar().get(Calendar.MONTH) + 1;
	}

	public int getDay() {
		return getCalendar().get(Calendar.DATE);
	}

	public int getWeekDay() {
		return getCalendar().get(Calendar.DAY_OF_WEEK);
	}

	public int getHour() {
		return getCalendar().get(Calendar.HOUR_OF_DAY);
	}

	public int getMinute() {
		return getCalendar().get(Calendar.MINUTE);
	}

	public int getSecond() {
		return getCalendar().get(Calendar.SECOND);
	}

	public int getMillisecond() {
		return getCalendar().get(Calendar.MILLISECOND);
	}

	public Date getLocalDate() {
		return new Date(getTimeInMillis());
	}

	public DateTime add(Timespan span) {
		return addMillisecond(span.getTimeInMillis());
	}

	public DateTime add(TimespanUnit time) {
		return addMillisecond(time.getTimeInMillis());
	}

	public DateTime addYear(int year) {
		return add(new TimespanUnit(year, TimeUnits.Year));
	}

	public DateTime addMonth(int month) {
		return add(new TimespanUnit(month, TimeUnits.Month));
	}

	public DateTime addDay(int day) {
		return add(new TimespanUnit(day, TimeUnits.Day));
	}

	public DateTime addHour(int hour) {
		return add(new TimespanUnit(hour, TimeUnits.Hour));
	}

	public DateTime addMinute(int minute) {
		return add(new TimespanUnit(minute, TimeUnits.Minute));
	}

	public DateTime addSecond(int second) {
		return add(new TimespanUnit(second, TimeUnits.Second));
	}

	public DateTime addMillisecond(long millisecond) {
		return new DateTime(getTimeInMillis() + millisecond);
	}

	public DateTime ceilByMinutes(int minutesStep) {
		long t = minutesStep * 60 * 1000;
		return new DateTime(this.getTimeInMillis() / t * t);
	}

	public Timespan timespanFrom(DateTime begin) {
		return new Timespan(getTimeInMillis() - begin.getTimeInMillis());
	}

	public Timespan timespanToNow() {
		return DateTime.getNow().timespanFrom(this);
	}

	public boolean isPassed() {
		return getNowTimeMillis() > getTimeInMillis();
	}

	private String dateTimeString;

	public String toDateTimeString() {
		if (null == dateTimeString) {
			dateTimeString = toString(DateFormats.dateTime);
		}
		return dateTimeString;
	}

	private String dateString;

	public String toDateString() {
		if (null == dateString) {
			dateString = toString(DateFormats.date);
		}
		return dateString;
	}

	private String shortDateString;

	public String toShortDateString() {
		if (null == shortDateString) {
			shortDateString = toString(DateFormats.shortDate);
		}
		return shortDateString;
	}

	private String timeString;

	public String toTimeString() {
		if (null == timeString) {
			timeString = toString(DateFormats.time);
		}
		return timeString;
	}

	private String milliTimeString;

	public String toMilliTimeString() {
		if (null == milliTimeString) {
			milliTimeString = toString(DateFormats.milliTime);
		}
		return milliTimeString;
	}

	public String toString(String simpleDateFormat) {
		return toString(new SimpleDateFormat(simpleDateFormat));
	}

	public String toString(DateFormat format) {
		if (this._timeInMillis == 0) {
			return "";
		}
		return format.format(getLocalDate());
	}

	@Override
	public String toString() {
		return toDateTimeString();
	}

	@Override
	public boolean equals(Object o) {
		if (null != o && o instanceof DateTime) {
			return getTimeInMillis() == ((DateTime) o).getTimeInMillis();
		}
		return false;
	}

	@Override
	public int compareTo(DateTime time) {
		if (getTimeInMillis() > time.getTimeInMillis()) {
			return 1;
		}
		if (getTimeInMillis() < time.getTimeInMillis()) {
			return -1;
		}
		return 0;
	}

	public static long getNowTimeMillis() {
		return System.currentTimeMillis();
	}

	public static DateTime getNow() {
		return new DateTime(System.currentTimeMillis());
	}

	public static DateTime getToday() {
		DateTime now = getNow();
		return new DateTime(now.getYear(), now.getMonth(), now.getDay());
	}

	public static final DateTime ZeroValue = new DateTime(0);

	/*
	 * yyyy-MM-dd HH:mm:ss yyyy-MM-dd yyyyMMdd
	 */
	public static DateTime valueOf(String value) {
		if (value.length() == 0 || value.equals("0")) {
			return ZeroValue;
		}
		if (value.indexOf(':') > 0) {
			return valueOf(value, DateFormats.dateTime);
		}
		if (value.indexOf('-') > 0) {
			return valueOf(value, DateFormats.date);
		}
		return valueOf(value, DateFormats.shortDate);
	}

	public static DateTime valueOf(String value, DateFormat format) {
		if (value.length() == 0 || value.equals("0")) {
			return ZeroValue;
		}
		try {
			return new DateTime(format.parse(value).getTime());
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	public static DateTime valueOf(String value, String format) {
		if (value.length() == 0 || value.equals("0")) {
			return ZeroValue;
		}
		try {
			return new DateTime(new SimpleDateFormat(format).parse(value)
					.getTime());
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	public static DateTime valueOfDate(String value) {
		return valueOf(value, DateFormats.date);
	}

	public static DateTime valueOfDateTime(String value) {
		return valueOf(value, DateFormats.dateTime);
	}

	public static DateTime valueOfShortDate(String value) {
		return valueOf(value, DateFormats.shortDate);
	}

}
