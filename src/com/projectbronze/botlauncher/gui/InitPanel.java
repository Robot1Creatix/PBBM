package com.projectbronze.botlauncher.gui;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ContainerListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.projectbronze.botlauncher.gui.misc.DirectoryChooser;
import com.projectbronze.botlauncher.json.BotJson.Mode;

public class InitPanel extends PanelBase
{
	private MainPanel parrent;
	private JTextField name, author, mainClass, path;
	private JComboBox<String> mode;
	private JCheckBox autoRestart;
	private JFileChooser dirSelector, pathSelector;
	private JButton create, selectDir, selectPath;
	private JLabel dir, dirL, nameL, authorL, mainClassL, pathL, modeL, autoRestartL, error;
	public InitPanel(MainPanel parrent)
	{
		this.parrent = parrent;
		initComponents();
		initMisc();
	}
	
	private void initComponents()
	{
		add(dirL = new JLabel("Директория бота:"), 0, 0);
		add(nameL = new JLabel("Название бота:"), 0, 1);
		add(authorL = new JLabel("Имя автора:"), 0, 2);
		add(mainClassL = new JLabel("Главный класс:"), 0, 3);
		add(modeL = new JLabel("Режим запуска бота:"), 0, 4);
		add(pathL = new JLabel("Путь для запуска бота:"), 0, 5);
		add(autoRestartL = new JLabel("Использовать авто-перезапуск:"), 0, 6);
		add(dir = new JLabel(), 3, 0);
		add(selectDir = new JButton("Выбрать директорию"), 4, 0);
		add(name = new JTextField(10), 3, 1);
		add(author = new JTextField(System.getProperty("user.name"), 10), 3, 2);
		add(mainClass = new JTextField(10), 3, 3);
		add(mode = new JComboBox<String>(new String[] {"jar", "class", "src"}), 3, 4);
		add(path = new JTextField(10), 3, 5);
		add(selectPath = new JButton("Выбрать путь"), 4, 5);
		add(autoRestart = new JCheckBox(), 3, 6);
		add(create = new JButton("Создать бота"), 1, 7);
		add(error = new JLabel(""), 1, 8);
		dirSelector = new DirectoryChooser();
		pathSelector = new JFileChooser(new File("."));
		pathSelector.setFileSelectionMode(JFileChooser.FILES_ONLY);
	}
	private Runnable updateMainClass = () -> mainClass.setText(String.format("com.%s.%s.Core", author.getText(), name.getText()));
	private DocumentListener updMCListner = new DocumentListener()
	{
		@Override
		public void removeUpdate(DocumentEvent e)
		{
			updateMainClass.run();
		}
		
		@Override
		public void insertUpdate(DocumentEvent e)
		{
			updateMainClass.run();
		}
		
		@Override
		public void changedUpdate(DocumentEvent e)
		{
			updateMainClass.run();
		}
	};
	private void initMisc()
	{
		selectDir.addActionListener(e -> dirSelector.showOpenDialog(InitPanel.this));
		selectPath.addActionListener(e -> pathSelector.showOpenDialog(InitPanel.this));
		name.getDocument().addDocumentListener(updMCListner);
		author.getDocument().addDocumentListener(updMCListner);
		dirSelector.addActionListener(e -> {
			File f = dirSelector.getSelectedFile();
			dir.setText(f.getAbsolutePath());
			name.setText(f.getName());
			pathSelector.setCurrentDirectory(f);
		});
		pathSelector.addActionListener(e -> {
			path.setText(pathSelector.getSelectedFile().getAbsolutePath());
		});
		mode.addActionListener(e -> {
			Mode mode = Mode.valueOf(((String) this.mode.getSelectedItem()).toUpperCase());
			switch(mode)
			{
				case SRC:
				case CLASS:
				{
					pathSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					break;
				}
				case JAR:
				{
					pathSelector.setFileSelectionMode(JFileChooser.FILES_ONLY);
					break;
				}
			}
		});
		create.addActionListener(e -> {
			error.setForeground(Color.RED);
			if(name.getText().equals("") || author.getText().equals("") || mainClass.getText().equals("") || path.getText().equals("") || dirSelector.getSelectedFile() == null)
			{
				error.setText("Все параметры обязательны");
				return;
			}
			File dir = dirSelector.getSelectedFile();
			File path = pathSelector.getSelectedFile();
			File bot = new File(dir, "bot.json");
			if(bot.exists())
			{
				error.setText("В данной директории уже создан бот");
			}
			
		}); 
	}
}
