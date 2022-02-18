package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonObject;

public class DocField extends TypedDocumentedObject {
	public int access = 0; // 0 - getter, 1 - getter/setter, 2 - setter
	public boolean modNullable = false;
	public boolean modStatic = false;
	public boolean modDeprecated = false;
	public String enumConstantAlias = "";

	@Override
	public JsonObject toJson() {
		JsonObject o = super.toJson();

		if (access != 0) {
			o.add("access", access);
		}

		if (modNullable) {
			o.add("nullable", true);
		}

		if (modStatic) {
			o.add("static", true);
		}

		if (modDeprecated) {
			o.add("deprecated", true);
		}

		if (!enumConstantAlias.isEmpty()) {
			o.add("enum_constant", enumConstantAlias);
		}

		return o;
	}
}
