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
package com.neophob.sematrix.core.glue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

import com.neophob.sematrix.core.color.ColorSet;
import com.neophob.sematrix.core.effect.Effect;
import com.neophob.sematrix.core.effect.PixelControllerEffect;
import com.neophob.sematrix.core.effect.Effect.EffectName;
import com.neophob.sematrix.core.fader.IFader;
import com.neophob.sematrix.core.fader.PixelControllerFader;
import com.neophob.sematrix.core.fader.Fader.FaderName;
import com.neophob.sematrix.core.generator.Generator;
import com.neophob.sematrix.core.generator.PixelControllerGenerator;
import com.neophob.sematrix.core.generator.Generator.GeneratorName;
import com.neophob.sematrix.core.glue.helper.InitHelper;
import com.neophob.sematrix.core.input.ISound;
import com.neophob.sematrix.core.input.SoundDummy;
import com.neophob.sematrix.core.input.SoundMinim;
import com.neophob.sematrix.core.jmx.PixelControllerStatus;
import com.neophob.sematrix.core.jmx.TimeMeasureItemGlobal;
import com.neophob.sematrix.core.listener.MessageProcessor;
import com.neophob.sematrix.core.mixer.Mixer;
import com.neophob.sematrix.core.mixer.PixelControllerMixer;
import com.neophob.sematrix.core.mixer.Mixer.MixerName;
import com.neophob.sematrix.core.osc.PixelControllerOscServer;
import com.neophob.sematrix.core.output.IOutput;
import com.neophob.sematrix.core.output.PixelControllerOutput;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.properties.ConfigConstant;
import com.neophob.sematrix.core.properties.ValidCommands;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.resize.Resize.ResizeName;

/**
 * The Class Collector.
 */
public class Collector extends Observable {

	private static final Logger LOG = Logger.getLogger(Collector.class.getName());
	
	/** The Constant EMPTY_CHAR. */
	private static final String EMPTY_CHAR = " ";
	
	/** The Constant NR_OF_PRESENT_SLOTS. */
	public static final int NR_OF_PRESET_SLOTS = 128;

	/** The singleton instance. */
	private static Collector instance = new Collector();

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
	
	/** The fps. */
	private int fps;
	
	/** The current visual. */
	private int currentVisual;

	/** The current output. */
	private int currentOutput;

	/** present settings. */
	private int selectedPreset;
	
	/** The present. */
	private List<PresetSettings> presets;
	
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
	
	private PixelControllerFader pixelControllerFader;
	
	private PixelControllerOscServer oscServer;
	
	private ApplicationConfigurationHelper ph;
	
	/** The is loading present. */
	private boolean isLoadingPresent=false;
	
	private PixelControllerStatus pixConStat;

	private List<ColorSet> colorSets;		
	
	/** The random mode. */
	private boolean inPauseMode = false;

	private boolean internalVisualsVisible = true;

	private IOutput output;
	
	private ISound sound;
	
	private FileUtils fileUtils;
	
	
	
	/**
	 * Instantiates a new collector.
	 */
	private Collector() {	
		allVisuals = new CopyOnWriteArrayList<Visual>();

		this.nrOfScreens = 0;
		ioMapping = new CopyOnWriteArrayList<OutputMapping>();
		initialized=false;

		selectedPreset=0;		
		presets = PresetSettings.initializePresetSettings(NR_OF_PRESET_SLOTS);
				
		pixelControllerShufflerSelect = new PixelControllerShufflerSelect();
		pixelControllerShufflerSelect.initAll();		 
	}

	/**
	 * initialize the collector.
	 *
	 * @param papplet the PApplet
	 * @param ph the PropertiesHelper
	 */
	public synchronized void init(FileUtils fileUtils, ApplicationConfigurationHelper ph) {
		LOG.log(Level.INFO, "Initialize collector");
		if (initialized) {
			return;
		}

		this.fileUtils = fileUtils;
		this.nrOfScreens = ph.getNrOfScreens();
		this.ph = ph;
		this.fps = ph.parseFps();
		
		this.colorSets = InitHelper.getColorPalettes(fileUtils);
		
		//choose sound implementation
		if (ph.isAudioAware()) {
			try {		
				sound = new SoundMinim(ph.getSoundSilenceThreshold());			
			} catch (Exception e) {
				LOG.log(Level.WARNING, "FAILED TO INITIALIZE SOUND INSTANCE. Disable sound input.");				
			} catch (LinkageError e) {
				LOG.log(Level.WARNING, "FAILED TO INITIALIZE SOUND INSTANCE (Linkage error). Disable sound input.");			
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
		pixelControllerGenerator = new PixelControllerGenerator(ph, fileUtils, matrix, this.fps, 
				sound, pixelControllerResize.getResize(ResizeName.PIXEL_RESIZE));
		pixelControllerGenerator.initAll();
		
		pixelControllerEffect = new PixelControllerEffect(matrix, sound);
		pixelControllerEffect.initAll();

		pixelControllerMixer = new PixelControllerMixer(matrix, sound);
		pixelControllerMixer.initAll();
		
		pixelControllerFader = new PixelControllerFader(ph, matrix, this.fps);
		
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
		pixelControllerOutput = new PixelControllerOutput();
		pixelControllerOutput.initAll();
		
		this.presets = fileUtils.loadPresents(NR_OF_PRESET_SLOTS);
		
		//create an empty mapping
		ioMapping.clear();
		for (int n=0; n<nrOfScreens; n++) {
			ioMapping.add(new OutputMapping(pixelControllerFader.getVisualFader(FaderName.SWITCH), n));			
		}
		
		pixConStat = new PixelControllerStatus(fps);
		
		initialized=true;
	}
	
	/**
	 * start tcp and osc server
	 * 
	 * @param papplet
	 * @param ph
	 */
	public synchronized void initDaemons(ApplicationConfigurationHelper ph) {
        //Start OSC Server (OSC Interface)
        try {           
            int listeningOscPort = Integer.parseInt(ph.getProperty(ConfigConstant.NET_OSC_LISTENING_PORT, "9876") );
            oscServer = new PixelControllerOscServer(listeningOscPort);
            oscServer.startServer();
            //register osc server in the statistic class
            this.pixConStat.setOscServerStatistics(oscServer);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "failed to start OSC Server", e);
        }          	   
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
				
		//update the current value of frames per second
/*		if (papplet!=null) {
			pixConStat.setCurrentFps(papplet.frameRate);
			pixConStat.setFrameCount(papplet.frameCount);			
		}*/

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

	
	private void saveImage(Visual v, String filename, int[] data) {
		try {
		    // retrieve image
			
			//maybe colorSet.convertToColorSetImage(effect1.getBuffer(generator1.internalBuffer))
		    BufferedImage bi = new BufferedImage(v.getGenerator1().getInternalBufferXSize(), 
		    		v.getGenerator1().getInternalBufferYSize(), BufferedImage.TYPE_INT_RGB);
		    File outputfile = new File(filename);
		    ImageIO.write(bi, "png", outputfile);
		} catch (IOException e) {
		    LOG.log(Level.SEVERE, "Failed to save screenshot "+filename, e);
		}
	}
	
	/**
	 * create screenshot
	 */
	public void saveScreenshot() {
		int ofs=0;		
		int frames = pixelControllerGenerator.getFrames();
		String suffix = ".png";
		for (Visual v: allVisuals) {
			String prefix = "screenshot/"+frames+"-"+ofs+"-";
			saveImage(v, prefix+"gen1"+suffix, v.getGenerator1().internalBuffer);
			saveImage(v, prefix+"gen2"+suffix, v.getGenerator2().internalBuffer);

			saveImage(v, prefix+"fx1"+suffix, v.getEffect1Buffer());
			saveImage(v, prefix+"fx2"+suffix, v.getEffect2Buffer());

			saveImage(v, prefix+"mix"+suffix, v.getMixerBuffer());
			ofs++;
		}
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

	public boolean isRandomPresetMode() {
		return randomPresetMode;
	}

	public void setRandomPresetMode(boolean randomPresetMode) {
		this.randomPresetMode = randomPresetMode;
	}
	
	public void savePresets() {
		fileUtils.savePresents(presets);
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
		
		ret.add(ValidCommands.CHANGE_PRESENT +EMPTY_CHAR+selectedPreset);						
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
		return selectedPreset;
	}

	/**
	 * Sets the selected present.
	 *
	 * @param selectedPresent the new selected present
	 */
	public void setSelectedPreset(int selectedPreset) {
		this.selectedPreset = selectedPreset;
	}

	/**
	 * Gets the present.
	 *
	 * @return the present
	 */
	public List<PresetSettings> getPresets() {
		return presets;
	}

	/**
	 * Sets the present.
	 *
	 * @param present the new present
	 */
	public void setPresets(List<PresetSettings> preset) {
		this.presets = preset;
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
     * @return the ph
     */
    public ApplicationConfigurationHelper getPh() {
        return ph;
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
	public PixelControllerStatus getPixConStat() {
		return pixConStat;
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
     * @param output the output to set
     */
    public void setOutput(IOutput output) {
        this.output = output;
    }    
    	
    /**
     * 
     * @return
     */
    public String getOutputDeviceName() {
        if (this.output==null) {
            return "";
        }
        return output.getType().toString();
    }
    
    /**
     * 
     * @return
     */
    public Boolean isOutputDeviceConnected() {
        if (this.output==null || !this.output.isSupportConnectionState()) {
            return null;
        }
        
        return this.output.isSupportConnectionState() && this.output.isConnected();
    }
    
    /**
     * 
     * @return
     */
    public IOutput getOutputDevice() {
        return this.output;
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
		
		ret.add(ValidCommands.CHANGE_PRESENT +EMPTY_CHAR+selectedPreset);						
		
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
