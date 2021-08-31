package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonArray;
import com.kubejs.wiki.json.JsonObject;
import com.kubejs.wiki.reader.CharTest;
import com.kubejs.wiki.reader.LineReader;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class WikiGenerator {
	public static void main(String[] args) throws Exception {
		new WikiGenerator().run(System.getenv("KUBEJS_DOCS_TOKEN"));
	}

	public final List<DocNamespace> namespaces;
	public final Map<String, DocClass> classLookup;

	public WikiGenerator() {
		namespaces = new ArrayList<>();
		classLookup = new HashMap<>();
	}

	public DocClass requireClass(String s) {
		DocClass c = classLookup.get(s);

		if (c == null) {
			throw new RuntimeException("Class '" + s + "' not found!");
		}

		return c;
	}

	public void run(String token) throws Exception {
		Path docs = Paths.get("docs").normalize().toAbsolutePath();
		List<DocClass> classes = new ArrayList<>();

		Files.list(docs).filter(Files::isDirectory).forEach(dir -> {
			DocNamespace namespace = new DocNamespace();
			namespace.namespace = dir.getFileName().toString();
			String docsString = dir.toString().replace('\\', '/');

			System.out.println("Loading docs from " + docsString);

			Path readme = dir.resolve("README.md");

			if (Files.exists(readme)) {
				try {
					namespace.info.addAll(Files.readAllLines(readme));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				Files.walk(dir).filter(p -> p.getFileName().toString().endsWith(".kjsdoc")).forEach(p -> {
					DocClass c = new DocClass();
					c.namespace = namespace;
					c.file = p.normalize().toAbsolutePath();
					String pathString0 = c.file.toString().replace('\\', '/').replace(docsString, "");
					c.path = pathString0.substring(1, pathString0.length() - 7);
					System.out.println("- " + c.path);
					namespace.classes.add(c);
					classes.add(c);
					c.id = classes.size();
				});
			} catch (IOException e) {
				e.printStackTrace();
			}

			namespaces.add(namespace);
		});

		for (DocClass c : classes) {
			c.lines = Files.readAllLines(c.file);

			for (String s : c.lines) {
				if (s.startsWith("alias ")) {
					String s1 = s.substring(6).trim();

					if (!s1.isEmpty()) {
						classLookup.put(s1, c);
					}
				} else if (s.startsWith("displayname ")) {
					c.name = s.substring(12).trim();
				}
			}

			if (!c.name.isEmpty()) {
				classLookup.put(c.name, c);
			}
		}

		for (DocClass c : classes) {
			classLookup.put(c.getPathName(), c);
			classLookup.put(c.path.replace('/', '.'), c);
			classLookup.put(c.path, c);
		}

		for (DocClass c : classes) {
			TypedDocumentedObject lastObject = c;

			String exampleId = "";
			String exampleTitle = "";
			List<String> exampleLines = new ArrayList<>();

			for (int line = 0; line < c.lines.size(); line++) {
				try {
					String s = c.lines.get(line).trim();

					if (s.startsWith("```")) {
						if (exampleId.isEmpty()) {
							String[] s1 = s.substring(3).split(" ", 2);
							exampleId = s1[0];
							exampleTitle = s1.length == 2 ? s1[1] : "";
						} else {
							DocExample e = new DocExample();
							e.id = exampleId;
							e.title = exampleTitle;
							e.text = new ArrayList<>(exampleLines);
							c.examples.add(e);
							exampleId = "";
							exampleTitle = "";
						}
					} else if (!exampleId.isEmpty()) {
						exampleLines.add(s);
					} else if (s.startsWith("#")) {
						String s1 = s.substring(1).trim();

						if (s1.startsWith("@") && lastObject instanceof DocMethod) {
							int si = s1.indexOf(' ');
							String pname = s1.substring(1, si);
							DocParam param = ((DocMethod) lastObject).params.stream().filter(p -> p.type.name.equals(pname)).findFirst().orElse(null);

							if (param != null) {
								param.info.add(s1.substring(si + 1).trim());
							} else {
								throw new RuntimeException("Can't find param " + pname + "!");
							}
						} else {
							lastObject.info.add(s1);
						}
					} else if (!s.isEmpty() && !s.startsWith("//")) {
						LineReader reader = new LineReader(s);
						String type = reader.readJavaName();

						switch (type) {
							case "extends" -> c.extendsClass = readType(c, reader);
							case "implements" -> c.implementsClass.add(readType(c, reader));
							case "typescript" -> c.typescript = reader.read();
							case "type" -> c.classtype = reader.read();
							case "generic" -> c.generics.add(reader.readJavaName());
							case "event" -> c.events.add(reader.read(CharTest.EVENT_ID));
							case "canCancel" -> c.canCancel = reader.read().equalsIgnoreCase("true");
							case "binding" -> c.binding = reader.readJavaName();
							case "throws" -> {
								if (lastObject instanceof DocMethod) {
									((DocMethod) lastObject).throwsTypes.add(reader.readJavaName());
								}
							}
							case "displayname", "alias" -> {
								// Ignore
							}
							default -> {
								String t = type;
								boolean modNullable = false;
								boolean modStatic = false;
								boolean modFinal = false;
								boolean modDeprecated = false;
								boolean modOptional = false;

								if (t.equals("nullable")) {
									modNullable = true;
									t = reader.read();
								}

								if (t.equals("static")) {
									modStatic = true;
									t = reader.read();
								}

								if (t.equals("final")) {
									modFinal = true;
									t = reader.read();
								}

								if (t.equals("deprecated")) {
									modDeprecated = true;
									t = reader.read();
								}

								if (t.equals("optional")) {
									modOptional = true;
									t = reader.read();
								}

								DocType dt = readType(c, t, reader);
								dt.name = reader.read();

								if (reader.skipWhitespace().read(CharTest.FUNC_OPEN).equals("(")) {
									DocMethod method = new DocMethod();
									method.type = dt;
									lastObject = method;
									method.modNullable = modNullable;
									method.modStatic = modStatic;
									method.modFinal = modFinal;
									method.modDeprecated = modDeprecated;
									method.modOptional = modOptional;

									if (!reader.skipWhitespace().read(CharTest.FUNC_CLOSE).equals(")")) {
										do {
											String pt = reader.readJavaName();
											boolean pNullable = false;
											boolean pOptional = false;

											if (t.equals("nullable")) {
												pNullable = true;
												t = reader.read();
											}

											if (t.equals("optional")) {
												pOptional = true;
												t = reader.read();
											}

											DocParam p = new DocParam();
											p.type = readType(c, pt, reader);
											p.type.name = reader.read();
											p.modNullable = pNullable;
											p.modOptional = pOptional;
											method.params.add(p);
										} while (!reader.isEOL() && !reader.skipWhitespace().read(CharTest.FUNC_CLOSE_OR_COMMA).equals(")"));
									}

									c.methods.add(method);
								} else {
									DocField field = new DocField();
									field.type = dt;
									lastObject = field;
									field.modNullable = modNullable;
									field.modStatic = modStatic;
									field.modFinal = modFinal;
									field.modDeprecated = modDeprecated;
									c.fields.add(field);
								}

								System.out.println(dt);
							}
						}
					}
				} catch (Exception ex) {
					throw new RuntimeException("Failed to handle line #" + (line + 1) + " '" + c.lines.get(line) + "' of " + c.path, ex);
				}
			}
		}

		JsonObject json = new JsonObject();
		json.add("version", Instant.now().toString());

		JsonArray narray = new JsonArray();

		for (DocNamespace n : namespaces) {
			narray.add(n.toJson());
		}

		json.add("namespaces", narray);

		String jsonString = json.toString();
		byte[] jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);

		System.out.println();
		System.out.printf("Uploading json (%.2f KB):%n", jsonBytes.length / 1024D);
		System.out.println();
		System.out.println(jsonString);
		System.out.println();

		if (token == null || token.isBlank()) {
			System.out.println("No token provided! Exiting...");
			return;
		}

		HttpURLConnection connection = (HttpURLConnection) new URL("https://wiki.kubejs.com/upload").openConnection();
		connection.setRequestMethod("POST");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		connection.setRequestProperty("User-Agent", "GitHubActions");
		connection.setRequestProperty("Authorization", "Bearer " + token);

		try (OutputStream stream = new BufferedOutputStream(connection.getOutputStream())) {
			stream.write(jsonBytes);
		}

		if (connection.getResponseCode() / 100 != 2) {
			throw new RuntimeException("Failed to connect! Error code " + connection.getResponseCode());
		}

		connection.disconnect();
	}

	private DocType readType(DocClass context, String type, LineReader reader) {
		reader.skipWhitespace();
		DocType t = new DocType();

		if (context.generics.contains(type)) {
			t.typeClass = new DocClass();
			t.typeClass.path = type;
		} else {
			t.typeClass = requireClass(type);

			if (reader.read(CharTest.DIAMOND_OPEN).equals("<")) {
				t.generics.add(readType(context, reader));

				while (reader.read(CharTest.DIAMOND_CLOSE_OR_COMMA).equals(",")) {
					t.generics.add(readType(context, reader));
				}
			}
		}

		return t;
	}

	private DocType readType(DocClass context, LineReader reader) {
		return readType(context, reader.readJavaName(), reader);
	}
}