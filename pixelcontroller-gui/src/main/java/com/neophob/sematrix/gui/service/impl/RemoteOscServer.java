package com.neophob.sematrix.gui.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observer;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.neophob.sematrix.core.api.CallbackMessageInterface;
import com.neophob.sematrix.core.jmx.PixelControllerStatusMBean;
import com.neophob.sematrix.core.output.IOutput;
import com.neophob.sematrix.core.preset.PresetSettings;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.properties.ValidCommands;
import com.neophob.sematrix.core.sound.ISound;
import com.neophob.sematrix.core.sound.SoundDummy;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.OutputMapping;
import com.neophob.sematrix.core.visual.color.ColorSet;
import com.neophob.sematrix.gui.service.PixConServer;
import com.neophob.sematrix.mdns.client.MDnsClientException;
import com.neophob.sematrix.mdns.client.PixMDnsClient;
import com.neophob.sematrix.mdns.client.impl.MDnsClientFactory;
import com.neophob.sematrix.mdns.server.PixMDnsServer;
import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.client.PixOscClient;
import com.neophob.sematrix.osc.client.impl.OscClientFactory;
import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.OscMessageHandler;
import com.neophob.sematrix.osc.server.PixOscServer;
import com.neophob.sematrix.osc.server.impl.OscServerFactory;

/**
 * communicate service with remote pixelcontroller instance
 * send and recieve data via osc
 * 
 * @author michu
 *
 */
public class RemoteOscServer extends OscMessageHandler implements PixConServer, Runnable {

	private static final Logger LOG = Logger.getLogger(RemoteOscServer.class.getName());

	private static final String TARGET_HOST = "pixelcontroller.local";
	private static final int REMOTE_OSC_SERVER_PORT = 9876;
	private static final int LOCAL_OSC_SERVER_PORT = 9875;

	//size of recieving buffer, should fit a whole image buffer
	private static final int BUFFER_SIZE = 8192;
	
	private static final long GUISTATE_POLL_SLEEP = 400;

	private PixOscServer oscServer;
	private PixOscClient oscClient;
	
	private float steps;
		
	private Set<String> recievedMessages;
	private RemoteOscObservable remoteObserver;
	private CallbackMessageInterface<String> setupFeedback;
	private int serverPort;
	
	private boolean initialized;
	private String version;
	private ApplicationConfigurationHelper config;
	private MatrixData matrix;
	private List<ColorSet> colorSets;
	private ISound sound;
	private List<OutputMapping> outputMapping;
	private IOutput output;
	private List<String> guiState;
	private PresetSettings presetSettings;
	private PixelControllerStatusMBean jmxStatistics;
	
	public RemoteOscServer(CallbackMessageInterface<String> msgHandler) {
		setupFeedback = msgHandler;
	}

	@Override
	public void start() {
		LOG.log(Level.INFO,	"Start Frontend OSC Server at port {0}", new Object[] { LOCAL_OSC_SERVER_PORT });
		
		this.sound = new SoundDummy();
		this.steps = 1/12f;

		Thread startThread = new Thread(this);
		startThread.setName("GUI Poller");
		startThread.setDaemon(true);
		startThread.start();
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
		return initialized;
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
		return jmxStatistics.getCurrentFps();
	}

	@Override
	public long getFrameCount() {
		return jmxStatistics.getFrameCount();
	}

	@Override
	public long getServerStartTime() {
		return jmxStatistics.getStartTime();
	}

	@Override
	public long getRecievedOscPackets() {
		return jmxStatistics.getRecievedOscPakets();		
	}

	@Override
	public long getRecievedOscBytes() {
		return jmxStatistics.getRecievedOscBytes();
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
		//TODO
		return 0;
	}

	@Override
	public PresetSettings getCurrentPresetSettings() {		
		return presetSettings;
	}

	@Override
	public void updateNeededTimeForMatrixEmulator(long t) {
		//ignored, as not relevant
	}

	@Override
	public void updateNeededTimeForInternalWindow(long t) {
		//ignored, as not relevant
	}

	@Override
	public void sendMessage(String[] msg) {
		sendOscMessage(msg);
	}

	@Override
	public void refreshGuiState() {
		//ignored, the gui is refreshed async
	}

	@Override
	public void observeVisualState(Observer o) {
		remoteObserver.addObserver(o);
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
		
		recievedMessages.add(command.toString());
		
		try {
			switch (command) {
			case GET_VERSION:
				this.version = oscIn.getArgs()[0];
				setupFeedback.handleMessage("Found PixelController Version "+this.version);
				break;

			case GET_CONFIGURATION:
				config = convertToObject(oscIn.getBlob(), ApplicationConfigurationHelper.class);
				setupFeedback.handleMessage("Recieved Configuration");
				break;

			case GET_MATRIXDATA:
				matrix = convertToObject(oscIn.getBlob(), MatrixData.class);
				setupFeedback.handleMessage("Recieved Matrixdata");
				break;
				
			case GET_COLORSETS:
				colorSets = convertToObject(oscIn.getBlob(), ArrayList.class);
				setupFeedback.handleMessage("Recieved Colorsets");
				break;
			
			case GET_OUTPUTMAPPING:
				outputMapping = convertToObject(oscIn.getBlob(), ArrayList.class);
				setupFeedback.handleMessage("Recieved Output mapping");
				break;
				
			case GET_OUTPUTBUFFER:
				output = convertToObject(oscIn.getBlob(), IOutput.class);
				break;

			case GET_GUISTATE:
				guiState = convertToObject(oscIn.getBlob(), ArrayList.class);
				remoteObserver.notifyGuiUpdate(guiState);
				break;
				
			case GET_PRESETSETTINGS:
				presetSettings = convertToObject(oscIn.getBlob(), PresetSettings.class);				
				break;
				
			case GET_JMXSTATISTICS:
				jmxStatistics = convertToObject(oscIn.getBlob(), PixelControllerStatusMBean.class);
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

	@Override
	public void run() {
		try {
			setupFeedback.handleMessage("Detect PixelController OSC Port");
			try {
				PixMDnsClient client = MDnsClientFactory.queryService(PixMDnsServer.REMOTE_TYPE_UDP, 6000);
				client.start();
				if (client.mdnsServerFound()) {
					serverPort = client.getPort();
					setupFeedback.handleMessage("... found on port "+client.getPort()+", name: "+client.getServerName());
				} else {
					setupFeedback.handleMessage("... not found, use default port "+REMOTE_OSC_SERVER_PORT);
					serverPort = REMOTE_OSC_SERVER_PORT;
				}
			} catch (MDnsClientException e) {
				LOG.log(Level.WARNING, "Service discover failed.", e);
				serverPort = REMOTE_OSC_SERVER_PORT;
				setupFeedback.handleMessage("... not found, use default port "+REMOTE_OSC_SERVER_PORT);
			}
			
			setupFeedback.handleMessage("Start OSC Server");
			this.oscServer = OscServerFactory.createServerTcp(this, LOCAL_OSC_SERVER_PORT, BUFFER_SIZE);
			this.oscServer.startServer();
			setupFeedback.handleMessage(" ... started");
			setupFeedback.handleMessage("Connect to PixelController OSC Server");
			this.oscClient = OscClientFactory.createClientUdp(TARGET_HOST, serverPort, BUFFER_SIZE);
			setupFeedback.handleMessage(" ... done");			
			this.remoteObserver = new RemoteOscObservable(); 
			this.initialized = false;
			this.recievedMessages = new HashSet<String>();			
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to start Remote OSC Server!", e);
			return;
		}
		
		//first step, get static values
		Set<String> initCommands = new HashSet<String>();
		initCommands.add(ValidCommands.GET_VERSION.toString());
		initCommands.add(ValidCommands.GET_CONFIGURATION.toString());
		initCommands.add(ValidCommands.GET_MATRIXDATA.toString());
		initCommands.add(ValidCommands.GET_COLORSETS.toString());
		initCommands.add(ValidCommands.GET_OUTPUTBUFFER.toString());
		initCommands.add(ValidCommands.GET_GUISTATE.toString());			
		initCommands.add(ValidCommands.GET_OUTPUTMAPPING.toString());
		initCommands.add(ValidCommands.GET_PRESETSETTINGS.toString());
		initCommands.add(ValidCommands.GET_JMXSTATISTICS.toString());				


		while(!recievedMessages.containsAll(initCommands)) {			
			for (String s: initCommands) {
				if (!recievedMessages.contains(s)) {
					LOG.log(Level.INFO, "Request "+s+" from OSC Server");
					sendOscMessage(s);					
				}
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				//ignored
			}
		}
		initialized = true;
				
		long l = 0;
		while (true) {
			sendOscMessage(ValidCommands.GET_OUTPUTBUFFER);
		
			if (l%5==0) {
				sendOscMessage(ValidCommands.GET_GUISTATE);			
				sendOscMessage(ValidCommands.GET_OUTPUTMAPPING);
				sendOscMessage(ValidCommands.GET_PRESETSETTINGS);
				sendOscMessage(ValidCommands.GET_JMXSTATISTICS);				
			}			
			
			try {
				Thread.sleep(GUISTATE_POLL_SLEEP);
			} catch (InterruptedException e) {
				//ignore
			}
			
			l++;
		}
	}

	@Override
	public float getSetupSteps() {
		return steps;
	}
	
}
