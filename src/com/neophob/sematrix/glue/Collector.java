package com.neophob.sematrix.glue;

import java.net.BindException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang.StringUtils;

import processing.core.PApplet;

import com.neophob.sematrix.effect.BeatHorizShift;
import com.neophob.sematrix.effect.BeatVerticalShift;
import com.neophob.sematrix.effect.Effect;
import com.neophob.sematrix.effect.Effect.EffectName;
import com.neophob.sematrix.effect.Emboss;
import com.neophob.sematrix.effect.Inverter;
import com.neophob.sematrix.effect.PassThru;
import com.neophob.sematrix.effect.RotoZoom;
import com.neophob.sematrix.effect.Threshold;
import com.neophob.sematrix.effect.Tint;
import com.neophob.sematrix.effect.Voluminize;
import com.neophob.sematrix.fader.Crossfader;
import com.neophob.sematrix.fader.Fader;
import com.neophob.sematrix.fader.Fader.FaderName;
import com.neophob.sematrix.fader.SlideLeftRight;
import com.neophob.sematrix.fader.SlideUpsideDown;
import com.neophob.sematrix.fader.Switch;
import com.neophob.sematrix.generator.Blinkenlights;
import com.neophob.sematrix.generator.Cell;
import com.neophob.sematrix.generator.Fire;
import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.generator.Generator.GeneratorName;
import com.neophob.sematrix.generator.Image;
import com.neophob.sematrix.generator.ImageZoomer;
import com.neophob.sematrix.generator.Metaballs;
import com.neophob.sematrix.generator.PassThruGen;
import com.neophob.sematrix.generator.PixelImage;
import com.neophob.sematrix.generator.Plasma2;
import com.neophob.sematrix.generator.SimpleColors;
import com.neophob.sematrix.generator.TextureDeformation;
import com.neophob.sematrix.generator.Textwriter;
import com.neophob.sematrix.input.Sound;
import com.neophob.sematrix.input.SoundMinim;
import com.neophob.sematrix.listener.MessageProcessor;
import com.neophob.sematrix.listener.MessageProcessor.ValidCommands;
import com.neophob.sematrix.listener.TcpServer;
import com.neophob.sematrix.mixer.AddSat;
import com.neophob.sematrix.mixer.Checkbox;
import com.neophob.sematrix.mixer.MinusHalf;
import com.neophob.sematrix.mixer.Mix;
import com.neophob.sematrix.mixer.Mixer;
import com.neophob.sematrix.mixer.Mixer.MixerName;
import com.neophob.sematrix.mixer.Multiply;
import com.neophob.sematrix.mixer.NegativeMultiply;
import com.neophob.sematrix.mixer.PassThruMixer;
import com.neophob.sematrix.mixer.Voluminizer;
import com.neophob.sematrix.mixer.Xor;
import com.neophob.sematrix.output.Output;
import com.neophob.sematrix.properties.PropertiesHelper;
import com.neophob.sematrix.resize.PixelResize;
import com.neophob.sematrix.resize.QualityResize;
import com.neophob.sematrix.resize.Resize;
import com.neophob.sematrix.resize.Resize.ResizeName;

public class Collector {

	/** 
	 * TODO make dynamic
	 * nr of shuffler entries. enable/disable option for random mode 
	 * 
	 */
	
	private static final int SHUFFLER_OPTIONS = 14;
	
	public static final int NR_OF_PRESENT_SLOTS = 128;
	
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
	private List<Resize> allResizers;
	private List<Boolean> shufflerSelect;

	/** fx to screen mapping */
	private List<OutputMapping> ioMapping;

	private int nrOfScreens;
	private int fps;
	private int frames;
	
	/** present settings */
	private int selectedPresent;
	private List<PresentSettings> present;
	//track r/g/b
	private Tint tint;
	private int rotoZoomAngle=0;
	
	private Blinkenlights blinkenlights;
	private Image image;
	private ImageZoomer imageZoomer;
	
	private TextureDeformation textureDeformation;
	
	private Textwriter textwriter;
	
	private Threshold thresold;
	
	private TcpServer pdSrv;
	
	private boolean isLoadingPresent=false;
	
	//TODO maybe a map instead of a list wozld be better...
	private Collector() {
		allOutputs = new CopyOnWriteArrayList<Output>();

		allGenerators = new CopyOnWriteArrayList<Generator>();			
		allEffects = new CopyOnWriteArrayList<Effect>();
		allMixer = new CopyOnWriteArrayList<Mixer>();

		allVisuals = new CopyOnWriteArrayList<Visual>();
		allResizers = new CopyOnWriteArrayList<Resize>();

		this.nrOfScreens = 0;
		ioMapping = new CopyOnWriteArrayList<OutputMapping>();
		init=false;

		selectedPresent=0;
		present = new CopyOnWriteArrayList<PresentSettings>();
		for (int n=0; n<NR_OF_PRESENT_SLOTS; n++) {
			present.add(new PresentSettings());
		}

		shufflerSelect = new CopyOnWriteArrayList<Boolean>();
		for (int n=0; n<SHUFFLER_OPTIONS; n++) {
			shufflerSelect.add(true);
		}

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
		String fileBlinken = PropertiesHelper.getInstance().getProperty("initial.blinken");
		blinkenlights = new Blinkenlights(fileBlinken);
		String fileImageSimple = PropertiesHelper.getInstance().getProperty("initial.image.simple");
		image = new Image(fileImageSimple);		
		new Plasma2();
		new SimpleColors();
		new Fire();
		new PassThruGen();
		new Metaballs();
		new PixelImage();
		String fileTextureDeformation = PropertiesHelper.getInstance().getProperty("initial.texture");
		textureDeformation = new TextureDeformation(fileTextureDeformation);
		String text = PropertiesHelper.getInstance().getProperty("initial.text");
		textwriter = new Textwriter(
				PropertiesHelper.getInstance().getProperty("font.filename"), 
				Integer.parseInt(PropertiesHelper.getInstance().getProperty("font.size")),
				text
		);
		String fileImageZoomer = PropertiesHelper.getInstance().getProperty("initial.image.zoomer");
		imageZoomer = new ImageZoomer(fileImageZoomer);
		new Cell();
		
		//create effects
		new Inverter();
		new PassThru();
		new RotoZoom(1.5f, 2.3f);
		new BeatVerticalShift();
		new BeatHorizShift();
		new Voluminize();
		tint = new Tint();
		thresold = new Threshold();
		new Emboss();

		//create mixer
		new AddSat();
		new Multiply();
		new Mix();
		new PassThruMixer();
		new NegativeMultiply();
		new Checkbox();
		new Voluminizer();
		new Xor();
		new MinusHalf();

		//create 5 visuals
		for (int n=0; n<nrOfScreens+1; n++) {
			switch (n%5) {
			case 0:
				new Visual(GeneratorName.BLINKENLIGHTS);
				break;
			case 1:
				new Visual(GeneratorName.METABALLS);
				break;
			case 2:
				new Visual(GeneratorName.SIMPLECOLORS);
				break;
			case 3:
				new Visual(GeneratorName.PLASMA);
				break;
			case 4:
				new Visual(GeneratorName.IMAGE);
				break;
			case 5:
				new Visual(GeneratorName.FIRE);
				break;
			}
		}
		
		//create resizer
		new PixelResize();
		new QualityResize();
		
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
		if (isLoadingPresent()) return;
		
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
		for (Generator m: allGenerators) {
			for (int i=0; i<u; i++) {
				m.update();	
			}			
		}
		for (Effect e: allEffects) {
			e.update();
		}
		for (Output o: allOutputs) {
			o.update();
		}
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
		ret.add(ValidCommands.CHANGE_TINT+" "+tint.getR()+" "+tint.getG()+" "+tint.getB());
		ret.add(ValidCommands.CHANGE_ROTOZOOM+" "+rotoZoomAngle);
		ret.add(ValidCommands.BLINKEN+" "+blinkenlights.getFilename());
		ret.add(ValidCommands.IMAGE+" "+image.getFilename());
		ret.add(ValidCommands.IMAGE_ZOOMER+" "+imageZoomer.getFilename());
		ret.add(ValidCommands.CHANGE_SHUFFLER_SELECT+" "+getShufflerStatus());
		ret.add(ValidCommands.TEXTDEF_FILE+" "+textureDeformation.getFilename());
		ret.add(ValidCommands.TEXTDEF+" "+textureDeformation.getLut());
		ret.add(ValidCommands.TEXTWR+" "+textwriter.getText());
		ret.add(ValidCommands.CHANGE_PRESENT +" "+selectedPresent);
		ret.add(ValidCommands.CHANGE_THRESHOLD_VALUE +" "+thresold.getThreshold());
		ret.add(ValidCommands.CHANGE_OUTPUT+" "+output);
		ret.add(ValidCommands.CHANGE_OUTPUT_EFFECT+" "+outputEffect);
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
		if (index<allVisuals.size()) {
			return allVisuals.get(index);			
		} 
		return allVisuals.get(0);
	}

	public void setAllVisuals(List<Visual> allVisuals) {
		this.allVisuals = allVisuals;
	}

	/*
	 * MIXER ======================================================
	 */
	
	public List<Resize> getAllResizers() {
		return allResizers;
	}
	
	public Resize getResize(ResizeName name) {
		for (Resize r: allResizers) {
			if (r.getId() == name.getId()) {
				return r;
			}
		}
		return null;
	}
	
	public void addResize(Resize resize) {
		allResizers.add(resize);
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

	/* 
	 * PRESENT ======================================================
	 */
	
	public int getRotoZoomAngle() {
		return rotoZoomAngle;
	}

	public void setRotoZoomAngle(int rotoZoomAngle) {
		this.rotoZoomAngle = rotoZoomAngle;
	}

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

	public void setRGB(int r, int g, int b) {
		tint.setColor(r, g, b);
	}

	public int getR() {
		return tint.getR();
	}

	public int getG() {
		return tint.getG();
	}

	public int getB() {
		return tint.getB();
	}
	
	public String getFileBlinken() {
		return blinkenlights.getFilename();
	}

	public void setFileBlinken(String fileBlinken) {
		blinkenlights.loadFile(fileBlinken);
	}
	
	public String getFileImageSimple() {
		return image.getFilename();
	}

	public void setFileImageSimple(String fileImageSimple) {
		image.loadFile(fileImageSimple);

	}
	

	public String getFileImageZoomer() {
		return imageZoomer.getFilename();
	}

	public void setFileImageZoomer(String fileImageZoomer) {
		imageZoomer.loadImage(fileImageZoomer);
	}

	public String getFileTextureDeformation() {
		return textureDeformation.getFilename();
	}

	public void setFileTextureDeformation(String fileTextureDeformation) {
		textureDeformation.loadFile(fileTextureDeformation);
	}

	public int getTextureDeformationLut() {
		return textureDeformation.getLut();
	}

	public void setTextureDeformationLut(int textureDeformationLut) {
		textureDeformation.changeLUT(textureDeformationLut);
	}

	public String getText() {
		return textwriter.getText();
	}

	public void setText(String text) {
		textwriter.createTextImage(text);
	}

		
	/* 
	 * SHUFFLER OPTIONS ======================================================
	 */

	
	/**
	 * returns string for current status. the order is fix and
	 * defined by gui
	 */
	private String getShufflerStatus() {
		String s="";
		int value;
		
		for (int i=0; i<shufflerSelect.size(); i++) {
			value=0;
			if (shufflerSelect.get(i)) value=1;
			s+=" "+value;			
		}
		return s;
	}

	public List<Boolean> getShufflerSelect() {
		return shufflerSelect;
	}

	public boolean getShufflerSelect(ShufflerOffset ofs) {
		return shufflerSelect.get(ofs.getOffset());
	}

	public void setShufflerSelect(int ofs, Boolean value) {
		this.shufflerSelect.set(ofs, value);
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
	

	public void setThresholdValue(int thresholdValue) {
		thresold.setThreshold(thresholdValue);
	}

	//TODO static is NOT sexy!
	public int getFaderCount() {
		return 4;
	}

	public synchronized boolean isLoadingPresent() {
		return isLoadingPresent;
	}

	public synchronized void setLoadingPresent(boolean isLoadingPresent) {
		this.isLoadingPresent = isLoadingPresent;
	}
	
	

}
