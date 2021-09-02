package com.kubejs.wiki;

public class DocException extends RuntimeException {
	public DocException(String s) {
		super(s);
	}

	public DocException(String s, Throwable t) {
		super(s, t);
	}
}
