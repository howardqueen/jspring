package com.jspring.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jspring.patterns.behavioral.observers.*;

public final class Timer implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(Timer.class);

	private final Thread thread;
	private final long timespan;
	private boolean running = false;

	public Timer(int sleepSeconds) {
		timespan = sleepSeconds * 1000;
		thread = new Thread(this);
	}

	@Override
	public void run() {
		try {
			Thread.sleep(timespan);
			while (running) {
				eventElasped.tryFire(this, null);
				Thread.sleep(timespan);
			}
		} catch (Exception e) {
			log.warn(String.format("Timer %s, %s", e.getClass().getSimpleName(), e.getMessage()));
		}
	}

	public void start(boolean daemon) {
		if (running) {
			return;
		}
		running = true;
		thread.setDaemon(daemon);
		thread.start();
	}

	public void start() {
		start(true);
	}

	public void stop() {
		running = false;
	}

	public IEvent<Object> eventElasped = Event.newInstansce();

}
