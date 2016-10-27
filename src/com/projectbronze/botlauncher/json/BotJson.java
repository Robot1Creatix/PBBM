package com.projectbronze.botlauncher.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BotJson
{
	public static enum Mode
	{
		JAR,
		CLASS,
		SRC;
	}

	private static final String NAME = "name";
	private static final String AUTHOR = "author";
	private static final String MAIN_CLASS = "main_class";
	private static final String USE_JDAE = "use_jdae";
	private static final String MODE = "mode";
	private static final String AUTO_RESTART = "auto_restart";
	private JsonObject json;

	//================
	//    Instance     
	//================

	public BotJson(File json) throws IOException
	{
		FileReader r = new FileReader(json);
		this.json = new JsonParser().parse(r).getAsJsonObject();
		r.close();
	}

	public String getName()
	{
		return json.get(NAME).getAsString();
	}

	public String getAuthor()
	{
		return json.get(AUTHOR).getAsString();
	}

	public String getMainClass()
	{
		return json.get(MAIN_CLASS).getAsString();
	}

	public boolean getUseJDAEnchacer()
	{
		return json.get(USE_JDAE).getAsBoolean();
	}

	public Mode getMode()
	{
		return Mode.valueOf(json.get(MODE).getAsString());
	}

	public boolean getAutoRestart()
	{
		return json.get(AUTO_RESTART).getAsBoolean();
	}

	//================
	//     Static     
	//================

	/**
	 * Aks questions to user and creates json
	 * @throws IOException If console reading meets error
	 */
	public static void init() throws IOException
	{
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		String name = initName(console);
		String author = initAuthor(console);
		String mainClass = initMainClass(console, name, author);
		boolean useJDAE = initJDAE(console);
		Mode mode = initMode(console);
		boolean autoRestart = initAutoRestart(console);
		create(name, author, mainClass, useJDAE, mode, autoRestart);
		console.close();
	}

	private static String initName(BufferedReader console) throws IOException
	{
		String curDir = new File("").getAbsolutePath();
		curDir = curDir.substring(curDir.lastIndexOf(File.separatorChar) + 1);
		System.out.printf("Bot name (%s): ", curDir);
		String name = console.readLine();
		return name.equals("") ? curDir : name;
	}

	private static String initAuthor(BufferedReader console) throws IOException
	{
		String curUser = System.getProperty("user.name");
		System.out.printf("Author name (%s): ", curUser);
		String author = console.readLine();
		return author.equals("") ? curUser : author;
	}

	private static String initMainClass(BufferedReader console, String name, String author) throws IOException
	{
		String suggestedClass = String.format("com.%s.%s.Core", author, name);
		System.out.printf("Main class (%s): ", suggestedClass);
		String clazz = console.readLine();
		return clazz.equals("") ? suggestedClass : clazz;
	}

	private static Boolean initJDAE(BufferedReader console) throws IOException
	{
		System.out.print("Use JDA Enchacer (true|y|yes): ");
		String use = console.readLine();
		return use.equals("") ? true : (use == "true" || use == "y" || use == "yes");
	}

	private static Mode initMode(BufferedReader console) throws IOException
	{
		Mode mode = null;
		while (mode == null)
		{
			System.out.print("Mode [jar|class|src] (src): ");
			try
			{
				String m = console.readLine();
				mode = m.equals("") ? Mode.SRC : Mode.valueOf(m.toUpperCase());
			}
			catch (IllegalArgumentException e)
			{
				continue;
			}
		}
		return mode;
	}

	private static boolean initAutoRestart(BufferedReader console) throws IOException
	{
		System.out.print("Auto restart bot? (true|y|yes): ");
		String use = console.readLine();
		return use.equals("") ? true : (use == "true" || use == "y" || use == "yes");
	}

	public static File create(String name, String author, String mainclass, boolean useJDAE, Mode mode, boolean autoRestart) throws IOException
	{
		File json = new File("bot.json");
		json.createNewFile();
		FileWriter w = new FileWriter(json);
		w.write(createJson(name, author, mainclass, useJDAE, mode, autoRestart));
		w.close();
		return json;
	}

	private static String createJson(String name, String author, String mainclass, boolean useJDAE, Mode mode, boolean autoRestart)
	{
		JsonObject root = new JsonObject();
		root.addProperty(NAME, name);
		root.addProperty(AUTHOR, author);
		root.addProperty(MAIN_CLASS, mainclass);
		root.addProperty(USE_JDAE, useJDAE);
		root.addProperty(MODE, mode.toString());
		root.addProperty(AUTO_RESTART, autoRestart);
		return new GsonBuilder().setPrettyPrinting().create().toJson(root);
	}
}
