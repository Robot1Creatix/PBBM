package com.projectbronze.botlauncher.gui;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import com.projectbronze.botlauncher.gui.misc.DirectoryChooser;
import com.projectbronze.botlauncher.json.BotJson;

public class MainPanel extends PanelBase
{
	public JComboBox<BotJson> botSelector;
	public JButton addBot;
	public JButton initBot;
	public JFileChooser botChooser;
	public BotInfo botInfo;
	public MainPanel()
	{
		super();
		initComponents();
		initMisc();
		initListners();
	}
	
	
	private void initComponents()
	{
		add(botSelector = new JComboBox<BotJson>(), 0, 0);
		add(addBot = new JButton("Добавить бота"), 1, 0);
		add(botInfo = new BotInfo(), 0, 1);
		add(initBot = new JButton("Создать бота"), 0, 2);
		botChooser = new JFileChooser(new File("."));
	}
	
	private void initListners()
	{
		botChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		botChooser.setFileFilter(new FileFilter()
		{
			@Override
			public String getDescription()
			{
				return "Bot";
			}
			
			@Override
			public boolean accept(File f)
			{
				return f.getName().equals("bot.json") || f.isDirectory();
			}
		});
		botChooser.addActionListener(e -> {
			if(e.getActionCommand().equals("ApproveSelection"))
			{
				try
				{
					BotJson bot = new BotJson(botChooser.getSelectedFile()); 
					botSelector.addItem(bot);
					botSelector.setSelectedItem(bot);
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});
		botSelector.addActionListener(e -> {
			botInfo.setBotInfo((BotJson) botSelector.getSelectedItem());
		});
		addBot.addActionListener(e -> {
			botChooser.showOpenDialog(MainPanel.this);
		});
		initBot.addActionListener(e -> {
			new InitFrame(this);
			this.setEnabled(false);
		});
	}
	
	private void initMisc()
	{
		botSelector.setPreferredSize(new Dimension(200, 30));
	}
}
