package com.projectbronze.botlauncher.gui.misc;

import java.io.File;
import javax.swing.JFileChooser;

public class DirectoryChooser extends JFileChooser
{

	public DirectoryChooser()
	{
		this(new File("."));
	}
	
	public DirectoryChooser(File curDir)
	{
		super(curDir);
		setFileSelectionMode(DIRECTORIES_ONLY);
	}
	
}
