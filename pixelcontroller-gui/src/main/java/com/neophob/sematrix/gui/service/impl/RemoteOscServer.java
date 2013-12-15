package com.neophob.sematrix.gui.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.neophob.sematrix.core.listener.MessageProcessor;
import com.neophob.sematrix.core.output.IOutput;
import com.neophob.sematrix.core.preset.PresetSettings;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.properties.ValidCommands;
import com.neophob.sematrix.core.resize.IResize;
import com.neophob.sematrix.core.resize.PixelResize;
import com.neophob.sematrix.core.sound.ISound;
import com.neophob.sematrix.core.sound.SoundDummy;
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
	private ApplicationConfigurationHelper config;
	private MatrixData matrix;
	private List<ColorSet> colorSets;
	private ISound sound;
	private List<OutputMapping> outputMapping;
	private IOutput output;
	
	public RemoteOscServer() throws OscServerException, OscClientException {
		LOG.log(Level.INFO,	"Start Frontend OSC Server at port {0}", new Object[] { LOCAL_OSC_SERVER_PORT });
		oscServer = OscServerFactory.createServer(this, LOCAL_OSC_SERVER_PORT, BUFFER_SIZE);
		oscClient = OscClientFactory.createClient(TARGET_HOST, REMOTE_OSC_SERVER_PORT, BUFFER_SIZE);
	}

	@Override
	public void start() {
		this.oscServer.startServer();
		this.sound = new SoundDummy();
		
		//request static values
		sendOscMessage(ValidCommands.GET_VERSION);
		sendOscMessage(ValidCommands.GET_CONFIGURATION);
		sendOscMessage(ValidCommands.GET_MATRIXDATA);
		sendOscMessage(ValidCommands.GET_COLORSETS);
		sendOscMessage(ValidCommands.GET_OUTPUTMAPPING);
		sendOscMessage(ValidCommands.GET_OUTPUTBUFFER);
	}

	@Override
	public String getVersion() {		
		return version;
	}

	@Override
	public ApplicationConfigurationHelper getConfig() {		
		return config;
	}

	@Override
	public List<ColorSet> getColorSets() {
		return colorSets;
	}

	@Override
	public boolean isInitialized() {
		return true;
	}

	@Override
	public int[] getOutputBuffer(int nr) {
		return output.getBufferForScreen(nr, true);
	}

	@Override
	public IOutput getOutput() {
		return output;
	}

	@Override
	public List<OutputMapping> getAllOutputMappings() {
		return outputMapping;
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
		return sound;
	}

	@Override
	public MatrixData getMatrixData() {
		return matrix;
	}

	@Override
	public int getNrOfVisuals() {
		return this.config.getNrOfScreens()+1+this.config.getNrOfAdditionalVisuals();
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
		sendOscMessage(msg);
	}

	@Override
	public void refreshGuiState() {
		//pixelController.refreshGuiState();
	}

	@Override
	public void registerObserver(Observer o) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public int[] getVisualBuffer(int nr) {
		//cannot use output buffer - one visual is missing
		return new int[matrix.getBufferXSize()*matrix.getBufferYSize()];
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

	private void sendOscMessage(String[] s) {
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
		
		try {
			switch (command) {
			case GET_VERSION:
				this.version = oscIn.getArgs()[0];
				System.out.println("version: "+this.version);
				break;

			case GET_CONFIGURATION:
				System.out.println("cfg: "+command+", size: "+oscIn.getBlob().length);
				config = convertToObject(oscIn.getBlob(), ApplicationConfigurationHelper.class);
				break;

			case GET_MATRIXDATA:
				matrix = convertToObject(oscIn.getBlob(), MatrixData.class);
				break;
				
			case GET_COLORSETS:
				colorSets = convertToObject(oscIn.getBlob(), ArrayList.class);
				break;
			
			case GET_OUTPUTMAPPING:
				outputMapping = convertToObject(oscIn.getBlob(), ArrayList.class);
				break;
				
			case GET_OUTPUTBUFFER:
				output = convertToObject(oscIn.getBlob(), IOutput.class);
				break;

			default:
				break;
			}			
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Failed to convert input data!", e);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T convertToObject(byte[] input, Class<T> type) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(input);
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
	
}
