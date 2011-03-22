package com.neophob.sematrix.effect;

import com.neophob.sematrix.fader.CrossfaderHelper;


/**
 * 
 * @author michu
 * 
 * TODO: endless zoom, nachdem der zoom weit im bild ist, ein crossfade auf das ursprŸngliche bild
 * 
 * ripped from http://www.openprocessing.org/visuals/?visualID=8030
 */
public class RotoZoom extends Effect {
	
	public enum WORKMODE {
		PINGPONG,
		ZOOM
	}
	
	private float angle;
	private float scale, scale2;
	private float scaleOrig;
	private float faderPos;
	private float dscalee=0.03f;
	
	private WORKMODE workmode = WORKMODE.ZOOM;

	private int[] rotoZoomedBuffer=null;
	
	/**
	 * 
	 * @param scale
	 * @param angle
	 */
	public RotoZoom(float scale, float angle) {
		super(EffectName.ROTOZOOM);
		this.scale = scale;
		this.scaleOrig = scale;
		this.angle = angle;
		this.faderPos = 0.0f;		
	}


	
	/**
	 * 
	 */
	public int[] getBuffer(int[] buffer) {		
		//lazy init buffer
		if (this.rotoZoomedBuffer==null) {
			this.rotoZoomedBuffer = new int[buffer.length];				
		}
		
		this.rotoZoomedBuffer = rotoZoom(scale, angle, buffer);
		
		
		if (workmode == WORKMODE.ZOOM && faderPos>0.0f) {			
			return CrossfaderHelper.getBuffer(faderPos, this.rotoZoomedBuffer, rotoZoom(scale2, angle, buffer));
		}
		return this.rotoZoomedBuffer;
	}
	

	/**
	 * 
	 */
	public void update() {
		angle+=0.02f;
		scale-=dscalee;

		if (workmode == WORKMODE.ZOOM) {
			if (this.scale < 0.4f) {
				faderPos += 0.04f;
				scale2-=dscalee;
							
				if (faderPos>0.98f) {
					//finished fading - reset values
					this.faderPos = 0.0f;
					this.scale = this.scale2;
					this.scale2 = this.scaleOrig;
				}			
			}			
		} else {
			if (scale<0.13f || scale>1.6f) {
				dscalee*=-1;
			}			
		}

	}

	
	/**
	 * do the actual roto zooming
	 * 
	 * @param scaleP
	 * @param angleP
	 * @param buffer
	 */
	private int[] rotoZoom(float scaleP, float angleP, int bufferP[]) {
		int[] tmp = new int[bufferP.length];
		int x,y,offs=0,soffs;
		float tx,ty;
		
		float ca=(float)(scaleP*Math.cos(angleP));//cosAng);
		float sa=(float)(scaleP*Math.sin(angleP));//sinAng);

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
				tmp[offs++]=bufferP[soffs&(bufferP.length-1)];
			}
		}
		
		return tmp;
	}

}
