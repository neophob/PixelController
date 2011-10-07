/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
 *
 * This file is part of PixelController.
 *
 * PixelController is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PixelController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PixelController.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.neophob.sematrix.glue;

import java.net.BindException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import processing.core.PApplet;

import com.neophob.sematrix.effect.PixelControllerEffect;
import com.neophob.sematrix.fader.Fader;
import com.neophob.sematrix.generator.PixelControllerGenerator;
import com.neophob.sematrix.input.Sound;
import com.neophob.sematrix.input.SoundDummy;
import com.neophob.sematrix.input.SoundMinim;
import com.neophob.sematrix.jmx.PixelControllerStatus;
import com.neophob.sematrix.listener.MessageProcessor;
import com.neophob.sematrix.listener.TcpServer;
import com.neophob.sematrix.mixer.PixelControllerMixer;
import com.neophob.sematrix.output.PixelControllerOutput;
import com.neophob.sematrix.properties.ConfigConstant;
import com.neophob.sematrix.properties.PropertiesHelper;
import com.neophob.sematrix.properties.ValidCommands;
import com.neophob.sematrix.resize.PixelControllerResize;

/**
 * The Class Collector.
 */
public final class Collector {

	private static final Logger LOG = Logger.getLogger(Collector.class.getName());
	
	/** The Constant EMPTY_CHAR. */
	private static final String EMPTY_CHAR = " ";
	
	/** The Constant NR_OF_PRESENT_SLOTS. */
	public static final int NR_OF_PRESENT_SLOTS = 128;
	
	/** The instance. */
	private static Collector instance = new Collector();

	/** The random mode. */
	private boolean randomMode = false;

	/** The initialized. */
	private boolean initialized;
	
	/** The papplet. */
	private PApplet papplet;
	
	/** The matrix. */
	private MatrixData matrix;

	/** all input elements. */	
	private List<Visual> allVisuals;

	/** fx to screen mapping. */
	private List<OutputMapping> ioMapping;

	/** The nr of screens. */
	private int nrOfScreens;
	
	/** The fps. */
	private int fps;
	
	/** The frames. */
	private int frames;
	private int framesEffective;
	
	/** The current visual. */
	private int currentVisual;

	/** The current output. */
	private int currentOutput;

	/** present settings. */
	private int selectedPresent;
	
	/** The present. */
	private List<PresentSettings> present;
	
	/** The pixel controller generator. */
	private PixelControllerGenerator pixelControllerGenerator;
	
	/** The pixel controller mixer. */
	private PixelControllerMixer pixelControllerMixer;
	
	/** The pixel controller effect. */
	private PixelControllerEffect pixelControllerEffect;
	
	/** The pixel controller resize. */
	private PixelControllerResize pixelControllerResize;
	
	/** The pixel controller output. */
	private PixelControllerOutput pixelControllerOutput;
	
	/** The pixel controller shuffler select. */
	private PixelControllerShufflerSelect pixelControllerShufflerSelect;
	
	/** The pd srv. */
	@SuppressWarnings("unused")
	private TcpServer pdSrv;
	
	private PropertiesHelper ph;
	
	/** The is loading present. */
	private boolean isLoadingPresent=false;
	
	private boolean soundAware=false;
	
	private PixelControllerStatus pixConStat;

	
	/**
	 * Instantiates a new collector.
	 */
	private Collector() {	
		allVisuals = new CopyOnWriteArrayList<Visual>();

		this.nrOfScreens = 0;
		ioMapping = new CopyOnWriteArrayList<OutputMapping>();
		initialized=false;

		selectedPresent=0;
		present = new CopyOnWriteArrayList<PresentSettings>();
		for (int n=0; n<NR_OF_PRESENT_SLOTS; n++) {
			present.add(new PresentSettings());
		}

		pixelControllerShufflerSelect = new PixelControllerShufflerSelect();
		pixelControllerShufflerSelect.initAll();		 
	}

	/**
	 * initialize the collector.
	 *
	 * @param papplet the papplet
	 * @param fps the fps
	 */
	public synchronized void init(PApplet papplet, int fps, PropertiesHelper ph) {
		if (initialized) {
			return;
		}
		
		this.papplet = papplet;
		this.nrOfScreens = ph.getNrOfScreens();
		this.fps = fps;
		this.ph = ph;
		
		//choose sound implementation
		try {
			Sound.getInstance().setImplementation(new SoundMinim());			
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "FAILED TO INITIALIZE SOUND INSTANCE, Exception: {0}.", e);
			Sound.getInstance().setImplementation(new SoundDummy());
		}
		
		new MatrixData(ph.getDeviceXResolution(), ph.getDeviceYResolution());

		pixelControllerResize = new PixelControllerResize();
		pixelControllerResize.initAll();

		//create generators
		pixelControllerGenerator = new PixelControllerGenerator(ph);
		pixelControllerGenerator.initAll();
		
		pixelControllerEffect = new PixelControllerEffect();
		pixelControllerEffect.initAll();

		pixelControllerMixer = new PixelControllerMixer();
		pixelControllerMixer.initAll();
		
		//create visuals
		int additionalVisuals = 1+ph.getNrOfAdditionalVisuals();
		if (additionalVisuals>32) {
			//just make sure we don't kill the cpu...
			additionalVisuals = 32;
		}
		Visual.initializeVisuals(nrOfScreens+additionalVisuals);
				
		pixelControllerOutput = new PixelControllerOutput();
		pixelControllerOutput.initAll();
		
		ph.loadPresents();
		soundAware = ph.isAudioAware();
		
		//create an empty mapping
		ioMapping.clear();
		for (int n=0; n<nrOfScreens; n++) {
			ioMapping.add(new OutputMapping(n));			
		}

		//Start tcp server
		int listeningPort = Integer.parseInt(ph.getProperty(ConfigConstant.NET_LISTENING_PORT, "3448") );
		int sendPort = Integer.parseInt(ph.getProperty(ConfigConstant.NET_SEND_PORT, "3449") );
		String listeningIp = ph.getProperty(ConfigConstant.NET_LISTENING_ADDR, "127.0.0.1");
		
		try {		    
			pdSrv = new TcpServer(papplet, listeningPort, listeningIp, sendPort);
		} catch (BindException e) {
		    LOG.log(Level.SEVERE, "failed to start TCP Server", e);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "failed to start TCP Server", e);
        }

		pixConStat = new PixelControllerStatus(fps);
		
		initialized=true;
	}

	/**
	 * update the whole system:
	 * -generators
	 * -effects
	 * -outputs
	 * 
	 * update the generators, if the sound is
	 * louder, update faster.
	 */
	public void updateSystem() {
		//do not update system if presents are loading
		if (isLoadingPresent()) {
			return;
		}
		
		int u = 1;
		
		if (soundAware) {
			//get sound volume
			float f = Sound.getInstance().getVolumeNormalized();
			u = (int)(0.5f+f*1.5f);
			//check for silence - in this case update slowly
			if (u<1) {
				if (frames%3==1) {
					u=1;
				}
			}
			if (Sound.getInstance().isKick()) {
				u+=3;
			}
			if (Sound.getInstance().isHat()) {
				u+=1;
			}			
		}
		
		//update the current value of frames per second
		pixConStat.setCurrentFps(papplet.frameRate);
		pixConStat.setFrameCount(papplet.frameCount);

		framesEffective+=u;
		long l = System.currentTimeMillis();
		//update generator depending on the input sound
		for (int i=0; i<u; i++) {
			pixelControllerGenerator.update();			
		}
		pixConStat.addGeneratorUpdateTime(System.currentTimeMillis()-l);
		
		l = System.currentTimeMillis();
		pixelControllerEffect.update();
		pixConStat.addEffectUpdateTime(System.currentTimeMillis()-l);
		
		l = System.currentTimeMillis();
		pixelControllerOutput.update();
		pixConStat.addOutputUpdateTime(System.currentTimeMillis()-l);
				
		//cleanup faders
		l = System.currentTimeMillis();
		for (OutputMapping om: ioMapping) {
			Fader fader = om.getFader();
			if (fader.isDone()) {
				//fading is finished
				fader.cleanUp();
			}
		}
		pixConStat.addFaderUpdateTime(System.currentTimeMillis()-l);
		
		if (randomMode) {
			Shuffler.shuffleStuff();
		}
		
		frames++;
	}

	/**
	 * Gets the single instance of Collector.
	 *
	 * @return single instance of Collector
	 */
	public static Collector getInstance() {
		return instance;
	}


	/**
	 * Gets the nr of screens.
	 *
	 * @return the nr of screens
	 */
	public int getNrOfScreens() {
		return nrOfScreens;
	}


	/**
	 * which fx for screenOutput?.
	 *
	 * @param screenOutput the screen output
	 * @return fx nr.
	 */
	public int getFxInputForScreen(int screenOutput) {
		return ioMapping.get(screenOutput).getVisualId();
	}

	/**
	 * define which fx is shown on which screen, without fading.
	 *
	 * @param screenOutput which screen nr
	 * @param visualInput which visual
	 */
	public void mapInputToScreen(int screenOutput, int visualInput) {
		OutputMapping o = ioMapping.get(screenOutput);
		o.setVisualId(visualInput);
		ioMapping.set(screenOutput, o);
	}

	/**
	 * get all screens with a specific visual
	 * used for crossfading.
	 *
	 * @param oldVisual the old visual
	 * @return the all screens with visual
	 */
	public List<Integer> getAllScreensWithVisual(int oldVisual) {
		List<Integer> ret = new ArrayList<Integer>();
		int ofs=0;
		for (OutputMapping o: ioMapping) {
			if (o.getVisualId()==oldVisual) {
				ret.add(ofs);
			}
			ofs++;
		}
		return ret;
	}

	/**
	 * Gets the papplet.
	 *
	 * @return the papplet
	 */
	public PApplet getPapplet() {
		return papplet;
	}

	/**
	 * Gets the fps.
	 *
	 * @return the fps
	 */
	public int getFps() {
		return fps;
	}

	/**
	 * Checks if is random mode.
	 *
	 * @return true, if is random mode
	 */
	public boolean isRandomMode() {
		return randomMode;
	}

	/**
	 * Sets the random mode.
	 *
	 * @param randomMode the new random mode
	 */
	public void setRandomMode(boolean randomMode) {
		this.randomMode = randomMode;
	}

	/**
	 * load a saved preset.
	 *
	 * @param preset the new current status
	 */
	public void setCurrentStatus(List<String> preset) {
		setLoadingPresent(true);
		for (String s: preset) {		
			s = StringUtils.trim(s);
			s = StringUtils.removeEnd(s, ";");
			MessageProcessor.processMsg(StringUtils.split(s, ' '), false);
		}
		setLoadingPresent(false);
	}
	
	/**
	 * update the visual setting in the gui.
	 *
	 * @return the current mini status
	 */
	public List<String> getCurrentMiniStatus() {
		List<String> ret = new ArrayList<String>();
		
		//get visual status
		Visual v = allVisuals.get(currentVisual);
		ret.add(ValidCommands.CHANGE_GENERATOR_A+EMPTY_CHAR+v.getGenerator1Idx());
		ret.add(ValidCommands.CHANGE_GENERATOR_B+EMPTY_CHAR+v.getGenerator2Idx());
		ret.add(ValidCommands.CHANGE_EFFECT_A+EMPTY_CHAR+v.getEffect1Idx());
		ret.add(ValidCommands.CHANGE_EFFECT_B+EMPTY_CHAR+v.getEffect2Idx());
		ret.add(ValidCommands.CHANGE_MIXER+EMPTY_CHAR+v.getMixerIdx());

		//get output status
		OutputMapping om = ioMapping.get(currentOutput); 
		
		LOG.log(Level.INFO, "currentOutput: {0}, check: {1}, visual: {2} "
				, new Object[] { currentOutput, om.getVisualId(), om.getVisualId() });

		
		ret.add(ValidCommands.CHANGE_OUTPUT_EFFECT+EMPTY_CHAR+om.getEffect().getId());
		ret.add(ValidCommands.CHANGE_OUTPUT_FADER+EMPTY_CHAR+om.getFader().getId());
		ret.add(ValidCommands.CHANGE_OUTPUT_VISUAL+EMPTY_CHAR+om.getVisualId());
		return ret;
	}

	/**
	 * get current state of visuals/outputs
	 * as string list - used to save current settings.
	 *
	 * @return the current status
	 */
	public List<String> getCurrentStatus() {		
		List<String> ret = new ArrayList<String>();				
		
		//all visuals
		Visual v = allVisuals.get(currentVisual);
		ret.add(ValidCommands.CHANGE_GENERATOR_A+EMPTY_CHAR+v.getGenerator1Idx());
		ret.add(ValidCommands.CHANGE_GENERATOR_B+EMPTY_CHAR+v.getGenerator2Idx());
		ret.add(ValidCommands.CHANGE_EFFECT_A+EMPTY_CHAR+v.getEffect1Idx());
		ret.add(ValidCommands.CHANGE_EFFECT_B+EMPTY_CHAR+v.getEffect2Idx());
		ret.add(ValidCommands.CHANGE_MIXER+EMPTY_CHAR+v.getMixerIdx());
		
		//get output status
		OutputMapping om = ioMapping.get(currentOutput); 
		ret.add(ValidCommands.CHANGE_OUTPUT_EFFECT+EMPTY_CHAR+om.getEffect().getId());
		ret.add(ValidCommands.CHANGE_OUTPUT_FADER+EMPTY_CHAR+om.getFader().getId());
		ret.add(ValidCommands.CHANGE_OUTPUT_VISUAL+EMPTY_CHAR+om.getVisualId());
		
		//add element status
		ret.addAll(pixelControllerEffect.getCurrentState());
		ret.addAll(pixelControllerGenerator.getCurrentState());
		ret.addAll(pixelControllerShufflerSelect.getCurrentState());
		
		ret.add(ValidCommands.CHANGE_PRESENT +EMPTY_CHAR+selectedPresent);
		ret.add(ValidCommands.CURRENT_OUTPUT +EMPTY_CHAR+currentOutput);		
		ret.add(ValidCommands.CURRENT_VISUAL+EMPTY_CHAR+currentVisual);
		return ret;
	}

	/*
	 * MATRIX ======================================================
	 */

	/**
	 * Gets the matrix.
	 *
	 * @return the matrix
	 */
	public MatrixData getMatrix() {
		return matrix;
	}

	/**
	 * Sets the matrix.
	 *
	 * @param matrix the new matrix
	 */
	public void setMatrix(MatrixData matrix) {
		this.matrix = matrix;
	}


	/*
	 * VISUAL ======================================================
	 */

	/**
	 * Adds the visual.
	 *
	 * @param visual the visual
	 */
	public void addVisual(Visual visual) {
		allVisuals.add(visual);
	}

	/**
	 * Gets the all visuals.
	 *
	 * @return the all visuals
	 */
	public List<Visual> getAllVisuals() {
		return allVisuals;
	}

	/**
	 * Gets the visual.
	 *
	 * @param index the index
	 * @return the visual
	 */
	public Visual getVisual(int index) {
		if (index<allVisuals.size()) {
			return allVisuals.get(index);			
		} 
		return allVisuals.get(0);
	}

	/**
	 * Sets the all visuals.
	 *
	 * @param allVisuals the new all visuals
	 */
	public void setAllVisuals(List<Visual> allVisuals) {
		this.allVisuals = allVisuals;
	}


	/* 
	 * PRESENT ======================================================
	 */
	
	/**
	 * Gets the selected present.
	 *
	 * @return the selected present
	 */
	public int getSelectedPresent() {
		return selectedPresent;
	}

	/**
	 * Sets the selected present.
	 *
	 * @param selectedPresent the new selected present
	 */
	public void setSelectedPresent(int selectedPresent) {
		this.selectedPresent = selectedPresent;
	}

	/**
	 * Gets the present.
	 *
	 * @return the present
	 */
	public List<PresentSettings> getPresent() {
		return present;
	}

	/**
	 * Sets the present.
	 *
	 * @param present the new present
	 */
	public void setPresent(List<PresentSettings> present) {
		this.present = present;
	}
	
	
	/*
	 * OUTPUT MAPPING ======================================================
	 */
	
	/**
	 * Gets the all output mappings.
	 *
	 * @return the all output mappings
	 */
	public List<OutputMapping> getAllOutputMappings() {
		return ioMapping;
	}

	/**
	 * Gets the output mappings.
	 *
	 * @param index the index
	 * @return the output mappings
	 */
	public OutputMapping getOutputMappings(int index) {
		return ioMapping.get(index);
	}

		
	
	/**
	 * Gets the current visual.
	 *
	 * @return the current visual
	 */
	public int getCurrentVisual() {
		return currentVisual;
	}

	/**
	 * Sets the current visual.
	 *
	 * @param currentVisual the new current visual
	 */
	public void setCurrentVisual(int currentVisual) {
		if (currentVisual<allVisuals.size()) {
			this.currentVisual = currentVisual;			
		}
	}

	
	
	public int getCurrentOutput() {
		return currentOutput;
	}

	public void setCurrentOutput(int currentOutput) {
		if (currentOutput<ioMapping.size()) {
			this.currentOutput = currentOutput;			
		}
	}

	/**
	 * Checks if is loading present.
	 *
	 * @return true, if is loading present
	 */
	public synchronized boolean isLoadingPresent() {
		return isLoadingPresent;
	}

	/**
	 * Sets the loading present.
	 *
	 * @param isLoadingPresent the new loading present
	 */
	public synchronized void setLoadingPresent(boolean isLoadingPresent) {
		this.isLoadingPresent = isLoadingPresent;
	}

	
	//getShufflerSelect
	
	/**
	 * Gets the shuffler select.
	 *
	 * @param ofs the ofs
	 * @return the shuffler select
	 */
	public boolean getShufflerSelect(ShufflerOffset ofs) {
		return pixelControllerShufflerSelect.getShufflerSelect(ofs);	
	}
	
	/**
	 * Gets the pixel controller shuffler select.
	 *
	 * @return the pixel controller shuffler select
	 */
	public PixelControllerShufflerSelect getPixelControllerShufflerSelect() {
		return pixelControllerShufflerSelect;
	}

	/**
	 * Gets the pixel controller mixer.
	 *
	 * @return the pixel controller mixer
	 */
	
	public PixelControllerMixer getPixelControllerMixer() {
		return pixelControllerMixer;
	}
	
	/**
	 * Gets the pixel controller effect.
	 *
	 * @return the pixel controller effect
	 */
	public PixelControllerEffect getPixelControllerEffect() {
		return pixelControllerEffect;
	}
	
	/**
	 * Gets the pixel controller generator.
	 *
	 * @return the pixel controller generator
	 */
	public PixelControllerGenerator getPixelControllerGenerator() {
		return pixelControllerGenerator;
	}
	
	/**
	 * Gets the pixel controller resize.
	 *
	 * @return the pixel controller resize
	 */
	public PixelControllerResize getPixelControllerResize() {
		return pixelControllerResize;
	}

	/**
	 * Gets the pixel controller output.
	 *
	 * @return the pixel controller output
	 */
	public PixelControllerOutput getPixelControllerOutput() {
		return pixelControllerOutput;
	}

    /**
     * @return the ph
     */
    public PropertiesHelper getPh() {
        return ph;
    }

    /**
     * 
     * @return
     */
	public int getFrames() {
		return framesEffective;
	}
    
    

}
