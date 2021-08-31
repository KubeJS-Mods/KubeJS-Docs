package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonObject;

public class DocField extends TypedDocumentedObject {
	public boolean modNullable = false;
	public boolean modStatic = false;
	public boolean modFinal = false;
	public boolean modDeprecated = false;

	@Override
	public JsonObject toJson() {
		JsonObject o = super.toJson();

		if (modNullable) {
			o.add("nullable", true);
		}

		if (modStatic) {
			o.add("static", true);
		}

		if (modFinal) {
			o.add("final", true);
		}

		if (modDeprecated) {
			o.add("deprecated", true);
		}

		return o;
	}
}
