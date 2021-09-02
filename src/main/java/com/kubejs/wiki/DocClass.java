package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonArray;
import com.kubejs.wiki.json.JsonObject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class DocClass extends TypedDocumentedObject {
	public Path file;
	public DocNamespace namespace;
	public int id = 0;
	public List<String> lines;
	public String unique = "";
	public String path = "";
	public String classType = "class";
	public String typescript = "";
	public String displayName = "";
	public DocType extendsType;
	public List<String> generics = new ArrayList<>(0);
	public List<DocType> implementsTypes = new ArrayList<>(0);
	public List<DocField> fields = new ArrayList<>(0);
	public List<DocMethod> methods = new ArrayList<>(5);
	public List<String> events = new ArrayList<>(0);
	public List<String> recipes = new ArrayList<>(0);
	public Boolean canCancel = null;
	public List<DocExample> examples = new ArrayList<>(0);
	public String binding = "";

	@Override
	public String toString() {
		return namespace.namespace + ":" + path;
	}

	public String getPathName() {
		int i = path.lastIndexOf('.');
		return i == -1 ? path : path.substring(i + 1);
	}

	@Override
	public JsonObject toJson() {
		JsonObject o = super.toJson();

		o.add("id", id);
		o.add("path", path);
		o.add("unique", unique);

		if (!classType.equals("class")) {
			o.add("classType", classType);
		}

		if (!typescript.isEmpty()) {
			o.add("typescript", typescript);
		}

		if (!displayName.isEmpty()) {
			o.add("displayName", displayName);
		}

		if (!events.isEmpty()) {
			JsonArray a = new JsonArray();

			for (String s : events) {
				a.add(s);
			}

			o.add("events", a);
		}

		if (!recipes.isEmpty()) {
			JsonArray a = new JsonArray();

			for (String s : recipes) {
				a.add(s);
			}

			o.add("recipes", a);
		}

		if (canCancel != null) {
			o.add("canCancel", canCancel);
		}

		if (!examples.isEmpty()) {
			JsonArray a = new JsonArray();

			for (DocExample e : examples) {
				a.add(e.toJson());
			}

			o.add("examples", a);
		}

		if (!binding.isEmpty()) {
			o.add("binding", binding);
		}

		if (extendsType != null) {
			o.add("extends", extendsType.toJson());
		}

		if (!generics.isEmpty()) {
			JsonArray a = new JsonArray();

			for (String s : generics) {
				a.add(s);
			}

			o.add("generics", a);
		}

		if (!implementsTypes.isEmpty()) {
			JsonArray a = new JsonArray();

			for (DocType c : implementsTypes) {
				a.add(c.toJson());
			}

			o.add("implements", a);
		}

		if (!fields.isEmpty()) {
			JsonArray a = new JsonArray();

			for (DocField c : fields) {
				a.add(c.toJson());
			}

			o.add("fields", a);
		}

		if (!methods.isEmpty()) {
			JsonArray a = new JsonArray();

			for (DocMethod c : methods) {
				a.add(c.toJson());
			}

			o.add("methods", a);
		}

		return o;
	}

	public DocType itselfType() {
		DocType type = new DocType();
		type.typeClass = this;

		for (String s : generics) {
			DocType g = new DocType();
			g.typeClass = new DocClass();
			g.typeClass.path = s;
			type.generics.add(g);
		}

		return type;
	}
}
