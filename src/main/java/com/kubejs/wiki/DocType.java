package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonArray;
import com.kubejs.wiki.json.JsonElement;
import com.kubejs.wiki.json.JsonObject;
import com.kubejs.wiki.json.JsonString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class DocType {
	public static final int TYPE_CLASS = 0;
	public static final int TYPE_GENERIC = 1;
	public static final int TYPE_UNDOCUMENTED = 2;

	public static DocType generic(String s) {
		DocType t = new DocType();
		t.docClass = new DocClass();
		t.docClass.name = s;
		t.type = DocType.TYPE_GENERIC;
		return t;
	}

	public static DocType undocumented(String s) {
		DocType t = new DocType();
		t.docClass = new DocClass();
		t.docClass.name = s;
		t.type = DocType.TYPE_UNDOCUMENTED;
		return t;
	}

	public static DocType ofClass(DocClass c, boolean b) {
		DocType t = new DocType();
		t.docClass = c;
		t.type = DocType.TYPE_CLASS;

		if (b) {
			for (String s : c.generics) {
				t.generics.add(DocType.generic(s));
			}
		}

		return t;
	}

	public int type = TYPE_CLASS;
	public DocClass docClass;
	public List<DocType> generics = new ArrayList<>(0);

	private DocType() {
	}

	public JsonElement toJson() {
		if (type == TYPE_CLASS && generics.isEmpty()) {
			return new JsonString(docClass.unique);
		}

		JsonObject o = new JsonObject();
		o.add("name", type == 0 ? docClass.unique : docClass.name);

		if (type != 0) {
			o.add("type", type);
		}

		if (!generics.isEmpty()) {
			JsonArray a = new JsonArray();

			for (DocType t : generics) {
				a.add(t.toJson());
			}

			o.add("generics", a);
		}

		return o;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		append(sb);
		return sb.toString();
	}

	public boolean is(DocType type) {
		return type == this || docClass.is(type.docClass) && generics.size() == type.generics.size() && generics.equals(type.generics);
	}

	public void append(StringBuilder sb) {
		sb.append(docClass.name);

		if (!generics.isEmpty()) {
			sb.append('<');

			for (int i = 0; i < generics.size(); i++) {
				if (i > 0) {
					sb.append(',');
				}

				generics.get(i).append(sb);
			}

			sb.append('>');
		}
	}
}