package com.jspring;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.UUID;

public final class Environment {

	/**
	 * http://hdwangyi.iteye.com/blog/105707
	 */
	public static String getClassPath(Class<?> userClass) {
		String clsName = userClass.getName() + ".class";
		Package pack = userClass.getPackage();
		if (null != pack) {
			String path = "";
			String packName = pack.getName();
			if (packName.startsWith("java.") || packName.startsWith("javax.")) {
				throw new IllegalArgumentException("Get class path failed with " + userClass.getName()
						+ ", replace it with a user-class(not from system).");
			}
			clsName = clsName.substring(packName.length() + 1);
			if (packName.indexOf(".") < 0) {
				path = packName + "/";
			} else {
				int start = 0, end = 0;
				end = packName.indexOf(".");
				while (end != -1) {
					path = path + packName.substring(start, end) + "/";
					start = end + 1;
					end = packName.indexOf(".", start);
				}
				path = path + packName.substring(start) + "/";
			}
			clsName = path + clsName;
		}
		String directory;
		java.net.URL url = ClassLoader.getSystemClassLoader().getResource(clsName);
		if (null == url) {
			/*
			 * System.out.println(String.format(
			 * "SystemClassLoader can't get resource of %s, try userClassLoader(Now I guess it's a webapp)."
			 * , clsName));
			 */
			url = userClass.getClassLoader().getResource(clsName);
		}
		directory = url.getPath();
		if (directory.startsWith("rsrc:")) {
			throw new IllegalArgumentException("Get class path failed with " + userClass.getName()
					+ ", replace it with a user-class(call Environment.init(...)).");
		}
		//
		directory = directory.substring(directory.indexOf("file:") + 5);
		directory = directory.substring(0, directory.indexOf(clsName) - 1);
		//
		if (directory.endsWith("!")) {
			directory = directory.substring(0, directory.lastIndexOf('/'));
		}
		try {
			directory = java.net.URLDecoder.decode(directory, System.getProperty("file.encoding"));// ;
																									// "utf-8");
		} catch (Exception e) {
			throw new RuntimeException("Get application domain failed, inner exception: " + e.toString());
		}
		if (!directory.endsWith("\\") && !directory.endsWith("/")) {
			return directory.replace('\\', '/') + "/";
		}
		return directory.replace('\\', '/');
	}

	public final static String NewLine = System.getProperty("line.separator");

	public final static String NewLineForLinux = "\n";
	public final static String newLineForWindows = "\r\n";
	public final static String newLineForHtml = "<br/>";

	public static String newGuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static String getUserDirectory() {
		return System.getProperty("user.dir");
	}

	private static int _pid;

	public static int getPid() {
		if (_pid <= 0) {
			RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
			String name = runtime.getName(); // format: "pid@hostname"
			try {
				_pid = Integer.parseInt(name.substring(0, name.indexOf('@')));
			} catch (Exception e) {
				throw Exceptions.newInstance(e);
			}
		}
		return _pid;
	}

}
