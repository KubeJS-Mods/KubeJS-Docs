package com.kubejs.wiki.json;

public class JsonNumber extends JsonElement {
	public final Number number;

	public JsonNumber(Number n) {
		number = n;
	}

	@Override
	public void append(StringBuilder sb) {
		sb.append(number);
	}
}
