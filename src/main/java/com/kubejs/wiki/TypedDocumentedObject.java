package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonArray;
import com.kubejs.wiki.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class TypedDocumentedObject {
	public DocType type;
	public List<String> info = new ArrayList<>(0);

	public JsonObject toJson() {
		JsonObject o = new JsonObject();

		if (type != null) {
			o.add("type", type.toJson());
		}

		if (!info.isEmpty()) {
			JsonArray a = new JsonArray();

			for (String s : info) {
				a.add(s);
			}

			o.add("info", a);
		}

		return o;
	}
}
