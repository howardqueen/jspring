package com.jspring.log;

/**
 * @author HowardQian(howard.queen@qq.com), 2012-05-08 16:01
 */
public interface ILogParser<T> {
	T tryParseLine(String line);
}
