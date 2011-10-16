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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.neophob.sematrix.properties.ValidCommands;

/**
 * The Class PixelControllerShufflerSelect.
 */
public class PixelControllerShufflerSelect implements PixelControllerElement {

	/** The Constant SHUFFLER_OPTIONS. */
	private static final int SHUFFLER_OPTIONS = 15;

	
	/** fx to screen mapping. */
	private List<Boolean> shufflerSelect;

	/**
	 * Instantiates a new pixel controller shuffler select.
	 */
	public PixelControllerShufflerSelect() {
		shufflerSelect = new CopyOnWriteArrayList<Boolean>();
		for (int n=0; n<SHUFFLER_OPTIONS; n++) {
			shufflerSelect.add(true);
		}

	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.glue.PixelControllerElement#initAll()
	 */
	public void initAll() {
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.glue.PixelControllerElement#getCurrentState()
	 */
	public List<String> getCurrentState() {
		List<String> ret = new ArrayList<String>();
		
		ret.add(ValidCommands.CHANGE_SHUFFLER_SELECT+" "+getShufflerStatus());

		return ret;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.glue.PixelControllerElement#update()
	 */
	@Override
	public void update() {

	}

	/* 
	 * SHUFFLER OPTIONS ======================================================
	 */

	
	/**
	 * returns string for current status. the order is fix and
	 * defined by gui
	 *
	 * @return the shuffler status
	 */
	private String getShufflerStatus() {
	    StringBuffer sb=new StringBuffer();
		int value;
		
		for (int i=0; i<shufflerSelect.size(); i++) {
			value=0;
			if (shufflerSelect.get(i)) {
			    value=1;
			}
			sb.append(' ');
			sb.append(value);				
		}
		return sb.toString();
	}

	/**
	 * Gets the shuffler select.
	 *
	 * @return the shuffler select
	 */
	public List<Boolean> getShufflerSelect() {
		return shufflerSelect;
	}

	/**
	 * Gets the shuffler select.
	 *
	 * @param ofs the ofs
	 * @return the shuffler select
	 */
	public boolean getShufflerSelect(ShufflerOffset ofs) {
		return shufflerSelect.get(ofs.getOffset());
	}

	/**
	 * Sets the shuffler select.
	 *
	 * @param ofs the ofs
	 * @param value the value
	 */
	public void setShufflerSelect(int ofs, Boolean value) {
		this.shufflerSelect.set(ofs, value);
	}


}
