/**
 * Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.gui.callback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * this class track which gui elements needs an update
 * 
 * @author michu
 *
 */
public class GuiState {

	private Map<String, String> state;
	private Map<String, String> diff;

	public GuiState() {
		state = new HashMap<String, String>();
		diff = new HashMap<String, String>();
	}
	
	/**
	 * convert a list of string to a string string map 
	 * @param o
	 * @return
	 */
	public void updateState(List<?> o) {
		Map<String, String> newState = new HashMap<String, String>();
		newState.putAll(state);
		
		for (Object obj: o) {
			String s = (String)obj;			
			String[] tmp = s.split(" ");
			if (tmp.length>1) {
				newState.put(tmp[0], tmp[1]);	
			} else {
				newState.put(tmp[0], "");
			}
		}
		
		diff = getDifference(newState);
		state = newState;
	}

	
	/**
	 * 
	 * @param currentState
	 * @param newState
	 * @return
	 */
	private Map<String, String> getDifference(Map<String, String> newState) {
		Map<String, String> ret = new HashMap<String, String>();
		
		for (Map.Entry<String, String> e: newState.entrySet()) {			
			if (state.containsKey(e.getKey())) {
				String currentValue = state.get(e.getKey());
				if (!StringUtils.equals(e.getValue(), currentValue)) {
					//value differs, add it
					ret.put(e.getKey(), e.getValue());					
				}				
			} else {
				//new value was not part of the old state - add it
				ret.put(e.getKey(), e.getValue());
			}
		}
		
		return ret;
	}

	/**
	 * @return the state
	 */
	public Map<String, String> getState() {
		return state;
	}

	/**
	 * @return the diff
	 */
	public Map<String, String> getDiff() {
		return diff;
	}

}
