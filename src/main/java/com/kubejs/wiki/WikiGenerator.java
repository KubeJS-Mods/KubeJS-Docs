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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
	public final Map<String, Collection<DocClass>> classLookup;
	public final Map<String, Collection<DocClass>> uniqueNames;

	public WikiGenerator() {
		namespaces = new ArrayList<>();
		classLookup = new HashMap<>();
		uniqueNames = new HashMap<>();
	}

	public DocClass requireClass(String s) {
		Collection<DocClass> c = classLookup.getOrDefault(s, Collections.emptyList());

		if (c.isEmpty()) {
			throw new DocException("Class '" + s + "' not found!");
		} else if (c.size() >= 2) {
			throw new DocException("Class '" + s + "' has ambiguous name! Shared classes: " + c);
		}

		return c.iterator().next();
	}

	public void addLookup(String s, DocClass c) {
		classLookup.computeIfAbsent(s, s1 -> new HashSet<>()).add(c);
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
					throw new DocException("", e);
				}
			}

			try {
				Files.walk(dir).filter(p -> p.getFileName().toString().endsWith(".kjsdoc")).forEach(p -> {
					DocClass c = new DocClass();
					c.namespace = namespace;
					c.file = p.normalize().toAbsolutePath();
					String pathString0 = c.file.toString().replace('\\', '/').replace(docsString, "");
					c.name = pathString0.substring(1, pathString0.length() - 7).replace('/', '.');
					System.out.println("- " + c.name);
					namespace.classes.add(c);
					classes.add(c);

					try {
						c.lines = Files.readAllLines(c.file);
					} catch (IOException e) {
						throw new DocException("", e);
					}

					for (String s : c.lines) {
						if (s.startsWith("alias ")) {
							String s1 = s.substring(6).trim();

							if (!s1.isEmpty()) {
								addLookup(s1, c);
							}
						}
					}

					addLookup(c.getPathName(), c);
					addLookup(c.name, c);
					uniqueNames.computeIfAbsent(c.getPathName(), s -> new ArrayList<>()).add(c);
				});
			} catch (IOException e) {
				throw new DocException("", e);
			}

			namespaces.add(namespace);
		});

		for (Map.Entry<String, Collection<DocClass>> entry : uniqueNames.entrySet()) {
			if (entry.getValue().size() == 1) {
				entry.getValue().iterator().next().unique = entry.getKey();
			} else {
				for (DocClass c : entry.getValue()) {
					c.unique = c.name;
				}
			}
		}

		DocType booleanType = classes.stream().filter(c -> c.name.equals("java.lang.Boolean")).findFirst().map(DocClass::itselfType).orElse(null);

		for (DocClass c : classes) {
			TypedDocumentedObject lastObject = c;

			String exampleId = "";
			String exampleTitle = "";
			List<String> exampleLines = new ArrayList<>();

			System.out.println("> " + c.name);

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
						lastObject.info.add(s.substring(1).trim());
					} else if (!s.isEmpty() && !s.startsWith("//")) {
						System.out.println("> --- " + s);
						LineReader reader = new LineReader(s);
						String type = reader.readJavaName();

						switch (type) {
							case "extends" -> c.extendsType = readType(c, reader);
							case "implements" -> c.implementsTypes.add(readType(c, reader));
							case "primitive", "interface", "enum", "annotation" -> c.classType = type;
							case "typescript" -> c.typescript = reader.read();
							case "displayName" -> c.displayName = reader.readJavaName();
							case "generic" -> c.generics.add(reader.readJavaName());
							case "event" -> c.events.add(reader.read(CharTest.EVENT_ID));
							case "recipe" -> c.recipes.add(reader.read(CharTest.RESOURCE_LOCATION));
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
								boolean modDefault = false;
								boolean modItself = false;

								if (t.equals("public") || t.equals("private") || t.equals("protected")) {
									throw new DocException("Method can't have a modifier!");
								}

								if (t.equals("nullable")) {
									modNullable = true;
									t = reader.readJavaName();
								}

								if (t.equals("static")) {
									modStatic = true;
									t = reader.readJavaName();
								}

								if (t.equals("final")) {
									modFinal = true;
									t = reader.readJavaName();
								}

								if (t.equals("deprecated")) {
									modDeprecated = true;
									t = reader.readJavaName();
								}

								if (t.equals("default")) {
									if (modFinal) {
										throw new DocException("Can't mix 'default' and 'final'!");
									}

									modDefault = true;
									t = reader.readJavaName();
								}

								if (t.equals("itself")) {
									if (modStatic) {
										throw new DocException("Can't mix 'itself' and 'static'!");
									}

									modItself = true;
								}

								DocType dt = modItself ? c.itselfType() : readType(c, t, reader);
								String name = reader.readJavaName();

								if (reader.skipWhitespace().read(CharTest.FUNC_OPEN).equals("(")) {
									DocMethod method = new DocMethod();
									method.name = name;
									method.type = dt;
									lastObject = method;
									method.modNullable = modNullable;
									method.modStatic = modStatic;
									method.modFinal = !modFinal;
									method.modDeprecated = modDeprecated;
									method.modDefault = modDefault;
									method.modItself = modItself;

									if (!reader.skipWhitespace().read(CharTest.FUNC_CLOSE).equals(")")) {
										do {
											String pt = reader.readJavaName();
											boolean pNullable = false;
											boolean pDefault = false;

											if (pt.equals("nullable")) {
												pNullable = true;
												pt = reader.readJavaName();
											}

											if (pt.equals("default")) {
												pDefault = true;
												pt = reader.readJavaName();
											}

											DocParam p = new DocParam();
											p.type = readType(c, pt, reader);
											p.name = reader.readJavaName();
											p.modNullable = pNullable;
											p.modDefault = pDefault;
											method.params.add(p);
										} while (!reader.isEOL() && !reader.skipWhitespace().read(CharTest.FUNC_CLOSE_OR_COMMA).equals(")"));
									}

									if (method.name.length() >= 3 && CharTest.AZ_U.test(method.name.charAt(2)) && method.name.startsWith("is") && method.params.size() == 0 && method.type.is(booleanType)) {
										DocBean bean = c.bean(2, method.name);

										if (bean.modStatic == method.modStatic && bean.type != null && !bean.type.is(method.type)) {
											bean.hasConflicts = true;
										} else {
											bean.type = method.type;
											bean.modStatic = method.modStatic;
										}

										if (method.modDeprecated) {
											bean.modDeprecated = true;
										}

										bean.hasGetter = true;
										method.bean = bean;
									} else if (method.name.length() >= 4 && CharTest.AZ_U.test(method.name.charAt(3)) && method.name.startsWith("get") && method.params.size() == 0) {
										DocBean bean = c.bean(3, method.name);

										if (bean.modStatic == method.modStatic && bean.type != null && !bean.type.is(method.type)) {
											bean.hasConflicts = true;
										} else {
											bean.type = method.type;
											bean.modStatic = method.modStatic;
										}

										if (method.modNullable) {
											bean.modNullable = true;
										}

										if (method.modDeprecated) {
											bean.modDeprecated = true;
										}

										bean.hasGetter = true;
										method.bean = bean;
									} else if (method.name.length() >= 4 && CharTest.AZ_U.test(method.name.charAt(3)) && method.name.startsWith("set") && method.params.size() == 1) {
										DocBean bean = c.bean(3, method.name);

										if (bean.modStatic == method.modStatic && bean.type != null && !bean.type.is(method.params.get(0).type)) {
											bean.hasConflicts = true;
										} else {
											bean.type = method.params.get(0).type;
											bean.modStatic = method.modStatic;
										}

										if (method.params.get(0).modNullable) {
											bean.modNullable = true;
										}

										if (method.modDeprecated) {
											bean.modDeprecated = true;
										}

										bean.hasSetter = true;
										method.bean = bean;
									}

									c.methods.add(method);
								} else {
									DocField field = new DocField();
									field.name = name;
									field.type = dt;
									lastObject = field;
									field.access = modFinal ? 0 : 1;
									field.modNullable = modNullable;
									field.modStatic = modStatic;
									field.modDeprecated = modDeprecated;
									c.fields.add(field);
								}
							}
						}
					}
				} catch (Exception ex) {
					throw new DocException("Failed to handle line #" + (line + 1) + " '" + c.lines.get(line) + "' of " + c.name, ex);
				}
			}
		}

		for (DocClass c : classes) {
			c.methods.removeIf(m -> m.bean != null && !m.bean.hasConflicts);

			for (DocBean bean : c.beans.values()) {
				if (!bean.hasConflicts) {
					DocField field = new DocField();
					field.name = bean.name;

					if (!bean.hasGetter) {
						field.access = 2;
					} else if (bean.hasSetter) {
						field.access = 1;
					} else {
						field.access = 0;
					}

					if (bean.modStatic) {
						field.modStatic = true;
					}

					if (bean.modDeprecated) {
						field.modDeprecated = true;
					}

					if (bean.modNullable) {
						field.modNullable = true;
					}

					field.type = bean.type;
					c.fields.add(field);
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

		HttpURLConnection connection = (HttpURLConnection) new URL("https://kubejs.com/wiki/upload").openConnection();
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
			throw new DocException("Failed to connect! Error code " + connection.getResponseCode());
		}

		connection.disconnect();
	}

	private DocType readType(DocClass context, String type, LineReader reader) {
		reader.skipWhitespace();

		if (type.equals("itself")) {
			return context.itselfType();
		}

		DocType t;

		if (type.startsWith("$_")) {
			throw new DocException("Class can't start with $_!");
		} else if (context.generics.contains(type)) {
			t = DocType.generic(type);
		} else if (!classLookup.containsKey(type)) {
			t = DocType.undocumented(type);
		} else {
			t = DocType.ofClass(requireClass(type), false);

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