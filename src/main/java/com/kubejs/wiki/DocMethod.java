package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonArray;
import com.kubejs.wiki.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class DocMethod extends TypedDocumentedObject {
	public List<DocParam> params = new ArrayList<>(0);
	public boolean modNullable = false;
	public boolean modStatic = false;
	public boolean modFinal = false;
	public boolean modDeprecated = false;
	public List<DocType> throwsTypes = new ArrayList<>(0);

	public JsonObject toJson() {
		JsonObject o = new JsonObject();
		o.add("type", type.toJson());

		if (!params.isEmpty()) {
			JsonArray a = new JsonArray();

			for (DocParam p : params) {
				a.add(p.toJson());
			}

			o.add("params", a);
		}

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

		if (!throwsTypes.isEmpty()) {
			JsonArray a = new JsonArray();

			for (DocType p : throwsTypes) {
				a.add(p.toJson());
			}

			o.add("throws", a);
		}

		return o;
	}
}
