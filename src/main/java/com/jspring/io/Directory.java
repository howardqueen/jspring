package com.jspring.io;

import java.io.File;
import java.util.ArrayList;

import com.jspring.Exceptions;

public class Directory {

	private Directory() {
	}

	public static void rename(String sourcePath, String targetPath) {
		new File(sourcePath).renameTo(new File(targetPath));
	}

	public static void move(String sourcePath, String targetPath) {
		rename(sourcePath, targetPath);
	}

	public static void delete(String path) {
		File f = new File(path);
		if (f.isDirectory() && f.exists()) {
			delete(f);
		}
	}

	private static void delete(File file) {
		for (File f : file.listFiles()) {
			if (f.isDirectory()) {
				delete(f);
				continue;
			}
			if (!f.delete()) {
				throw Exceptions.newInstance("Delete failed: " + f.getPath());
			}
		}
		if (!file.delete()) {
			throw Exceptions.newInstance("Delete failed: " + file.getPath());
		}
	}

	public static boolean isExist(String path) {
		File f = new File(path);
		return f.isDirectory() && f.exists();
	}

	public static void create(String path) {
		create(path, false);
	}

	public static void create(String path, boolean recursive) {
		File f = new File(path);
		if (!f.exists()) {
			if (recursive) {
				f.mkdirs();
			} else {
				f.mkdir();
			}
		}
	}

	public static String[] getSubPaths(String path) {
		File f = new File(path);
		if (f.isDirectory() && f.exists()) {
			ArrayList<String> ls = new ArrayList<String>();
			for (File s : f.listFiles()) {
				if (s.isDirectory()) {
					ls.add(s.getName());
				}
			}
			return ls.toArray(new String[ls.size()]);
		}
		return new String[0];
	}

	public static String[] getSubFiles(String path) {
		File f = new File(path);
		if (f.isDirectory() && f.exists()) {
			ArrayList<String> ls = new ArrayList<String>();
			for (File s : f.listFiles()) {
				if (s.isFile()) {
					ls.add(s.getName());
				}
			}
			return ls.toArray(new String[ls.size()]);
		}
		return new String[0];
	}

}
