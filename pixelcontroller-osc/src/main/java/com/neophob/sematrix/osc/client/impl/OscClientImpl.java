package com.neophob.sematrix.osc.client.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.osc.client.IClient;
import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.model.OscMessage;

import de.sciss.net.OSCClient;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCPacket;
import de.sciss.net.OSCServer;

/**
 * 
 * @author michu
 *
 */
class OscClientImpl implements IClient {

	private static final Logger LOG = Logger.getLogger(OscClientImpl.class.getName());

	private final boolean useTcp;
	private OSCClient client;
	private String targetIp;
	private int targetPort;
	
	public OscClientImpl(boolean useTcp) throws OscClientException {
		try {
			if (useTcp) {
				client = OSCClient.newUsing(OSCServer.TCP);							
			} else {
				client = OSCClient.newUsing(OSCServer.UDP);							
			}
			this.useTcp = useTcp;
			this.targetPort = 0;
			this.targetIp = "";
			LOG.log(Level.INFO, "OSC Client Factory, initialized, buffersize: "+client.getBufferSize());
		} catch (IOException e) {
			throw new OscClientException("Failed to initialize OSC Client", e);
		}
	}
	
	/**
	 * 
	 * @param targetIp
	 * @param targetPort
	 * @return
	 */
	private boolean sendMessageToSameHost(String targetIp, int targetPort) {
		return this.targetPort == targetPort && this.targetIp.equals(targetIp);
	}

	/**
	 * 
	 * @param targetIp
	 * @param targetPort
	 * @return
	 */
	private boolean useUdpAndChangedTarget(String targetIp, int targetPort) {
		if (client.getProtocol().equals(OSCServer.TCP)) {
			return false;
		}
		
		return this.targetPort != targetPort || !this.targetIp.equals(targetIp);
	}
	
	@Override
	public void sendMessage(String targetIp, int targetPort, OscMessage msg) throws OscClientException {
		//check if the tcp client connection needs to disconnect
		if (client.isConnected() && !sendMessageToSameHost(targetIp, targetPort)) {
			this.disconnect();
		}

		//if the server using the udp protocol and the target changes - reinit
		boolean useUdpAndChangedTarget = useUdpAndChangedTarget(targetIp, targetPort);

		if (!client.isConnected() || useUdpAndChangedTarget) {			
			try {
				client.setTarget( new InetSocketAddress( targetIp, targetPort ));
				client.start();
				LOG.log(Level.INFO, "OSC Client start, target: {0}:{1}",
						new Object[]{targetIp, targetPort});

				this.targetPort = targetPort;
				this.targetIp = targetIp;
			} catch (IOException e) {
				throw new OscClientException("Failed to start OSC client", e);
			}
		}

		//TODO type check
		OSCPacket oscPacket = null;
		if (msg.getArgs()==null) {
			oscPacket = new OSCMessage(msg.getOscPattern());
		} else {
			oscPacket = new OSCMessage(msg.getOscPattern(), (Object[])msg.getArgs());	
		}
		 
		try {
			//LOG.log(Level.INFO, "Send OSC Package "+oscPacket+" to "+targetPort);
			client.send(oscPacket);
		} catch (IOException e) {
			throw new OscClientException("Failed to send OSC Message", e);
		}

	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isConnected() {
		if (client == null) {
			return false;
		}
		return client.isConnected();
	}
	

	@Override
	public void disconnect() throws OscClientException {
		if (!useTcp) {
			LOG.log(Level.INFO, "No need to disconnect, using a UDP Client");
			return;
		}
		
		if (client.isConnected()) {
			try {
				LOG.log(Level.INFO, "OSC Client, disconnect");
				client.stop();
				targetPort = 0;
				targetIp = "";
			} catch (Exception e) {
				throw new OscClientException("Failed to stop OSC client", e);
			}
		}
		
	}

}
