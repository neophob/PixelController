package com.neophob.sematrix.listener;

import java.net.BindException;
import java.net.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import processing.core.PApplet;
import processing.net.Client;
import processing.net.Server;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.input.Sound;
 
public class TcpServer implements Runnable {
	
	public enum ValidCommands {
		STATUS,
		CHANGE_GENERATOR_A,
		CHANGE_GENERATOR_B,
		CHANGE_EFFECT_A,
		CHANGE_EFFECT_B,
		CHANGE_MIXER,
		CHANGE_OUTPUT,
		CHANGE_OUTPUT_EFFECT,
		CHANGE_FADER,
		CHANGE_TINT,
		CHANGE_PRESENT,
		SAVE_PRESENT,
		LOAD_PRESENT,
		BLINKEN,
		IMAGE,
		RANDOM
	}

	private static Logger log = Logger.getLogger(TcpServer.class.getName());
	
	private Server tcpServer=null;
	private Client client;
	private String lastMsg="";
	
	private Thread runner;
	private PApplet app;
	private int sendPort;
	private String sendHost;
	private int count=0;
	
	public TcpServer(PApplet app, int listeningPort, String sendHost, int sendPort) throws BindException {
		this.app = app;
		app.registerDispose(this);
		
		this.sendHost = sendHost;
		this.sendPort = sendPort;
		
		try {
			serverWrapper(listeningPort);
			log.log(Level.INFO,	"Server started at port {0}", new Object[] { listeningPort });
			this.runner = new Thread(this);
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
	 * 
	 */
	public void run() {
		log.log(Level.INFO,	"Ready receiving messages...");
		while (Thread.currentThread() == runner) {
			count++;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}

			if (tcpServer!=null) {
				sendSoundStatus();
				if (Collector.getInstance().isRandomMode() && (count%20)==2) {
					sendStatusToGui();					
				}
				
				Client c = tcpServer.available();
				if (c!=null && c.available()>0) {					
					String msg = lastMsg+StringUtils.replace(c.readString(), "\n", "");
					msg = StringUtils.trim(msg);
					int msgCount = StringUtils.countMatches(msg, ";");
					log.log(Level.INFO,	"Got Message: {0}, cnt: {1}", new Object[] {msg, msgCount});
					//ideal, one message receieved
					if (msgCount==1) {
						msg = StringUtils.removeEnd(msg, ";");
						lastMsg="";
						sendMsg(StringUtils.split(msg, ' '));						
					//missing end of message... save it
					} else if (msgCount==0) {
						lastMsg=msg;
					//more than one message receieved, split it
					} else {
						lastMsg="";
						String[] msgs = msg.split(";");
						for (String s: msgs) {
							s = StringUtils.trim(s);
							s = StringUtils.removeEnd(s, ";");
							sendMsg(StringUtils.split(s, ' '));
						}
					}
				}							
			}
		}
	}
	
	/**
	 * 
	 * @param msg
	 */
	private void sendMsg(String[] msg) {
		ValidCommands response = MessageProcessor.processMsg(msg);
		if (response!=null) {
			if (response == ValidCommands.STATUS) {
				sendStatusToGui();
			}
		}
	}
	
	/**
	 * send beat detection to gui
	 */
	private void sendSoundStatus() {
		boolean bhat = Sound.getInstance().isHat();
		boolean bkick = Sound.getInstance().isKick();
		boolean bsnare = Sound.getInstance().isSnare();
		int hat=0, kick=0, snare=0;
		if (bhat) hat=1;
		if (bkick) kick=1;
		if (bsnare) snare=1;
		sendFudiMsg("SND_HAT "+hat);
		sendFudiMsg("SND_KICK "+kick);
		sendFudiMsg("SND_SNARE "+snare);
	}
	

	
	/**
	 * 
	 * @param msg
	 */
	private synchronized void sendFudiMsg(String msg) {
		if (client==null) {
			connectToClient();
		}
		
		if (client!=null) {
			client.write(msg+";");	
		}		
	}
	
	/**
	 * 
	 */
	public void sendStatusToGui() {
		for (String s:Collector.getInstance().getCurrentStatus()) {
			sendFudiMsg(s);
		}
	}
	
	
	/**
	 * 
	 */
	private void connectToClient() {
		try {
			//client = new Client(app, sendHost, sendPort);
			clientConnectionWrapper();
			log.log(Level.INFO,	"Pure Data Client connected!");
		} catch (Exception e) {
			log.log(Level.WARNING, "Pure Data Client not found!");
			client = null;			
		}		
	}
	
	/**
	 * stupid wrapper, as processing does not declare throws!
	 * @throws ConnectException
	 * @throws BindException
	 */
	private void clientConnectionWrapper() throws ConnectException, BindException {
		client = new Client(app, sendHost, sendPort);
	}
	
	/**
	 * 
	 * @param listeningPort
	 */
	private void serverWrapper(int listeningPort) throws ConnectException, BindException {
		tcpServer = new Server(app, listeningPort);
	}
}
