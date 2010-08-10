package com.neophob.sematrix.glue;

import com.neophob.sematrix.effect.Effect;
import com.neophob.sematrix.effect.Effect.EffectName;
import com.neophob.sematrix.fader.Fader;
import com.neophob.sematrix.fader.Fader.FaderName;

/**
 * class used to store input/ouput mapping
 * @author michu
 *
 */
public class OutputMapping {

	/**
	 * the visual input object
	 */
	private int visualId;
	
	/**
	 * the output screen nr
	 */
	private int screenNr;
	private Fader fader;
	private Effect effect;
	
	public OutputMapping() {
		this.visualId = 0;
		this.screenNr = 0;
		this.fader = Collector.getInstance().getFader(FaderName.SWITCH);
		this.effect = Collector.getInstance().getEffect(EffectName.PASSTHRU);
	}

	public OutputMapping(int visualId, int screenNr) {
		this();
		this.visualId = visualId;
		this.screenNr = screenNr;
	}

	public int getVisualId() {
		return visualId;
	}

	public void setVisualId(int visualId) {
		this.visualId = visualId;
	}

	public int getScreenNr() {
		return screenNr;
	}

	public void setScreenNr(int screenNr) {
		this.screenNr = screenNr;
	}
	
	public Effect getEffect() {
		return effect;
	}

	public void setEffect(Effect effect) {
		this.effect = effect;
	}

	public Fader getFader() {
		return fader;
	}

	public void setFader(Fader fader) {
		this.fader = fader;
	}
	
}
