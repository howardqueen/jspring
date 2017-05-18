package com.jspring.net;

import com.jspring.Encodings;
import com.jspring.Environment;
import com.jspring.Exceptions;
import com.jspring.Strings;
import com.jspring.collections.ICallbacks;
import com.jspring.collections.KeyValue;
import com.jspring.collections.Percents;
import com.jspring.web.ContentTypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

//import sun.misc.BASE64Encoder;

//@SuppressWarnings("restriction")
public final class WebClient {

	// private static final ILog log = LogManager.newLog(WebClient.class);

	public static enum RequestProperties {
		userAgent("User-Agent"), referer("Referer"), authorization("Authorization"), contentType(
				"Content-Type"), charset("Charset");
		public final String name;

		RequestProperties(String name) {
			this.name = name;
		}
	}

	public static class WebClientArgs {
		public final String url;

		private final ArrayList<KeyValue<String, String>> parameters = new ArrayList<KeyValue<String, String>>();

		public void addParameter(String key, String value) {
			if (Strings.isNullOrEmpty(key)) {
				throw Exceptions.newNullArgumentException("key");
			}
			parameters.add(new KeyValue<String, String>(key, value));
		}

		public void removeParameter(String key) {
			if (Strings.isNullOrEmpty(key)) {
				throw Exceptions.newNullArgumentException("key");
			}
			int i = 0;
			for (KeyValue<String, String> kv : parameters) {
				if (kv.key.equals(key)) {
					parameters.remove(i);
					return;
				}
				i++;
			}
		}

		public WebClientArgs(String url) {
			this.url = url;
		}

		//
		public int connectTimeout = 30000;
		public int readTimeout = 30000;

		private ArrayList<KeyValue<RequestProperties, String>> properties = new ArrayList<KeyValue<RequestProperties, String>>();

		public void addProperty(RequestProperties property, String value) {
			if (null == property) {
				throw Exceptions.newNullArgumentException("property");
			}
			properties.add(new KeyValue<RequestProperties, String>(property, value));
		}

		public void removeProperty(RequestProperties property) {
			if (null == property) {
				throw Exceptions.newNullArgumentException("property");
			}
			int i = 0;
			for (KeyValue<RequestProperties, String> kv : properties) {
				if (kv.key.equals(property)) {
					properties.remove(i);
					return;
				}
				i++;
			}
		}

//		public void addProperty4Authorization(String username, String password) {
//			String encoding = new BASE64Encoder().encode((username + ":" + password).getBytes());
//			addProperty(RequestProperties.authorization, "Basic " + encoding);
//		}

		//
		public Encodings responseEncoding = null;// = Encodings.UTF8;
		public Encodings readAsEncoding = null;// = Encodings.UTF8;
		//
		public ICallbacks<HttpURLConnection, Boolean> onConnected = null;
		public ICallbacks<Percents, Boolean> onProgressChanged = null;
	}

	// /////////////////////////////////////////////////
	// /
	// /////////////////////////////////////////////////
	private WebClient() {
	}

	private static HttpURLConnection newConnection4Get(WebClientArgs args) throws Exception {
		String url = args.url;
		if (args.parameters.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (KeyValue<String, String> kv : args.parameters) {
				sb.append('&');
				sb.append(kv.key);
				if (!Strings.isNullOrEmpty(kv.value)) {
					sb.append('=');
					try {
						sb.append(java.net.URLEncoder.encode(kv.value, Encodings.UTF8.value));
					} catch (Exception e) {
						throw Exceptions.newInstance(e);
					}
				}
			}
			url = url + (url.indexOf('?') < 0 ? '?' : '&') + sb.substring(1);
		}
		HttpURLConnection uc = (HttpURLConnection) new URL(url).openConnection();
		//
		if (args.properties.size() > 0) {
			for (KeyValue<RequestProperties, String> kv : args.properties) {
				// log.debug("Property: " + kv.key.name + "=" + kv.value);
				uc.setRequestProperty(kv.key.name, kv.value);
			}
		}
		//
		uc.setConnectTimeout(args.connectTimeout);
		uc.setReadTimeout(args.readTimeout);
		uc.setRequestMethod("GET");
		return uc;
	}

	private static HttpURLConnection newConnection4Post(WebClientArgs args) throws Exception {
		HttpURLConnection uc = (HttpURLConnection) new URL(args.url).openConnection();
		//
		if (args.properties.size() > 0) {
			for (KeyValue<RequestProperties, String> kv : args.properties) {
				// log.debug("Property: " + kv.key.name + "=" + kv.value);
				uc.setRequestProperty(kv.key.name, kv.value);
			}
		}
		//
		uc.setConnectTimeout(args.connectTimeout);
		uc.setReadTimeout(args.readTimeout);
		uc.setRequestMethod("POST");
		// 发送POST请求必须设置如下两行
		uc.setDoOutput(true);
		uc.setDoInput(true);
		return uc;
	}

	// /////////////////////////////////////////////////
	// /
	// /////////////////////////////////////////////////
	// /////////////////////////////////////////////////
	// /
	// /////////////////////////////////////////////////
	public static String get(WebClientArgs args) {
		HttpURLConnection uc = null;
		try {
			uc = newConnection4Get(args);
			uc.connect();
			if (null == args.onConnected || args.onConnected.callback(uc)) {
				return get(uc, args);
			}
			return null;
		} catch (Exception e) {
			throw Exceptions.newInstance(e.getClass().getSimpleName() + ", " + e.getMessage());
		} finally {
			if (null != uc) {
				try {
					uc.disconnect();
				} catch (Exception e) {
				}
			}
		}
	}

	private static String get(URLConnection uc, WebClientArgs args) throws Exception {
		BufferedReader in = null;
		try {
			if (null == args.responseEncoding) {
				in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			} else {
				in = new BufferedReader(new InputStreamReader(uc.getInputStream(), args.responseEncoding.value));
			}
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
				sb.append(Environment.NewLine);
			}
			if (null == args.readAsEncoding) {
				return new String(sb.toString().getBytes());
			}
			return new String(sb.toString().getBytes(), args.readAsEncoding.value);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
		}
	}

	// /////////////////////////////////////////////////
	// /
	// /////////////////////////////////////////////////
	// /////////////////////////////////////////////////
	// /
	// /////////////////////////////////////////////////
	public static long getAndSave(WebClientArgs args, String targetFilename) {
		HttpURLConnection uc = null;
		try {
			uc = newConnection4Get(args);
			uc.connect();
			if (null == args.onConnected || args.onConnected.callback(uc)) {
				return getAndSave(uc, args, targetFilename);
			}
			return -1;
		} catch (Exception e) {
			throw Exceptions.newInstance(e.getClass().getSimpleName() + ", " + e.getMessage());
		} finally {
			if (null != uc) {
				try {
					uc.disconnect();
				} catch (Exception e) {
				}
			}
		}
	}

	private static long getAndSave(URLConnection conn, WebClientArgs args, String targetFilename) throws Exception {
		InputStream inStream = null;
		FileOutputStream fs = null;
		try {
			// 下载网络文件
			inStream = conn.getInputStream();
			fs = new FileOutputStream(targetFilename);
			byte[] buffer = new byte[10240];
			if (null == args.onProgressChanged) {
				long bytesum = 0;
				int byteread = 0;
				while ((byteread = inStream.read(buffer)) >= 0) {
					if (byteread > 0) {
						bytesum += byteread;
						fs.write(buffer, 0, byteread);
					}
				}
				return bytesum;
			}
			long bytesum = 0;
			int byteread = 0;
			long totalSize = conn.getContentLength();
			int prePercentsValue = 0;
			while ((byteread = inStream.read(buffer)) >= 0) {
				if (byteread > 0) {
					bytesum += byteread;
					fs.write(buffer, 0, byteread);
					int percentsValue = (int) (bytesum * 100 / totalSize);
					if (percentsValue != prePercentsValue) {
						if (!args.onProgressChanged.callback(new Percents(percentsValue))) {
							return -1;
						}
						prePercentsValue = percentsValue;
					}
				}
			}
			args.onProgressChanged.callback(new Percents(100));
			return bytesum;
		} finally {
			if (null != inStream) {
				try {
					inStream.close();
				} catch (Exception e) {
				}
			}
			if (null != fs) {
				try {
					fs.close();
				} catch (Exception e) {
				}
			}
		}
	}

	// /////////////////////////////////////////////////
	// /
	// /////////////////////////////////////////////////
	// /////////////////////////////////////////////////
	// /
	// /////////////////////////////////////////////////
	public static String post(WebClientArgs args) {
		HttpURLConnection uc = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			uc = newConnection4Post(args);
			uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			uc.connect();
			out = new PrintWriter(uc.getOutputStream());
			//
			StringBuilder sb = new StringBuilder();
			if (args.parameters.size() > 0) {
				for (KeyValue<String, String> kv : args.parameters) {
					if (!Strings.isNullOrEmpty(kv.value)) {
						sb.append('&');
						sb.append(kv.key);
						sb.append('=');
						try {
							sb.append(java.net.URLEncoder.encode(kv.value, Encodings.UTF8.value));
						} catch (Exception e) {
							throw Exceptions.newInstance(e);
						}
					}
				}
			}
			out.print(sb.substring(1));
			out.flush();
			//
			if (null == args.responseEncoding) {
				in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			} else {
				in = new BufferedReader(new InputStreamReader(uc.getInputStream(), args.responseEncoding.value));
			}
			//
			sb.setLength(0);
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
				sb.append(Environment.NewLine);
			}
			if (null == args.readAsEncoding) {
				return new String(sb.toString().getBytes());
			}
			return new String(sb.toString().getBytes(), args.readAsEncoding.value);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
			if (null != in) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
			if (null != uc) {
				try {
					uc.disconnect();
				} catch (Exception e) {
				}
			}
		}
	}

	private static final String _BOUNDARY = "_IFENGSAO_BOUNDARY_";

	// private static final byte[] BOUNDARY_END = ("--" + _BOUNDARY +
	// "--\r\n").getBytes();

	// @SafeVarargs
	@SuppressWarnings("unchecked")
	public static String postFile(WebClientArgs args, KeyValue<String, ContentTypes>... sourceFiles) {
		HttpURLConnection uc = null;
		OutputStream out = null;
		BufferedReader in = null;
		try {
			uc = newConnection4Post(args);
			uc.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + _BOUNDARY);
			uc.connect();
			out = uc.getOutputStream();
			//
			StringBuilder sb = new StringBuilder();
			if (args.parameters.size() > 0) {
				for (KeyValue<String, String> kv : args.parameters) {
					if (!Strings.isNullOrEmpty(kv.value)) {
						// log.debug("Parameter: " + kv.key + "=" + kv.value);
						// out.write(BOUNDARY_END);
						sb.setLength(0);
						sb.append("--");
						sb.append(_BOUNDARY);
						sb.append("\r\nContent-Disposition: form-data; name=\"");
						sb.append(kv.key);
						sb.append("\"\r\n\r\n");
						sb.append(kv.value);
						sb.append("\r\n");
						out.write(sb.toString().getBytes());
					}
				}
			}
			//
			byte[] buffer = new byte[1024];
			for (KeyValue<String, ContentTypes> fc : sourceFiles) {
				File f = new File(fc.key);
				// log.debug("Parameter: " + f.getName() + "=" +
				// fc.value.value);
				//
				// out.write(BOUNDARY_END);
				sb.setLength(0);
				sb.append("--");
				sb.append(_BOUNDARY);
				sb.append("\r\nContent-Disposition: form-data; name=\"file\"; filename=\"");
				sb.append(f.getName());
				sb.append("\"\r\n");
				sb.append("Content-Type: ");
				sb.append(fc.value.value);
				sb.append("\r\n\r\n");
				out.write(sb.toString().getBytes());
				//
				FileInputStream fi = null;
				try {
					fi = new FileInputStream(f);
					int c = fi.read(buffer);
					while (c > 0) {
						out.write(buffer, 0, c);
						c = fi.read(buffer);
					}
				} finally {
					if (null != fi) {
						try {
							fi.close();
						} catch (Exception e) {
						}
					}
				}
				out.write("\r\n".getBytes());
			}
			// out.write(BOUNDARY_END);
			sb.setLength(0);
			sb.append("--");
			sb.append(_BOUNDARY);
			sb.append("--\r\n\r\n");
			out.write(sb.toString().getBytes());
			out.flush();
			//
			if (null == args.responseEncoding) {
				in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			} else {
				in = new BufferedReader(new InputStreamReader(uc.getInputStream(), args.responseEncoding.value));
			}
			//
			sb.setLength(0);
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
				sb.append(Environment.NewLine);
			}
			if (null == args.readAsEncoding) {
				return new String(sb.toString().getBytes());
			}
			return new String(sb.toString().getBytes(), args.readAsEncoding.value);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
			if (null != in) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
			if (null != uc) {
				try {
					uc.disconnect();
				} catch (Exception e) {
				}
			}
		}
	}

}
