package com.kubejs.wiki.reader;

public class RangeCharTest implements CharTest {
	private final char from;
	private final char to;

	public RangeCharTest(char f, char t) {
		from = f;
		to = t;
	}

	@Override
	public boolean test(char c) {
		return c >= from && c <= to;
	}
}
