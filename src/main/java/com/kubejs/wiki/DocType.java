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
	public String name = "";
	public DocClass typeClass;
	public List<DocType> generics = new ArrayList<>(0);

	public JsonElement toJson() {
		if (name.isEmpty() && generics.isEmpty()) {
			if (typeClass.id == 0) {
				return new JsonString(typeClass.path);
			} else {
				return new JsonNumber(typeClass.id);
			}
		}

		JsonObject o = new JsonObject();

		if (typeClass.id == 0) {
			o.add("class", typeClass.path);
		} else {
			o.add("class", typeClass.id);
		}

		if (!name.isEmpty()) {
			o.add("name", name);
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

	public void append(StringBuilder sb) {
		if (!name.isEmpty()) {
			sb.append(name);
			sb.append(' ');
		}

		sb.append(typeClass.path);

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