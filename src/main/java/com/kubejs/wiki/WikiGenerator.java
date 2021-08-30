package com.kubejs.wiki;

import com.kubejs.wiki.json.JsonObject;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class WikiGenerator {
	public static void main(String[] args) throws Exception {
		new WikiGenerator(System.getenv("KUBEJS_DOCS_TOKEN")).run();
	}

	private final String token;
	public final List<DocClass> classes;
	public final Map<String, DocClass> classLookup;

	public WikiGenerator(String t) {
		token = t;
		classes = new ArrayList<>();
		classLookup = new HashMap<>();
	}

	public DocClass requireClass(String s) {
		DocClass c = classLookup.get(s);

		if (c == null) {
			throw new RuntimeException("Class '" + s + "' not found!");
		}

		return c;
	}

	public void run() throws Exception {
		Path docs = Paths.get("docs").normalize().toAbsolutePath();
		String docsString = docs.toString().replace('\\', '/');

		System.out.println("Loading docs from " + docsString);

		Files.walk(docs).filter(p -> p.getFileName().toString().endsWith(".kjsdoc")).forEach(p -> {
			DocClass c = new DocClass();
			c.file = p.normalize().toAbsolutePath();
			String pathString0 = c.file.toString().replace('\\', '/').replace(docsString, "");
			c.path = pathString0.substring(1, pathString0.length() - 7);
			System.out.println("- " + c.path);
			classes.add(c);
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

		JsonObject json = new JsonObject();

		for (DocClass c : classes) {
			json.add(c.path, c.toJson());
		}

		String jsonString = json.toString();
		byte[] jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);

		System.out.println();
		System.out.printf("Uploading json (%.2f KB):%n", jsonBytes.length / 1024D);
		System.out.println();
		System.out.println(jsonString);
		System.out.println();

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
}