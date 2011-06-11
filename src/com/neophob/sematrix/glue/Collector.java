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

import org.apache.commons.lang.StringUtils;

import processing.core.PApplet;

import com.neophob.sematrix.effect.PixelControllerEffect;
import com.neophob.sematrix.fader.Fader;
import com.neophob.sematrix.generator.PixelControllerGenerator;
import com.neophob.sematrix.input.Sound;
import com.neophob.sematrix.input.SoundMinim;
import com.neophob.sematrix.listener.MessageProcessor;
import com.neophob.sematrix.listener.TcpServer;
import com.neophob.sematrix.listener.MessageProcessor.ValidCommands;
import com.neophob.sematrix.mixer.PixelControllerMixer;
import com.neophob.sematrix.output.PixelControllerOutput;
import com.neophob.sematrix.properties.PropertiesHelper;
import com.neophob.sematrix.resize.PixelControllerResize;

public class Collector {

	/** 
	 * TODO make dynamic
	 * nr of shuffler entries. enable/disable option for random mode 
	 * 
	 */
	
	public static final int NR_OF_PRESENT_SLOTS = 128;
	
	private static Collector instance = new Collector();

	private boolean randomMode = false;

	private boolean init;
	private PApplet papplet;
	private MatrixData matrix;

	/** all input elements*/	
	private List<Visual> allVisuals;

	/** fx to screen mapping */
	private List<OutputMapping> ioMapping;

	private int nrOfScreens;
	private int fps;
	private int frames;
	
	private int currentVisual;
	
	/** present settings */
	private int selectedPresent;
	private List<PresentSettings> present;
	
	private PixelControllerGenerator pixelControllerGenerator;
	private PixelControllerMixer pixelControllerMixer;
	private PixelControllerEffect pixelControllerEffect;
	private PixelControllerResize pixelControllerResize;
	private PixelControllerOutput pixelControllerOutput;
	private PixelControllerShufflerSelect pixelControllerShufflerSelect;
	
	@SuppressWarnings("unused")
	private TcpServer pdSrv;
	
	private boolean isLoadingPresent=false;

	/**
	 * 
	 */
	private Collector() {	
		allVisuals = new CopyOnWriteArrayList<Visual>();

		this.nrOfScreens = 0;
		ioMapping = new CopyOnWriteArrayList<OutputMapping>();
		init=false;

		selectedPresent=0;
		present = new CopyOnWriteArrayList<PresentSettings>();
		for (int n=0; n<NR_OF_PRESENT_SLOTS; n++) {
			present.add(new PresentSettings());
		}

		pixelControllerShufflerSelect = new PixelControllerShufflerSelect();
		pixelControllerShufflerSelect.initAll();
	}

	/**
	 * initialize the collector
	 * @param papplet
	 * @param nrOfScreens
	 */
	public void init(PApplet papplet, int fps, int deviceXsize, int deviceYsize) {
		if (init) return;
		this.papplet = papplet;
		this.nrOfScreens = PropertiesHelper.getInstance().getNrOfScreens();
		this.fps = fps;
		
		//choose sound implementation
		Sound.getInstance().setImplementation(new SoundMinim());
//		Sound.getInstance().setImplementation(new SoundDummy());
		new MatrixData(deviceXsize, deviceYsize);

		this.initSystem();

		//create an empty mapping
		ioMapping.clear();
		for (int n=0; n<nrOfScreens; n++) {
			ioMapping.add(new OutputMapping(n, 0));			
		}

		//Start tcp server
		int listeningPort = Integer.parseInt( PropertiesHelper.getInstance().getProperty("net.listening.port", "3449") );
		int sendPort = Integer.parseInt( PropertiesHelper.getInstance().getProperty("net.send.port", "3445") );
		
		try {
			pdSrv = new TcpServer(papplet, listeningPort, "127.0.0.1", sendPort);
		} catch (BindException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		init=true;
	}

	/**
	 * initialize generators, mixer, effects....
	 */
	private void initSystem() {
		//create generators
		
		pixelControllerGenerator = new PixelControllerGenerator();
		pixelControllerGenerator.initAll();
		
		pixelControllerEffect = new PixelControllerEffect();
		pixelControllerEffect.initAll();

		pixelControllerMixer = new PixelControllerMixer();
		pixelControllerMixer.initAll();
		
		//create 5 visuals
		Visual.initializeVisuals(nrOfScreens);
		
		pixelControllerResize = new PixelControllerResize();
		pixelControllerResize.initAll();
		
		pixelControllerOutput = new PixelControllerOutput();
		pixelControllerOutput.initAll();
		
		PropertiesHelper.getInstance().loadPresents();
	}

	/**
	 * update the whole system:
	 *  -generators
	 *  -effects
	 *  -outputs
	 *  
	 *  update the generators, if the sound is
	 *  louder, update faster
	 */
	public void updateSystem() {
		//do not update system if presents are loading
		if (isLoadingPresent()) {
			return;
		}
		
		//get sound volume
		float f = Sound.getInstance().getVolumeNormalized();
		int u = (int)(0.5f+f*1.5f);
		//check for silence - in this case update slowly
		if (u<1) {
			if (frames%3==1) {
				u=1;
			}
		}
		if (Sound.getInstance().isKick()) u+=3;
		if (Sound.getInstance().isHat()) u+=1;
		
		//update generator depending on the input sound
		for (int i=0; i<u; i++) {
			pixelControllerGenerator.update();			
		}
		pixelControllerEffect.update();
		pixelControllerOutput.update();
		
		//cleanup faders
		for (OutputMapping om: ioMapping) {
			Fader fader = om.getFader();
			if (fader.isDone()) {
				//fading is finished
				fader.cleanUp();
			}
		}
		
		if (randomMode) {
			Shuffler.shuffleStuff();
		}
		
		frames++;
	}

	/**
	 * 
	 * @return
	 */
	public static Collector getInstance() {
		return instance;
	}


	public int getNrOfScreens() {
		return nrOfScreens;
	}


	/**
	 * which fx for screenOutput?
	 * @param screenOutput
	 * @return fx nr.
	 */
	public int getFxInputForScreen(int screenOutput) {
		return ioMapping.get(screenOutput).getVisualId();
	}

	/**
	 * define which fx is shown on which screen, without fading
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
	 * used for crossfading
	 * @param oldVisual
	 * @return
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

	public PApplet getPapplet() {
		return papplet;
	}

	public int getFps() {
		return fps;
	}

	public boolean isRandomMode() {
		return randomMode;
	}

	public void setRandomMode(boolean randomMode) {
		this.randomMode = randomMode;
	}

	/**
	 * load a saved preset
	 * @param preset
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
	 * update the visual setting in the gui
	 * @return
	 */
	public List<String> getCurrentMiniStatus() {
		List<String> ret = new ArrayList<String>();
		
		String gen1=getAllVisuals().get(currentVisual).getGenerator1Idx()+"";
		String gen2=getAllVisuals().get(currentVisual).getGenerator2Idx()+"";
		String fx1=getAllVisuals().get(currentVisual).getEffect1Idx()+"";
		String fx2=getAllVisuals().get(currentVisual).getEffect2Idx()+"";
		String mix=getAllVisuals().get(currentVisual).getMixerIdx()+"";
		
		ret.add(ValidCommands.CHANGE_GENERATOR_A+" "+gen1);
		ret.add(ValidCommands.CHANGE_GENERATOR_B+" "+gen2);
		ret.add(ValidCommands.CHANGE_EFFECT_A+" "+fx1);
		ret.add(ValidCommands.CHANGE_EFFECT_B+" "+fx2);
		ret.add(ValidCommands.CHANGE_MIXER+" "+mix);

		return ret;
	}

	/**
	 * get current state of visuals/outputs
	 * as string list - used to save current settings
	 */
	public List<String> getCurrentStatus() {
		List<String> ret = new ArrayList<String>();
		
		String gen1="";
		String gen2="";
		String fx1="";
		String fx2="";
		String mix="";
		for (Visual v: getAllVisuals()) {
			gen1+=v.getGenerator1Idx()+" ";
			gen2+=v.getGenerator2Idx()+" ";
			fx1+=v.getEffect1Idx()+" ";
			fx2+=v.getEffect2Idx()+" ";
			mix+=v.getMixerIdx()+" ";					
		}
		
		String fader="";
		String output="";
		String outputEffect="";
		for (OutputMapping o: getAllOutputMappings()) {
			fader+=o.getFader().getId()+" ";
			output+=o.getVisualId()+" ";
			outputEffect+=o.getEffect().getId()+" ";
		}
		ret.add(ValidCommands.CHANGE_GENERATOR_A+" "+gen1);
		ret.add(ValidCommands.CHANGE_GENERATOR_B+" "+gen2);
		ret.add(ValidCommands.CHANGE_EFFECT_A+" "+fx1);
		ret.add(ValidCommands.CHANGE_EFFECT_B+" "+fx2);
		ret.add(ValidCommands.CHANGE_MIXER+" "+mix);
		ret.add(ValidCommands.CHANGE_FADER+" "+fader);		

		//add element status
		ret.addAll(pixelControllerEffect.getCurrentState());
		ret.addAll(pixelControllerGenerator.getCurrentState());
		ret.addAll(pixelControllerShufflerSelect.getCurrentState());
		
		ret.add(ValidCommands.CHANGE_PRESENT +" "+selectedPresent);
		ret.add(ValidCommands.CHANGE_OUTPUT+" "+output);
		ret.add(ValidCommands.CHANGE_OUTPUT_EFFECT+" "+outputEffect);
		ret.add(ValidCommands.CURRENT_VISUAL+" "+currentVisual);
		return ret;
	}

	/*
	 * MATRIX ======================================================
	 */

	public MatrixData getMatrix() {
		return matrix;
	}

	public void setMatrix(MatrixData matrix) {
		this.matrix = matrix;
	}


	/*
	 * VISUAL ======================================================
	 */

	public void addVisual(Visual visual) {
		allVisuals.add(visual);
	}

	public List<Visual> getAllVisuals() {
		return allVisuals;
	}

	public Visual getVisual(int index) {
		if (index<allVisuals.size()) {
			return allVisuals.get(index);			
		} 
		return allVisuals.get(0);
	}

	public void setAllVisuals(List<Visual> allVisuals) {
		this.allVisuals = allVisuals;
	}


	/* 
	 * PRESENT ======================================================
	 */
	
	public int getSelectedPresent() {
		return selectedPresent;
	}

	public void setSelectedPresent(int selectedPresent) {
		this.selectedPresent = selectedPresent;
	}

	public List<PresentSettings> getPresent() {
		return present;
	}

	public void setPresent(List<PresentSettings> present) {
		this.present = present;
	}
	
	
	/*
	 * OUTPUT MAPPING ======================================================
	 */
	
	public List<OutputMapping> getAllOutputMappings() {
		return ioMapping;
	}

	public OutputMapping getOutputMappings(int index) {
		return ioMapping.get(index);
	}

		
	
	public int getCurrentVisual() {
		return currentVisual;
	}

	public void setCurrentVisual(int currentVisual) {
		this.currentVisual = currentVisual;
	}

	public synchronized boolean isLoadingPresent() {
		return isLoadingPresent;
	}

	public synchronized void setLoadingPresent(boolean isLoadingPresent) {
		this.isLoadingPresent = isLoadingPresent;
	}

	//getShufflerSelect
	
	public boolean getShufflerSelect(ShufflerOffset ofs) {
		return pixelControllerShufflerSelect.getShufflerSelect(ofs);	
	}
	
	public PixelControllerShufflerSelect getPixelControllerShufflerSelect() {
		return pixelControllerShufflerSelect;
	}

	/**
	 * 
	 * @return
	 */
	
	public PixelControllerMixer getPixelControllerMixer() {
		return pixelControllerMixer;
	}
	
	public PixelControllerEffect getPixelControllerEffect() {
		return pixelControllerEffect;
	}
	
	public PixelControllerGenerator getPixelControllerGenerator() {
		return pixelControllerGenerator;
	}
	
	public PixelControllerResize getPixelControllerResize() {
		return pixelControllerResize;
	}

	public PixelControllerOutput getPixelControllerOutput() {
		return pixelControllerOutput;
	}

}
