/**
 * Copyright (C) 2011-2013 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.core.visual;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.glue.PixelControllerShufflerSelect;
import com.neophob.sematrix.core.glue.Shuffler;
import com.neophob.sematrix.core.glue.ShufflerOffset;
import com.neophob.sematrix.core.glue.helper.InitHelper;
import com.neophob.sematrix.core.jmx.PixelControllerStatusMBean;
import com.neophob.sematrix.core.jmx.TimeMeasureItemGlobal;
import com.neophob.sematrix.core.listener.MessageProcessor;
import com.neophob.sematrix.core.output.PixelControllerOutput;
import com.neophob.sematrix.core.preset.PresetServiceImpl;
import com.neophob.sematrix.core.preset.PresetSettings;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.properties.ValidCommands;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.sound.ISound;
import com.neophob.sematrix.core.sound.SoundDummy;
import com.neophob.sematrix.core.sound.SoundMinim;
import com.neophob.sematrix.core.visual.color.ColorSet;
import com.neophob.sematrix.core.visual.effect.Effect;
import com.neophob.sematrix.core.visual.effect.Effect.EffectName;
import com.neophob.sematrix.core.visual.effect.PixelControllerEffect;
import com.neophob.sematrix.core.visual.fader.Fader.FaderName;
import com.neophob.sematrix.core.visual.fader.IFader;
import com.neophob.sematrix.core.visual.fader.PixelControllerFader;
import com.neophob.sematrix.core.visual.generator.Generator;
import com.neophob.sematrix.core.visual.generator.Generator.GeneratorName;
import com.neophob.sematrix.core.visual.generator.PixelControllerGenerator;
import com.neophob.sematrix.core.visual.mixer.Mixer;
import com.neophob.sematrix.core.visual.mixer.Mixer.MixerName;
import com.neophob.sematrix.core.visual.mixer.PixelControllerMixer;

/**
 * The Class Collector.
 * 
 * TODO: collector should caputre the current STATE of the application
 * REMOVE service initialisation (mdns, osc...), remove matrix data
 * 
 * 
 */
public class VisualState extends Observable {

	private static final Logger LOG = Logger.getLogger(VisualState.class.getName());

	/** The Constant EMPTY_CHAR. */
	private static final String EMPTY_CHAR = " ";

	/** The singleton instance. */
	private static VisualState instance = new VisualState();

	/** The random mode. */
	private boolean randomMode = false;

	/** The random mode. */
	private boolean randomPresetMode = false;

	/** The initialized. */
	private boolean initialized;

	/** The matrix. */
	private MatrixData matrix;

	/** all input elements. */	
	private List<Visual> allVisuals;

	/** fx to screen mapping. */
	private List<OutputMapping> ioMapping;

	/** The nr of screens. */
	private int nrOfScreens;

	/** The current visual. */
	private int currentVisual;

	/** The current output. */
	private int currentOutput;

	/** The pixel controller generator. */
	private PixelControllerGenerator pixelControllerGenerator;

	/** The pixel controller mixer. */
	private PixelControllerMixer pixelControllerMixer;

	/** The pixel controller effect. */
	private PixelControllerEffect pixelControllerEffect;

	/** The pixel controller resize. */
	private PixelControllerResize pixelControllerResize;

	/** The pixel controller output. */
	//TODO REMOVE ME
	private PixelControllerOutput pixelControllerOutput;

	/** The pixel controller shuffler select. */
	private PixelControllerShufflerSelect pixelControllerShufflerSelect;

	private PixelControllerFader pixelControllerFader;

	/** The is loading present. */
	private boolean isLoadingPresent=false;

	private List<ColorSet> colorSets;		

	/** The random mode. */
	private boolean inPauseMode = false;

	private boolean internalVisualsVisible = true;

	private ISound sound;

	//TODO Remove me
	private PresetServiceImpl presetService;
	
	/**
	 * Instantiates a new collector.
	 */
	private VisualState() {	
		allVisuals = new CopyOnWriteArrayList<Visual>();

		this.nrOfScreens = 0;
		ioMapping = new CopyOnWriteArrayList<OutputMapping>();
		initialized=false;

		pixelControllerShufflerSelect = new PixelControllerShufflerSelect();
		pixelControllerShufflerSelect.initAll();		 
	}

	/**
	 * initialize the collector.
	 *
	 * @param papplet the PApplet
	 * @param ph the PropertiesHelper
	 */
	public synchronized void init(FileUtils fileUtils, ApplicationConfigurationHelper ph, PixelControllerStatusMBean statistic) {
		LOG.log(Level.INFO, "Initialize collector");
		if (initialized) {
			return;
		}

		presetService = new PresetServiceImpl(fileUtils);		
		
		this.nrOfScreens = ph.getNrOfScreens();
		int fps = (int)(ph.parseFps());
		if (fps<1) {
			fps = 1;
		}

		this.colorSets = InitHelper.getColorPalettes(fileUtils);

		//choose sound implementation
		if (ph.isAudioAware()) {
			try {		
				sound = new SoundMinim(ph.getSoundSilenceThreshold());			
			} catch (Exception e) {
				LOG.log(Level.WARNING, "FAILED TO INITIALIZE SOUND INSTANCE. Disable sound input.");				
			} catch (Error e) {
				LOG.log(Level.WARNING, "FAILED TO INITIALIZE SOUND INSTANCE (Error). Disable sound input.", e);			
			}			
		} 

		if (sound==null) {
			sound = new SoundDummy();
		}

		//create the device with specific size
		this.matrix = new MatrixData(ph.getDeviceXResolution(), ph.getDeviceYResolution());

		pixelControllerResize = new PixelControllerResize();
		pixelControllerResize.initAll();

		//create generators
		pixelControllerGenerator = new PixelControllerGenerator(ph, fileUtils, matrix, fps, 
				sound, pixelControllerResize.getResize(ResizeName.PIXEL_RESIZE));
		pixelControllerGenerator.initAll();

		pixelControllerEffect = new PixelControllerEffect(matrix, sound);
		pixelControllerEffect.initAll();

		pixelControllerMixer = new PixelControllerMixer(matrix, sound);
		pixelControllerMixer.initAll();

		pixelControllerFader = new PixelControllerFader(ph, matrix, fps);

		//create visuals
		int additionalVisuals = 1+ph.getNrOfAdditionalVisuals();
		LOG.log(Level.INFO, "Initialize "+(nrOfScreens+additionalVisuals)+" Visuals");
		try {
			Generator genPassThru = pixelControllerGenerator.getGenerator(GeneratorName.PASSTHRU);
			Effect effPassThru = pixelControllerEffect.getEffect(EffectName.PASSTHRU);
			Mixer mixPassThru = pixelControllerMixer.getMixer(MixerName.PASSTHRU);
			for (int i=1; i<nrOfScreens+additionalVisuals+1; i++) {
				Generator g = pixelControllerGenerator.getGenerator(
						GeneratorName.values()[ i%(GeneratorName.values().length) ]
						);
				if (g==null) {
					//its possible we select an inactive generator, in this case just ignore it...
					additionalVisuals++;
					LOG.log(Level.INFO, "Ignore null Visual, take next...");
				} else {
					allVisuals.add(new Visual(g, genPassThru, effPassThru, effPassThru, mixPassThru, colorSets.get(0)));
				}
			}

		} catch (IndexOutOfBoundsException e) {
			LOG.log(Level.SEVERE, "Failed to initialize Visual, maybe missing palette files?\n");
			throw new IllegalArgumentException("Failed to initialize Visuals, maybe missing palette files?");
		}

		LOG.log(Level.INFO, "Initialize output");
		pixelControllerOutput = new PixelControllerOutput(statistic);
		pixelControllerOutput.initAll();

		//create an empty mapping
		ioMapping.clear();
		for (int n=0; n<nrOfScreens; n++) {
			ioMapping.add(new OutputMapping(pixelControllerFader.getVisualFader(FaderName.SWITCH), n));			
		}

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
	public void updateSystem(PixelControllerStatusMBean pixConStat) {
		//do not update system if presents are loading
		if (isLoadingPresent()) {
			return;
		}

		long l = System.currentTimeMillis();
		//update generator depending on the input sound
		pixelControllerGenerator.update();			
		pixConStat.trackTime(TimeMeasureItemGlobal.GENERATOR, System.currentTimeMillis()-l);

		l = System.currentTimeMillis();
		pixelControllerEffect.update();
		pixConStat.trackTime(TimeMeasureItemGlobal.EFFECT, System.currentTimeMillis()-l);

		l = System.currentTimeMillis();
		pixelControllerOutput.update();
		pixConStat.trackTime(TimeMeasureItemGlobal.OUTPUT_SCHEDULE, System.currentTimeMillis()-l);

		//cleanup faders
		l = System.currentTimeMillis();
		for (OutputMapping om: ioMapping) {
			IFader fader = om.getFader();
			if (fader!=null && fader.isStarted() && fader.isDone()) {
				//fading is finished, cleanup
				fader.cleanUp();

				if (fader.getScreenOutput()>=0) {
					mapInputToScreen(fader.getScreenOutput(), fader.getNewVisual());			
					LOG.log(Level.INFO, "Cleanup {0}, new visual: {1}, output screen: {2}", 
							new Object[] { fader.getFaderName(), fader.getNewVisual(), fader.getScreenOutput() });
				} else {
					LOG.log(Level.INFO, "Cleanup preset {0}, new visual: {1}", 
							new Object[] { fader.getFaderName(), fader.getNewVisual() });			
				}
			}
		}
		pixConStat.trackTime(TimeMeasureItemGlobal.FADER, System.currentTimeMillis()-l);

		if (randomMode) {
			Shuffler.shuffleStuff(sound);
		} else if (randomPresetMode) {
			Shuffler.randomPresentModeShuffler(sound);
		}
	}

	/**
	 * Gets the single instance of Collector.
	 *
	 * @return single instance of Collector
	 */
	public static VisualState getInstance() {
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

	public boolean isRandomPresetMode() {
		return randomPresetMode;
	}

	public void setRandomPresetMode(boolean randomPresetMode) {
		this.randomPresetMode = randomPresetMode;
	}

	public void savePresets() {
		presetService.savePresents();
	}

	/**
	 * load a saved preset.
	 *
	 * @param preset the new current status
	 */
	public void setCurrentStatus(List<String> preset) {
		LOG.log(Level.FINEST, "--------------");
		long start=System.currentTimeMillis();
		setLoadingPresent(true);
		for (String s: preset) {		
			s = StringUtils.trim(s);
			s = StringUtils.removeEnd(s, ";");
			LOG.log(Level.FINEST, "LOAD PRESET: "+s);
			MessageProcessor.processMsg(StringUtils.split(s, ' '), false, null);
		}
		setLoadingPresent(false);
		long needed=System.currentTimeMillis()-start;
		LOG.log(Level.INFO, "Preset loaded in "+needed+"ms");
	}

	/**
	 * get current state of visuals/outputs
	 * as string list - used to save current settings.
	 *
	 * @return the current status
	 */
	public List<String> getCurrentStatus() {		
		List<String> ret = new ArrayList<String>();

		//get visual status
		int n=0;
		for (Visual v: allVisuals) {
			ret.add(ValidCommands.CURRENT_VISUAL +EMPTY_CHAR+n++);
			ret.add(ValidCommands.CHANGE_GENERATOR_A+EMPTY_CHAR+v.getGenerator1Idx());
			ret.add(ValidCommands.CHANGE_GENERATOR_B+EMPTY_CHAR+v.getGenerator2Idx());
			ret.add(ValidCommands.CHANGE_EFFECT_A+EMPTY_CHAR+v.getEffect1Idx());
			ret.add(ValidCommands.CHANGE_EFFECT_B+EMPTY_CHAR+v.getEffect2Idx());
			ret.add(ValidCommands.CHANGE_MIXER+EMPTY_CHAR+v.getMixerIdx());
			ret.add(ValidCommands.CURRENT_COLORSET+EMPTY_CHAR+v.getColorSet().getName());
		}

		//get output status
		int ofs=0;
		for (OutputMapping om: ioMapping) {
			ret.add(ValidCommands.CURRENT_OUTPUT +EMPTY_CHAR+ofs);
			ret.add(ValidCommands.CHANGE_OUTPUT_FADER+EMPTY_CHAR+om.getFader().getId());
			ret.add(ValidCommands.CHANGE_OUTPUT_VISUAL+EMPTY_CHAR+om.getVisualId());
			ofs++;
		}

		//add element status
		ret.addAll(pixelControllerEffect.getCurrentState());
		ret.addAll(pixelControllerGenerator.getCurrentState());
		ret.addAll(pixelControllerShufflerSelect.getCurrentState());

		ret.add(ValidCommands.CHANGE_PRESET +EMPTY_CHAR+presetService.getSelectedPreset());						
		if (inPauseMode) {
			ret.add(ValidCommands.FREEZE+EMPTY_CHAR);
		}
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
		if (index>=0 && index<allVisuals.size()) {
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
	public int getSelectedPreset() {
		return presetService.getSelectedPreset();
	}

	/**
	 * Sets the selected present.
	 *
	 * @param selectedPresent the new selected present
	 */
	public void setSelectedPreset(int selectedPreset) {
		presetService.setSelectedPreset(selectedPreset);
	}

	/**
	 * Gets the present.
	 *
	 * @return the present
	 */
	public List<PresetSettings> getPresets() {
		return presetService.getPresets();
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
		if (currentVisual >= 0 && currentVisual < allVisuals.size()) {
			this.currentVisual = currentVisual;			
		}
	}

	/**
	 * 
	 * @return
	 */
	public int getCurrentOutput() {
		return currentOutput;
	}

	/**
	 * 
	 * @param currentOutput
	 */
	public void setCurrentOutput(int currentOutput) {
		if (currentOutput >= 0 && currentOutput < ioMapping.size()) {
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
	 * 
	 * @return
	 */
	public PixelControllerFader getPixelControllerFader() {
		return pixelControllerFader;
	}

	/**
	 * 
	 * @return
	 */
	public int getFrames() {
		return pixelControllerGenerator.getFrames();
	}

	/**
	 * 
	 * @return
	 */
	public List<ColorSet> getColorSets() {
		return colorSets;
	}

	/**
	 * 
	 * @param colorSets
	 */
	public void setColorSets(List<ColorSet> colorSets) {
		this.colorSets = colorSets;
	}

	/**
	 * 
	 */
	public void togglePauseMode() {
		if (inPauseMode) {
			inPauseMode=false;
		} else {
			inPauseMode=true;
		}
	}

	/**
	 * 
	 */
	public void toggleInternalVisual() {
		if (internalVisualsVisible) {
			internalVisualsVisible=false;
		} else {
			internalVisualsVisible=true;
		}
	}



	public boolean isInternalVisualsVisible() {
		return internalVisualsVisible;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isInPauseMode() {
		return inPauseMode;
	}


	/**
	 * sound implementation
	 * @return
	 */
	public ISound getSound() {
		return sound;
	}


	private List<String> getGuiState() {
		List<String> ret = new ArrayList<String>();

		Visual v = allVisuals.get(currentVisual);		
		ret.add(ValidCommands.CURRENT_VISUAL +EMPTY_CHAR+currentVisual);
		ret.add(ValidCommands.CHANGE_GENERATOR_A+EMPTY_CHAR+v.getGenerator1Idx());
		ret.add(ValidCommands.CHANGE_GENERATOR_B+EMPTY_CHAR+v.getGenerator2Idx());
		ret.add(ValidCommands.CHANGE_EFFECT_A+EMPTY_CHAR+v.getEffect1Idx());
		ret.add(ValidCommands.CHANGE_EFFECT_B+EMPTY_CHAR+v.getEffect2Idx());
		ret.add(ValidCommands.CHANGE_MIXER+EMPTY_CHAR+v.getMixerIdx());
		ret.add(ValidCommands.CURRENT_COLORSET+EMPTY_CHAR+v.getColorSet().getName());

		//get output status
		int ofs=0;
		for (OutputMapping om: ioMapping) {
			ret.add(ValidCommands.CURRENT_OUTPUT +EMPTY_CHAR+ofs);
			ret.add(ValidCommands.CHANGE_OUTPUT_FADER+EMPTY_CHAR+om.getFader().getId());
			ret.add(ValidCommands.CHANGE_OUTPUT_VISUAL+EMPTY_CHAR+om.getVisualId());
			ofs++;
		}

		ret.addAll(pixelControllerEffect.getCurrentState());
		ret.addAll(pixelControllerGenerator.getCurrentState());
		ret.addAll(pixelControllerShufflerSelect.getCurrentState());

		ret.add(ValidCommands.CHANGE_PRESET +EMPTY_CHAR+presetService.getSelectedPreset());						
		ret.add(ValidCommands.FREEZE+EMPTY_CHAR+inPauseMode);

		return ret;
	}

	/**
	 * 
	 */
	public void notifyGuiUpdate() {
		setChanged();
		notifyObservers(getGuiState());
	}

}
