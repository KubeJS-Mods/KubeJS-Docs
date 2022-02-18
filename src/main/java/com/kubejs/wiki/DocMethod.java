package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonArray;
import com.kubejs.wiki.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class DocMethod extends TypedDocumentedObject {
	public List<DocParam> params = new ArrayList<>(0);
	public boolean modNullable = false;
	public boolean modStatic = false;
	public boolean modReadonly = false;
	public boolean modDeprecated = false;
	public boolean modDefault = false;
	public boolean modItself = false;
	public List<String> throwsTypes = new ArrayList<>(0);
	public DocBean bean = null;
	public List<String> generics = new ArrayList<>(0);

	@Override
	public JsonObject toJson() {
		JsonObject o = super.toJson();

		if (modNullable) {
			o.add("nullable", true);
		}

		if (modStatic) {
			o.add("static", true);
		}

		//if (modFinal) {
		//	o.add("final", true);
		//}

		if (modDeprecated) {
			o.add("deprecated", true);
		}

		//if (modDefault) {
		//	o.add("default", true);
		//}

		if (modItself) {
			o.add("itself", true);
			o.remove("type");
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

		if (!generics.isEmpty()) {
			JsonArray a = new JsonArray();

			for (String p : generics) {
				a.add(p);
			}

			o.add("generics", a);
		}

		return o;
	}
}
