package com.projectbronze.botlauncher.gui;

import javax.swing.JLabel;
import com.projectbronze.botlauncher.json.BotJson;

public class BotInfo extends PanelBase
{
	private JLabel name, author, mainClass, mode, path, autoRestart;

	public BotInfo()
	{
		constrains.ipady = 15;
		init();
		setBotInfo(null);
	}

	private void init()
	{
		add(name = new JLabel(), 0, 0);
		add(author = new JLabel(), 0, 2);
		add(mainClass = new JLabel(), 0, 3);
		add(mode = new JLabel(), 0, 4);
		add(path = new JLabel(), 0, 5);
		add(autoRestart = new JLabel(), 0, 6);
	}

	public void setBotInfo(BotJson bot)
	{
		if (bot == null)
		{
			name.setText("Название: ");
			author.setText("Автор: ");
			mainClass.setText("Главный класс: ");
			mode.setText("Режим: ");
			path.setText("Путь (От директории запуска): ");
			autoRestart.setText("Использовать авто-перезапуск: ");
		}
		else
		{
			name.setText("Название: " + bot.getName());
			author.setText("Автор: " + bot.getAuthor());
			mainClass.setText("Главный класс: " + bot.getMainClass());
			mode.setText("Режим: " + bot.getMode());
			path.setText("Путь (От директории запуска): " + bot.getPath());
			autoRestart.setText("Использовать авто-перезапуск: " + (bot.getAutoRestart() ? "Да" : "Нет"));
		}
	}
}
