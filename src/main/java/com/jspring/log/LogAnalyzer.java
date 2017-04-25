package com.jspring.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jspring.Processes;
import com.jspring.Processes.ConsoleThread;
import com.jspring.io.Files;
import com.jspring.io.ITextReader;
import com.jspring.patterns.behavioral.observers.Event;
import com.jspring.patterns.behavioral.observers.IEvent;

public final class LogAnalyzer<T> {

	public static class StatResult {
		public int succLines = 0;
		public int failedLines = 0;
	}

	private class MyConsoleThread extends ConsoleThread {

		public StatResult result = new StatResult();

		@Override
		public void perform(ITextReader info, ITextReader error) {
			result.succLines = 0;
			result.failedLines = 0;
			int count = 0;
			String line;
			while (null != (line = info.readLine())) {
				T ali = parser.tryParseLine(line);
				if (null == ali) {
					result.failedLines++;
					continue;
				}
				actionReadItem.tryFire(this, ali);
				count++;
				if (count >= 100000) {
					result.succLines += count;
					count = 0;
					log.debug(String.format("  - %,d", result.succLines));
				}
			}
			result.succLines += count;
		}

		@Override
		protected void info(String line) {
			log.debug(line);
		}

		@Override
		protected void error(String line) {
			log.warn(line);
		}

	}

	private final static Logger log = LoggerFactory.getLogger(LogAnalyzer.class);

	private final ILogParser<T> parser;

	private LogAnalyzer(ILogParser<T> parser) {
		this.parser = parser;
	}

	public final IEvent<T> actionReadItem = Event.newInstansce();

	public StatResult readFile(String filename) {
		log.info(String.format(" ++ Read %s ...", filename));
		MyConsoleThread t = new MyConsoleThread();
		t.perform(Files.newReader(filename), null);
		log.info(String.format(" -- Success %,d, failed %,d.", t.result.succLines, t.result.failedLines));
		return t.result;
	}

	public StatResult readProcess(String rumtimeCommand) {
		log.info(String.format(" ++ %s", rumtimeCommand));
		MyConsoleThread t = new MyConsoleThread();
		Processes.execute(rumtimeCommand, t);
		log.info(String.format(" -- Success %,d, failed %,d.", t.result.succLines, t.result.failedLines));
		return t.result;
	}

	public StatResult readFileSilence(String filename) {
		MyConsoleThread t = new MyConsoleThread();
		t.perform(Files.newReader(filename), null);
		return t.result;
	}

	public StatResult readProcessSilence(String rumtimeCommand) {
		MyConsoleThread t = new MyConsoleThread();
		Processes.execute(rumtimeCommand, t);
		return t.result;
	}

	public static <K> LogAnalyzer<K> newInstance(ILogParser<K> parser) {
		return new LogAnalyzer<K>(parser);
	}

	public static LogAnalyzer<AccessLogItem> newInstance() {
		return new LogAnalyzer<AccessLogItem>(new AccessLogParser());
	}

}
