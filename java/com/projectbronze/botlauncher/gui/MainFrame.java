package com.projectbronze.botlauncher.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;

public class MainFrame extends JFrame
{
	public MainFrame()
	{
		super("ProjectBronze bot manager");
		setPreferredSize(new Dimension(800, 600));
		setSize(800, 600);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(new MainPanel(), BorderLayout.CENTER);
		setVisible(true);
	}
}
