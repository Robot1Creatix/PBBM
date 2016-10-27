package com.projectbronze.botlauncher.api;

import net.dv8tion.jda.JDA;

public interface IBot
{
	public void start();
	public default void stop(boolean free)
	{
		getBot().shutdown(free);
	}
	public default void restart()
	{
		stop(false);
		start();
	}
	public JDA getBot();
}
