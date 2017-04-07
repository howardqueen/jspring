package com.jspring.log;

public class AccessLogItem {
	public String time;
	public String ip;
	public String ua;
	public AccessLogUrl referrer;
	//
	public String method;
	public AccessLogUrl url;
	public String cookie;
	//
	public int timespan;
	public int size;
	public int status;
	public int timewait;

	public double getSpeedKpsBySecond() {
		return (double) size / 1024 * 8000 / timespan;
	}

	public static String getCsvTitle() {
		StringBuilder sb = new StringBuilder();
		sb.append("time");
		sb.append(',');
		sb.append("ip");
		sb.append(',');
		sb.append("ua");
		sb.append(',');
		AccessLogUrl.appendCsvTitlesTo(sb);
		sb.append(',');
		sb.append("method");
		sb.append(',');
		AccessLogUrl.appendCsvTitlesTo(sb);
		sb.append(',');
		sb.append("cookie");
		sb.append(',');
		sb.append("timespan");
		sb.append(',');
		sb.append("size");
		sb.append(',');
		sb.append("status");
		sb.append(',');
		sb.append("timewait");
		return sb.toString();
	}

	public String toCsvString() {
		StringBuilder sb = new StringBuilder();
		sb.append('\'');
		sb.append(time);
		sb.append('\'');
		sb.append(',');
		sb.append('\'');
		sb.append(ip);
		sb.append('\'');
		sb.append(',');
		sb.append('\'');
		sb.append(ua);
		sb.append('\'');
		sb.append(',');
		this.referrer.appendTo(sb);
		sb.append(',');
		sb.append('\'');
		sb.append(method);
		sb.append('\'');
		sb.append(',');
		url.appendTo(sb);
		sb.append(',');
		sb.append('\'');
		sb.append(cookie);
		sb.append('\'');
		sb.append(',');
		sb.append(timespan);
		sb.append(',');
		sb.append(size);
		sb.append(',');
		sb.append(status);
		sb.append(',');
		sb.append(timewait);
		return sb.toString();
	}

	@Override
	public String toString() {
		return toCsvString();
	}

}
