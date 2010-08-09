package com.neophob.sematrix.glue;

import java.util.List;

/**
 * simple class to store present set's
 * @author michu
 *
 */
public class PresentSettings {
	private List<String> present;

	public List<String> getPresent() {
		return present;
	}

	public void setPresent(List<String> present) {
		this.present = present;
	}
	
	public void setPresent(String[] present) {
		this.present.clear();
		for (String s: present) {
			this.present.add(s);
		}
	}
	
	public String getSettingsAsString() {		
		if (present==null) {
			return "";
		}
		
		String ret="";
		
		for (String s: present) {
			ret+=s+";";
		}
		return ret;
	}
	
}
