package com.neophob.sematrix.core.sound;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * define how the generators should react on the beats
 * @author michu
 *
 */
public enum BeatToAnimation {

	LINEAR(0),
	
	MODERATE(1),
	
	HEAVY(2)
	
	;
	
	/** The id. */
	private int id;

	BeatToAnimation(int id) {
		this.id = id;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * 
	 * @return
	 */
	public String guiText() {
		return WordUtils.capitalizeFully(StringUtils.replace(this.name(), "_", " "));		
	}

}
