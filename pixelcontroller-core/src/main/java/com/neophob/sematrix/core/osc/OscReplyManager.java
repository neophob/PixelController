package com.neophob.sematrix.core.osc;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.api.CallbackMessage;
import com.neophob.sematrix.core.api.PixelController;
import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.glue.impl.FileUtilsRemoteImpl;
import com.neophob.sematrix.core.osc.remotemodel.ImageBuffer;
import com.neophob.sematrix.core.properties.Command;
import com.neophob.sematrix.core.properties.ValidCommands;
import com.neophob.sematrix.core.rmi.RmiApi;
import com.neophob.sematrix.core.rmi.impl.RmiOscImpl;
import com.neophob.sematrix.core.visual.OutputMapping;
import com.neophob.sematrix.core.visual.Visual;
import com.neophob.sematrix.core.visual.color.ColorSet;
import com.neophob.sematrix.osc.client.OscClientException;
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
	private static final int SEND_STATISTICS_TO_REMOTEOBSERVER = 5000;

	private PixelController pixelController;

	private FileUtils fileUtilRemote;

	private int sendError;

	private Thread oscSendThread;
	private boolean startSendImageThread = false;

	private RmiApi remoteServer;


	public OscReplyManager(PixelController pixelController) {
		this.pixelController = pixelController;
		boolean useCompression = pixelController.getConfig().parseRemoteConnectionUseCompression();
		this.remoteServer = new RmiOscImpl(useCompression, PixelControllerOscServer.REPLY_PACKET_BUFFERSIZE);
		LOG.log(Level.INFO, "OscReplyManager started, use compression: "+useCompression);
	}

	public void handleClientResponse(OscMessage oscIn, String[] msg) throws OscClientException {

		ValidCommands cmd = ValidCommands.valueOf(msg[0]);
		SocketAddress target = null;
		if (oscIn!=null) {
			target = oscIn.getSocketAddress();			
		}
		Command command = new Command(cmd);

		switch (cmd) {
		case GET_CONFIGURATION:	
			remoteServer.sendPayload(target, command, pixelController.getConfig());
			break;

		case GET_MATRIXDATA:				
			remoteServer.sendPayload(target, command, pixelController.getMatrix());
			break;

		case GET_VERSION:
			remoteServer.sendPayload(target, command, pixelController.getVersion());
			break;

		case GET_COLORSETS:
			remoteServer.sendPayload(target, command, (ArrayList<ColorSet>)pixelController.getColorSets());
			break;

		case GET_OUTPUTMAPPING:
			remoteServer.sendPayload(target, command, (CopyOnWriteArrayList<OutputMapping>)pixelController.getAllOutputMappings());
			break;

		case GET_OUTPUT:
			remoteServer.sendPayload(target, command, pixelController.getOutput());
			break;

		case GET_GUISTATE:
			remoteServer.sendPayload(target, command, (ArrayList<String>)pixelController.getGuiState());
			break;

		case GET_PRESETSETTINGS:
			remoteServer.sendPayload(target, command, pixelController.getPresetService().getSelectedPresetSettings());
			break;

		case GET_JMXSTATISTICS:
			remoteServer.sendPayload(target, command, pixelController.getPixConStat());			
			break;

		case GET_FILELOCATION:
			remoteServer.sendPayload(target, command, getLazyfileUtilsRemote());
			break;

		case GET_IMAGEBUFFER:
			remoteServer.sendPayload(target, command, getVisualBuffer());
			break;

		case REGISTER_VISUALOBSERVER:
			pixelController.observeVisualState(this);
			//send back ack
			remoteServer.sendPayload(target, command, new byte[0]);
			sendError = 0;
			startSendImageThread = true;
			oscSendThread = new Thread(this);
			oscSendThread.setName("OSC Send Image Worker");
			oscSendThread.setDaemon(true);					
			oscSendThread.start();			
			break;

		case UNREGISTER_VISUALOBSERVER:
			pixelController.stopObserveVisualState(this);
			remoteServer.sendPayload(target, command, new byte[0]);
			startSendImageThread = false;
			break;

		default:
			LOG.log(Level.INFO, cmd.toString()+" unknown command ignored");
			break;
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
		try {
			sendOscMessage(ValidCommands.GET_GUISTATE);
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

	private void sendOscMessage(ValidCommands cmd) throws OscClientException {
		String[] msg = new String[] {cmd.toString()};
		handleClientResponse(null, msg);
	}

	@Override
	public void run() {
		long sleepTime = (long)(1000f/pixelController.getConfig().parseRemoteFps());
		LOG.log(Level.INFO, "OSC Sender thread started, sleeptime: "+sleepTime);

		long waitTime = 0;
		try {
			while (startSendImageThread) {
				sendOscMessage(ValidCommands.GET_IMAGEBUFFER);
				Thread.sleep(sleepTime);
				waitTime += sleepTime;

				if (waitTime > SEND_STATISTICS_TO_REMOTEOBSERVER) {
					waitTime = 0;
					sendOscMessage(ValidCommands.GET_OUTPUTMAPPING);
					sendOscMessage(ValidCommands.GET_PRESETSETTINGS);
					sendOscMessage(ValidCommands.GET_JMXSTATISTICS);									
				}
			}			
		} catch (Exception e) {
			LOG.log(Level.WARNING, "OSC Sender thread failed", e);
		}

		LOG.log(Level.INFO, "OSC Sender thread ended");
	}	
}
