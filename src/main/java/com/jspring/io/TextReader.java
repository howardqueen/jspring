package com.jspring.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jspring.Encodings;
import com.jspring.Exceptions;

class TextReader implements ITextReader {

	protected InputStream fis = null;
	protected InputStreamReader isr = null;
	protected BufferedReader br = null;

	public TextReader(InputStream inputStream, Encodings encoding) {
		this.fis = inputStream;
		try {
			this.isr = new InputStreamReader(this.fis, encoding.value);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
		this.br = new BufferedReader(this.isr);
	}

	@Override
	public String readLine() {
		try {
			return this.br.readLine();
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	public static final int ReadEnd = -1;

	@Override
	public int read() {
		try {
			return this.br.read();
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	@Override
	public int read(char[] buffer) {
		try {
			return this.br.read(buffer);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	@Override
	public int read(char[] buffer, int index, int count) {
		try {
			return this.br.read(buffer, index, count);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	@Override
	public void tryClose() {
		if (null != this.br) {
			try {
				this.br.close();
			} catch (Exception e) {
			}
		}
		if (null != this.isr) {
			try {
				this.isr.close();
			} catch (Exception e) {
			}
		}
		if (null != this.fis) {
			try {
				this.fis.close();
			} catch (Exception e) {
			}
		}
	}

}
