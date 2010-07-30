package com.neophob.sematrix.effect;

import com.neophob.sematrix.generator.Generator;

/**
 * 
 * @author michu
 * ripped from http://www.openprocessing.org/visuals/?visualID=8030
 */
public class RotoZoom extends Effect {

	private float angle;
	private float scale;
	private float dscalee=0.01f;
	
	public RotoZoom(float scale, float angle) {
		super(EffectName.ROTOZOOM);
		this.scale = scale;
		this.angle = angle;
	}

	public int[] getBuffer(Generator generator) {
		int[] buffer = generator.getBuffer();
		int[] ret = new int[buffer.length];

		int x,y,offs=0,soffs;
		float tx,ty;
		
		float ca=(float)(scale*Math.cos(angle));//cosAng);
		float sa=(float)(scale*Math.sin(angle));//sinAng);

		float txx=0-(generator.getInternalBufferYSize()/2)*sa;
		float tyy=0+(generator.getInternalBufferYSize()/2)*ca;
		
		for (y=0;y<generator.getInternalBufferYSize();y++) {
			txx-=sa;
			tyy+=ca;
			ty=tyy;
			tx=txx;
			for (x=0;x<generator.getInternalBufferXSize();x++) {
				tx+=ca;
				ty+=sa;
				soffs=(int)tx+(int)(ty)*generator.getInternalBufferXSize();
				ret[offs++]=buffer[soffs&(buffer.length-1)];
			}
		}

		angle+=0.01f;
		scale+=dscalee;
//		if (scale<0.53 || scale>3) {
		if (scale<0.13f || scale>1.6f) {
			dscalee*=-1;
		}
		
		return ret;
	}


}
