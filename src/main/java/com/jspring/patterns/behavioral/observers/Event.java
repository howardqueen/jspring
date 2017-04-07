package com.jspring.patterns.behavioral.observers;

import org.slf4j.*;

import com.jspring.Exceptions;

import java.util.ArrayList;

/**
 * @author HowardQian(howard.queen@qq.com), 2012-04-26 14:53
 */
public final class Event<T> extends ArrayList<IListener<T>> implements IEvent<T> {
	private static final long serialVersionUID = 1L;

	protected static Logger log = LoggerFactory.getLogger(Event.class);

	private Event() {
	}

	@Override
	public void fire(Object sender, T context) {
		for (IListener<T> listener : this) {
			listener.eventOccur(sender, context);
		}
	}

	@Override
	public void tryFire(Object sender, T args) {
		for (IListener<T> listener : this) {
			try {
				listener.eventOccur(sender, args);
			} catch (Exceptions e) {
				log.error(e.getMessage());
			} catch (Exception e) {
				log.error(Exceptions.getStackTrace(e));
			}
		}
	}

	public static <T> IEvent<T> newInstansce() {
		return new Event<T>();
	}

}
