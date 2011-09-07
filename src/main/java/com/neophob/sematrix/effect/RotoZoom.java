/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
 *
 * This file is part of PixelController.
 *
 * PixelController is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PixelController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PixelController.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.neophob.sematrix.effect;

import java.util.Random;

import com.neophob.sematrix.fader.CrossfaderHelper;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.ShufflerOffset;
import com.neophob.sematrix.resize.Resize.ResizeName;


/**
 * The Class RotoZoom.
 *
 * @author michu
 * 
 * ripped from http://www.openprocessing.org/visuals/?visualID=8030
 */
public class RotoZoom extends Effect {

	//endless zooming or zoomin-zoomout...
	/**
	 * The Enum WORKMODE.
	 */
	public enum WORKMODE {
		
		/** The PINGPONG. */
		PINGPONG,
		
		/** The ZOOM. */
		ZOOM
	}

	/** The angle. */
	private float angle;
	
	/** The angle orig. */
	private int angleOrig;
	
	/** The angle diff. */
	private float angleDiff;
	
	/** The scale2. */
	private float scale, scale2;
	
	/** The scale orig. */
	private float scaleOrig;
	
	/** The fader pos. */
	private float faderPos;
	
	/** The dscalee. */
	private float dscalee=0.03f;

	/** The workmode. */
	private WORKMODE workmode = WORKMODE.ZOOM;

	/** The roto zoomed buffer. */
	private int[] rotoZoomedBuffer=null;

	/**
	 * Instantiates a new roto zoom.
	 *
	 * @param controller the controller
	 * @param scale the scale
	 * @param angle the angle
	 */
	public RotoZoom(PixelControllerEffect controller, float scale, float angle) {
		super(controller, EffectName.ROTOZOOM, ResizeName.QUALITY_RESIZE);
		this.scale = scale;
		this.scaleOrig = scale;
		this.angle = angle;
		this.faderPos = 0.0f;
		this.angleDiff = 0.02f;
	}

	/**
	 * Gets the angle.
	 *
	 * @return the angle
	 */
	public int getAngle() {
		return angleOrig;
	}

	/**
	 * Sets the angle.
	 *
	 * @param angle from -127 to 127
	 * @return the int
	 */
	public int setAngle(int angle) {
		if (angle > 127) angle = 127;
		if (angle < -127) angle = -127;
		
		this.angleOrig = angle;
		
		//137 sound funny - but correct
		//using 137 - the max value is 10 used for the diff!
		if (angle>0) {
			angle=137-angle;			
		} else {
			angle=-137-angle;
		}
		
		if (angle != 0) {
			float f = (1.0f / (float)angle)*2;
			this.angleDiff = f;	
		} else {
			this.angleDiff = 0.0f;
		}
		return angle;
	}

	
	/**
	 * Sets the zoom.
	 *
	 * @param zoom the zoom
	 * @return the int
	 */
	public int setZoom(int zoom) {
		return 0;
	}


	/* (non-Javadoc)
	 * @see com.neophob.sematrix.effect.Effect#getBuffer(int[])
	 */
	public int[] getBuffer(int[] buffer) {		
		this.rotoZoomedBuffer = rotoZoom(scale, angle, buffer);

		//the crossfade is used for the endless zoom option
		if (workmode == WORKMODE.ZOOM && faderPos>0.0f) {			
			return CrossfaderHelper.getBuffer(faderPos, this.rotoZoomedBuffer, rotoZoom(scale2, angle, buffer));
		}
		return this.rotoZoomedBuffer;
	}


	/* (non-Javadoc)
	 * @see com.neophob.sematrix.effect.Effect#update()
	 */
	public void update() {
		angle+=this.angleDiff;
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
			//WORKMODE.PINGPONG
			if (scale<0.13f || scale>1.6f) {
				dscalee*=-1;
			}			
		}

	}


	/**
	 * do the actual roto zooming.
	 *
	 * @param scaleP the scale p
	 * @param angleP the angle p
	 * @param bufferP the buffer p
	 * @return the int[]
	 */
	private int[] rotoZoom(float scaleP, float angleP, int bufferP[]) {
		int[] tmp = new int[bufferP.length];
		int x,y,offs=0,soffs;
		float tx,ty;

		float ca=(float)(scaleP*Math.cos(angleP));//cosAng);
		float sa=(float)(scaleP*Math.sin(angleP));//sinAng);

		float txx=0-(internalBufferXSize/2.0f)*sa;
		float tyy=0+(internalBufferYSize/2.0f)*ca;

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

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.effect.Effect#shuffle()
	 */
	@Override
	public void shuffle() {
		if (Collector.getInstance().getShufflerSelect(ShufflerOffset.ROTOZOOMER)) {
			int tmpAngle = (new Random().nextInt(255))-128;
			this.setAngle(tmpAngle);					
		}
	}
	
	
	

}
