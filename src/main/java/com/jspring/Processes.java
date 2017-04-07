package com.jspring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jspring.io.Files;
import com.jspring.io.ITextReader;

/**
 * Execute any command in current system, get the output stream.
 * 
 * @author hqian
 */
public final class Processes {

	private static final Logger log = LoggerFactory.getLogger(Processes.class);

	public static abstract class ConsoleThread implements Runnable {

		protected void perform(ITextReader info, ITextReader error) {
			String line;
			while (null != (line = info.readLine())) {
				info(line);
			}
			while (null != (line = error.readLine())) {
				error(line);
			}
		}

		protected abstract void info(String line);

		private ITextReader info;

		public void setInfo(ITextReader reader) {
			this.info = reader;
		}

		protected abstract void error(String line);

		private ITextReader error;

		public void setError(ITextReader reader) {
			this.error = reader;
		}

		@Override
		public void run() {
			perform(info, error);
			synchronized (this) {
				this.notify();
			}
		}
	}

	public static void execute(String command) {
		execute(command, new ConsoleThread() {
			@Override
			protected void info(String line) {
				log.debug(line);
			}

			@Override
			protected void error(String line) {
				log.warn(line);
			}
		});
	}

	public static void execute(String command, ConsoleThread consoleThread) {
		log.debug(command);
		try {
			Process p = Runtime.getRuntime().exec(command);
			ITextReader r = Files.newReader(p.getInputStream());
			ITextReader e = Files.newReader(p.getErrorStream());
			consoleThread.setInfo(r);
			consoleThread.setError(e);
			Thread t = new Thread(consoleThread);
			t.setDaemon(true);
			t.start();
			synchronized (consoleThread) {
				consoleThread.wait();
			}
			p.waitFor();
			r.tryClose();
			e.tryClose();
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	public static void executeNotWait(String command) {
		log.debug(command);
		try {
			Runtime.getRuntime().exec(command);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

}
