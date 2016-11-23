package com.projectbronze.botlauncher.nogui;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.projectbronze.botlauncher.Core;
import com.projectbronze.botlauncher.api.IBot;
import com.projectbronze.botlauncher.json.BotJson;
import com.projectbronze.botlauncher.launcher.BotLauncher;

import net.dv8tion.jda.JDA.Status;

public class NoGuiManager extends Thread {

	public static final String BOT_MSG = "BOT_MSG", MSG_END = "MSG_END", OK = "OK";
	public static final String UNABLE_TO_PARSE = "UNABLE_TO_PARSE", UNKNOWN_COMMAND = "UNKNOW_COMMAND", NOT_ENOUGH_ARGS = "NOT_ENOUGH_ARGS";
	public static final String DIR_NOT_EXITST = "NOT_EXIST", NOT_DIR = "NOT_DIR", BOT_NOT_FOUND = "NOT_FOUND", BOT_DIR_NOT_SETED = "DIR_NOT_SETED";
	public static final String BOT_ALREADY_STARTED = "ALREADY_STARTED", BOT_NOT_STARTED = "NOT_STARTED";
	public static final String START_BOT = "START", STOP_BOT = "STOP", RESTART_BOT = "RESTART", GET_BOT_STATE = "GET_STATE", SET_BOT_DIR = "SET_DIR", GET_BOT_DIR = "GET_DIR", GET_BOT_JSON = "GET_JSON", STOP_MANAGER = "STOP_MANAGER";

	private final INoGuiHandler handler;
	private IBot bot;
	private File botDir;
	private BotJson json;
	private HandlerPrintStream ps;
	private BotThread thread;
	private volatile BotState state = BotState.NOT_STARTED;

	public NoGuiManager(INoGuiHandler handler) {
		this.handler = handler;
		ps = new HandlerPrintStream();
		thread = new BotThread();
	}

	@Override
	public void run() {
		try {
			loop: while (true) {
				List<String> cmd = readCommand();
				if (cmd.isEmpty()) {
					continue;
				}
				String command = cmd.get(0);
				synchronized (handler) {
					switch (command) {
						case SET_BOT_DIR: {
							if (checkArgs(2, cmd)) {
								String dir = cmd.get(1);
								if (dir.equals("null")) {
									dir = null;
									json = null;
									handler.writeLine(OK);
								}
								File d = new File(dir);
								if (!d.exists()) {
									handler.writeLine(DIR_NOT_EXITST);
									handler.writeLine(d.getAbsolutePath());
								} else if (!d.isDirectory()) {
									handler.writeLine(NOT_DIR);
									handler.writeLine(d.getAbsolutePath());
								} else if (d.list((di, name) -> di.equals(d) && name.equals("bot.json")).length == 0) {
									handler.writeLine(BOT_NOT_FOUND);
									handler.writeLine(d.getAbsolutePath());
								} else {
									botDir = d;
									json = new BotJson(new File(d, "bot.json"));
									handler.writeLine(OK);
								}
							}
							break;
						}
						case GET_BOT_DIR: {
							handler.writeLine(botDir == null ? BOT_DIR_NOT_SETED : botDir.getAbsolutePath());
							break;
						}
						case GET_BOT_JSON: {
							handler.writeLine(json == null ? BOT_DIR_NOT_SETED : json.getJson().toString());
							break;
						}
						case START_BOT: {
							if (json == null) {
								handler.writeLine(BOT_DIR_NOT_SETED);
							} else if (bot != null) {
								handler.writeLine(BOT_ALREADY_STARTED);
							} else {
								state = BotState.STARTING;
								if (!thread.isAlive()) {
									thread.start();
								}
								handler.writeLine(OK);
							}
							break;
						}
						case RESTART_BOT: {
							if (bot == null) {
								handler.writeLine(BOT_NOT_STARTED);
							} else {
								state = BotState.RESTARTING;
								handler.writeLine(OK);
							}
							break;
						}
						case STOP_BOT: {
							if (checkArgs(2, cmd)) {
								boolean free;
								String freeS = cmd.get(1);
								if (freeS.equals("true")) {
									free = true;
								} else if (freeS.equals("false")) {
									free = false;
								} else {
									handler.writeLine(UNABLE_TO_PARSE);
									handler.writeLine(freeS);
									break;
								}
								state = BotState.STOPPING;
								handler.writeLine(OK);
							}
							break;
						}
						case STOP_MANAGER: {
							state = BotState.FINISED;
							handler.writeLine(OK);
							handler.writeLine(MSG_END);
							break loop;
						}
						case GET_BOT_STATE:
						{
							handler.writeLine(state.name());
							break;
						}
						default: {
							handler.writeLine(UNKNOWN_COMMAND);
							break;
						}
					}
					handler.writeLine(MSG_END);
				}
			}
		} catch (Throwable t) {
			Core.err.error("NoGui manager %s has an uncaught excepiton", handler);
			t.printStackTrace(Core.err);
			try {
				handler.writeLine(t.getLocalizedMessage());
			} catch (Throwable t1) {
				Core.err.println("Unable to send exception message");
				t1.printStackTrace(Core.err);
			}
		}
	}

	private List<String> readCommand() throws Exception {
		List<String> cmd = new ArrayList<>();
		for (String line = handler.readLine(); !line.equals(MSG_END); line = handler.readLine()) {
			cmd.add(line);
		}
		return cmd;
	}

	private boolean checkArgs(int minArgs, List<String> cmd) throws Exception {
		if (cmd.size() < minArgs) {
			notEnoughArgs(minArgs);
			return false;
		} else {
			return true;
		}
	}

	private void notEnoughArgs(int minArgs) throws Exception {
		handler.writeLine(NOT_ENOUGH_ARGS);
		handler.writeLine(minArgs + "");
	}

	private class HandlerPrintStream extends PrintStream {
		public HandlerPrintStream() {
			super(System.out);
		}

		@Override
		public void print(String s) {
			super.print(s);
			synchronized (handler) {
				try {
					handler.writeLine(BOT_MSG);
					handler.writeLine(s);
					handler.writeLine(MSG_END);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static enum BotState {
		NOT_STARTED,
		STOPPING,
		STARTING,
		WORKING,
		RESTARTING,
		FINISED;
	}

	private class BotThread extends Thread {

		@Override
		public void run() {
			loop: while (true) {
				BotState state = NoGuiManager.this.state;
				switch (state) {
					case NOT_STARTED:
					case WORKING: {
						break;
					}
					case STOPPING: {
						if (bot != null && bot.getBot().getStatus() == Status.CONNECTED) {
							bot.stop(false);
						}
						bot = null;
						NoGuiManager.this.state = BotState.NOT_STARTED;
						break;
					}
					case RESTARTING: {
						bot.restart(ps);
						NoGuiManager.this.state = BotState.WORKING;
						break;
					}
					case STARTING: {
						if (bot == null) {
							try {
								bot = BotLauncher.launchBot(botDir, ps);
							} catch (ClassNotFoundException | InstantiationException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException | IOException e) {
								throw new RuntimeException(e);
							}
							NoGuiManager.this.state = BotState.WORKING;
						}
						break;
					}
					case FINISED: {
						if (bot != null) {
							bot.stop(true);
							bot = null;
							
						}
						break loop;
					}
				}
			}
		}
	}

}
