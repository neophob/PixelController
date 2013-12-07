package com.neophob.sematrix.core.api.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.api.CallbackMessageInterface;
import com.neophob.sematrix.core.glue.Collector;
import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.output.ArduinoOutput;
import com.neophob.sematrix.core.output.Output;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.setup.InitApplication;

/**
 * 
 * @author michu
 *
 */
final class PixelControllerServerImpl extends PixelControllerServer implements Runnable {

	private static final Logger LOG = Logger.getLogger(PixelControllerServerImpl.class.getName());

	private Collector collector;

	/** The output. */
	private Output output;

	private ApplicationConfigurationHelper applicationConfig;
	private FileUtils fileUtils;
	private Framerate framerate;

	private Thread runner;

	private boolean initialized = false;
	
	/**
	 * 
	 * @param handler
	 */
	public PixelControllerServerImpl(CallbackMessageInterface<String> handler) {
		super(handler);
		
		this.runner = new Thread(this);
		this.runner.setName("PixelController Core");
		this.runner.setDaemon(true);
	}

	@Override
	public void start() {
		clientNotification("\n\nPixelController "+getVersion()+" - http://www.pixelinvaders.ch\n\n");

		this.runner.start();
	}

	@Override
	public void stop() {
		runner = null;
	}

	@Override
	public void run() {
		long cnt=0;
		
		clientNotification("Load Configuration");
		LOG.log(Level.INFO, "\n\nPixelController "+getVersion()+" - http://www.pixelinvaders.ch\n\n");                
		fileUtils = new FileUtils();
		applicationConfig = InitApplication.loadConfiguration(fileUtils);

		clientNotification("Create Collector");
		LOG.log(Level.INFO, "Create Collector");
		this.collector = Collector.getInstance();

		clientNotification("Initialize System");
		LOG.log(Level.INFO, "Initialize System");
		this.collector.init(fileUtils, applicationConfig);     
		framerate = new Framerate(applicationConfig.parseFps());

		clientNotification("Initialize OSC Server");
		LOG.log(Level.INFO, "Initialize OSC Server");
		this.collector.initDaemons(applicationConfig);     

		clientNotification("Initialize Output device");
		LOG.log(Level.INFO, "Initialize Output device");
		this.output = InitApplication.getOutputDevice(this.collector, applicationConfig);
		if (this.output==null) {
			throw new IllegalArgumentException("No output device found!");
		}
		this.collector.setOutput(output);

		InitApplication.setupInitialConfig(collector, applicationConfig);
		
		LOG.log(Level.INFO, "--- PixelController Setup END ---");
		LOG.log(Level.INFO, "---------------------------------");
		LOG.log(Level.INFO, "");		
		
		initialized = true;

		LOG.log(Level.INFO, "Enter main loop");
		while (Thread.currentThread() == runner) {
			if (Collector.getInstance().isInPauseMode()) {
				//no update here, we're in pause mode
				return;
			}

			if (this.output != null && this.output.getClass().isAssignableFrom(ArduinoOutput.class)) {
				this.output.logStatistics();
			}

			try {
				Collector.getInstance().updateSystem();			
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Collector.getInstance().updateSystem() failed!", e);
			}
			
			Collector.getInstance().getPixConStat().setCurrentFps(framerate.getFps());
			Collector.getInstance().getPixConStat().setFrameCount(cnt++);

			framerate.waitForFps(); 
		}
		LOG.log(Level.INFO, "Main loop finished...");
	}
	
	
	public String getVersion() {
		String version = this.getClass().getPackage().getImplementationVersion();
		if (version != null && !version.isEmpty()) {
			return "v"+version;
		}
		return "Developer Snapshot"; 
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public ApplicationConfigurationHelper getConfig() {
		return applicationConfig;
	}

	@Override
	public Output getOutput() {
		return output;
	}

	@Override
	public float getFps() {
		return framerate.getFps();
	}


}
