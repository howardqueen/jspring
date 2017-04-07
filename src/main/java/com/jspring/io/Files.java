package com.jspring.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.jspring.Encodings;
import com.jspring.Environment;
import com.jspring.Exceptions;

/**
 * Help you to oprate file with default encoding UTF-8
 * 
 * @author howard.qian(howard.queen@gmail.com)
 */
public class Files {
	private Files() {
	}

	public static void rename(String sourceFilename, String targetFilename) {
		new File(sourceFilename).renameTo(new File(targetFilename));
	}

	public static void move(String sourceFilename, String targetFilename) {
		rename(sourceFilename, targetFilename);
	}

	public static void delete(String filename) {
		File f = new File(filename);
		if (f.isFile() && f.exists()) {
			f.delete();
		}
	}

	public static boolean isExist(String filename) {
		File f = new File(filename);
		return f.isFile() && f.exists();
	}

	public static ITextReader newReader(String filename) {
		return newReader(filename, Encodings.UTF8);
	}

	public static ITextReader newReader(String filename, Encodings encoding) {
		try {
			return newReader(new FileInputStream(filename), encoding);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	public static ITextReader newReader(InputStream inputStream) {
		return newReader(inputStream, Encodings.UTF8);
	}

	public static ITextReader newReader(InputStream inputStream, Encodings encoding) {
		return new TextReader(inputStream, encoding);
	}

	public static String readAllText(String filename) {
		return readAllText(filename, Encodings.UTF8);
	}

	public static String readAllText(String filename, Encodings encoding) {
		ITextReader reader = null;
		try {
			reader = newReader(filename, encoding);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append(Environment.NewLine);
			}
			return sb.toString();
		} finally {
			if (null != reader) {
				reader.tryClose();
			}
		}
	}

	public static String readAllText(InputStream inputStream) {
		return readAllText(inputStream, Encodings.UTF8);
	}

	public static String readAllText(InputStream inputStream, Encodings encoding) {
		ITextReader reader = null;
		try {
			reader = newReader(inputStream, encoding);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append(Environment.NewLine);
			}
			return sb.toString();
		} finally {
			if (null != reader) {
				reader.tryClose();
			}
		}
	}

	public static String[] readLines(String filename) {
		return readLines(filename, Encodings.UTF8);
	}

	public static String[] readLines(String filename, Encodings encoding) {
		ITextReader reader = null;
		try {
			reader = newReader(filename, encoding);
			ArrayList<String> ls = new ArrayList<String>();
			String line;
			while ((line = reader.readLine()) != null) {
				ls.add(line);
			}
			return ls.toArray(new String[ls.size()]);
		} finally {
			if (null != reader) {
				reader.tryClose();
			}
		}
	}

	public static void read(String filename, ILineReader reader, Encodings encoding) {
		ITextReader r = newReader(filename, encoding);
		String line;
		while ((line = r.readLine()) != null) {
			if (!reader.read(line)) {
				break;
			}
		}
		r.tryClose();
	}

	public static void read(String filename, ILineReader reader) {
		read(filename, reader, Encodings.UTF8);
	}

	public static ITextWriter newWriter(OutputStream fos) {
		return new TextWriter(fos, Encodings.UTF8);
	}

	public static ITextWriter newWriter(OutputStream fos, Encodings encoding) {
		return new TextWriter(fos, encoding);
	}

	public static ITextWriter newWriter(String filename, boolean append) {
		try {
			return new TextWriter(new FileOutputStream(filename, append), Encodings.UTF8);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	public static ITextWriter newWriter(String filename, boolean append, Encodings encoding) {
		try {
			return new TextWriter(new FileOutputStream(filename, append), encoding);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	public static ITextWriter newWriter(StringBuilder builder) {
		return new StringBuilderWriter(builder);
	}

	public static void writeAllText(String filename, boolean append, String text) {
		writeAllText(filename, append, text, Encodings.UTF8);
	}

	public static void writeAllText(String filename, boolean append, String text, Encodings encoding) {
		ITextWriter writer = null;
		try {
			writer = newWriter(filename, append, encoding);
			writer.write(text);
		} finally {
			if (null != writer) {
				writer.tryClose();
			}
		}
	}

}
