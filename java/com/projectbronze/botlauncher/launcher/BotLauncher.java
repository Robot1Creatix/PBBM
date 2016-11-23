package com.projectbronze.botlauncher.launcher;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.projectbronze.botlauncher.api.IBot;
import com.projectbronze.botlauncher.json.BotJson;
import com.projectbronze.botlauncher.json.Config;

public class BotLauncher {
	private static boolean libsLoaded = false;

	public static IBot launchBot(File dir, PrintStream info) throws IOException, ClassNotFoundException, InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException {
		File botFile = new File(dir, "bot.json");
		if (!botFile.exists()) {
			throw new IllegalArgumentException("This directory doesn't contains bot.json");
		}
		if (!libsLoaded) {
			addLibs();
			libsLoaded = true;
		}
		BotJson bot = new BotJson(botFile);
		switch (bot.getMode()) {
			case SRC: {

				break;
			}
			case CLASS:
			case JAR: {
				addFileToLoader(new File(dir, bot.getPath()));
			}
		}
		IBot ret = launch(bot, info);
		return ret;
	}

	private static final URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
	private static final Method addUrl;
	static {
		try {
			addUrl = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			addUrl.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	private static void addUrlToLoader(URL url) {
		try {
			addUrl.invoke(loader, url);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static void addUrlsToLoader(URL[] urls) {
		for (URL url : urls) {
			addUrlToLoader(url);
		}

	}

	private static void addFileToLoader(File file) {
		try {
			addUrlToLoader(file.toURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private static void addFilesToLoader(File[] files) {
		if (files != null) {
			for (File f : files) {
				addFileToLoader(f);
			}
		}
	}

	public static void addLibs() {
		addFilesToLoader(new File(Config.libsDirectory).listFiles());
	}

	private static IBot launch(BotJson bot, PrintStream info) throws ClassNotFoundException, InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException {
		Class<?> botClass = Class.forName(bot.getMainClass(), true, ClassLoader.getSystemClassLoader());
		if (!IBot.class.isAssignableFrom(botClass)) {
			throw new IllegalArgumentException("Main class of bot must impliment IBot interface");
		}
		try {
			Constructor<? extends IBot> ibotcon = botClass.asSubclass(IBot.class).getConstructor();
			ibotcon.setAccessible(true);
			IBot ibot = ibotcon.newInstance();
			ibot.start(info);
			return ibot;
		} catch (IllegalAccessException e) {
			throw new SecurityException(e);
		}
	}
}
