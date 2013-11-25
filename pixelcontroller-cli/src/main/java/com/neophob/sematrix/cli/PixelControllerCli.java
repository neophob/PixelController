package com.neophob.sematrix.cli;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.neophob.sematrix.core.glue.Collector;
import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.output.ArduinoOutput;
import com.neophob.sematrix.core.output.Output;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.setup.InitApplication;

/**
 * PixelController CLI Daemon
 * 
 * @author michu
 *
 */
public class PixelControllerCli {
	/** The log. */
	private static final Logger LOG = Logger.getLogger(PixelControllerCli.class.getName());

	/** The Constant FPS. */
	public static final int FPS = 25;

	private Collector collector;

	/** The output. */
	private Output output;

	private ApplicationConfigurationHelper applicationConfig;
	private FileUtils fileUtils;
	private Framerate framerate;

	/**
	 * 
	 */
	public PixelControllerCli() {
		LOG.log(Level.INFO, "\n\nPixelController "+getVersion()+" - http://www.pixelinvaders.ch\n\n");                
		fileUtils = new FileUtils();
		applicationConfig = InitApplication.loadConfiguration(fileUtils);

		LOG.log(Level.INFO, "Create Collector");
		this.collector = Collector.getInstance();

		LOG.log(Level.INFO, "Initialize System");
		this.collector.init(fileUtils, applicationConfig);     
		framerate = new Framerate(applicationConfig.parseFps());

		LOG.log(Level.INFO, "Initialize TCP/OSC Server");
		this.collector.initDaemons(applicationConfig);     

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

	}


	public void mainLoop() {
		LOG.info("enter main loop...");
		long cnt = 0;

		while (true) {
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

			framerate.waitForFps(cnt++); 
		}
	}


	/**
	 * 
	 * @return
	 */
	public String getVersion() {
		String version = this.getClass().getPackage().getImplementationVersion();
		if (StringUtils.isNotBlank(version)) {
			return "v"+version;
		}
		return "Developer Snapshot"; 
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String args[]) {
		new PixelControllerCli().mainLoop();                
	}

}
