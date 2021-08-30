package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonArray;
import com.kubejs.wiki.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class DocConstructor {
	public DocType type;
	public List<DocParam> params = new ArrayList<>(0);

	public JsonObject toJson() {
		JsonObject o = new JsonObject();
		o.add("type", type.toJson());

		if (!params.isEmpty()) {
			JsonArray a = new JsonArray();

			for (DocParam p : params) {
				a.add(p.toJson());
			}

			o.add("params", a);
		}

		return o;
	}
}
