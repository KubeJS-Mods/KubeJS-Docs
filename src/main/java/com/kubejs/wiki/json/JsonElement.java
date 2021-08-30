package com.kubejs.wiki.json;

public abstract class JsonElement {
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		append(sb);
		return sb.toString();
	}

	public abstract void append(StringBuilder sb);
}
