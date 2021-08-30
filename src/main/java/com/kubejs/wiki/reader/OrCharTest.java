package com.kubejs.wiki.reader;

public class OrCharTest implements CharTest {
	private final CharTest c1;
	private final CharTest c2;

	OrCharTest(CharTest t1, CharTest t2) {
		c1 = t1;
		c2 = t2;
	}

	@Override
	public boolean test(char c) {
		return c1.test(c) || c2.test(c);
	}
}
