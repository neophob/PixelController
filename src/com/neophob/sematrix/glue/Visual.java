package com.neophob.sematrix.glue;

import com.neophob.sematrix.effect.Effect;
import com.neophob.sematrix.effect.Effect.EffectName;
import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.generator.Generator.GeneratorName;
import com.neophob.sematrix.mixer.Mixer;
import com.neophob.sematrix.mixer.Mixer.MixerName;

public class Visual {

	private Generator generator1;
	private Generator generator2;
	private Effect effect1;
	private Effect effect2;
	private Mixer mixer;

	public Visual(GeneratorName generatorName) {
		this.generator1 = Collector.getInstance().getGenerator(generatorName);
		this.generator2 = Collector.getInstance().getGenerator(GeneratorName.PASSTHRU);
		this.effect1 = Collector.getInstance().getEffect(EffectName.PASSTHRU);
		this.effect2 = Collector.getInstance().getEffect(EffectName.PASSTHRU);
		this.mixer = Collector.getInstance().getMixer(MixerName.PASSTHRU);

		Collector.getInstance().addVisual(this);
	}

	public int[] getBuffer() {
		return this.getMixerBuffer();
	}

	public boolean isVisualOnScreen(int screenNr) {
		int fxInput = Collector.getInstance().getFxInputForScreen(screenNr);
		if (fxInput == getGenerator1Idx()) {
			return true;
		}
		return false;
	}

	public Generator getGenerator1() {
		return generator1;
	}

	public int getGenerator1Idx() {
		return generator1.getId();
	}

	public void setGenerator1(Generator generator1) {
		this.generator1 = generator1;
	}

	public void setGenerator1(int index) {
		this.generator1 = Collector.getInstance().getGenerator(index);
	}

	public Generator getGenerator2() {
		return generator2;
	}

	public int getGenerator2Idx() {
		return generator2.getId();
	}

	public void setGenerator2(Generator generator2) {
		this.generator2 = generator2;
	}

	public void setGenerator2(int index) {
		this.generator2 = Collector.getInstance().getGenerator(index);
	}

	public Effect getEffect1() {
		return effect1;
	}

	public int getEffect1Idx() {
		return effect1.getId();
	}

	public int[] getEffect1Buffer() {
		return effect1.getBuffer(generator1.getBuffer());
	}

	public void setEffect1(Effect effect1) {
		this.effect1 = effect1;
	}

	public void setEffect1(int index) {
		this.effect1 = Collector.getInstance().getEffect(index);
	}

	public Effect getEffect2() {
		return effect2;
	}

	public int getEffect2Idx() {
		return effect2.getId();
	}

	public int[] getEffect2Buffer() {
		return effect2.getBuffer(generator2.getBuffer());
	}

	public void setEffect2(Effect effect2) {
		this.effect2 = effect2;
	}

	public void setEffect2(int index) {
		this.effect2 = Collector.getInstance().getEffect(index);
	}

	public Mixer getMixer() {
		return mixer;
	}

	public int[] getMixerBuffer() {
		return mixer.getBuffer(this);
	}

	public int getMixerIdx() {
		return mixer.getId();
	}

	public void setMixer(Mixer mixer1) {
		this.mixer = mixer1;
	}

	public void setMixer(int index) {
		this.mixer = Collector.getInstance().getMixer(index);
	}


}
