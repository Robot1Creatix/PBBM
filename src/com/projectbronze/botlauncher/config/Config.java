package com.projectbronze.botlauncher.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import com.projectbronze.botlauncher.Core;

public class Config
{

	private static Properties cfg;
	public static File cfgFile;

	public static String startUpBotDirectory;
	public static boolean debugMode, onlyConsole;

	public static void init()
	{
		cfg = new Properties();
		cfgFile = new File(Core.jarDir, "config.cfg");
		if (!cfgFile.exists())
		{
			initFile();
		}
		initValues();
	}

	public static void save()
	{
		try
		{
			cfgFile.createNewFile();
			OutputStream o = new FileOutputStream(cfgFile);
			cfg.store(o, "Config File");
			o.close();
		}
		catch (IOException e)
		{
			e.printStackTrace(Core.err);
		}
	}

	public static void updateCfg()
	{
		cfg.setProperty("StartUp", startUpBotDirectory);
		cfg.setProperty("DebugMode", debugMode + "");
		cfg.setProperty("OnlyConsole", onlyConsole + "");
		save();
	}
	
	private static void initFile()
	{
		cfg.setProperty("StartUp", "NONE");
		cfg.setProperty("DebugMode", "false");
		cfg.setProperty("OnlyConsole", "true");
		save();
	}

	private static void initValues()
	{
		try
		{
			InputStream i = new FileInputStream(cfgFile);
			cfg.load(i);
			startUpBotDirectory = cfg.getProperty("StartUp", "NONE");
			debugMode = Boolean.getBoolean(cfg.getProperty("DebugMode", "false"));
			onlyConsole = Boolean.getBoolean(cfg.getProperty("OnlyConsole", "true"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}