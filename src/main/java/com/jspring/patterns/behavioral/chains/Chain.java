package com.jspring.patterns.behavioral.chains;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author HowardQian(howard.queen@qq.com), 2012-05-08 12:53
 */
public final class Chain<T, K> extends ArrayList<IHandler<T, K>> implements IChain<T, K> {
	private static final long serialVersionUID = 1L;

	protected static Logger log = LoggerFactory.getLogger(Chain.class);

	private Chain() {
	}

	@Override
	public K handle(Object requestor, T context) {
		for (IHandler<T, K> executor : this) {
			K r = executor.handle(requestor, context);
			if (null != r) {
				return r;
			}
		}
		return null;
	}

	public static <T, K> IChain<T, K> newInstance() {
		return new Chain<T, K>();
	}

}
