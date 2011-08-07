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

import com.neophob.sematrix.listener.MessageProcessor.ValidCommands;

public class PixelControllerShufflerSelect implements PixelControllerElement {

	/**
	 * 
	 */
	private static final int SHUFFLER_OPTIONS = 14;

	
	/** fx to screen mapping */
	private List<Boolean> shufflerSelect;

	public PixelControllerShufflerSelect() {
		shufflerSelect = new CopyOnWriteArrayList<Boolean>();
		for (int n=0; n<SHUFFLER_OPTIONS; n++) {
			shufflerSelect.add(true);
		}

	}
	
	/**
	 * 
	 */
	public void initAll() {
	}
	
	/**
	 * 
	 */
	public List<String> getCurrentState() {
		List<String> ret = new ArrayList<String>();
		
		ret.add(ValidCommands.CHANGE_SHUFFLER_SELECT+" "+getShufflerStatus());

		return ret;
	}

	@Override
	public void update() {

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


}
