package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonArray;
import com.kubejs.wiki.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class DocExample {
	public String id = "";
	public String title = "";
	public List<String> text = new ArrayList<>(3);

	public JsonObject toJson() {
		JsonObject o = new JsonObject();
		o.add("id", id);

		if (!title.isEmpty()) {
			o.add("title", title);
		}

		JsonArray a = new JsonArray();

		for (String s : text) {
			a.add(s);
		}

		o.add("text", a);
		return o;
	}
}
