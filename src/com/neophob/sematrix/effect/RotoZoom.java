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
		float tx,ty,txx=0,tyy=0,ca,sa;
		
		ca=(float)(scale*Math.cos(angle));//cosAng);
		sa=(float)(scale*Math.sin(angle));//sinAng);

		for (y=0;y<generator.getInternalBufferYSize();y++) {
			txx+=-sa;
			tyy+=ca;
			ty=tyy;
			tx=txx;
			for (x=0;x<generator.getInternalBufferXSize();x++) {
				tx+=ca;
				ty+=sa;
				soffs=(int)(ty)*generator.getInternalBufferXSize();
				soffs+=(int)tx;
				
				int a = buffer[soffs&(buffer.length-1)];
				ret[offs++]=a;
			}
		}

		angle+=0.01f;
		scale+=dscalee;
//		if (scale<0.53 || scale>3) {
		if (scale<0.6 || scale>1.4) {
			dscalee*=-1;
		}
		
		return ret;
	}


}
