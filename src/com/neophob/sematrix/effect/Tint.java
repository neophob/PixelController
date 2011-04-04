package com.neophob.sematrix.effect;

import com.neophob.sematrix.resize.Resize.ResizeName;


public class Tint extends Effect {

	private int r,g,b;
	
	public Tint() {
		super(EffectName.TINT, ResizeName.QUALITY_RESIZE);
		r=255;
		g=255;
		b=255;
	}

	/**
	 * update tint color
	 * @param r
	 * @param g
	 * @param b
	 */
	public void setColor(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	/**
	 * 
	 */
	public int[] getBuffer(int[] buffer) {
		int[] ret = new int[buffer.length];
		
		short cr,cg,cb;
		int col;

		for (int i=0; i<buffer.length; i++){
			col = buffer[i];
    		cr = (short) ((col>>16)&255);
    		cr = (short)(cr*r/255);
    		cg = (short) ((col>>8)&255);
    		cg = (short)(cg*g/255);
    		cb = (short) (col&255);
    		cb = (short)(cb*b/255);
    		
    		ret[i]= (cr << 16) | (cg << 8) | cb;
		}
		return ret;

	}
	

}
