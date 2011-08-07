/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
 *
 * This file is part of PixelController.
 *
 * PixelController is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PixelController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PixelController.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.neophob.sematrix.listener;

import java.net.BindException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import processing.core.PApplet;
import processing.net.Client;
import processing.net.Server;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.listener.MessageProcessor.ValidCommands;

/**
 * 
 * @author michu
 *
 */
public final class TcpServer implements Runnable {

	private static final long CONNECT_RETRY_IN_MS = 16000;
	private static final String FUDI_MSG_END_MARKER = ";";
	private static final int FLOODING_TIME = 300;

	private static Logger log = Logger.getLogger(TcpServer.class.getName());

	private Server tcpServer=null;
	private Client client;
	private String lastMsg="";

	private Thread runner;
	private PApplet app;
	private int sendPort;
	private String sendHost;
	private int count=0;
	private long lastConnectTimestamp;
	private long lastMessageSentTimestamp;
	private boolean pdClientConnected=false;

	/**
	 * Start listening server, receieves fudi messages
	 * 
	 * @param app
	 * @param listeningPort
	 * @param sendHost
	 * @param sendPort
	 * @throws BindException
	 */
	public TcpServer(PApplet app, int listeningPort, String sendHost, int sendPort) throws BindException {		
		this.app = app;  
		app.registerDispose(this);

		this.sendHost = sendHost;
		this.sendPort = sendPort;

		try {
			serverWrapper(listeningPort);
			log.log(Level.INFO,	"Server started at port {0}", new Object[] { listeningPort });
			this.runner = new Thread(this);
			this.runner.setName("ZZ TCP Server");
			this.runner.start();

			connectToClient();
			if (client==null) {
				log.log(Level.INFO,	"Pure Data Client not available yet!");
			}
		} catch (Exception e) {
			log.log(Level.INFO,	"Failed to start TCP Server {0}", new Object[] { e });
		}				
	}

	public void dispose() {
		runner = null;
	}

	/**
	 * tcp server thread
	 */
	public void run() {
		log.log(Level.INFO,	"Ready receiving messages...");
		while (Thread.currentThread() == runner) {

			if (tcpServer!=null) {
				try {					
					
					//check if client is available
					if (client!=null && client.active()) {						
						//do not send sound status to gui - very cpu intensive!
						//sendSoundStatus();
						
						if ((count%20)==2 && Collector.getInstance().isRandomMode()) {
							sendStatusToGui();
						}
					}					

					Client c = tcpServer.available();
					if (c!=null && c.available()>0) {					
						
						//clean message
						String msg = lastMsg+StringUtils.replace(c.readString(), "\n", "");
						msg = StringUtils.trim(msg);
						
						int msgCount = StringUtils.countMatches(msg, FUDI_MSG_END_MARKER);
						log.log(Level.INFO,	"Got Message: {0}, cnt: {1}", new Object[] {msg, msgCount});
						
						//work around bug - the puredata gui sends back a message as soon we send one
						long delta = System.currentTimeMillis() - lastMessageSentTimestamp;											
						if (delta < FLOODING_TIME) {
							log.log(Level.INFO,	"Ignore message, flooding protection ({0}<{1})", 
									new String[] { ""+delta, ""+FLOODING_TIME });
							//delete message
							msgCount=0;
							msg="";
						}
						
						//ideal, one message receieved
						if (msgCount==1) {
							msg = StringUtils.removeEnd(msg, FUDI_MSG_END_MARKER);
							lastMsg="";
							processMessage(StringUtils.split(msg, ' '));						
						} else if (msgCount==0) {
							//missing end of message... save it
							lastMsg=msg;							
						} else {
							//more than one message receieved, split it
							//TODO: reuse partial messages
							lastMsg="";
							String[] msgs = msg.split(FUDI_MSG_END_MARKER);
							for (String s: msgs) {
								s = StringUtils.trim(s);
								s = StringUtils.removeEnd(s, FUDI_MSG_END_MARKER);
								processMessage(StringUtils.split(s, ' '));
							}
						}
					}												
				} catch (Exception e) {}
			}

			count++;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				//Ignored
			}

		}
	}

	/**
	 * 
	 * @param msg
	 */
	private void processMessage(String[] msg) {
		ValidCommands response = MessageProcessor.processMsg(msg, true);

		if (response != null) {
			switch (response) {
			case STATUS:
				sendStatusToGui();
				break;

			case STATUS_MINI:
				sendStatusToGuiMini();
				break;

			default:
				break;
			}
			try {
				Thread.sleep(100);				
			} catch (Exception e) {}
			
		}
	}

	/**
	 * send beat detection to gui
	 */
/*	private void sendSoundStatus() {
		int hat=0, kick=0, snare=0;
		if (Sound.getInstance().isHat()) hat=1;
		if (Sound.getInstance().isKick()) kick=1;
		if (Sound.getInstance().isSnare()) snare=1;
		sendFudiMsg("SND_HAT "+hat);
		sendFudiMsg("SND_KICK "+kick);
		sendFudiMsg("SND_SNARE "+snare);
	}*/



	/**
	 * 
	 * @param msg
	 */
	private synchronized void sendFudiMsg(String msg) {
		try {

			long l = System.currentTimeMillis()-lastConnectTimestamp;
			if (client==null &&  l > CONNECT_RETRY_IN_MS) {
				connectToClient();
			}

			if (client!=null && client.active()) {
				writeToClient(msg+FUDI_MSG_END_MARKER);	
			}
		} catch (Exception e) {
			//client disconnected!
			if (pdClientConnected) {
				log.warning("Failed to send data to the pure data client: "+e.getMessage());
				pdClientConnected=false;
				client.dispose();
				client=null;
			}
		}
	}

	/**
	 * send data to client and handle exceptions!
	 * @param s
	 * @throws Exception
	 */
	private void writeToClient(String s) throws Exception {
		client.output.write(s.getBytes());
		lastMessageSentTimestamp = System.currentTimeMillis();
	}

	/**
	 * 
	 */
	public void sendStatusToGui() {
		
		for (String s:Collector.getInstance().getCurrentStatus()) {
			sendFudiMsg(s);
			//x
			System.out.println(s);
		}
	}

	
	/**
	 * refresh gui if we selected a new visual
	 */
	public void sendStatusToGuiMini() {
		for (String s:Collector.getInstance().getCurrentMiniStatus()) {
			sendFudiMsg(s);
		}
		
	}

	/**
	 * 
	 */
	private void connectToClient() {
		Socket socket = new Socket();
		lastConnectTimestamp = System.currentTimeMillis();
		try {
			socket.connect(new InetSocketAddress(sendHost, sendPort), 2000);
			client = new Client(app, socket);
			log.log(Level.INFO,	"Pure Data Client connected at "+sendHost+":"+sendPort+"!");
			pdClientConnected=true;
		} catch (Exception e) {
			log.log(Level.WARNING, "Pure Data Client not found at "+sendHost+":"+sendPort);
			pdClientConnected=false;
			client = null;
			if (socket!=null) {
				try {
					socket.close();
				} catch (Exception e2) {}
			}
		}		
	}

	/**
	 * 
	 * @param listeningPort
	 */
	private void serverWrapper(int listeningPort) throws ConnectException, BindException {
		tcpServer = new Server(app, listeningPort);
	}
}
