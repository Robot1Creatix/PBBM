package com.projectbronze.botlauncher.nogui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.projectbronze.botlauncher.Core;
import com.projectbronze.botlauncher.json.Config;

public class SocketNoGuiHandler implements INoGuiHandler, AutoCloseable {
	private Socket sc;
	private final ServerSocket ssc;
	private DataInputStream r;
	private DataOutputStream w;
	private boolean gotStop = false;

	public SocketNoGuiHandler() throws IOException {
		ssc = new ServerSocket(Config.socketPort);
		Core.log.info("Socket started");
		awaitConnect();
	}

	@Override
	public String readLine() throws Exception {
		try {
			String line = r.readUTF();
			if (line.equals(NoGuiManager.STOP_MANAGER)) {
				gotStop = true;
			}

			return line;
		} catch (EOFException | SocketException e) {
			Core.log.info("Connection lost");
			awaitConnect();
			return readLine();
		}
	}

	@Override
	public void writeLine(String line) throws Exception {
		w.writeUTF(line);
		if (w.equals(NoGuiManager.MSG_END)) {
			w.flush();
			if (gotStop) {
				close();
			}
		}
	}

	@Override
	public void close() throws Exception {
		sc.close();
		ssc.close();
	}
	
	private void awaitConnect() throws IOException
	{
		Core.log.info("Waiting for connection");
		sc = ssc.accept();
		Core.log.info("Got connection from %s", sc.getInetAddress().getHostAddress());
		r = new DataInputStream(sc.getInputStream());
		w = new DataOutputStream(sc.getOutputStream());
	}

}
