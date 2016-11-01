package com.projectbronze.botlauncher.utils;

import java.io.PrintStream;

import javax.swing.JTextArea;

public class TextAreaPrintStream extends PrintStream {
	private JTextArea target;
	public TextAreaPrintStream(JTextArea target) {
		super(System.out);
		this.target = target;
	}
	
	@Override
    public void print(String s) {
        super.print(s);
        target.append(s);
    }
    
    @Override
    public void println(String x) {
        super.println(x);
        appendNL();
    }
    
    @Override
    public void println(int x) {
        super.println(x);
        appendNL();
    }
    
    @Override
    public void println(boolean x) {
        super.println(x);
        appendNL();
    }
    
    @Override
    public void println(char x) {
        super.println(x);
        appendNL();
    }
    
    @Override
    public void println(char[] x) {
        super.println(x);
        appendNL();
    }
    
    @Override
    public void println(double x) {
        super.println(x);
        appendNL();
    }
    
    @Override
    public void println(float x) {
        super.println(x);
        appendNL();
    }
    
    @Override
    public void println(long x) {
        super.println(x);
        appendNL();
    }
    
    @Override
    public void println(Object x) {
        super.println(x);
        appendNL();
    }
    
    @Override
    public void println() {
        super.println();
        appendNL();
    }
    
    protected void appendNL()
    {
        target.append("\n");
    }
	
}
