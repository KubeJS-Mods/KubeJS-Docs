package com.kubejs.wiki.reader;

public class LineReader {
	public final String string;
	public int pos;

	public LineReader(String s) {
		string = s;
	}

	public boolean isEOL() {
		return pos >= string.length();
	}

	public String read(CharTest predicate) {
		if (isEOL()) {
			return "";
		}

		int p0 = pos;

		while (predicate.test(string.charAt(pos))) {
			pos++;

			if (pos >= string.length()) {
				break;
			}
		}

		return string.substring(p0, pos);
	}

	public String read() {
		return skipWhitespace().read(CharTest.W);
	}

	public String readJavaName() {
		return skipWhitespace().read(CharTest.JAVA_W);
	}

	public LineReader skipWhitespace() {
		read(CharTest.WHITESPACE);
		return this;
	}
}
