package com.jspring.log;

import org.apache.log4j.PropertyConfigurator;

import com.jspring.Environment;
import com.jspring.io.Files;
import com.jspring.io.ITextWriter;

public class LogUtils {
	private LogUtils() {
	}

	private static enum DatePatterns {
		Monthly("'.'yyMM'.log'"), Daily("'.'yyMMdd'.log'"), HalfDaily("'.'yyMMdda'.log'"), Hourly(
				"'.'yyMMdd'.'HH'.log'"), Minutely("'.'yyMMdd'.'HHmm'.log'");

		public final String value;

		DatePatterns(String value) {
			this.value = value;
		}
	}

	public static void configLog4j(Class<?> mainClass) {
		String baseDir = Environment.getClassPath(mainClass);
		String log4jFilename = baseDir + "log4j.properties";
		if (!Files.isExist(log4jFilename)) {
			System.out.println("CREATE LOG4J PROPERTIES FILE: " + log4jFilename);
			ITextWriter w = Files.newWriter(log4jFilename, false);
			w.writeLine("log4j.rootLogger=t,d,i,w,e,f,c");
			writeConsoleAppender(w, "info", "c");
			writeRollingFileAppender(w, "trace", "t");
			writeRollingFileAppender(w, "debug", "d");
			writeDailyRollingFileAppender(w, "info", "i", DatePatterns.Hourly);
			writeDailyRollingFileAppender(w, "warn", "w", DatePatterns.HalfDaily);
			writeDailyRollingFileAppender(w, "error", "e", DatePatterns.Daily);
			writeDailyRollingFileAppender(w, "fatal", "f", DatePatterns.Monthly);
			w.tryClose();
		}
		System.setProperty("jspring.log.path", baseDir + "logs/");
		PropertyConfigurator.configure(log4jFilename);
	}

	private static void writeDailyRollingFileAppender(ITextWriter w, String threshold, String name,
			DatePatterns datePattern) {
		w.write('#');
		w.writeLine(threshold);

		w.write("log4j.appender.");
		w.write(name);
		w.writeLine("=org.apache.log4j.DailyRollingFileAppender");

		w.write("log4j.appender.");
		w.write(name);
		w.writeLine(".layout=org.apache.log4j.PatternLayout");

		w.write("log4j.appender.");
		w.write(name);
		w.writeLine(".layout.ConversionPattern=%d{HH\\:mm\\:ss}[%p][%c]%m%n");

		w.write("log4j.appender.");
		w.write(name);
		w.writeLine(".DatePattern=" + datePattern.value);

		w.write("log4j.appender.");
		w.write(name);
		w.write(".Threshold=");
		w.writeLine(threshold.toUpperCase());

		w.write("log4j.appender.");
		w.write(name);
		w.write(".File=${jspring.log.path}");
		w.writeLine(threshold);
	}

	private static void writeRollingFileAppender(ITextWriter w, String threshold, String name) {
		w.write('#');
		w.writeLine(threshold);

		w.write("log4j.appender.");
		w.write(name);
		w.writeLine("=org.apache.log4j.RollingFileAppender");

		w.write("log4j.appender.");
		w.write(name);
		w.writeLine(".layout=org.apache.log4j.PatternLayout");

		w.write("log4j.appender.");
		w.write(name);
		w.writeLine(".layout.ConversionPattern=%d{HH\\:mm\\:ss}[%p][%c]%m%n");

		w.write("log4j.appender.");
		w.write(name);
		w.writeLine(".MaxFileSize=512KB");

		w.write("log4j.appender.");
		w.write(name);
		w.writeLine(".MaxBackupIndex=10");

		w.write("log4j.appender.");
		w.write(name);
		w.write(".Threshold=");
		w.writeLine(threshold.toUpperCase());

		w.write("log4j.appender.");
		w.write(name);
		w.write(".File=${jspring.log.path}");
		w.writeLine(threshold);
	}

	private static void writeConsoleAppender(ITextWriter w, String threshold, String name) {
		w.write('#');
		w.writeLine(threshold);

		w.write("log4j.appender.");
		w.write(name);
		w.writeLine("=org.apache.log4j.ConsoleAppender");

		w.write("log4j.appender.");
		w.write(name);
		w.writeLine(".layout=org.apache.log4j.PatternLayout");

		w.write("log4j.appender.");
		w.write(name);
		w.writeLine(".layout.ConversionPattern=%d{HH\\:mm\\:ss}[%p][%c]%m%n");

		w.write("log4j.appender.");
		w.write(name);
		w.write(".Threshold=");
		w.writeLine(threshold.toUpperCase());
	}

}
