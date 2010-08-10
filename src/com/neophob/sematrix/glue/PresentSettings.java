package com.neophob.sematrix.glue;

import java.util.ArrayList;
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
		List<String> list=new ArrayList<String>();
		for (String s: present) {
			list.add(s);
		}
		this.present=list;
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
