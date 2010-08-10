package com.neophob.sematrix.effect;


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

	public int[] getBuffer(int[] buffer) {
		int[] ret = new int[buffer.length];

		int x,y,offs=0,soffs;
		float tx,ty;
		
		float ca=(float)(scale*Math.cos(angle));//cosAng);
		float sa=(float)(scale*Math.sin(angle));//sinAng);

		float txx=0-(internalBufferXSize/2)*sa;
		float tyy=0+(internalBufferYSize/2)*ca;
		
		for (y=0;y<internalBufferYSize;y++) {
			txx-=sa;
			tyy+=ca;
			ty=tyy;
			tx=txx;
			for (x=0;x<internalBufferXSize;x++) {
				tx+=ca;
				ty+=sa;
				soffs=(int)tx+(int)(ty)*internalBufferXSize;
				ret[offs++]=buffer[soffs&(buffer.length-1)];
			}
		}

		return ret;
	}

	public void update() {
		angle+=0.02f;
		scale+=dscalee;
//		if (scale<0.53 || scale>3) {
		if (scale<0.13f || scale>1.6f) {
			dscalee*=-1;
		}		
	}


}
