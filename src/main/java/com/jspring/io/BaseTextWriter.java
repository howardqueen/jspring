package com.jspring.io;

import com.jspring.Environment;

public abstract class BaseTextWriter implements ITextWriter {

	@Override
	public void writeLine() {
		write(Environment.NewLine);
	}

	@Override
	public void write(boolean b) {
		write(String.valueOf(b));
	}

	@Override
	public void write(byte b) {
		write(String.valueOf(b));
	}

	@Override
	public void write(double d) {
		write(String.valueOf(d));
	}

	@Override
	public void write(float f) {
		write(String.valueOf(f));
	}

	@Override
	public void write(int i) {
		write(String.valueOf(i));
	}

	@Override
	public void write(long l) {
		write(String.valueOf(l));
	}

	@Override
	public void write(short s) {
		write(String.valueOf(s));
	}

	@Override
	public void writeLine(boolean b) {
		write(b);
		writeLine();
	}

	@Override
	public void writeLine(byte b) {
		write(b);
		writeLine();
	}

	@Override
	public void writeLine(double d) {
		write(d);
		writeLine();
	}

	@Override
	public void writeLine(float f) {
		write(f);
		writeLine();
	}

	@Override
	public void writeLine(int i) {
		write(i);
		writeLine();
	}

	@Override
	public void writeLine(long l) {
		write(l);
		writeLine();
	}

	@Override
	public void writeLine(short s) {
		write(s);
		writeLine();
	}

	@Override
	public void writeLine(char c) {
		write(c);
		writeLine();
	}

	@Override
	public void writeLine(char[] cs) {
		write(cs);
		writeLine();
	}

	@Override
	public void writeLine(String text) {
		write(text);
		writeLine();
	}

	@Override
	public void writeFormat(String format, Object... params) {
		write(String.format(format, params));
	}

	@Override
	public void writeLineFormat(String format, Object... params) {
		write(String.format(format, params));
		writeLine();
	}
}
