package com.neophob.sematrix.effect;


public class Threshold extends Effect {

	private short threshold;
	
	public Threshold() {
		super(EffectName.THRESHOLD);
		this.threshold = 128;
	}

	public int[] getBuffer(int[] buffer) {
		int[] ret = new int[buffer.length];
		
		short cr,cg,cb;
		int col;

		for (int i=0; i<buffer.length; i++){
			col = buffer[i];
    		cr=(short) ((col>>16)&255);
    		cg=(short) ((col>>8)&255);
    		cb=(short) ( col&255);
    		
    		if (cr<this.threshold) cr=0; else cr=255;
    		if (cg<this.threshold) cg=0; else cg=255;
    		if (cb<this.threshold) cb=0; else cb=255;
    		
    		ret[i]= (cr << 16) | (cg << 8) | cb;
		}
		return ret;
	}
	
	public void setThreshold(int threshold) {
		this.threshold = (short)threshold;
	}
	

}
