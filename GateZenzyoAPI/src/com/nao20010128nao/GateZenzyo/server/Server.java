package com.nao20010128nao.GateZenzyo.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import com.nao20010128nao.GateZenzyo.server.network.gate_zenzyo.Info;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class Server {
	public static final byte[] SUPPORTED_COMPRESSIONS = new byte[] { Info.SUPPORTED_COMPRESSIONS_NONE,
			Info.SUPPORTED_COMPRESSIONS_GZIP, Info.SUPPORTED_COMPRESSIONS_DEFLATE };

	String[] args;
	String ip;
	int port;
	int bindPort = 19132;
	Map<InetAddress, Connection> connections = new HashMap<>();

	public static void main(String... args) {
		System.out.println("  ______                                 _____       _       \r\n"
				+ " |___  /                                / ____|     | |      \r\n"
				+ "    / / ___ _ __  _____   _  ___ ______| |  __  __ _| |_ ___ \r\n"
				+ "   / / / _ \\ '_ \\|_  / | | |/ _ \\______| | |_ |/ _` | __/ _ \\\r\n"
				+ "  / /_|  __/ | | |/ /| |_| | (_) |     | |__| | (_| | ||  __/\r\n"
				+ " /_____\\___|_| |_/___|\\__, |\\___/       \\_____|\\__,_|\\__\\___|\r\n"
				+ "                       __/ |                                 \r\n"
				+ "                      |___/                                  ");
		new Server(args);
	}

	public Server(String[] args) {
		this.args = args;
		OptionParser op = new OptionParser();
		op.accepts("port").withRequiredArg();

		OptionSet os = op.parse(args);
		if (os.has("ip")) {
			ip = os.valueOf("ip").toString();
		} else {
			System.err.println("ip is not set!");
			System.exit(0);
		}
		if (os.has("port")) {
			port = new Integer(os.valueOf("port").toString());
		} else {
			System.err.println("port is not set!");
			System.exit(0);
		}
		if (os.has("bind-port")) {
			bindPort = new Integer(os.valueOf("bind-port").toString());
		}

		new ServerThread().start();
		new ConnectionCheckThread().start();
	}

	public void removeEntry(Connection con) {
		for (Map.Entry<InetAddress, Connection> ent : connections.entrySet()) {
			if (ent.getValue() == con) {
				connections.remove(ent.getKey());
				return;
			}
		}
	}

	public class ServerThread extends Thread {
		@Override
		public void run() {
			DatagramSocket ds = null;
			try {
				System.out.println("Binding on port " + bindPort + "...");
				ds = new DatagramSocket(bindPort);
				while (true) {
					try {
						DatagramPacket dp = new DatagramPacket(new byte[102400], 102400);
						ds.receive(dp);
						if (connections.containsKey(dp.getAddress())) {
							connections.get(dp.getAddress()).process(dp);
						} else {
							Connection con = new Connection(ip, bindPort, dp.getAddress(), Server.this);
							connections.put(dp.getAddress(), con);
							con.process(dp);
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
			} finally {
				if (ds != null)
					ds.close();
			}
		}
	}

	public class ConnectionCheckThread extends Thread {
		@Override
		public void run() {
			// TODO 自動生成されたメソッド・スタブ

		}
	}
}
