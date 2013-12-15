package com.neophob.sematrix.gui.service.impl;

import java.util.List;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.neophob.sematrix.core.output.IOutput;
import com.neophob.sematrix.core.preset.PresetSettings;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.properties.ValidCommands;
import com.neophob.sematrix.core.sound.ISound;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.OutputMapping;
import com.neophob.sematrix.core.visual.color.ColorSet;
import com.neophob.sematrix.gui.service.PixConServer;
import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.client.PixOscClient;
import com.neophob.sematrix.osc.client.impl.OscClientFactory;
import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.OscMessageHandler;
import com.neophob.sematrix.osc.server.OscServerException;
import com.neophob.sematrix.osc.server.PixOscServer;
import com.neophob.sematrix.osc.server.impl.OscServerFactory;

public class RemoteOscServer extends OscMessageHandler implements PixConServer {

	private static final Logger LOG = Logger.getLogger(RemoteOscServer.class.getName());

	private static final String TARGET_HOST = "pixelcontroller.local";
	private static final int REMOTE_OSC_SERVER_PORT = 9876;
	private static final int LOCAL_OSC_SERVER_PORT = 9875;

	//size of recieving buffer, should fit a whole image buffer
	private static final int BUFFER_SIZE = 50000;

	private PixOscServer oscServer;
	private PixOscClient oscClient;
	private String version;
	
	public RemoteOscServer() throws OscServerException, OscClientException {
		LOG.log(Level.INFO,	"Start Frontend OSC Server at port {0}", new Object[] { LOCAL_OSC_SERVER_PORT });
		oscServer = OscServerFactory.createServer(this, LOCAL_OSC_SERVER_PORT, BUFFER_SIZE);
		oscClient = OscClientFactory.createClient(TARGET_HOST, REMOTE_OSC_SERVER_PORT, 50000);
	}

	@Override
	public void start() {
		oscServer.startServer();
		
		//request static valies
		sendOscMessage(ValidCommands.GET_VERSION);
		sendOscMessage(ValidCommands.GET_CONFIGURATION);
	}

	@Override
	public String getVersion() {		
		return version;
	}

	@Override
	public ApplicationConfigurationHelper getConfig() {
		sendOscMessage(ValidCommands.GET_CONFIGURATION);
		return null;
	}

	@Override
	public List<ColorSet> getColorSets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInitialized() {
		return true;
	}

	@Override
	public int[] getOutputBuffer(int nr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IOutput getOutput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OutputMapping> getAllOutputMappings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getCurrentFps() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getFrameCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getServerStartTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getRecievedOscPackets() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getRecievedOscBytes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ISound getSoundImplementation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MatrixData getMatrixData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNrOfVisuals() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCurrentPreset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PresetSettings getCurrentPresetSettings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateNeededTimeForMatrixEmulator(long t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNeededTimeForInternalWindow(long t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMessage(String[] msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void refreshGuiState() {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerObserver(Observer o) {
		// TODO Auto-generated method stub

	}

	private void sendOscMessage(ValidCommands cmd) {
		sendOscMessage(cmd.toString());
	}
	
	private void sendOscMessage(String s) {
		OscMessage msg = new OscMessage(s);
		try {
			oscClient.sendMessage(msg);
		} catch (OscClientException e) {
			LOG.log(Level.SEVERE, "failed to send osc message!", e);
		}	
	}

	@Override
	public void handleOscMessage(OscMessage oscIn) {
		LOG.log(Level.INFO, "got message: "+oscIn);
		
		if (StringUtils.isBlank(oscIn.getPattern())) {
			LOG.log(Level.INFO,	"Ignore empty OSC message...");
			return;
		}

		String pattern = oscIn.getPattern();

		ValidCommands command;		
		try {
			command = ValidCommands.valueOf(pattern);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Unknown message: "+pattern, e);
			return;			
		}
		
		switch (command) {
		case GET_VERSION:
			System.out.println("version: "+command);
			break;

		case GET_CONFIGURATION:
			System.out.println("cfg: "+command);
			break;

		default:
			break;
		}
	}

	@Override
	public int[] getVisualBuffer(int nr) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
