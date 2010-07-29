package com.neophob.sematrix.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import processing.core.PApplet;
import processing.net.Client;
import processing.net.Server;

import com.neophob.sematrix.generator.Blinkenlights;
import com.neophob.sematrix.generator.Image;
import com.neophob.sematrix.generator.Generator.GeneratorName;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.OutputMapping;
import com.neophob.sematrix.glue.Visual;
 
public class TcpServer implements Runnable {
	
	public enum ValidCommands {
		STATUS,
		CHANGE_GENERATOR_A,
		CHANGE_GENERATOR_B,
		CHANGE_EFFECT_A,
		CHANGE_EFFECT_B,
		CHANGE_MIXER,
		CHANGE_OUTPUT,
		CHANGE_FADER,
		BLINKEN,
		IMAGE
	}

	private static Logger log = Logger.getLogger(TcpServer.class.getName());
	
	private Server tcpServer=null;
	private Client client;
	private String lastMsg="";
	
	private Thread runner;
	private PApplet app;
	private int sendPort;
	private String sendHost;
	
	public TcpServer(PApplet app, int port, String sendHost, int sendPort) {
		this.app = app;
		app.registerDispose(this);
		
		this.sendHost = sendHost;
		this.sendPort = sendPort;
		
		tcpServer = new Server(app, port);
		this.runner = new Thread(this);
		this.runner.start();
		
		log.log(Level.INFO,	"Server started at port {0}", new Object[] { port });
		
		connectToClient();
		if (client==null) {
			log.log(Level.INFO,	"Pure Data Client not available yet!");
		}
	}
	
	public void dispose() {
		runner = null;
	}
	
	public void run() {
		log.log(Level.INFO,	"Ready receiving messages...");
		while (Thread.currentThread() == runner) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}

			if (tcpServer!=null) {
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
						this.processMsg(StringUtils.split(msg, ' '));						
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
							this.processMsg(StringUtils.split(s, ' '));
						}
					}
				}							
			}
		}
	}
	
	private synchronized void processMsg(String[] msg) {
		if (msg==null || msg.length<1) {
			return;
		}
		
		try {
			ValidCommands cmd = ValidCommands.valueOf(msg[0]);
			
			switch (cmd) {
			case STATUS:
				log.log(Level.INFO,	"Send Status");				
				sendFudiMsg("hello from processing");
				
				String gen1="";
				String gen2="";
				String fx1="";
				String fx2="";
				String mix="";
				for (Visual v: Collector.getInstance().getAllVisuals()) {
					gen1+=v.getGenerator1Idx()+" ";
					gen2+=v.getGenerator2Idx()+" ";
					fx1+=v.getEffect1Idx()+" ";
					fx2+=v.getEffect2Idx()+" ";
					mix+=v.getMixerIdx()+" ";					
				}
				
				String fader="";
				String output="";
				for (OutputMapping o: Collector.getInstance().getAllOutputMappings()) {
					fader+=o.getFader().getId()+" ";
					output+=o.getVisualId()+" ";
				}

				sendFudiMsg("GENERATOR_A "+gen1);
				sendFudiMsg("GENERATOR_B "+gen2);
				sendFudiMsg("EFFECT_A "+fx1);
				sendFudiMsg("EFFECT_B "+fx2);
				sendFudiMsg("MIXER "+mix);
				sendFudiMsg("FADER "+fader);
				sendFudiMsg("OUTPUT "+output);

				break;

			case CHANGE_GENERATOR_A:
				try {
					int a = Integer.parseInt(msg[1]);
					int b = Integer.parseInt(msg[2]);
					int c = Integer.parseInt(msg[3]);
					int d = Integer.parseInt(msg[4]);
					int e = Integer.parseInt(msg[5]);
					Collector.getInstance().getVisual(0).setGenerator1(a);
					Collector.getInstance().getVisual(1).setGenerator1(b);
					Collector.getInstance().getVisual(2).setGenerator1(c);
					Collector.getInstance().getVisual(3).setGenerator1(d);
					Collector.getInstance().getVisual(4).setGenerator1(e);
				} catch (Exception e) {e.printStackTrace();}
				break;
			case CHANGE_GENERATOR_B:
				try {
					int a = Integer.parseInt(msg[1]);
					int b = Integer.parseInt(msg[2]);
					int c = Integer.parseInt(msg[3]);
					int d = Integer.parseInt(msg[4]);
					int e = Integer.parseInt(msg[5]);
					Collector.getInstance().getVisual(0).setGenerator2(a);
					Collector.getInstance().getVisual(1).setGenerator2(b);
					Collector.getInstance().getVisual(2).setGenerator2(c);
					Collector.getInstance().getVisual(3).setGenerator2(d);
					Collector.getInstance().getVisual(4).setGenerator2(e);
				} catch (Exception e) {e.printStackTrace();}
				break;
			
			case CHANGE_EFFECT_A:
				try {
					int a = Integer.parseInt(msg[1]);
					int b = Integer.parseInt(msg[2]);
					int c = Integer.parseInt(msg[3]);
					int d = Integer.parseInt(msg[4]);
					int e = Integer.parseInt(msg[5]);
					Collector.getInstance().getVisual(0).setEffect1(a);
					Collector.getInstance().getVisual(1).setEffect1(b);
					Collector.getInstance().getVisual(2).setEffect1(c);
					Collector.getInstance().getVisual(3).setEffect1(d);
					Collector.getInstance().getVisual(4).setEffect1(e);
				} catch (Exception e) {e.printStackTrace();}
				break;
				
			case CHANGE_EFFECT_B:
				try {					
					int a = Integer.parseInt(msg[1]);
					int b = Integer.parseInt(msg[2]);
					int c = Integer.parseInt(msg[3]);
					int d = Integer.parseInt(msg[4]);
					int e = Integer.parseInt(msg[5]);
					Collector.getInstance().getVisual(0).setEffect2(a);
					Collector.getInstance().getVisual(1).setEffect2(b);
					Collector.getInstance().getVisual(2).setEffect2(c);
					Collector.getInstance().getVisual(3).setEffect2(d);
					Collector.getInstance().getVisual(4).setEffect2(e);
				} catch (Exception e) {e.printStackTrace();}
				break;

			case CHANGE_MIXER:
				try {					
					int a = Integer.parseInt(msg[1]);
					int b = Integer.parseInt(msg[2]);
					int c = Integer.parseInt(msg[3]);
					int d = Integer.parseInt(msg[4]);
					int e = Integer.parseInt(msg[5]);
					Collector.getInstance().getVisual(0).setMixer(a);
					Collector.getInstance().getVisual(1).setMixer(b);
					Collector.getInstance().getVisual(2).setMixer(c);
					Collector.getInstance().getVisual(3).setMixer(d);
					Collector.getInstance().getVisual(4).setMixer(e);
				} catch (Exception e) {e.printStackTrace();}
				break;

			case CHANGE_OUTPUT:
				try {					
					int newFxA = Integer.parseInt(msg[1]);
					int newFxB = Integer.parseInt(msg[2]);
					int oldFxA = Collector.getInstance().getFxInputForScreen(0);
					int oldFxB = Collector.getInstance().getFxInputForScreen(1);
					if(oldFxA!=newFxA) {
						log.log(Level.INFO,	"Change Output 0, old fx: {0}, new fx {1}", new Object[] {oldFxA, newFxA});
						//Collector.getInstance().mapInputToScreen(0, newFxA);						
						Collector.getInstance().getAllOutputMappings().get(0).getFader().startFade(newFxA, 0);
					}
					if(oldFxB!=newFxB) {
						log.log(Level.INFO,	"Change Output 1, old fx: {0}, new fx {1}", new Object[] {oldFxB, newFxB});
						//Collector.getInstance().mapInputToScreen(1, newFxB);
						Collector.getInstance().getAllOutputMappings().get(1).getFader().startFade(newFxB, 1);
					}
				} catch (Exception e) {e.printStackTrace();}
				break;
	
			case CHANGE_FADER:
				try {					
					int a = Integer.parseInt(msg[1]);
					int b = Integer.parseInt(msg[2]);
					Collector.getInstance().getAllOutputMappings().get(0).setFader(Collector.getInstance().getFader(a));					
					Collector.getInstance().getAllOutputMappings().get(1).setFader(Collector.getInstance().getFader(b));					
				} catch (Exception e) {e.printStackTrace();}
				break;
			
			case BLINKEN:
				try {
					String fileToLoad = msg[1];
					Blinkenlights blink = (Blinkenlights)Collector.getInstance().getGenerator(GeneratorName.BLINKENLIGHTS);
					blink.loadFile(fileToLoad);
				} catch (Exception e) {e.printStackTrace();}

			case IMAGE:
				try {
					String fileToLoad = msg[1];
					Image img = (Image)Collector.getInstance().getGenerator(GeneratorName.IMAGE);
					img.loadFile(fileToLoad);
				} catch (Exception e) {e.printStackTrace();}
				
			default:
				System.out.println("valid: "+cmd);
				for (int i=1; i<msg.length; i++) System.out.println(msg[i]);
				break;
			}
		} catch (IllegalArgumentException e) {
			log.log(Level.INFO,	"Illegal argument: <{0}>", new Object[] { msg[0] });
			return;
		}		
	}
	
	private void sendFudiMsg(String msg) {
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
	private void connectToClient() {
		try {
			client = new Client(app, sendHost, sendPort);
			log.log(Level.INFO,	"Pure Data Client connected!");
		} catch (Exception e) {
			client = null;			
		}		
	}
}
