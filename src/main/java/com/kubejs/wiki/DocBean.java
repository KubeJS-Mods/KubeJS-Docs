package com.kubejs.wiki;

public class DocBean extends TypedDocumentedObject {
	public boolean hasConflicts = false;
	public boolean hasSetter = false;
	public boolean hasGetter = false;
	public boolean modStatic = false;
	public boolean modDeprecated = false;
	public boolean modNullable = false;

	public DocBean(String n) {
		name = n;
	}
}
