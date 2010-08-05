package com.neophob.sematrix.effect;


public class Inverter extends Effect {

	public Inverter() {
		super(EffectName.INVERTER);
	}

	public int[] getBuffer(int[] buffer) {
		int[] ret = new int[buffer.length];
		
		short cr,cg,cb;//,ca;
		int col;

		for (int i=0; i<buffer.length; i++){
			col = buffer[i];
    		//ca=(short) (255-((col>>24)&255));
    		cr=(short) (255-((col>>16)&255));
    		cg=(short) (255-((col>>8)&255));
    		cb=(short) (255-( col&255));
    		
    		ret[i]= /*(ca << 24) | */(cr << 16) | (cg << 8) | cb;
		}
		return ret;
	}
	

}
