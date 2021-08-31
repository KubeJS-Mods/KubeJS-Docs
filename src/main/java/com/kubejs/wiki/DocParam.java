package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonObject;

public class DocParam extends TypedDocumentedObject {
	public boolean modNullable = false;
	public boolean modOptional = false;

	@Override
	public JsonObject toJson() {
		JsonObject o = super.toJson();

		if (modNullable) {
			o.add("nullable", true);
		}

		if (modOptional) {
			o.add("optional", true);
		}

		return o;
	}
}
