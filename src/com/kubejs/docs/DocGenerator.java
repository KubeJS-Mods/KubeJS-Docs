package com.kubejs.docs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class DocGenerator {
	public static final Map<String, DocClass> DOCUMENTED_CLASSES = new LinkedHashMap<>();

	public static DocClass getDocClass(String s) {
		return DOCUMENTED_CLASSES.computeIfAbsent(s, DocClass::new);
	}

	public static void main(String[] args) throws IOException {
		getDocClass("primitive").classType = DocClass.TYPE_NATIVE;

		Path docs = Paths.get("docs");
		Files.walk(docs).filter(p -> p.getFileName().toString().endsWith(".jdoc")).forEach(p -> getDocClass(docs.relativize(p).toString().replace('\\', '/').replace(".jdoc", "")).setDoc(p));

		for (DocClass c : new ArrayList<>(DOCUMENTED_CLASSES.values())) {
			System.out.println("Loading " + c.name);
			List<String> lines = Files.readAllLines(c.doc);
			System.out.println(lines);
		}
	}
}