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
