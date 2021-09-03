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
	public boolean modOptional = false;
	public boolean modItself = false;
	public List<String> throwsTypes = new ArrayList<>(0);
	public DocBean bean = null;

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

		if (modOptional) {
			o.add("optional", true);
		}

		if (modItself) {
			o.add("itself", true);
		}

		if (!throwsTypes.isEmpty()) {
			JsonArray a = new JsonArray();

			for (String p : throwsTypes) {
				a.add(p);
			}

			o.add("throws", a);
		}

		if (!params.isEmpty()) {
			JsonArray a = new JsonArray();

			for (DocParam p : params) {
				a.add(p.toJson());
			}

			o.add("params", a);
		}

		return o;
	}
}
