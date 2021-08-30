package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonObject;

public class DocParam {
	public DocType type;
	public boolean modNullable = false;
	public boolean modOptional = false;
	public boolean modDeprecated = false;

	public JsonObject toJson() {
		JsonObject o = new JsonObject();
		o.add("type", type.toJson());

		if (modNullable) {
			o.add("nullable", true);
		}

		if (modOptional) {
			o.add("optional", true);
		}

		if (modDeprecated) {
			o.add("deprecated", true);
		}

		return o;
	}
}
