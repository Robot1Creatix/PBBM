package com.projectbronze.botlauncher.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

import com.projectbronze.botlauncher.api.IBot;
import com.projectbronze.botlauncher.json.BotJson;
import com.projectbronze.botlauncher.json.Config;
import com.projectbronze.botlauncher.launcher.BotLauncher;
import com.projectbronze.botlauncher.utils.TextAreaPrintStream;

public class MainPanel extends PanelBase {
	public JComboBox<BotJson> botSelector;
	public JButton addBot;
	public JButton initBot;
	public JButton launchBot;
	public JButton addBotToDefault;
	public JFileChooser botChooser;
	public JTextArea console;
	public BotInfo botInfo;
	public HashMap<BotJson, JTextArea> consoleMap = new HashMap<>();
	private static class BotMapVal
	{
		public JTextArea console;
		public IBot bot;
	}
	public MainPanel() {
		super();
		initComponents();
		initListners();
		initMisc();
	}

	private void initComponents() {
		add(botSelector = new JComboBox<BotJson>(new Vector<BotJson>(Config.guiDefaultBots)), 0, 0);
		add(addBot = new JButton("Добавить бота"), 2, 0);
		add(botInfo = new BotInfo(), 0, 1);
		add(console = new JTextArea(20, 80), 2, 1);
		add(initBot = new JButton("Создать бота"), 0, 2);
		add(addBotToDefault = new JButton("Добавить бота к стандартным"), 2, 2);
		add(launchBot = new JButton("Запустить бота"), 1, 2);
		botChooser = new JFileChooser(new File("."));
		
	}

	private void initListners() {
		addBotToDefault.addActionListener(e -> {
			BotJson bot = (BotJson) botSelector.getSelectedItem();
			if(bot == null)
			{
				console.append("Бот не выбран");
			}
			else if(Config.guiDefaultBots.contains(bot.file))
			{
				console.append("Бот уже добавлен");
			}
			else
			{
				Config.guiDefaultBots.add(bot);
				Config.save();
				console.append("Бот добавлен");
			}
		});
		botChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		botChooser.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "Bot";
			}

			@Override
			public boolean accept(File f) {
				return f.getName().equals("bot.json") || f.isDirectory();
			}
		});
		botChooser.addActionListener(e -> {
			if (e.getActionCommand().equals("ApproveSelection")) {
				try {
					BotJson bot = new BotJson(botChooser.getSelectedFile());
					if (((DefaultComboBoxModel<BotJson>) botSelector.getModel()).getIndexOf(bot) == -1) {
						botSelector.addItem(bot);
					}
					botSelector.setSelectedItem(bot);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		botSelector.addActionListener(e -> {
			BotJson bot = (BotJson) botSelector.getSelectedItem();
			botInfo.setBotInfo(bot);
			if (console != null) {
				remove(console);
			}
			if (!consoleMap.containsKey(bot)) {
				JTextArea c = new JTextArea(20, 80);
				c.setFont(new Font("Roboto Medium", Font.PLAIN, 15));
				c.setMinimumSize(new Dimension(400, 400));
				consoleMap.put(bot, c);
				console = c;
			} else {
				console = consoleMap.get(bot);
			}
			add(console, 2, 1);
		});
		addBot.addActionListener(e -> {
			botChooser.showOpenDialog(MainPanel.this);
		});
		initBot.addActionListener(e -> {
			new InitFrame(this);
			this.setEnabled(false);
		});
		launchBot.addActionListener(e -> {
			if (botSelector.getSelectedItem() != null) {
				BotJson bot = (BotJson) botSelector.getSelectedItem();
				File dir = bot.file.getParentFile();
				try {
					BotLauncher.launchBot(dir, new TextAreaPrintStream(console));

				} catch (ClassNotFoundException e1) {
					console.append("Невозможно найти класс " + bot.getMainClass() + "\n");
				} catch (InstantiationException | NoSuchMethodException e1) {
					console.append("Главный класс бота не должен иметь конструктора или он должен быть без параметров\n");
				} catch (IllegalArgumentException e1) {
					console.append("Главный класс бота должен быть наследником IBot\n");
				} catch (InvocationTargetException e1) {
					console.append("Конструктор бота выдал исключение: " + e1.getTargetException().getMessage() + "\n");
				} catch (SecurityException e1) {
					console.append("Не удалось получить доступ к конструктору бота\n");
				} catch (IOException e1) {
					console.append("Что-то не пошло не так с загрузкой бота: " + e1.getMessage() + "\n");
				}
			}
		});
	}

	private void initMisc() {
		console.setFont(new Font("Roboto Medium", Font.PLAIN, 15));
		console.setMinimumSize(new Dimension(400, 400));
		botSelector.setMinimumSize(new Dimension(200, 30));
		botSelector.setSelectedIndex(0);//To perform listners actions
	}
}
