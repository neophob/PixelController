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

package com.neophob.sematrix.generator;

import java.security.InvalidParameterException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PConstants;
import processing.core.PImage;

import com.neophob.sematrix.effect.BoxFilter;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.ShufflerOffset;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * The Class TextureDeformation.
 *
 * @author michu
 */
public class TextureDeformation extends Generator {	

	/** The log. */
	private static Logger log = Logger.getLogger(TextureDeformation.class.getName());

	/** The h. */
	private int w, h;
	
	/** The m lut. */
	private int[] mLUT;
	
	/** The tmp array. */
	private int[] tmpArray;
	
	/** The texture img. */
	private PImage textureImg;
	
	/** The time displacement. */
	private int timeDisplacement;
	
	/** The lut. */
	private int lut;
	
	/** The filename. */
	private String filename;

	/**
	 * Instantiates a new texture deformation.
	 *
	 * @param controller the controller
	 * @param filename the filename
	 */
	public TextureDeformation(PixelControllerGenerator controller, String filename) {
		super(controller, GeneratorName.TEXTURE_DEFORMATION, ResizeName.PIXEL_RESIZE);
		w = getInternalBufferXSize();
		h = getInternalBufferYSize();
		mLUT =  new int[3 * w * h];
		tmpArray = new int[this.internalBuffer.length];
		// use higher resolution textures if things get to pixelated

		this.lut=9;
		loadFile(filename);
	}

	/**
	 * Change lut.
	 *
	 * @param lut the lut
	 */
	public void changeLUT(int lut) {
		this.lut = lut;
		createLUT(lut);
	}
	
	/**
	 * Load file.
	 *
	 * @param fileName the file name
	 */
	public void loadFile(String fileName) {
		this.filename = fileName;
		try {
			PImage tmpImage = Collector.getInstance().getPapplet().loadImage(Image.PREFIX+fileName);
			if (tmpImage==null || tmpImage.height<2) {
				throw new InvalidParameterException("invalid data");
			}
			textureImg = tmpImage;
			log.log(Level.INFO,
					"Loaded texture {0} ", new Object[] { fileName });
			createLUT(lut);
		} catch (Exception e) {
			log.log(Level.WARNING,
					"Failed to load texture {0}!", new Object[] { fileName });
		}
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.generator.Generator#update()
	 */
	@Override
	public void update() {
		textureImg.loadPixels();
		for (int pixelCount = 0; pixelCount < getInternalBufferSize(); pixelCount++)
		{
			int o = (pixelCount << 1) + pixelCount;  // equivalent to 3 * pixelCount
			int u = mLUT[o+0] + timeDisplacement;    // to look like its animating, add timeDisplacement
			int v = mLUT[o+1] + timeDisplacement;
			int adjustBrightness = mLUT[o+2];

			// get the R,G,B values from texture
			int currentPixel = textureImg.pixels[textureImg.width * (v & textureImg.height-1) + (u & textureImg.width-1)];

			// only apply brightness if it was calculated
			if (adjustBrightness != 0) {       
				int r,g,b;

				// disassemble pixel using bit mask to remove color components for greater speed
				r = currentPixel >> 16 & 0xFF;  
				g = currentPixel >> 8 & 0xFF;   
				b = currentPixel & 0xFF;              
		
				// make darker or brighter
				r += adjustBrightness;
				g += adjustBrightness;
				b += adjustBrightness;
		
				// constrain RGB to make sure they are within 0-255 color range
				r = constrain(r,0,255);
				g = constrain(g,0,255);
				b = constrain(b,0,255);
		
				// reassemble colors back into pixel
				currentPixel = (r << 16) | (g << 8) | (b);
			}

			// put texture pixel on buffer screen
			tmpArray[pixelCount] = currentPixel;
		}
		textureImg.updatePixels();
		this.internalBuffer = BoxFilter.applyBoxFilter(0, 1, tmpArray, getInternalBufferXSize());
		timeDisplacement++;
	}

	/**
	 * Constrain.
	 *
	 * @param amt the amt
	 * @param low the low
	 * @param high the high
	 * @return the int
	 */
	public static final int constrain(int amt, int low, int high) {
		return (amt < low) ? low : ((amt > high) ? high : amt);
	}

	/**
	 * Constrain.
	 *
	 * @param amt the amt
	 * @param low the low
	 * @param high the high
	 * @return the float
	 */
	public static final float constrain(float amt, float low, float high) {
		return (amt < low) ? low : ((amt > high) ? high : amt);
	}
	
	/**
	 * Creates the lut.
	 *
	 * @param effectStyle the effect style
	 */
	private void createLUT(int effectStyle){
		// increment placeholder
		int k = 0;

		// u and v are euclidean coordinates  
		float u,v,bright = 0; 

		for( int j=0; j < h; j++ )
		{
			float y = -1.00f + 2.00f*(float)j/(float)h;
			for( int i=0; i < w; i++ )
			{
				float x = -1.00f + 2.00f*(float)i/(float)w;
				float d = (float)Math.sqrt( x*x + y*y );
				float a = (float)Math.atan2( y, x );
				float r = d;
				switch(effectStyle) {
				case 1:   // stereographic projection / anamorphosis 
					u = (float)Math.cos( a )/d;
					v = (float)Math.sin( a )/d;
					bright = -10 * (2/(6*r + 3*x));
					break;
				case 2:  // hypnotic rainbow spiral
					v = (float)Math.sin(a+(float)Math.cos(3*r))/(float)(Math.pow(r,.2));
					u = (float)Math.cos(a+(float)Math.cos(3*r))/(float)(Math.pow(r,.2));
					bright = 1;
					break;
				case 3:  // rotating tunnel of wonder
					v = 2/(6*r + 3*x);
					u = a*3/PConstants.PI;
					bright = 15 * -v;
					break;
				case 4:  // wavy star-burst
					v = (-0.4f/r)+.1f*(float)Math.sin(8*a);
					u = .5f + .5f*a/PConstants.PI;
					bright=0;
					break;
				case 5:  // hyper-space travel
					u = (0.02f*y+0.03f)*(float)Math.cos(a*3)/r;
					v = (0.02f*y+0.03f)*(float)Math.sin(a*3)/r;
					bright=0;
					break;
				case 6:  // five point magnetic flare
					u = 1f/(r+0.5f+0.5f*(float)Math.sin(5*a));
					v = a*3/PConstants.PI;
					bright = 0;
					break;
				case 7:  // cloud like dream scroll
					u = 0.1f*x/(0.11f+r*0.5f);
					v = 0.1f*y/(0.11f+r*0.5f);
					bright=0;
					break;
				case 8:  // floor and ceiling with fade to dark horizon
					u = x/(float)Math.abs(y);
					v = 1/(float)Math.abs(y);
					bright = 10* -v;
					break;
				case 9:  // hot magma liquid swirl
					u = 0.5f*(a)/PConstants.PI;
					v = (float)Math.sin(2*r);
					bright = 0;
					break;
				case 10:  // clockwise flush down the toilet
					v = (float)Math.pow(r,0.1);
					u = (1*a/PConstants.PI)+r;
					bright=0;
					break;
				case 11:  // 3D ball
					v = x*(3-(float)Math.sqrt(4-5*r*r))/(r*r+1);
					u = y*(3-(float)Math.sqrt(4-5*r*r))/(r*r+1);
					bright = 7f * -18.7f*(x+y+r*r-(x+y-1)*(float)Math.sqrt(4-5*r*r)/3)/(r*r+1);
					break;
				default:  // show texture with no deformation or lighting
					u = x;
					v = y;
					bright = 0;
					break;
				}
				mLUT[k++] = (int)(textureImg.width*u) & textureImg.width-1;
				mLUT[k++] = (int)(textureImg.height*v) & textureImg.height-1;
				mLUT[k++] = (int)(bright);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.generator.Generator#shuffle()
	 */
	@Override
	public void shuffle() {
		if (Collector.getInstance().getShufflerSelect(ShufflerOffset.TEXTURE_DEFORMATION)) {
			Random rand = new Random();
			this.changeLUT(rand.nextInt(12));

			//TODO should be dynamic someday
			String files[] = new String[] {"1316.jpg", "ceiling.jpg", "circle.jpg", "gradient.jpg", "check.jpg"};
			int nr = rand.nextInt(files.length);
			loadFile(files[nr]);		
		}
	}

	/**
	 * Gets the filename.
	 *
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	

	/**
	 * Gets the lut.
	 *
	 * @return the lut
	 */
	public int getLut() {
		return lut;
	}

}
