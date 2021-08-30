package com.kubejs.wiki.json;

import java.util.ArrayList;
import java.util.List;

public class JsonArray extends JsonElement {
	public final List<JsonElement> list = new ArrayList<>();

	@Override
	public void append(StringBuilder sb) {
		sb.append('[');

		for (int i = 0; i < list.size(); i++) {
			if (i > 0) {
				sb.append(',');
			}

			list.get(i).append(sb);
		}

		sb.append(']');
	}

	public void add(JsonElement element) {
		list.add(element);
	}

	public void add(boolean value) {
		add(value ? JsonBoolean.TRUE : JsonBoolean.FALSE);
	}

	public void add(Number number) {
		add(new JsonNumber(number));
	}

	public void add(String value) {
		add(new JsonString(value));
	}
}
