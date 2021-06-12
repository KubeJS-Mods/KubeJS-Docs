package com.kubejs.docs;

import java.nio.file.Path;

/**
 * @author LatvianModder
 */
public class DocClass {
	public static final int TYPE_DOCUMENTED = 1;
	public static final int TYPE_JAVA = 2;
	public static final int TYPE_NATIVE = 3;

	public final String name;
	public Path doc;
	public int classType;
	public String displayName;

	public DocClass(String n) {
		name = n;
		doc = null;
		classType = TYPE_JAVA;
		displayName = name;
	}

	public void setDoc(Path p) {
		doc = p;
		classType = TYPE_DOCUMENTED;
	}
}
