package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonArray;
import com.kubejs.wiki.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class DocConstructor extends TypedDocumentedObject {
	public List<DocParam> params = new ArrayList<>(0);
	public List<String> throwsTypes = new ArrayList<>(0);
	public List<String> generics = new ArrayList<>(0);

	@Override
	public JsonObject toJson() {
		JsonObject o = super.toJson();

		if (!throwsTypes.isEmpty()) {
			JsonArray a = new JsonArray();

			for (String p : throwsTypes) {
				a.add(p);
			}

			o.add("throws", a);
		}

		if (!params.isEmpty()) {
			JsonArray a = new JsonArray();

			for (DocParam p : params) {
				a.add(p.toJson());
			}

			o.add("params", a);
		}

		if (!generics.isEmpty()) {
			JsonArray a = new JsonArray();

			for (String p : generics) {
				a.add(p);
			}

			o.add("generics", a);
		}

		return o;
	}
}
