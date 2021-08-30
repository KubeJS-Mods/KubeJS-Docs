package com.kubejs.wiki.json;

public class JsonString extends JsonElement {
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
		sb.append(string);
		sb.append('"');
	}
}
