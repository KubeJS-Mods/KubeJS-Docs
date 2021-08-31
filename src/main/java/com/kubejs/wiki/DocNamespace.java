package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonArray;
import com.kubejs.wiki.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class DocNamespace {
	public String namespace = "";
	public List<DocClass> classes = new ArrayList<>();
	public List<String> info = new ArrayList<>();

	public JsonObject toJson() {
		JsonObject o = new JsonObject();
		o.add("namespace", namespace);

		JsonArray i = new JsonArray();

		for (String s : info) {
			i.add(s);
		}

		o.add("info", i);

		JsonArray a = new JsonArray();

		for (DocClass c : classes) {
			a.add(c.toJson());
		}

		o.add("classes", a);

		return o;
	}
}
