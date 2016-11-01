package com.projectbronze.botlauncher.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.projectbronze.botlauncher.Core;
import com.projectbronze.botlauncher.utils.FileUtils;

public class Config {
	public static String startUpBotDirectory, libsDirectory;
	public static boolean debugMode;
	public static Set<BotJson> guiDefaultBots;

	public static void init() {
		File cfg = new File(Core.jarDir, "config.cfg");
		if (!cfg.exists()) {
			loadDefaultValues();
			save();
		} else {
			load(cfg);
		}
	}

	private static void loadDefaultValues() {
		startUpBotDirectory = "NONE";
		libsDirectory = new File(Core.jarDir, "libs").getAbsolutePath();
		debugMode = false;
		guiDefaultBots = new HashSet<BotJson>();
	}

	private static void load(File f) {
		try (BufferedReader r = FileUtils.createReader(f)) {
			JsonObject root = new JsonParser().parse(r).getAsJsonObject();
			JsonObject main = root.getAsJsonObject("main"), gui = root.getAsJsonObject("gui");
			startUpBotDirectory = main.get("defaultBotDir").getAsString();
			libsDirectory = main.get("libsDir").getAsString();
			debugMode = main.get("debug").getAsBoolean();
			String defaultBots = gui.get("defaultBots").getAsString();
			if (!defaultBots.equals("")) {
				guiDefaultBots = Arrays.stream(defaultBots.split(";")).map(s -> {
					try {
						return new BotJson(new File(s));
					} catch (FileNotFoundException e) {
						System.err.println("Бот не найден в " + s);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}).collect(Collectors.toSet());
			}
			else 
			{
				guiDefaultBots = new HashSet<BotJson>();
			}
		} catch (IOException e) {
			System.err.println("Unable to load config file");
			e.printStackTrace();
		}
	}

	public static void save() {
		File cfg = new File(Core.jarDir, "config.cfg");
		JsonObject root = generateJson();
		try (BufferedWriter w = FileUtils.createWriter(cfg, false)) {
			w.write(Core.gson.toJson(root));
		} catch (IOException e) {
			System.err.println("Unable to save config");
			e.printStackTrace();
		}
	}

	private static JsonObject generateJson() {
		JsonObject root = new JsonObject();
		JsonObject main = new JsonObject(), gui = new JsonObject();
		root.add("main", main);
		root.add("gui", gui);
		main.addProperty("defaultBotDir", startUpBotDirectory);
		main.addProperty("libsDir", libsDirectory);
		main.addProperty("debug", debugMode);
		gui.addProperty("defaultBots", guiDefaultBots.parallelStream().map(f -> f == null ? null : f.file.getAbsolutePath()).reduce((s1, s2) -> s1 += s2 == null ? "" : (";" + s2)).orElse(""));
		return root;
	}
}
