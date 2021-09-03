package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonArray;
import com.kubejs.wiki.json.JsonElement;
import com.kubejs.wiki.json.JsonNumber;
import com.kubejs.wiki.json.JsonObject;
import com.kubejs.wiki.json.JsonString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class DocType {
	public DocClass typeClass;
	public List<DocType> generics = new ArrayList<>(0);

	public JsonElement toJson() {
		if (generics.isEmpty()) {
			if (typeClass.id == 0) {
				return new JsonString(typeClass.name);
			} else {
				return new JsonNumber(typeClass.id);
			}
		}

		JsonObject o = new JsonObject();

		if (typeClass.id == 0) {
			o.add("class", typeClass.name);
		} else {
			o.add("class", typeClass.id);
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
		return type == this || typeClass.is(type.typeClass) && generics.size() == type.generics.size() && generics.equals(type.generics);
	}

	public void append(StringBuilder sb) {
		sb.append(typeClass.name);

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