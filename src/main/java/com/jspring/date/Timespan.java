package com.jspring.date;

import com.jspring.Strings;

public final class Timespan implements Comparable<Timespan> {

	public static Timespan valueOfTimeString(String value) {
		String[] t = value.split(":");
		int i = t[2].indexOf('.');
		if (i < 0) {
			return new Timespan(0, 0, 0, Strings.parseInt(t[0]),
					Strings.parseInt(t[1]), Strings.parseInt(t[2]));
		}
		return new Timespan(0, 0, 0, Strings.parseInt(t[0]),
				Strings.parseInt(t[1]), Strings.parseInt(t[2].substring(0, i)));
	}

	private final long _timeInMillis;

	public long getTimeInMillis() {
		return _timeInMillis;
	}

	private final int years, months, weeks, days, hours, minutes, seconds,
			milliseconds;

	public Timespan(long timeInMillis) {
		_timeInMillis = timeInMillis;

		years = (int) (timeInMillis / TimeUnits.Year.milliseconds);
		timeInMillis -= years * TimeUnits.Year.milliseconds;

		months = (int) (timeInMillis / TimeUnits.Month.milliseconds);
		timeInMillis -= months * TimeUnits.Month.milliseconds;

		weeks = (int) (timeInMillis / TimeUnits.Week.milliseconds);
		timeInMillis -= weeks * TimeUnits.Week.milliseconds;

		days = (int) (timeInMillis / TimeUnits.Day.milliseconds);
		timeInMillis -= days * TimeUnits.Day.milliseconds;

		hours = (int) (timeInMillis / TimeUnits.Hour.milliseconds);
		timeInMillis -= hours * TimeUnits.Hour.milliseconds;

		minutes = (int) (timeInMillis / TimeUnits.Minute.milliseconds);
		timeInMillis -= minutes * TimeUnits.Minute.milliseconds;

		seconds = (int) (timeInMillis / TimeUnits.Second.milliseconds);

		milliseconds = (int) (timeInMillis - seconds
				* TimeUnits.Second.milliseconds);
	}

	public Timespan(int years, int months, int days, int hours, int minutes,
			int seconds, int milliseconds) {
		this(new TimespanUnit(years, TimeUnits.Year).getTimeInMillis()
				+ new TimespanUnit(months, TimeUnits.Month).getTimeInMillis()
				+ new TimespanUnit(days, TimeUnits.Day).getTimeInMillis()
				+ new TimespanUnit(hours, TimeUnits.Hour).getTimeInMillis()
				+ new TimespanUnit(minutes, TimeUnits.Minute).getTimeInMillis()
				+ new TimespanUnit(seconds, TimeUnits.Second).getTimeInMillis()
				+ milliseconds);
	}

	public Timespan(int years, int months, int days, int hours, int minutes,
			int seconds) {
		this(new TimespanUnit(years, TimeUnits.Year).getTimeInMillis()
				+ new TimespanUnit(months, TimeUnits.Month).getTimeInMillis()
				+ new TimespanUnit(days, TimeUnits.Day).getTimeInMillis()
				+ new TimespanUnit(hours, TimeUnits.Hour).getTimeInMillis()
				+ new TimespanUnit(minutes, TimeUnits.Minute).getTimeInMillis()
				+ new TimespanUnit(seconds, TimeUnits.Second).getTimeInMillis());
	}

	public Timespan(int years, int months, int days) {
		this(new TimespanUnit(years, TimeUnits.Year).getTimeInMillis()
				+ new TimespanUnit(months, TimeUnits.Month).getTimeInMillis()
				+ new TimespanUnit(days, TimeUnits.Day).getTimeInMillis());
	}

	public int getYears() {
		return years;
	}

	public int getMonths() {
		return months;
	}

	public int getWeeks() {
		return weeks;
	}

	public int getDays() {
		return days;
	}

	public int getHours() {
		return hours;
	}

	public int getMinutes() {
		return minutes;
	}

	public int getSeconds() {
		return seconds;
	}

	public int getMilliseconds() {
		return milliseconds;
	}

	public Timespan add(Timespan span) {
		return addMillisecond(span.getTimeInMillis());
	}

	public Timespan add(TimespanUnit time) {
		return addMillisecond(time.getTimeInMillis());
	}

	public Timespan addYear(int year) {
		return add(new TimespanUnit(year, TimeUnits.Year));
	}

	public Timespan addMonth(int month) {
		return add(new TimespanUnit(month, TimeUnits.Month));
	}

	public Timespan addDay(int day) {
		return add(new TimespanUnit(day, TimeUnits.Day));
	}

	public Timespan addHour(int hour) {
		return add(new TimespanUnit(hour, TimeUnits.Hour));
	}

	public Timespan addMinute(int minute) {
		return add(new TimespanUnit(minute, TimeUnits.Minute));
	}

	public Timespan addSecond(int second) {
		return add(new TimespanUnit(second, TimeUnits.Second));
	}

	public Timespan addMillisecond(long millisecond) {
		return new Timespan(getTimeInMillis() + millisecond);
	}

	public Timespan substract(Timespan begin) {
		return new Timespan(getTimeInMillis() - begin.getTimeInMillis());
	}

	public String toString(char split, String yearName, String monthName,
			String weekName, String dayName, String hourName,
			String minuteName, String secondName, String millsecondName) {
		StringBuilder sb = new StringBuilder();
		if (getYears() != 0) {
			sb.append(split);
			sb.append(String.valueOf(getYears()));
			sb.append(yearName);
		}
		if (getMonths() != 0) {
			sb.append(split);
			sb.append(String.valueOf(getMonths()));
			sb.append(monthName);
		}
		if (getWeeks() != 0) {
			sb.append(split);
			sb.append(String.valueOf(getWeeks()));
			sb.append(weekName);
		}
		if (getDays() != 0) {
			sb.append(split);
			sb.append(String.valueOf(getDays()));
			sb.append(dayName);
		}
		if (getHours() != 0) {
			sb.append(split);
			sb.append(String.valueOf(getHours()));
			sb.append(hourName);
		}
		if (getMinutes() != 0) {
			sb.append(split);
			sb.append(String.valueOf(getMinutes()));
			sb.append(minuteName);
		}
		if (getSeconds() != 0) {
			sb.append(split);
			sb.append(String.valueOf(getSeconds()));
			sb.append(secondName);
		}
		if (getMilliseconds() != 0) {
			sb.append(split);
			sb.append(String.valueOf(getMilliseconds()));
			sb.append(millsecondName);
		}
		if (sb.length() > 0) {
			return sb.substring(1);
		}
		return "0" + millsecondName;
	}

	public String toString(char split, char yearName, String monthName,
			char weekName, char dayName, char hourName, char minuteName,
			char secondName, String millsecondName) {
		StringBuilder sb = new StringBuilder();
		if (getYears() != 0) {
			sb.append(split);
			sb.append(String.valueOf(getYears()));
			sb.append(yearName);
		}
		if (getMonths() != 0) {
			sb.append(split);
			sb.append(String.valueOf(getMonths()));
			sb.append(monthName);
		}
		if (getWeeks() != 0) {
			sb.append(split);
			sb.append(String.valueOf(getWeeks()));
			sb.append(weekName);
		}
		if (getDays() != 0) {
			sb.append(split);
			sb.append(String.valueOf(getDays()));
			sb.append(dayName);
		}
		if (getHours() != 0) {
			sb.append(split);
			sb.append(String.valueOf(getHours()));
			sb.append(hourName);
		}
		if (getMinutes() != 0) {
			sb.append(split);
			sb.append(String.valueOf(getMinutes()));
			sb.append(minuteName);
		}
		if (getSeconds() != 0) {
			sb.append(split);
			sb.append(String.valueOf(getSeconds()));
			sb.append(secondName);
		}
		if (getMilliseconds() != 0) {
			sb.append(split);
			sb.append(String.valueOf(getMilliseconds()));
			sb.append(millsecondName);
		}
		if (sb.length() > 0) {
			return sb.substring(1);
		}
		return "0" + millsecondName;
	}

	public String toLongString(char split) {
		return toString(split, "years", "months", "weeks", "days", "hours",
				"minutes", "seconds", "millseconds");
	}

	public String toLongString() {
		return toLongString(' ');
	}

	public String toShortString(char split) {
		return toString(split, 'y', "mth", 'w', 'd', 'h', 'm', 's', "ms");
	}

	public String toShortString() {
		return toShortString(' ');
	}

	@Override
	public String toString() {
		return toShortString();
	}

	@Override
	public boolean equals(Object o) {
		if (null != o && o instanceof Timespan) {
			return getTimeInMillis() == ((Timespan) o).getTimeInMillis();
		}
		return false;
	}

	@Override
	public int compareTo(Timespan span) {
		if (getTimeInMillis() > span.getTimeInMillis()) {
			return 1;
		}
		if (getTimeInMillis() < span.getTimeInMillis()) {
			return -1;
		}
		return 0;
	}

}