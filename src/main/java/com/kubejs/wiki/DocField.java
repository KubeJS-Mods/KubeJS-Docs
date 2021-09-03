package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonObject;

public class DocField extends TypedDocumentedObject {
	public boolean modNullable = false;
	public boolean modStatic = false;
	public boolean modFinal = false;
	public boolean modSetter = false;
	public boolean modDeprecated = false;
	public boolean modBean = false;

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

		if (modSetter) {
			o.add("setter", true);
		}

		if (modDeprecated) {
			o.add("deprecated", true);
		}

		if (modBean) {
			o.add("bean", true);
		}

		return o;
	}
}
