package com.projectbronze.botlauncher.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;

public class MainFrame extends JFrame
{
	private final MainPanel panel;
	public MainFrame()
	{
		super("ProjectBronze bot manager");
		setPreferredSize(new Dimension(800, 600));
		setSize(800, 600);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		add(panel = new MainPanel(), BorderLayout.CENTER);
		setVisible(true);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		panel.botMap.values().parallelStream().map(v -> v.bot).filter(b -> b != null).forEach(b -> b.stop(true));
	}
}
