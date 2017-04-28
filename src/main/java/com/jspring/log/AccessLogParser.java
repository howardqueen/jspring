package com.jspring.log;

/**
 * @author HowardQian(howard.queen@qq.com), 2012-04-26 13:30
 */
public class AccessLogParser implements ILogParser<AccessLogItem> {

	@Override
	public AccessLogItem tryParseLine(String line) {
		try {
			return parseLine(line);
		} catch (Exception e) {
			return null;
		}
	}

	private String[] shortPaths;

	public void setShortPaths(String[] value) {
		shortPaths = value;
	}

	public String[] getShortPaths() {
		return shortPaths;
	}

	public AccessLogItem parseLine(String line) {
		/*
		 * ip - - [date:time +timezone] timespan "method url protocal" size
		 * status "pre" "ua" "timewait" 119.167.145.200 - -
		 * [13/Aug/2013:00:06:03 +0800] 1222
		 * "GET /2013-05/25/0400020100519FFDFE92E3DBC801FF149FF690-4F63-0345-7BBD-D4984277BDD7.flv?bfrom=14712832&blength=-7&needsize=0&sid=1 HTTP/1.1"
		 * 34425249 206 "0" "" "102"
		 */
		AccessLogItem r = new AccessLogItem();
		// ip
		int i = line.indexOf(' ');
		r.ip = line.substring(0, i);
		// datetime
		i = line.indexOf(':', i + 1) + 1;
		int j = line.indexOf(' ', i + 1);
		r.time = line.substring(i, j);
		// timezone
		// i = line.indexOf('+', j + 1);
		// j = line.indexOf(']', i + 1);
		// r.timezone = line.substring(i, j);
		// timespan
		i = line.indexOf("] ", j + 1) + 2;
		j = line.indexOf(' ', i + 1);
		r.timespan = Integer.parseInt(line.substring(i, j));
		// method
		i = line.indexOf('\"', j + 1) + 1;
		j = line.indexOf(' ', i + 1);
		r.method = line.substring(i, j);
		// url
		i = line.indexOf(' ', j + 1);
		String url = line.substring(j + 1, i);
		// protocal
		j = line.indexOf('\"', i + 1);
		String protocal = line.substring(i + 1, j);
		r.url = AccessLogUrl.newInstance(protocal, null, url, shortPaths);// ;
		// size
		i = line.indexOf(' ', j + 1) + 1;
		j = line.indexOf(' ', i + 1);
		r.size = Integer.parseInt(line.substring(i, j));
		// status
		i = line.indexOf(' ', j + 1);
		r.status = Integer.parseInt(line.substring(j + 1, i));
		// pre
		i = line.indexOf('\"', i + 1) + 1;
		j = line.indexOf('\"', i);
		r.referrer = AccessLogUrl.newInstance(line.substring(i, j), shortPaths);
		// ua
		i = line.indexOf('\"', j + 1) + 1;
		j = line.indexOf('\"', i);
		r.ua = line.substring(i, j);
		// timewait
		i = line.indexOf('\"', j + 1) + 1;
		j = line.indexOf('\"', i + 1);
		r.timewait = Integer.parseInt(line.substring(i, j));
		return r;
	}

}
