package com.projectbronze.botlauncher.launcher;

import java.io.File;
import java.io.IOException;
import com.projectbronze.botlauncher.Core;
import com.projectbronze.botlauncher.json.BotJson;

public class BotLauncher
{
	public static Process launchBot(File dir) throws IOException
	{
		File botFile = new File("bot.json");
		if(!botFile.exists())
		{
			throw new IllegalArgumentException("This directory doesn't contains bot.json");
		}
		BotJson bot = new BotJson(botFile);
		switch(bot.getMode())
		{
			case SRC:
			{
				break;
			}
			case CLASS:
			{
				break;
			}
			case JAR:
			{
				String classpath = String.format("java -cp Bot.jar%s %s", (File.pathSeparatorChar + Core.jarDir.getAbsolutePath() + File.separatorChar + "libs" + File.separatorChar + "*"), bot.getMainClass());
				return new ProcessBuilder(classpath.split(" ")).directory(dir).inheritIO().start();
			}
		}
		return null;
	}
}
