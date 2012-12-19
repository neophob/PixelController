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
	 * do the actual roto zooming.
	 *
	 * @param scaleP the scale p
	 * @param angleP the angle p
	 * @param bufferP the buffer p
	 * @return the int[]
	 */
	protected int[] rotoZoom(float scaleP, float angleP, int bufferSrc[]) {
		int[] tmp = new int[internalBufferYSize*internalBufferXSize];
		int offs=0,soffs;
		float tx,ty;

        float ca=1;//(float)(scaleP*Math.cos(angleP));
        float sa=1;//(float)(scaleP*Math.sin(angleP));
		
		float txx=-15f;//.0f-(internalBufferXSize/2.0f)*sa;
		float tyy=15f;//.0f+(internalBufferYSize/2.0f)*ca;

		for (int y=0; y<internalBufferYSize; y++) {
		    
	        txx-=sa;
			tyy+=ca;
			System.out.println("txx: "+txx+", tyy="+tyy);
			
			ty=tyy;
			tx=txx;
			System.out.print(" tx: "+tx+", ty="+ty);
			for (int x=0; x<internalBufferXSize; x++) {
				tx+=ca;
				ty+=sa;				
				soffs = (int)(tx)+(int)(ty)*internalBufferXSize;
			    tmp[offs++] = bufferSrc[soffs&(bufferSrc.length-1)]&255;    
			}
			System.out.println("EOL");
		}

		System.out.println("internalBufferXSize: "+internalBufferXSize+", internalBufferYSize: "+internalBufferYSize+", tmp.length: "+tmp.length+" offs:"+offs);
		return tmp;
	}

}
