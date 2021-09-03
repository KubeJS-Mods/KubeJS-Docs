package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonObject;

public class DocParam {
	public String name;
	public DocType type;
	public boolean modNullable = false;
	public boolean modOptional = false;

	public JsonObject toJson() {
		JsonObject o = new JsonObject();
		o.add("name", name);

		if (type != null) {
			o.add("type", type.toJson());
		}

		if (modNullable) {
			o.add("nullable", true);
		}

		if (modOptional) {
			o.add("optional", true);
		}

		return o;
	}
}
