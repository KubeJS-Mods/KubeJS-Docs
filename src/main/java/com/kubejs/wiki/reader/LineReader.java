package com.kubejs.wiki.reader;

import com.kubejs.wiki.WikiGenerator;

public class LineReader {
	public final WikiGenerator generator;
	public final String string;
	public int pos;

	public LineReader(WikiGenerator g, String s) {
		generator = g;
		string = s;
	}

	public String read(CharTest predicate) {
		int p0 = pos;

		while (predicate.test(string.charAt(pos))) {
			pos++;

			if (pos >= string.length()) {
				break;
			}
		}

		return string.substring(p0, pos);
	}

	public void skipWhitespace() {
		read(CharTest.WHITESPACE);
	}
}
