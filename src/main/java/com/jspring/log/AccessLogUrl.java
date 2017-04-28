package com.jspring.log;

import com.jspring.Strings;

public class AccessLogUrl {
	private String domain;// baidu.com abc.com google.com.hk
	// private String protocal;// HTTP HTTPS
	private String subDomain;// www abc.agc
	private int port; // 8080
	private String path; // /map/user /
	private String filename;
	private String queryString;// user=a&debug=1

	// ///////////////////////////////////////
	// /
	// ///////////////////////////////////////
	public String getDomain() {
		return domain;
	}

	/*
	 * public String getProtocal() { return protocal; }
	 */

	public String getSubDomain() {
		return subDomain;
	}

	public int getPort() {
		return port;
	}

	public String getPath() {
		return path;
	}

	public String getFilename() {
		return filename;
	}

	public String getQueryString() {
		return queryString;
	}

	// ///////////////////////////////////////
	// /
	// ///////////////////////////////////////
	private String _fullUrl;

	public String getFullUrl() {
		if (null == _fullUrl) {
			if ("-".equals(getQueryString())) {
				_fullUrl = getBaseUrl();
			} else {
				_fullUrl = getBaseUrl() + "?" + getQueryString();
			}
		}
		return _fullUrl;
	}

	private String _baseUrl;

	/**
	 * Without QueryString
	 * 
	 * @return
	 */
	public String getBaseUrl() {
		if (null == _baseUrl) {
			if (port > 0) {
				if (port == 80) {// protocal + "://" +
					_baseUrl = subDomain + "." + domain + getPath() + getFilename();
				} else {
					_baseUrl = subDomain + "." + domain + ":" + String.valueOf(port) + getPath() + getFilename();
				}
			} else {
				_baseUrl = "localhost" + getPath() + getFilename();
			}
		}
		return _baseUrl;
	}

	private String _activeUrl;

	public String getActiveUrl() {
		if (null == _activeUrl) {
			int index = -1;
			if (getPath().length() > 1) {
				String[] t = getPath().substring(1).split("/");
				//
				for (int i = 0; i < t.length; i++) {
					if (isActivePart(t[i], 16)) {
						index = i;
						break;
					}
				}
				if (index == 0) {
					if (port > 0) {
						if (port == 80) {// protocal + "://" +
							_activeUrl = subDomain + "." + domain + "/?";
							return _activeUrl;
						} else {
							_activeUrl = subDomain + "." + domain + ":" + String.valueOf(port) + "/?";
							return _activeUrl;
						}
					} else {
						_activeUrl = "localhost" + "/?";
						return _activeUrl;
					}
				} else if (index > 0) {
					StringBuilder path = new StringBuilder();
					for (int i = 0; i < index; i++) {
						path.append('/');
						path.append(t[i]);
					}
					path.append('/');
					path.append('?');
					if (port > 0) {
						if (port == 80) {// protocal + "://" +
							_activeUrl = subDomain + "." + domain + path.toString();
							return _activeUrl;
						} else {
							_activeUrl = subDomain + "." + domain + ":" + String.valueOf(port) + path.toString();
							return _activeUrl;
						}
					} else {
						_activeUrl = "localhost" + path.toString();
						return _activeUrl;
					}
				}
			}
			int i;
			if ((i = getFilename().lastIndexOf('.')) >= 0) {
				if (isActivePart(getFilename().substring(0, i), 8)) {
					if (port > 0) {
						if (port == 80) {// protocal + "://" +
							_activeUrl = subDomain + "." + domain + getPath() + "?";
							return _activeUrl;
						} else {
							_activeUrl = subDomain + "." + domain + ":" + String.valueOf(port) + getPath() + "?";
							return _activeUrl;
						}
					} else {
						_activeUrl = "localhost" + getPath() + "?";
						return _activeUrl;
					}
				}
			} else {
				if (isActivePart(getFilename(), 8)) {
					if (port > 0) {
						if (port == 80) {// protocal + "://" +
							_activeUrl = subDomain + "." + domain + getPath() + "?";
							return _activeUrl;
						} else {
							_activeUrl = subDomain + "." + domain + ":" + String.valueOf(port) + getPath() + "?";
							return _activeUrl;
						}
					} else {
						_activeUrl = "localhost" + getPath() + "?";
						return _activeUrl;
					}
				}
			}
			_activeUrl = getBaseUrl();
		}
		return _activeUrl;
	}

	public static String[] SPLITE_CHARS = new String[] { "_", "-", "\\." };

	private boolean isActivePart(String part, int maxWordLength) {
		if (part.length() == 0) {
			return false;
		}
		boolean hit = false;
		for (String c : SPLITE_CHARS) {
			String[] t = part.split(c);
			if (t.length > 1) {
				for (String s : t) {//
					if (s.length() > maxWordLength && !Strings.containUpperCase(s)) {
						return true;
					}
					if (Strings.isNumeric(s)) {
						return true;
					}
				}
				hit = true;
			}
		}
		if (hit) {
			return false;
		}
		if (part.length() > maxWordLength && !Strings.containUpperCase(part)) {
			return true;
		}
		if (Strings.isNumeric(part)) {
			return true;
		}
		return false;
	}

	// ///////////////////////////////////////
	// /
	// ///////////////////////////////////////
	private AccessLogUrl() {
	}

	// ///////////////////////////////////////
	// /
	// ///////////////////////////////////////
	public static void appendCsvTitlesTo(StringBuilder builder) {
		// builder.append("protocal");
		// builder.append(',');
		builder.append("subDomain");
		builder.append(',');
		builder.append("domain");
		builder.append(',');
		builder.append("port");
		builder.append(',');
		builder.append("path");
		builder.append(',');
		builder.append("filename");
		builder.append(',');
		builder.append("queryString");
	}

	public void appendTo(StringBuilder builder) {
		// builder.append('\'');
		// builder.append(protocal);
		// builder.append('\'');
		// builder.append(',');
		builder.append('\'');
		builder.append(subDomain);
		builder.append('\'');
		builder.append(',');
		builder.append('\'');
		builder.append(domain);
		builder.append('\'');
		builder.append(',');
		builder.append(port);
		builder.append(',');
		builder.append('\'');
		builder.append(path);
		builder.append('\'');
		builder.append(',');
		builder.append('\'');
		builder.append(filename);
		builder.append('\'');
		builder.append(',');
		builder.append('\'');
		builder.append(queryString);
		builder.append('\'');
	}

	// ///////////////////////////////////////
	// /
	// ///////////////////////////////////////
	public static final String[] ROOT_DOMAINS = new String[] { "com", "cn", "net", "org", "biz", "info", "cc", "tv" };
	public static final char[] ILLEGLE_CHARS = new char[] { '#', ';', '=', '%', '&', ' ', '>', '<', '\'', ':' };

	public static AccessLogUrl newInstance(String fullUrl) {
		return newInstanceWithWhiteList(fullUrl, null);
	}

	public static AccessLogUrl newInstance(String protocal, String rootUrl, String pathAndQueryString) {
		return newInstanceWithWhiteList(protocal, rootUrl, pathAndQueryString, null);
	}

	public static AccessLogUrl newInstanceWithWhiteList(String fullUrl, String[] extentionWhiteList) {
		int i = fullUrl.indexOf("://");
		if (i < 0) {
			int j = fullUrl.indexOf('/', i + 3);
			if (j < 0) {
				return newInstanceWithWhiteList("http", fullUrl, "/", extentionWhiteList);
			}
			return newInstanceWithWhiteList("http", fullUrl.substring(0, j), fullUrl.substring(j), extentionWhiteList);
		}
		int j = fullUrl.indexOf('/', i + 3);
		if (j < 0) {
			return newInstanceWithWhiteList(fullUrl.substring(0, i), fullUrl.substring(i + 3), "/", extentionWhiteList);
		}
		return newInstanceWithWhiteList(fullUrl.substring(0, i), fullUrl.substring(i + 3, j), fullUrl.substring(j),
				extentionWhiteList);
	}

	/**
	 * 
	 * @param protocal
	 *            http https
	 * @param rootUrl
	 *            www.baidu.com ditu.google.com.cn
	 * @param pathAndQueryString
	 *            /user/show?id=123213
	 * @return
	 */
	public static AccessLogUrl newInstanceWithWhiteList(String protocal, String rootUrl, String pathAndQueryString,
			String[] extentionWhiteList) {
		AccessLogUrl r = new AccessLogUrl();
		// PROTOCAL
		// r.protocal = protocal.toLowerCase();
		// if (!r.protocal.equals("http") && !r.protocal.equals("https")) {
		// return null;
		// }
		// DOMAIN AND SUB_DOMAIN
		if (null == rootUrl) {
			r.port = 0;
			r.subDomain = null;
			r.domain = null;
		} else {
			if (rootUrl.indexOf('/') >= 0) {
				return null;
			}
			int i = rootUrl.lastIndexOf(':');
			if (i < 0) {
				r.port = 80;
			} else if (i == 0) {
				return null;
			} else {
				r.port = Strings.tryParseInt(rootUrl.substring(i + 1), 0);
				if (r.port <= 0) {
					return null;
				}
				rootUrl = rootUrl.substring(0, i);
			}
			if (rootUrl.indexOf('.') < 0) {
				return null;
			}
			String[] t = rootUrl.split("\\.");
			if (t.length < 2) {
				return null;
			}
			if (Strings.isNumeric(t[t.length - 1])) {
				return null;
			}
			boolean isEnd2 = false;
			String t1 = t[t.length - 2];
			for (String d : ROOT_DOMAINS) {
				if (d.equals(t1)) {
					isEnd2 = true;
					break;
				}
			}
			if (isEnd2) {
				if (t.length < 3) {
					return null;
				}
				if (t.length == 3) {
					r.subDomain = "www";
					r.domain = rootUrl;
				} else {
					i = rootUrl.length() - t[t.length - 1].length() - t[t.length - 2].length()
							- t[t.length - 3].length() - 3;
					r.subDomain = rootUrl.substring(0, i).toLowerCase();
					r.domain = rootUrl.substring(i + 1).toLowerCase();
				}
			} else {
				if (t.length == 2) {
					r.subDomain = "www";
					r.domain = rootUrl;
				} else {
					i = rootUrl.length() - t[t.length - 1].length() - t[t.length - 2].length() - 2;
					r.subDomain = rootUrl.substring(0, i).toLowerCase();
					r.domain = rootUrl.substring(i + 1).toLowerCase();
				}
			}
		}
		//
		if (pathAndQueryString.charAt(0) != '/') {
			return null;
		}
		if (pathAndQueryString.length() == 1) {
			r.path = "/";
			r.filename = "";
			r.queryString = "-";
			return r;
		}
		int i = pathAndQueryString.indexOf('?');
		if (i < 0) {
			r.path = pathAndQueryString;
			r.queryString = "-";
		} else {
			r.path = pathAndQueryString.substring(0, i);
			if (i == pathAndQueryString.length() - 1) {
				r.queryString = "-";
			} else {
				r.queryString = pathAndQueryString.substring(i + 1).replace('?', '&');
			}
		}
		for (char c : ILLEGLE_CHARS) {
			if ((i = r.path.indexOf(c)) >= 0) {
				r.path = r.path.substring(0, i);
			}
		}
		r.path = r.path.replace("//", "/");
		//
		i = r.path.lastIndexOf('/');
		if (i == 0) {
			r.filename = r.path.substring(1);
			r.path = "/";
		} else {
			// 为了保持目录的简洁可聚合，查询目录中的动态部分，作为文件名处理，从目录中排除
			int activeIndex = i;
			for (int m = i - 1; m >= 0; m--) {
				char ca = r.path.charAt(m);
				if (ca == '/') {
					activeIndex = m;
					continue;
				}
				if (ca < 42 || ca > 57) {
					break;
				}
			}
			// 按常规处理
			if (activeIndex == r.path.length() - 1) {
				r.filename = "";
			} else {
				r.filename = r.path.substring(activeIndex + 1);
				r.path = r.path.substring(0, activeIndex + 1);
			}
		}
		//
		if ((i = r.filename.lastIndexOf('.')) >= 0) {
			String ext = r.filename.substring(i + 1).toLowerCase();
			if (null == extentionWhiteList || extentionWhiteList.length == 0) {
				r.filename = r.filename.substring(0, i) + "." + ext;
				return r;
			}
			for (String e : extentionWhiteList) {
				if (e.equalsIgnoreCase(ext)) {
					r.filename = r.filename.substring(0, i) + "." + ext;
					return r;
				}
			}
			// Illegle extention
			return null;
		}
		//
		return r;
	}

	public static AccessLogUrl newInstanceWithBlackList(String fullUrl, String[] extentionBlackList) {
		int i = fullUrl.indexOf("://");
		if (i < 0) {
			int j = fullUrl.indexOf('/', i + 3);
			if (j < 0) {
				return newInstanceWithBlackList("http", fullUrl, "/", extentionBlackList);
			}
			return newInstanceWithBlackList("http", fullUrl.substring(0, j), fullUrl.substring(j), extentionBlackList);
		}
		int j = fullUrl.indexOf('/', i + 3);
		if (j < 0) {
			return newInstanceWithBlackList(fullUrl.substring(0, i), fullUrl.substring(i + 3), "/", extentionBlackList);
		}
		return newInstanceWithBlackList(fullUrl.substring(0, i), fullUrl.substring(i + 3, j), fullUrl.substring(j),
				extentionBlackList);
	}

	public static AccessLogUrl newInstanceWithBlackList(String protocal, String rootUrl, String pathAndQueryString,
			String[] extentionBlackList) {
		AccessLogUrl r = new AccessLogUrl();
		// PROTOCAL
		// r.protocal = protocal.toLowerCase();
		// if (!r.protocal.equals("http") && !r.protocal.equals("https")) {
		// return null;
		// }
		// DOMAIN AND SUB_DOMAIN
		if (null == rootUrl) {
			r.port = 0;
			r.subDomain = null;
			r.domain = null;
		} else {
			if (rootUrl.indexOf('/') >= 0) {
				return null;
			}
			int i = rootUrl.lastIndexOf(':');
			if (i < 0) {
				r.port = 80;
			} else if (i == 0) {
				return null;
			} else {
				r.port = Strings.tryParseInt(rootUrl.substring(i + 1), 0);
				if (r.port <= 0) {
					return null;
				}
				rootUrl = rootUrl.substring(0, i);
			}
			if (rootUrl.indexOf('.') < 0) {
				return null;
			}
			String[] t = rootUrl.split("\\.");
			if (t.length < 2) {
				return null;
			}
			if (Strings.isNumeric(t[t.length - 1])) {
				return null;
			}
			boolean isEnd2 = false;
			String t1 = t[t.length - 2];
			for (String d : ROOT_DOMAINS) {
				if (d.equals(t1)) {
					isEnd2 = true;
					break;
				}
			}
			if (isEnd2) {
				if (t.length < 3) {
					return null;
				}
				if (t.length == 3) {
					r.subDomain = "www";
					r.domain = rootUrl;
				} else {
					i = rootUrl.length() - t[t.length - 1].length() - t[t.length - 2].length()
							- t[t.length - 3].length() - 3;
					r.subDomain = rootUrl.substring(0, i).toLowerCase();
					r.domain = rootUrl.substring(i + 1).toLowerCase();
				}
			} else {
				if (t.length == 2) {
					r.subDomain = "www";
					r.domain = rootUrl;
				} else {
					i = rootUrl.length() - t[t.length - 1].length() - t[t.length - 2].length() - 2;
					r.subDomain = rootUrl.substring(0, i).toLowerCase();
					r.domain = rootUrl.substring(i + 1).toLowerCase();
				}
			}
		}
		//
		if (pathAndQueryString.charAt(0) != '/') {
			return null;
		}
		if (pathAndQueryString.length() == 1) {
			r.path = "/";
			r.filename = "";
			r.queryString = "-";
			return r;
		}
		int i = pathAndQueryString.indexOf('?');
		if (i < 0) {
			r.path = pathAndQueryString;
			r.queryString = "-";
		} else {
			r.path = pathAndQueryString.substring(0, i);
			if (i == pathAndQueryString.length() - 1) {
				r.queryString = "-";
			} else {
				r.queryString = pathAndQueryString.substring(i + 1).replace('?', '&');
			}
		}
		for (char c : ILLEGLE_CHARS) {
			if ((i = r.path.indexOf(c)) >= 0) {
				r.path = r.path.substring(0, i);
			}
		}
		r.path = r.path.replace("//", "/");
		//
		i = r.path.lastIndexOf('/');
		if (i == 0) {
			r.filename = r.path.substring(1);
			r.path = "/";
		} else {
			// 为了保持目录的简洁可聚合，查询目录中的动态部分，作为文件名处理，从目录中排除
			int activeIndex = i;
			for (int m = i - 1; m >= 0; m--) {
				char ca = r.path.charAt(m);
				if (ca == '/') {
					activeIndex = m;
					continue;
				}
				if (ca < 42 || ca > 57) {
					break;
				}
			}
			// 按常规处理
			if (activeIndex == r.path.length() - 1) {
				r.filename = "";
			} else {
				r.filename = r.path.substring(activeIndex + 1);
				r.path = r.path.substring(0, activeIndex + 1);
			}
		}
		//
		if ((i = r.filename.lastIndexOf('.')) >= 0) {
			String ext = r.filename.substring(i + 1).toLowerCase();
			if (null == extentionBlackList || extentionBlackList.length == 0) {
				r.filename = r.filename.substring(0, i) + "." + ext;
				return r;
			}
			for (String e : extentionBlackList) {
				if (e.equalsIgnoreCase(ext)) {
					return null;
				}
			}
			r.filename = r.filename.substring(0, i) + "." + ext;
			return null;
		}
		//
		return r;
	}

}
