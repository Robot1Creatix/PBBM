package com.projectbronze.botlauncher.gui;

import java.awt.BorderLayout;
import javax.swing.JFrame;

public class InitFrame extends JFrame
{
	public InitFrame(MainPanel parrent)
	{
		super("Создание бота");
		setSize(800, 600);
		setPreferredSize(getSize());
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		add(new InitPanel(parrent), BorderLayout.CENTER);
		setVisible(true);
	}
}
