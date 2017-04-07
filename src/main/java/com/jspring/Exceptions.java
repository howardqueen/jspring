package com.jspring;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author HowardQian(howard.queen@qq.com), 2012-05-18 13:32
 */
public final class Exceptions extends RuntimeException {
	private static final long serialVersionUID = 1L;

	protected Exceptions(String message) {
		super(message);
	}

	public static Exceptions newInstance(String message) {
		return new Exceptions(message);
	}

	public static Exceptions newInstance(String format, Object... args) {
		return newInstance(String.format(format, args));
	}

	public static Exceptions newInstance(Exceptions e) {
		return e;
	}

	public static Exceptions newInstance(Exception e) {
		if (e instanceof Exceptions) {
			return (Exceptions) e;
		}
		if (e instanceof RuntimeException) {
			if (!(e instanceof NullPointerException)
					&& !(e instanceof IllegalArgumentException)
					&& !(e instanceof IndexOutOfBoundsException)
					&& !(e instanceof ClassCastException)) {
				return newInstance(String.format("%s, %s", e.getClass()
						.getSimpleName(), e.getMessage()));
			}
		} else if (e instanceof ClassNotFoundException) {
			return newInstance(String.format("ClassNotFoundException, %s",
					e.getMessage()));
		}
		if (e instanceof IOException) {
			return newInstance(String.format("%s, %s", e.getClass()
					.getSimpleName(), e.getMessage()));
		}
		return newInstance(getStackTrace(e));
	}

	public static String getStackTrace(Exception e) {
		if (e instanceof Exceptions) {
			return e.getMessage();
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.close();
		return sw.toString();
	}

	public static Exceptions newInstance(Exceptions innerException,
			String message) {
		return newInstance("%s, %s", message, innerException.getMessage());
	}

	public static Exceptions newInstance(Exceptions innerException,
			String format, Object... args) {
		return newInstance(innerException, String.format(format, args));
	}

	public static Exceptions newInstance(Exception innerException,
			String message) {
		if (innerException instanceof Exceptions) {
			return newInstance("%s, %s", message, innerException.getMessage());
		}
		return newInstance("%s, %s, %s", message, innerException.getClass()
				.getSimpleName(), innerException.getMessage());
	}

	public static Exceptions newInstance(Exception innerException,
			String format, Object... args) {
		return newInstance(innerException, String.format(format, args));
	}

	public static Exceptions newIllegalArgumentException(String name) {
		return newInstance("Illegal argument: \"%s\"", name);
	}

	public static Exceptions newIllegalArgumentException(String name,
			String functionSource) {
		return newInstance("[%s]Illegal argument: \"%s\"", functionSource, name);
	}

	public static Exceptions newIllegalArgumentException(String name,
			String functionSource, String value) {
		return newInstance("[%s]Illegal argument: \"%s\", %s", functionSource,
				name, value);
	}

	public static Exceptions newNullArgumentException(String name) {
		return newInstance("Argument can't be null or empty: \"%s\"", name);
	}

	public static Exceptions newNullArgumentException(String name,
			String functionSource) {
		return newInstance("[%s]Argument can't be null or empty: \"%s\"",
				functionSource, name);
	}

	public static Exceptions newIllegalConfigException(String name) {
		return newInstance("Illegal config: \"%s\"", name);
	}

	public static Exceptions newIllegalConfigException(String name,
			String filename) {
		return newInstance("Illegal config in %s: \"%s\"", filename, name);
	}

}
