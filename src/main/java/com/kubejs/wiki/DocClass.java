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
	public List<String> lines;
	public String path = "";
	public String name = "";
	public String classtype = "class";
	public String typescript = "";
	public DocClass extendsClass;
	public List<DocType> generics = new ArrayList<>(0);
	public List<DocClass> implementsClass = new ArrayList<>(0);
	public List<DocConstructor> constructors = new ArrayList<>(0);
	public List<DocField> fields = new ArrayList<>(0);
	public List<DocMethod> methods = new ArrayList<>(5);
	public List<String> events = new ArrayList<>(0);
	public Boolean canCancel = null;

	public TypedDocumentedObject lastObject = this;

	public String getPathName() {
		int i = path.lastIndexOf('/');
		return i == -1 ? path : path.substring(i + 1);
	}

	public JsonObject toJson() {
		JsonObject o = new JsonObject();

		if (!name.isEmpty()) {
			o.add("name", name);
		}

		if (!classtype.equals("class")) {
			o.add("classtype", classtype);
		}

		if (!typescript.isEmpty()) {
			o.add("typescript", typescript);
		}

		if (extendsClass != null) {
			o.add("extends", extendsClass.path);
		}

		if (!generics.isEmpty()) {
			JsonArray a = new JsonArray();

			for (DocType s : generics) {
				a.add(s.toJson());
			}

			o.add("generics", a);
		}

		if (!implementsClass.isEmpty()) {
			JsonArray a = new JsonArray();

			for (DocClass c : implementsClass) {
				a.add(c.path);
			}

			o.add("implements", a);
		}

		if (!constructors.isEmpty()) {
			JsonArray a = new JsonArray();

			for (DocConstructor c : constructors) {
				a.add(c.toJson());
			}

			o.add("constructors", a);
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

		if (!events.isEmpty()) {
			JsonArray a = new JsonArray();

			for (String s : events) {
				a.add(s);
			}

			o.add("events", a);
		}

		if (canCancel != null) {
			o.add("canCancel", canCancel);
		}

		return o;
	}
}
