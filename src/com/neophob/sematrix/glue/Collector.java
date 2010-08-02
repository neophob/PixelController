package com.neophob.sematrix.glue;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.effect.BeatHorizShift;
import com.neophob.sematrix.effect.BeatVerticalShift;
import com.neophob.sematrix.effect.Effect;
import com.neophob.sematrix.effect.Inverter;
import com.neophob.sematrix.effect.PassThru;
import com.neophob.sematrix.effect.RotoZoom;
import com.neophob.sematrix.effect.Voluminize;
import com.neophob.sematrix.effect.Effect.EffectName;
import com.neophob.sematrix.fader.Crossfader;
import com.neophob.sematrix.fader.Fader;
import com.neophob.sematrix.fader.SlideLeftRight;
import com.neophob.sematrix.fader.SlideUpsideDown;
import com.neophob.sematrix.fader.Switch;
import com.neophob.sematrix.fader.Fader.FaderName;
import com.neophob.sematrix.generator.Blinkenlights;
import com.neophob.sematrix.generator.Fire;
import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.generator.Image;
import com.neophob.sematrix.generator.Metaballs;
import com.neophob.sematrix.generator.PassThruGen;
import com.neophob.sematrix.generator.Plasma2;
import com.neophob.sematrix.generator.SimpleColors;
import com.neophob.sematrix.generator.VolumeDisplay;
import com.neophob.sematrix.generator.Generator.GeneratorName;
import com.neophob.sematrix.input.Sound;
import com.neophob.sematrix.mixer.AddSat;
import com.neophob.sematrix.mixer.Mix;
import com.neophob.sematrix.mixer.Mixer;
import com.neophob.sematrix.mixer.Multiply;
import com.neophob.sematrix.mixer.PassThruMixer;
import com.neophob.sematrix.mixer.Mixer.MixerName;
import com.neophob.sematrix.output.Output;

public class Collector {

	private static Collector instance = new Collector();

	private boolean randomMode = false;

	private boolean init;
	private PApplet papplet;
	private MatrixData matrix;

	private List<Output> allOutputs;

	/** all input elements*/
	private List<Generator> allGenerators;		
	private List<Effect> allEffects;
	private List<Mixer> allMixer;
	private List<Visual> allVisuals;

	/** fx to screen mapping */
	private List<OutputMapping> ioMapping;

	private int nrOfScreens;
	private int fps;

	private Collector() {
		allOutputs = new ArrayList<Output>();

		allGenerators = new ArrayList<Generator>();			
		allEffects = new ArrayList<Effect>();
		allMixer = new ArrayList<Mixer>();

		allVisuals = new ArrayList<Visual>();

		this.nrOfScreens = 0;
		ioMapping = new LinkedList<OutputMapping>();
		init=false;
	}

	/**
	 * initialize the collector
	 * @param papplet
	 * @param nrOfScreens
	 */
	public void init(PApplet papplet, int fps, int nrOfScreens, int deviceXsize, int deviceYsize) {
		if (init) return;
		this.nrOfScreens = nrOfScreens;
		this.papplet = papplet;
		this.fps = fps;
		Sound.getInstance();
		new MatrixData(deviceXsize, deviceYsize);
		this.initSystem();

		//create an empty mapping
		ioMapping.clear();
		for (int n=0; n<nrOfScreens; n++) {
			ioMapping.add(new OutputMapping(n, 0));			
		}
		init=true;		
	}

	/**
	 * initialize generators, mixer, effects....
	 */
	private void initSystem() {
		//create generators
		new Blinkenlights("torus.bml");
		new Image("check.jpg");
		new Plasma2();
		new SimpleColors();
		new Fire();
		new PassThruGen();
		new Metaballs();
		new VolumeDisplay();

		//create effects
		new Inverter();
		new PassThru();
		new RotoZoom(0.7f, 2.3f);
		new BeatVerticalShift();
		new BeatHorizShift();
		new Voluminize();

		//create mixer
		new AddSat();
		new Multiply();
		new Mix();
		new PassThruMixer();

		//create 5 visuals
		new Visual(GeneratorName.PLASMA);
		new Visual(GeneratorName.METABALLS);
		new Visual(GeneratorName.SIMPLECOLORS);
		new Visual(GeneratorName.BLINKENLIGHTS);
		new Visual(GeneratorName.IMAGE);
	}

	/**
	 * workarround until processing resize works 
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	private static BufferedImage resize2(BufferedImage image, int width, int height) {
		int type = image.getType() == 0? BufferedImage.TYPE_INT_RGB : image.getType();
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.setComposite(AlphaComposite.Src);

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

		g.setRenderingHint(RenderingHints.KEY_RENDERING,
		RenderingHints.VALUE_RENDER_SPEED);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_OFF);

		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
		}

	/**
	 * 
	 * @param buffer
	 * @return
	 */
	public PImage getImageFromBuffer(int[] buffer, int deviceXSize, int deviceYSize) {
		//TODO: this is ugly!
		Generator gen1 = this.getGenerator(0);
/*		
		Processing resize is buggy!
 		PImage pImage = Collector.getInstance().getPapplet().createImage
		( gen1.getInternalBufferXSize(), gen1.getInternalBufferYSize(), PApplet.RGB );

		pImage.loadPixels();
		System.arraycopy(buffer, 0, pImage.pixels, 0, gen1.internalBuffer.length);
		pImage.updatePixels();
		BufferedImage resizedImage = resize2((BufferedImage)pImage.getImage(), deviceXSize, deviceYSize);

		*/
		BufferedImage bi = new BufferedImage(gen1.getInternalBufferXSize(), gen1.getInternalBufferYSize(), BufferedImage.TYPE_INT_RGB);
		bi.setRGB(0, 0, gen1.getInternalBufferXSize(), gen1.getInternalBufferYSize(), buffer, 0, gen1.getInternalBufferXSize());
		
		BufferedImage resizedImage = resize2(bi, deviceXSize, deviceYSize);
		
		PImage resized = new PImage(deviceXSize, deviceYSize);
		resizedImage.getRGB(0, 0, deviceXSize, deviceYSize, resized.pixels, 0, deviceXSize);
		
		return resized;
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
	 * how man screens share an fx? 
	 * @param fxInput
	 * @return how many
	 */
	public int howManyScreensShareThisFx(int fxInput) {
		int ret=0;

		for (OutputMapping o: ioMapping) {
			if (o.getVisualId()==fxInput) {
				ret++;
			}
		}

		return ret;
	}

	/**
	 * check which offset position the fx at this screen is
	 * @param screenOutput
	 * @return
	 */
	public int getOffsetForScreen(int screenOutput) {
		int ret=0;
		int fxInput = ioMapping.get(screenOutput).getVisualId();

		for (int i=0; i<screenOutput; i++) {
			if (ioMapping.get(i).getVisualId()==fxInput) {
				ret++;
			}
		}

		return ret;
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
	 * EFFECT ======================================================
	 */

	public Effect getEffect(EffectName name) {
		for (Effect fx: allEffects) {
			if (fx.getId() == name.getId()) {
				return fx;
			}
		}
		return null;
	}


	public List<Effect> getAllEffects() {
		return allEffects;
	}

	public Effect getEffect(int index) {
		for (Effect fx: allEffects) {
			if (fx.getId() == index) {
				return fx;
			}
		}
		return null;
	}

	public void addEffect(Effect effect) {
		allEffects.add(effect);
	}


	/*
	 * MIXER ======================================================
	 */

	public Mixer getMixer(MixerName name) {
		for (Mixer mix: allMixer) {
			if (mix.getId() == name.getId()) {
				return mix;
			}
		}
		return null;
	}

	public List<Mixer> getAllMixer() {
		return allMixer;
	}

	public Mixer getMixer(int index) {
		for (Mixer mix: allMixer) {
			if (mix.getId() == index) {
				return mix;
			}
		}
		return null;
	}

	public void addMixer(Mixer mixer) {
		allMixer.add(mixer);
	}


	/*
	 * GENERATOR ======================================================
	 */

	public Generator getGenerator(GeneratorName name) {
		for (Generator gen: allGenerators) {
			if (gen.getId() == name.getId()) {
				return gen;
			}
		}
		return null;
	}

	public List<Generator> getAllGenerators() {
		return allGenerators;
	}

	public Generator getGenerator(int index) {
		for (Generator gen: allGenerators) {
			if (gen.getId() == index) {
				return gen;
			}
		}
		return null;
	}

	/**
	 * how many screens 
	 * @return
	 */
	public int getAllInputsSize() {
		return allGenerators.size();
	}

	public void addInput(Generator input) {
		allGenerators.add(input);
	}


	/*
	 * OUTPUT ======================================================
	 */

	public List<Output> getAllOutputs() {
		return allOutputs;
	}

	public void addOutput(Output output) {
		System.out.println("regged: "+output.toString());
		allOutputs.add(output);
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
		return allVisuals.get(index);
	}

	public void setAllVisuals(List<Visual> allVisuals) {
		this.allVisuals = allVisuals;
	}

	/*
	 * OUTPUT MAPPING ======================================================
	 */
	public List<OutputMapping> getAllOutputMappings() {
		return ioMapping;
	}


	/* 
	 * FADER ======================================================
	 */

	/**
	 * return a NEW INSTANCE of a fader
	 * @param faderName
	 * @return
	 */
	public Fader getFader(FaderName faderName) {
		switch (faderName) {
		case CROSSFADE:
			return new Crossfader();
		case SWITCH:
			return new Switch();
		case SLIDE_UPSIDE_DOWN:
			return new SlideUpsideDown();
		case SLIDE_LEFT_RIGHT:
			return new SlideLeftRight();
		}
		return null;
	}

	public Fader getFader(int index) {
		switch (index) {
		case 0:
			return new Switch();
		case 1:
			return new Crossfader();
		case 2:
			return new SlideUpsideDown();
		case 3:
			return new SlideLeftRight();
		}
		return null;
	}

	public int getFaderCount() {
		return 4;
	}

}
