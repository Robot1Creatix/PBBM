package com.projectbronze.botlauncher.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class PanelBase extends JPanel
{
	protected GridBagConstraints constrains = new GridBagConstraints();
	public PanelBase()
	{
		super(new GridBagLayout());
		constrains.weightx = 10;
		constrains.weighty = 10;
	}
	
	protected void add(JComponent comp, int x, int y)
	{
		constrains.gridx = x;
		constrains.gridy = y;
		add(comp, constrains);
	}
}
