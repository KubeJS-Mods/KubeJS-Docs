package com.kubejs.wiki.json;

public class JsonString extends JsonElement {
	private static final String[] REPLACEMENT_CHARS;
	private static final String[] HTML_SAFE_REPLACEMENT_CHARS;

	static {
		REPLACEMENT_CHARS = new String[128];
		for (int i = 0; i <= 0x1f; i++) {
			REPLACEMENT_CHARS[i] = String.format("\\u%04x", i);
		}
		REPLACEMENT_CHARS['"'] = "\\\"";
		REPLACEMENT_CHARS['\\'] = "\\\\";
		REPLACEMENT_CHARS['\t'] = "\\t";
		REPLACEMENT_CHARS['\b'] = "\\b";
		REPLACEMENT_CHARS['\n'] = "\\n";
		REPLACEMENT_CHARS['\r'] = "\\r";
		REPLACEMENT_CHARS['\f'] = "\\f";
		HTML_SAFE_REPLACEMENT_CHARS = REPLACEMENT_CHARS.clone();
		HTML_SAFE_REPLACEMENT_CHARS['<'] = "\\u003c";
		HTML_SAFE_REPLACEMENT_CHARS['>'] = "\\u003e";
		HTML_SAFE_REPLACEMENT_CHARS['&'] = "\\u0026";
		HTML_SAFE_REPLACEMENT_CHARS['='] = "\\u003d";
		HTML_SAFE_REPLACEMENT_CHARS['\''] = "\\u0027";
	}

	public final String string;

	public JsonString(String s) {
		string = s;
	}

	@Override
	public void append(StringBuilder sb) {
		escape(sb, string);
	}

	public static void escape(StringBuilder sb, String string) {
		sb.append('"');
		int last = 0;
		int length = string.length();

		for (int i = 0; i < length; i++) {
			char c = string.charAt(i);
			String replacement;

			if (c < 128) {
				replacement = HTML_SAFE_REPLACEMENT_CHARS[c];

				if (replacement == null) {
					continue;
				}
			} else if (c == '\u2028') {
				replacement = "\\u2028";
			} else if (c == '\u2029') {
				replacement = "\\u2029";
			} else {
				continue;
			}

			if (last < i) {
				sb.append(string, last, i - last);
			}

			sb.append(replacement);
			last = i + 1;
		}

		if (last < length) {
			sb.append(string, last, length - last);
		}

		sb.append('"');
	}
}
