package com.kubejs.wiki.reader;

public interface CharTest {
	CharTest POUND = new SingleCharTest('#');
	CharTest UNDERSCORE = new SingleCharTest('_');
	CharTest PERIOD = new SingleCharTest('.');
	CharTest COMMA = new SingleCharTest(',');
	CharTest DIAMOND_OPEN = new SingleCharTest('<');
	CharTest DIAMOND_CLOSE = new SingleCharTest('>');
	CharTest BACKTICK = new SingleCharTest('`');
	CharTest AT = new SingleCharTest('@');

	CharTest AZ_L = new RangeCharTest('a', 'z');
	CharTest AZ_U = new RangeCharTest('A', 'Z');
	CharTest DIGIT = new RangeCharTest('0', '9');
	CharTest AZ = new OrCharTest(AZ_L, AZ_U);
	CharTest W = new OrCharTest(AZ, new OrCharTest(DIGIT, UNDERSCORE));
	CharTest WHITESPACE = new RangeCharTest((char) 0, ' ');

	boolean test(char c);
}