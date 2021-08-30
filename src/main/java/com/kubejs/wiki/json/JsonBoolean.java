package com.kubejs.wiki.json;

public class JsonBoolean extends JsonElement {
	public static final JsonBoolean TRUE = new JsonBoolean(true);
	public static final JsonBoolean FALSE = new JsonBoolean(false);

	public final boolean value;

	private JsonBoolean(boolean b) {
		value = b;
	}

	@Override
	public void append(StringBuilder sb) {
		sb.append(value);
	}
}
