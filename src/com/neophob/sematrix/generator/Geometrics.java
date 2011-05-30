package com.neophob.sematrix.generator;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import com.neophob.sematrix.input.Sound;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * 
 * @author michu
 *
 */
public class Geometrics extends Generator {

	private List<Drop> drops;
	//private int dropWallSize = 30;
	private int dropHue = 0;

	private Sound sound;
	private Random rndGen=new Random();

	/**
	 * 
	 */
	public Geometrics() {
		super(GeneratorName.GEOMETRICS, ResizeName.QUALITY_RESIZE);
		drops = new CopyOnWriteArrayList<Drop>();
		sound = Sound.getInstance();
	}

	/**
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	private int random(int min, int max) {
		int ret = rndGen.nextInt(max-min);
		return ret+min;
	}


	@Override
	public void update() {

		Arrays.fill(this.internalBuffer, 0);
		if (sound.isKick() || drops.size()==0) {
			drops.add(
					new Drop(random(12, 52), random(12, 52), dropHue)
			);
			dropHue += 4;
			if (dropHue > 100) {
				dropHue -= 100;
			}
		}

		for (Drop d: drops) {
			d.update();
			if (d.done()) {
				drops.remove(d); 
			}
		}
	}


	@Override
	public void close() {}




	// Class for Raindrops effect
	private class Drop {

		int xpos, ypos, dropcolor, dropSize;
		boolean finished;

		private Drop (int x, int y, int c) {
			xpos = x;
			ypos = y;
			//todo c is hsv, not rgb!
			//dropcolor = c << 16 | c << 8 | c;
			dropcolor = Color.HSBtoRGB(255f/(float)c, 0.98f, 0.9f);
			finished = false;
		}

		private void update() {
			if (!finished) {
				drawCircle(xpos,ypos, dropSize, dropcolor);
				if (dropSize < internalBufferXSize) {
					dropSize ++;
				} else {
					finished = true;
				}
			}
		}

		private boolean done() {
			return finished;
		}

		/**
		 * 
		 * @param thick
		 * @param px
		 * @param py
		 * @param col
		 */
		private void setPixel(int thick, int px, int py, int col) {			
			int ofs=px+py*internalBufferXSize;

			for (int x=0; x<thick; x++) {
				if (x+px+1 > internalBufferXSize || x+px<0) break;
				for (int y=0; y<thick; y++) {
					if (y+py+1 > internalBufferYSize || y+py<0) break;

					int pos = ofs+x+y*internalBufferXSize;
					
					if (pos>0 && pos < internalBuffer.length)
					try {
						internalBuffer[pos]=col;						
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("POS:"+pos);
						// TODO: handle exception
					}
				}
			}

		}

		/**
		 * 
		 * @param xp
		 * @param yp
		 * @param radius
		 * @param col
		 */
		private void drawCircle(int xp, int yp, int radius, int col) {
			int balance;
			int xoff=0;
			int yoff=radius;
			balance=- radius;

			while (xoff <= yoff) {
				setPixel(12, xp+xoff, yp+yoff, col);
				setPixel(12, xp-xoff, yp+yoff, col);
				setPixel(12, xp-xoff, yp-yoff, col);
				setPixel(12, xp+xoff, yp-yoff, col);
				setPixel(12, xp+yoff, yp+xoff, col);
				setPixel(12, xp-yoff, yp+xoff, col);
				setPixel(12, xp-yoff, yp-xoff, col);
				setPixel(12, xp+yoff, yp-xoff, col);

				balance += xoff++;

				if (balance+xoff >= 0) {
//				if ((balance += xoff++ + xoff)>= 0) {
					balance-=--yoff+yoff;
				}
			}

		}
	}

}
