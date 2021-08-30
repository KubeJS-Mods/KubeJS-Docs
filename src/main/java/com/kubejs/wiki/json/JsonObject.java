package com.kubejs.wiki.json;

import java.util.LinkedHashMap;
import java.util.Map;

public class JsonObject extends JsonElement {
	public final Map<String, JsonElement> map = new LinkedHashMap<>();

	@Override
	public void append(StringBuilder sb) {
		sb.append('{');

		boolean first = true;

		for (Map.Entry<String, JsonElement> entry : map.entrySet()) {
			if (first) {
				first = false;
			} else {
				sb.append(',');
			}

			JsonString.escape(sb, entry.getKey());
			sb.append(':');
			entry.getValue().append(sb);
		}

		sb.append('}');
	}

	public void add(String key, JsonElement element) {
		map.put(key, element);
	}

	public void add(String key, boolean value) {
		add(key, value ? JsonBoolean.TRUE : JsonBoolean.FALSE);
	}

	public void add(String key, Number number) {
		add(key, new JsonNumber(number));
	}

	public void add(String key, String value) {
		add(key, new JsonString(value));
	}
}
