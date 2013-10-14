/**
 * Copyright (C) 2011-2013 Michael Vogt <michu@neophob.com>
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

import com.neophob.sematrix.resize.Resize.ResizeName;


/**
 * The Class RotoZoom.
 *
 * @author michu
 * 
 * ripped from http://www.openprocessing.org/visuals/?visualID=8030
 */
public abstract class RotoZoomEffect extends Effect {

	/**
	 * Instantiates a new effect.
	 *
	 * @param controller the controller
	 * @param effectName the effect name
	 * @param resizeOption the resize option
	 */
	public RotoZoomEffect(PixelControllerEffect controller, EffectName effectName, ResizeName resizeOption) {
		super(controller, effectName, resizeOption);
	}
	
	/**
	 * 
	 * @param scale
	 * @param angleP
	 * @param bufferSrc
	 * @return
	 */
	protected int[] rotoZoom(float scaleP, float angleP, int bufferSrc[]) {
		int[] tmp = new int[bufferSrc.length];
		int offs=0,soffs;
		float tx,ty;

        float ca=(float)(scaleP*Math.cos(angleP));
        float sa=(float)(scaleP*Math.sin(angleP));
		
		float txx=0-(internalBufferXSize/2.0f)*sa;
		float tyy=0+(internalBufferYSize/2.0f)*ca;

		for (int y=0; y<internalBufferYSize; y++) {
		    
	        txx-=sa;
			tyy+=ca;
			
			ty=tyy;
			tx=txx;
			for (int x=0; x<internalBufferXSize; x++) {
				tx+=ca;
				ty+=sa;				
				soffs = (int)(tx)+(int)(ty)*internalBufferXSize;
			    tmp[offs++] = bufferSrc[soffs&(bufferSrc.length-1)];    
			}
		}

		return tmp;
	}

	/**
	 * 
	 * @param scaleX
	 * @param scaleY
	 * @param bufferSrc
	 * @return
	 */
	protected int[] zoom(float scaleX, float scaleY, int bufferSrc[]) {
		int[] tmp = new int[bufferSrc.length];
		int offs=0,soffs;
		float tx=0,ty;
		
		float dx = ((float)internalBufferXSize/scaleX)/internalBufferXSize;
		float txStart = internalBufferXSize*dx/2;
//		if (dx>1.0f) txStart=0;
//System.out.println("dx:"+dx+", txStart:"+txStart);
		
		float dy = ((float)internalBufferYSize/scaleY)/internalBufferYSize;
		ty = internalBufferYSize*dy/2;
//		if (dy>1.0f) ty=0;
		ty=0;txStart=0;
		
		for (int y=0; y<internalBufferYSize; y++) {
			tx=txStart;
			for (int x=0; x<internalBufferXSize; x++) {												
				soffs = (int)(tx)+(int)(ty)*internalBufferXSize;
			    tmp[offs++] = bufferSrc[soffs&(bufferSrc.length-1)];
			    tx+=dx;
			}
			ty+=dy;
		}

		return tmp;
	}
	


}
