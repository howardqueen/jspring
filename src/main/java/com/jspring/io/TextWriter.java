package com.jspring.io;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.jspring.Encodings;
import com.jspring.Exceptions;

class TextWriter extends BaseTextWriter {

	private OutputStream fos = null;
	private OutputStreamWriter out = null;

	public PrintWriter getPrintWriter() {
		return new PrintWriter(this.out);
	}

	public TextWriter(OutputStream fos, Encodings encoding) {
		try {
			this.fos = fos;
			this.out = new OutputStreamWriter(this.fos, encoding.value);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	@Override
	public void write(char c) {
		try {
			this.out.write(c);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	@Override
	public void write(char[] cs) {
		try {
			this.out.write(cs);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	@Override
	public void write(String text) {
		try {
			this.out.write(text);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	@Override
	public void tryClose() {
		if (null != this.out) {
			try {
				this.out.flush();
			} catch (Exception e) {
			}
			try {
				this.out.close();
			} catch (Exception e) {
			}
		}
		if (null != this.fos) {
			try {
				this.fos.close();
			} catch (Exception e) {
			}
		}
	}

}
