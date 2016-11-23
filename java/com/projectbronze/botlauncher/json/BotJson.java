package com.projectbronze.botlauncher.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.projectbronze.botlauncher.Core;

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
	private static final String MODE = "mode";
	private static final String PATH = "path";
	private static final String AUTO_RESTART = "auto_restart";
	

	//================
	//    Instance     
	//================

	private JsonObject json;
	public final File file;
	public BotJson(File json) throws IOException
	{
		this.file = json;
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

	public Mode getMode()
	{
		return Mode.valueOf(json.get(MODE).getAsString());
	}
	
	public String getPath()
	{
		return json.get(PATH).getAsString();
	}

	public boolean getAutoRestart()
	{
		return json.get(AUTO_RESTART).getAsBoolean();
	}
	
	public JsonObject getJson() {
		return json;
	}
	
	@Override
	public String toString()
	{
		return getName();
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
		Mode mode = initMode(console);
		String path = initPath(console, mode);
		boolean autoRestart = initAutoRestart(console);
		create(name, author, mainClass, mode, path, autoRestart);
		console.close();
	}

	private static String initName(BufferedReader console) throws IOException
	{
		String curDir = new File("").getAbsolutePath();
		curDir = curDir.substring(curDir.lastIndexOf(File.separatorChar) + 1);
		System.out.print(String.format("Bot name (%s): ", curDir));
		String name = console.readLine();
		return name.equals("") ? curDir : name;
	}

	private static String initAuthor(BufferedReader console) throws IOException
	{
		String curUser = System.getProperty("user.name");
		System.out.print(String.format("Author name (%s): ", curUser));
		String author = console.readLine();
		return author.equals("") ? curUser : author;
	}

	private static String initMainClass(BufferedReader console, String name, String author) throws IOException
	{
		String suggestedClass = String.format("com.%s.%s.Core", author, name);
		System.out.print(String.format("Main class (%s): ", suggestedClass));
		String clazz = console.readLine();
		return clazz.equals("") ? suggestedClass : clazz;
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
	
	private static String initPath(BufferedReader console, Mode mode) throws IOException
	{
		String def = "";
		switch (mode)
		{
			case SRC:
			{
				System.out.print("Enter source dir [separate by ;] (src): ");
				def = "src";
				break;
			}
			case CLASS:
			{
				System.out.print("Enter dir containig classes (bin): ");
				def = "bin";
				break;
			}
			case JAR:
			{	
				System.out.print("Enter path to jar file (Bot.jar): ");
				def = "Bot.jar";
				break;
			}
		}
		String path = console.readLine();
		return path.equals("") ? def : path;
	}

	private static boolean initAutoRestart(BufferedReader console) throws IOException
	{
		System.out.print("Auto restart bot? (true|y|yes): ");
		String use = console.readLine();
		return use.equals("") ? true : (use == "true" || use == "y" || use == "yes");
	}

	public static File create(String name, String author, String mainclass, Mode mode, String path, boolean autoRestart) throws IOException
	{
		return create(new File(""), name, author, mainclass, mode, path, autoRestart);
	}
	
	public static File create(File dir, String name, String author, String mainclass, Mode mode, String path, boolean autoRestart) throws IOException
	{
		File json = new File(dir, "bot.json");
		json.createNewFile();
		FileWriter w = new FileWriter(json);
		w.write(createJson(name, author, mainclass, mode, path, autoRestart));
		w.close();
		return json;
	}

	private static String createJson(String name, String author, String mainclass, Mode mode, String path, boolean autoRestart)
	{
		JsonObject root = new JsonObject();
		root.addProperty(NAME, name);
		root.addProperty(AUTHOR, author);
		root.addProperty(MAIN_CLASS, mainclass);
		root.addProperty(MODE, mode.toString());
		root.addProperty(PATH, path);
		root.addProperty(AUTO_RESTART, autoRestart);
		return Core.gson.toJson(root);
	}
	
	@Override
	public int hashCode()
	{
		int result = 1;
		result = 37 * result + (file == null ? 0 : file.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null || obj.getClass() != getClass())
		{
			return false;
		}
		return file.getAbsolutePath().equals(((BotJson) obj).file.getAbsolutePath());
	}
}
