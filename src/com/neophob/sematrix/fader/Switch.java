package com.neophob.sematrix.fader;


public class Switch extends Fader {

	public Switch() {
		super(FaderName.SWITCH, 0);
	}
	
	@Override
	public int[] getBuffer(int[] buffer) {
		return buffer;
	}
	
	@Override
	public boolean isDone() {
		return true;
	}

}
