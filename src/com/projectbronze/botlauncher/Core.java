package com.projectbronze.botlauncher;

import java.io.File;
import java.io.IOException;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import com.projectbronze.botlauncher.config.Config;
import com.projectbronze.botlauncher.gui.MainFrame;
import com.projectbronze.botlauncher.json.BotJson;
import com.projectbronze.botlauncher.launcher.BotLauncher;
import com.projectbronze.botlauncher.log.LogStream;

public class Core
{
	public static LogStream log, err;
	public static File jarDir;
	public static void main(String[] args) throws IOException
	{ 
		String s = Core.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", "\\ ");
		if(s.endsWith(".jar"))
		{
			s = s.substring(0, s.lastIndexOf('/'));
		}
		jarDir = new File(s);
		log = new LogStream(System.out);
		err = new LogStream(System.err);
		Config.init();
		File bot = new File("bot.json");
		if(args.length == 0)
		{
			if(bot.exists())
			{
				try
				{
					BotLauncher.launchBot(new File(new File("").getAbsolutePath()));
				}
				catch (ReflectiveOperationException e)
				{
					err.error("Unable to load bot");
					e.printStackTrace(err);
				}
			}
			else
			{
				err.error("Unable to find bot.json");
			}
		}
		else
		{
			switch(args[0])
			{
				case "autorun":
				case "auto":
				{
					if(Config.startUpBotDirectory.equals("NONE"))
					{
						err.error("Autorun bot not specified");
					}
					else
					{
						
					}
					break;
				}
				case "init":
				{
					BotJson.init();
					break;
				}
				case "setAuto":
				{
					if(bot.exists())
					{
						Config.startUpBotDirectory = new File("").getAbsolutePath();
						Config.updateCfg();
					}
					else
					{
						err.error("Unable to find bot.json, use `init` command");
					}
					break;
				}
				case "gui":
				{
					try
					{
						UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
					}
					catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
					{
						e.printStackTrace();
						try
						{
							UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
						}
						catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1)
						{
							e1.printStackTrace();
						}
					}
					SwingUtilities.invokeLater(MainFrame::new);
				}
			}
		}
	}
}
