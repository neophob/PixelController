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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.neophob.sematrix.input.Sound;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * create some drops
 * 
 * todo add more geometrics forms (ellipse, rectangle...)
 * 
 * @author michu
 */
public class Geometrics extends Generator {

	/** The Constant LOG. */
	//private static final Logger LOG = Logger.getLogger(Geometrics.class.getName());

	/** The Constant THICKNESS. */
	private static final int THICKNESS = 10;

	/** The drops. */
	private List<Drop> drops;

	/** The tmp. */
	private List<Drop> tmp;

	/** The drop hue. */
	//private int dropHue = 0;

	/** The sound. */
	private Sound sound;

	/** The rnd gen. */
	private Random rndGen=new Random();

	/**
	 * Instantiates a new geometrics.
	 *
	 * @param controller the controller
	 */
	public Geometrics(PixelControllerGenerator controller) {
		super(controller, GeneratorName.DROPS, ResizeName.QUALITY_RESIZE);
		//drops = new CopyOnWriteArrayList<Drop>();
		drops = new ArrayList<Drop>();
		tmp = new ArrayList<Drop>();
		sound = Sound.getInstance();
	}

	/**
	 * Random.
	 *
	 * @param min the min
	 * @param max the max
	 * @return the int
	 */
	private int random(int min, int max) {
		int ret = rndGen.nextInt(max-min);
		return ret+min;
	}


	/* (non-Javadoc)
	 * @see com.neophob.sematrix.generator.Generator#update()
	 */
	@Override
	public void update() {
		Arrays.fill(this.internalBuffer, 0);
		//maximal 4 active drops
		if ( (sound.isHat() || sound.isKick() || drops.size()==0) && drops.size()<5) {
			
			drops.add(
					new Drop(
							random(THICKNESS, internalBufferXSize), 
							random(THICKNESS, internalBufferYSize), random(0,255))
					);
		}

		tmp.clear();
		for (Drop d: drops) {
			d.update();
			if (d.done()) {
				tmp.add(d);
			}
		}

		//remove drops that are updated
		if (tmp.size()>0) {
			drops.removeAll(tmp);			
		}
	}



	/**
	 * Class for Raindrops effect.
	 *
	 * @author michu
	 */
	private final class Drop {

		/** The drop size. */
		int xpos, ypos, dropcolor, dropSize;

		/** The finished. */
		boolean finished;

		/**
		 * Instantiates a new drop.
		 *
		 * @param x the x
		 * @param y the y
		 * @param c the c
		 */
		private Drop (int x, int y, int color) {
			xpos = x;
			ypos = y;
			dropcolor = color;
			finished = false;
		}

		/**
		 * Update.
		 */
		private void update() {
			if (!finished) {
				drawCircle();
				if (dropSize < internalBufferXSize*2) {
					dropSize ++;
				} else {
					finished = true;
				}
			}
		}

		/**
		 * Done.
		 *
		 * @return true, if successful
		 */
		private boolean done() {
			return finished;
		}

		/**
		 * draw only inside the boundaries
		 * 
		 * @param x
		 * @param y
		 * @param col
		 */
	    private void setPixel(int x, int y, int col) {
	        if (y >= 0 && y < internalBufferYSize && x >= 0 && x < internalBufferXSize) {
	            internalBuffer[y * internalBufferXSize + x] = col;
	        }
	    }

		/**
		 * draw circle
		 */
		private void drawCircle() {
	        int dropsizeThickness = dropSize-THICKNESS;
	        
	        for (int i = 0; i < internalBufferXSize; i++) {
	            for (int j = 0; j < internalBufferYSize; j++) {
	                //calculate distance to center:
	                int x = xpos - i;
	                int y = ypos - j;
	                double r = Math.sqrt((x * x) + (y * y));

	                if (r<dropSize && r>dropsizeThickness) {
	                	setPixel(i, j, dropcolor);
	                }
	            }
	        }			
		}
	}

}
