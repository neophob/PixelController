package com.neophob.sematrix.core.rmi.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.properties.Command;
import com.neophob.sematrix.core.rmi.RmiApi;
import com.neophob.sematrix.core.rmi.compression.CompressApi;
import com.neophob.sematrix.core.rmi.compression.DecompressException;
import com.neophob.sematrix.core.rmi.compression.impl.CompressFactory;
import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.client.PixOscClient;
import com.neophob.sematrix.osc.client.impl.OscClientFactory;
import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.OscServerException;
import com.neophob.sematrix.osc.server.PixOscServer;
import com.neophob.sematrix.osc.server.impl.OscServerFactory;

public class RmiOscImpl implements RmiApi {

	private static final Logger LOG = Logger.getLogger(RmiOscImpl.class.getName());
	
	private PixOscServer oscServer;
	private PixOscClient oscClient;

	private CompressApi compressor; 
	private boolean useCompression;
	private int bufferSize;

	public RmiOscImpl(boolean useCompression, int bufferSize) {
		this.compressor = CompressFactory.getCompressApi();
		this.useCompression = useCompression;
		this.bufferSize = bufferSize;
		LOG.log(Level.INFO, "Start new OSC RMI Object, use compression: "+useCompression);
	}
	
	@Override
	public void startServer(Observer handler, int port, int bufferSize) throws OscServerException {
		this.oscServer = OscServerFactory.createServerTcp(handler, port, bufferSize);
		this.oscServer.startServer();
	}

	@Override
	public void startClient(String targetIp, int targetPort, int bufferSize) throws OscClientException {
		this.oscClient = OscClientFactory.createClientUdp(targetIp, targetPort, bufferSize);		
	}
	
	@Override
	public void shutdown() {
		if (oscServer!=null) {
			oscServer.stopServer();
		}
		if (oscClient!=null) {
			try {
				oscClient.disconnect();
			} catch (OscClientException e) {
				//ignored
			}
		}
	}
	
	private byte[] convertFromObject(Serializable s) {
		if (s==null) {
			return null;
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);   
			out.writeObject(s);
			if (!useCompression) {
				return bos.toByteArray();	
			}
			
			return compressor.compress(bos.toByteArray());
			
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Failed to serializable object", e);
			return new byte[0];
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
			try {
				bos.close();
			} catch (IOException ex) {
				// ignore close exception
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> T convertToObject(byte[] input, Class<T> type) throws IOException, ClassNotFoundException {

		ByteArrayInputStream bis;
		if (!useCompression) {
			bis = new ByteArrayInputStream(input);
		} else {
			try {
				bis = new ByteArrayInputStream(compressor.decompress(input, bufferSize));
			} catch (DecompressException e) {
				LOG.log(Level.INFO, "Failed to decompress data, disable compression");
				useCompression = false;
				bis = new ByteArrayInputStream(input);
			}
		}
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			return (T) in.readObject(); 
		} finally {
			try {
				bis.close();
			} catch (IOException ex) {
				// ignore close exception
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
		}		
	}

	@Override
	public void sendPayload(SocketAddress socket, Command cmd, Serializable data) throws OscClientException {
		OscMessage reply = new OscMessage(cmd.getValidCommand().toString(), cmd.getParameter(), convertFromObject(data));
		if (socket!=null) {
			this.verifyOscClient(socket);				
		}
		LOG.log(Level.INFO, cmd.getValidCommand()+" reply size: "+reply.getMessageSize());			
		this.oscClient.sendMessage(reply);
	}
	
	private synchronized void verifyOscClient(SocketAddress socket) throws OscClientException {
		InetSocketAddress remote = (InetSocketAddress)socket;
		boolean initNeeded = false;

		if (oscClient == null) {
			initNeeded = true;
		} else if (oscClient.getTargetIp() != remote.getAddress().getHostName()) {
			//TODO Verify port nr
			initNeeded = true;
		}

		if (initNeeded) {			
			//TODO make port configurable
			oscClient = OscClientFactory.createClientTcp(remote.getAddress().getHostName(), 
					9875, bufferSize);			
		}
	}


	@Override
	public <T> T reassembleObject(byte[] data, Class<T> type) {
		try {
			return convertToObject(data, type);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Failed to convert object", e);
		}
		return null;
	}


}
