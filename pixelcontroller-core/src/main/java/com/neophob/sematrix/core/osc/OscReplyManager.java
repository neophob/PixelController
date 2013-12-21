package com.neophob.sematrix.core.osc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;

import com.neophob.sematrix.core.api.CallbackMessage;
import com.neophob.sematrix.core.api.PixelController;
import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.glue.impl.FileUtilsRemoteImpl;
import com.neophob.sematrix.core.osc.remotemodel.ImageBuffer;
import com.neophob.sematrix.core.properties.ValidCommands;
import com.neophob.sematrix.core.visual.OutputMapping;
import com.neophob.sematrix.core.visual.Visual;
import com.neophob.sematrix.core.visual.color.ColorSet;
import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.client.PixOscClient;
import com.neophob.sematrix.osc.client.impl.OscClientFactory;
import com.neophob.sematrix.osc.model.OscMessage;

/**
 * the osc reply manager sends back gui information, encoded
 * as osc package.
 * 
 * @author michu
 *
 */
public class OscReplyManager extends CallbackMessage<ArrayList> implements Runnable {

	private static final Logger LOG = Logger.getLogger(OscReplyManager.class.getName());
	private static final int SEND_ERROR_THRESHOLD = 4;
	
	private PixelController pixelController;

	private PixOscClient oscClient;
	private FileUtils fileUtilRemote;
	
	private int sendError;
	private boolean useCompression;
	private LZ4Compressor compressor; 
	
	private Thread oscSendThread;
	private boolean startSendImageThread = false;

	
	public OscReplyManager(PixelController pixelController) {
		this.pixelController = pixelController;
		if (pixelController.getConfig().parseRemoteConnectionUseCompression()) {
			this.compressor = LZ4Factory.fastestJavaInstance().fastCompressor();
			this.useCompression = true;
		} else {
			this.useCompression = false;
		}
		
		oscSendThread = new Thread(this);
		oscSendThread.setName("OSC Send Image Worker");
		oscSendThread.setDaemon(true);		
	}

	public void handleClientResponse(OscMessage oscIn, String[] msg) throws OscClientException {

		ValidCommands cmd = ValidCommands.valueOf(msg[0]);
		OscMessage reply = null;

		switch (cmd) {
		case GET_CONFIGURATION:				
			reply = new OscMessage(cmd.toString(), convertFromObject(pixelController.getConfig()));						
			break;

		case GET_MATRIXDATA:				
			reply = new OscMessage(cmd.toString(), convertFromObject(pixelController.getMatrix()));			
			break;

		case GET_VERSION:
			reply = new OscMessage(cmd.toString(), pixelController.getVersion());
			break;

		case GET_COLORSETS:
			reply = new OscMessage(cmd.toString(), convertFromObject((ArrayList<ColorSet>)pixelController.getColorSets()));
			break;

		case GET_OUTPUTMAPPING:
			reply = new OscMessage(cmd.toString(), convertFromObject((CopyOnWriteArrayList<OutputMapping>)pixelController.getAllOutputMappings()));
			break;

		case GET_OUTPUT:
			reply = new OscMessage(cmd.toString(), convertFromObject(pixelController.getOutput()));
			break;

		case GET_GUISTATE:
			reply = new OscMessage(cmd.toString(), convertFromObject((ArrayList<String>)pixelController.getGuiState()));
			break;

		case GET_PRESETSETTINGS:
			reply = new OscMessage(cmd.toString(), convertFromObject(pixelController.getPresetService().getSelectedPresetSettings()));
			break;

		case GET_JMXSTATISTICS:
			reply = new OscMessage(cmd.toString(), convertFromObject(pixelController.getPixConStat()));
			break;
			
		case GET_FILELOCATION:
			reply = new OscMessage(cmd.toString(), convertFromObject(getLazyfileUtilsRemote()));
			break;
			
		case GET_IMAGEBUFFER:
			reply = new OscMessage(cmd.toString(), convertFromObject(getVisualBuffer()));			
			break;

		case REGISTER_VISUALOBSERVER:
			pixelController.observeVisualState(this);
			//send back ack
			reply = new OscMessage(cmd.toString(), new byte[0]);
			sendError = 0;
			startSendImageThread = true;
			oscSendThread.start();
			break;

		case UNREGISTER_VISUALOBSERVER:
			pixelController.stopObserveVisualState(this);
			reply = new OscMessage(cmd.toString(), new byte[0]);
			startSendImageThread = false;
			break;

		default:
			LOG.log(Level.INFO, cmd.toString()+" unknown command ignored");
			break;
		}

		if (reply!=null) {
			this.verifyOscClient(oscIn.getSocketAddress());
			LOG.log(Level.INFO, cmd.toString()+" reply size: "+reply.getMessageSize());			
			this.oscClient.sendMessage(reply);
		}
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
					9875, PixelControllerOscServer.REPLY_PACKET_BUFFERSIZE);			
		}
	}

	/**
	 * message from visual state, something changed. if a remote client is registered
	 * we send the update to the remote client.
	 * 
	 * @param guiState
	 */
	@Override
	public void handleMessage(ArrayList guiState) {
		if (oscClient!=null) {
			
			OscMessage reply = new OscMessage(ValidCommands.GET_GUISTATE.toString(),
					convertFromObject((ArrayList<String>)pixelController.getGuiState()));			
			try {
				LOG.log(Level.INFO, reply.getPattern()+" reply size: "+reply.getMessageSize());				
				this.oscClient.sendMessage(reply);
				sendError = 0;
			} catch (OscClientException e) {
				LOG.log(Level.SEVERE, "Failed to send observer message, error no: "+sendError, e);
				if (sendError++ > SEND_ERROR_THRESHOLD) {
					//disable remote observer after some errors
					pixelController.stopObserveVisualState(this);
					LOG.log(Level.SEVERE, "Disable remote observer");
				}
			}			
		}
	}
	
	private synchronized FileUtils getLazyfileUtilsRemote() {
		if (this.fileUtilRemote == null) {			
			this.fileUtilRemote = new FileUtilsRemoteImpl(
						this.pixelController.getFileUtils().findBlinkenFiles(),
						this.pixelController.getFileUtils().findImagesFiles()
					);
		}
		return this.fileUtilRemote;
	}

	private ImageBuffer getVisualBuffer() {
		try {
			int nrOfOutputs = pixelController.getConfig().getNrOfScreens();		
			int[][] outputBuffer = new int[nrOfOutputs][];
			for (int i=0; i<nrOfOutputs; i++) {
				outputBuffer[i] = pixelController.getOutput().getBufferForScreen(i, true);
			}
					
			List<Visual> allVisuals = pixelController.getVisualState().getAllVisuals();
			int[][] visualBuffer = new int[allVisuals.size()][];
			for (int i=0; i<allVisuals.size(); i++) {
				visualBuffer[i] = allVisuals.get(i).getBuffer();
			}
			
			return new ImageBuffer(outputBuffer, visualBuffer);			
		} catch (Exception e) {
			return new ImageBuffer(new int[0][0], new int[0][0]);
		}
	}

	private byte[] convertFromObject(Serializable s) {
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

	@Override
	public void run() {
		long sleepTime = (long)(1000f/pixelController.getConfig().parseRemoteFps());
		LOG.log(Level.INFO, "OSC Sender thread started, sleeptime: "+sleepTime+", use compression: "+this.useCompression);
		
		try {
			while (startSendImageThread) {
				OscMessage imgData = new OscMessage(ValidCommands.GET_IMAGEBUFFER.toString(), convertFromObject(getVisualBuffer()));
				LOG.log(Level.INFO, ValidCommands.GET_IMAGEBUFFER.toString()+" reply size: "+imgData.getMessageSize());			
				this.oscClient.sendMessage(imgData);	
				
				Thread.sleep(sleepTime);
			}			
		} catch (Exception e) {
			LOG.log(Level.WARNING, "OSC Sender thread failed", e);
		}
		
		LOG.log(Level.INFO, "OSC Sender thread ended");
		
	}	
}
