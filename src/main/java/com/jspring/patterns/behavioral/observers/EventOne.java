package com.jspring.patterns.behavioral.observers;

import org.slf4j.*;

import com.jspring.Exceptions;

/**
 * @author HowardQian(howard.queen@qq.com), 2012-04-26 14:53
 */
public final class EventOne<T> implements IEventOne<T> {

	protected static Logger log = LoggerFactory.getLogger(Event.class);

	private EventOne() {
	}

	@Override
	public void fire(Object sender, T context) {
		if (null == listener) {
			return;
		}
		listener.eventOccur(sender, context);
	}

	@Override
	public void tryFire(Object sender, T args) {
		if (null == listener) {
			return;
		}
		try {
			listener.eventOccur(sender, args);
		} catch (Exceptions e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(Exceptions.getStackTrace(e));
		}
	}

	public static <T> IEventOne<T> newInstansce() {
		return new EventOne<T>();
	}

	private IListener<T> listener;

	@Override
	public void setListener(IListener<T> listener) {
		this.listener = listener;
	}

}
