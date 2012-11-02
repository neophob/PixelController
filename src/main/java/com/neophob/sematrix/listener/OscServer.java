package com.neophob.sematrix.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import oscP5.OscEventListener;
import oscP5.OscMessage;
import oscP5.OscP5;
import oscP5.OscStatus;
import processing.core.PApplet;

public class OscServer implements OscEventListener {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(OscServer.class.getName());

	private int listeningPort;

	private OscP5 oscP5;

	/**
	 * 
	 * @param listeningPort
	 */
	public OscServer(PApplet papplet, int listeningPort) {
		this.listeningPort = listeningPort;
		LOG.log(Level.INFO,	"Start OSC Server at port {0}", new Object[] { listeningPort });
		this.oscP5 = new OscP5(papplet, this.listeningPort);
		this.oscP5.addListener(this);
	}

	/**
	 * 
	 * @param theOscMessage
	 */
	public void oscEvent(OscMessage theOscMessage) {
		LOG.log(Level.INFO,	"Received an osc message. with address pattern {0} typetag {1}.", 
				new Object[] { theOscMessage.addrPattern(), theOscMessage.typetag() });		
	}

	@Override
	public void oscStatus(OscStatus arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}
