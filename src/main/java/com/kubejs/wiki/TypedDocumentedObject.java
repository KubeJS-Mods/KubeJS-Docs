package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonArray;
import com.kubejs.wiki.json.JsonObject;
import com.kubejs.wiki.json.JsonString;

import java.util.ArrayList;
import java.util.List;

public class TypedDocumentedObject {
	public String name = "";
	public DocType type;
	public List<String> info = new ArrayList<>(0);

	public JsonObject toJson() {
		JsonObject o = new JsonObject();

		if (!name.isEmpty()) {
			o.add("name", name);
		}

		if (type != null) {
			o.add("type", type.toJson());
		}

		if (!info.isEmpty()) {
			JsonArray a = new JsonArray();

			for (String s : info) {
				String s0 = "> \"" + s + "\"";
				String s1 = "> " + JsonString.escape(new StringBuilder(), s);

				if (!s0.equals(s1)) {
					System.out.println("Escaped >");
					System.out.println(s0);
					System.out.println(s1);
				}

				a.add(s);
			}

			o.add("info", a);
		}

		return o;
	}
}
