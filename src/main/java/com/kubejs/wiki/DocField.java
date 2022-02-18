package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonObject;

public class DocField extends TypedDocumentedObject {
	public int access = 0; // 0 - getter, 1 - getter/setter, 2 - setter
	public boolean modNullable = false;
	public boolean modStatic = false;
	public boolean modDeprecated = false;
	public String enumConstant = "";

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

		if (!enumConstant.isEmpty()) {
			o.add("enum_constant", enumConstant);
		}

		return o;
	}
}
