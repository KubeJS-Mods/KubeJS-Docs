package com.kubejs.wiki.reader;

public class SingleCharTest implements CharTest {
	private final char character;

	SingleCharTest(char c) {
		character = c;
	}

	@Override
	public boolean test(char c) {
		return character == c;
	}
}
