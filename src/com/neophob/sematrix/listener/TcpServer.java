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
		CHANGE_SHUFFLER_SELECT,
		CHANGE_THRESHOLD_VALUE,
		SAVE_PRESENT,
		LOAD_PRESENT,
		BLINKEN,
		IMAGE,
		IMAGE_ZOOMER,
		TEXTDEF,
		TEXTDEF_FILE,
		TEXTWR,
		RANDOM
	}

	private static final long CONNECT_RETRY_IN_MS = 8000;

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
	private boolean pdClientConnected=false;

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
	 * 
	 */
	public void run() {
		log.log(Level.INFO,	"Ready receiving messages...");
		while (Thread.currentThread() == runner) {

			if (tcpServer!=null) {
				try {
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
				} catch (Exception e) {}
			}

			count++;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}

		}
	}

	/**
	 * 
	 * @param msg
	 */
	private void sendMsg(String[] msg) {
		ValidCommands response = MessageProcessor.processMsg(msg, true);
		if (response!=null && response == ValidCommands.STATUS) {
				sendStatusToGui();
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
		try {

			long l = System.currentTimeMillis()-lastConnectTimestamp;
			if (client==null &&  l > CONNECT_RETRY_IN_MS) {
				connectToClient();
			}

			if (client!=null && client.active()) {
				writeToClient(msg+";");	
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
		Socket socket = new Socket();
		lastConnectTimestamp = System.currentTimeMillis();
		try {
			socket.connect(new InetSocketAddress(sendHost, sendPort), 4000);
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
