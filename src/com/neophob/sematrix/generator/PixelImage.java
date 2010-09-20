package com.neophob.sematrix.generator;

import java.security.InvalidParameterException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.glue.Collector;

/**
 * @author mvogt
 * TODO: synchronize sound buffer size with fps and steps size 
 * 
 */
public class PixelImage extends Generator {

	private static Logger log = Logger.getLogger(PixelImage.class.getName());

	private static final int PIXELNR = 8;
	private static final String FILENAME = "8x8.png";
	
	private PImage[] images;
//	private int[][] images;
	private int nrOfImages;
	
	private Random r = new Random();
	private int currentImage;
	
	public PixelImage() throws InvalidParameterException {
		super(GeneratorName.PIXELIMAGE);
		PApplet parent = Collector.getInstance().getPapplet(); 
		
		//load whole image
		PImage pimage = parent.loadImage(FILENAME);
		if (pimage.width!=PIXELNR) {
			throw new InvalidParameterException("image width must be "+PIXELNR+" pixels!");
		}
		
		nrOfImages = pimage.height/PIXELNR;
		if (nrOfImages<1) {
			throw new InvalidParameterException("image height must be >8 pixels!");
		}
		
		log.log(Level.INFO, "found "+nrOfImages+" images in file "+FILENAME);
		
		//create multiple images
		pimage.loadPixels();
		images = new PImage[nrOfImages];
		for (int i=0; i<nrOfImages; i++) {
			this.images[i] = new PImage(PIXELNR, PIXELNR, PApplet.RGB);
			this.images[i].loadPixels();
			System.arraycopy(pimage.pixels, i*PIXELNR*PIXELNR, this.images[i].pixels, 0, PIXELNR*PIXELNR);
			this.images[i].updatePixels();
		}
/*		images = new int[nrOfImages][PIXELNR*PIXELNR];
		for (int i=0; i<nrOfImages; i++) {
			System.arraycopy(pimage.pixels, i*PIXELNR*PIXELNR, this.images[i][0], 0, PIXELNR*PIXELNR);
		}
*/		pimage.updatePixels();
		
		currentImage=r.nextInt(nrOfImages);
	}

	@Override
	public void update() {		
		if (r.nextInt(10)==4) {
			currentImage=r.nextInt(nrOfImages);
			
			int ofs=0, xofs, yofs=-1, dst=0;
			int xDiff = internalBufferXSize/PIXELNR;
			int yDiff = internalBufferYSize/PIXELNR;

			//resize image from 8x8 to 128x128
			for (int y=0; y<internalBufferXSize; y++) {
				if (y%yDiff==0) yofs++;
				xofs=-1;
				for (int x=0; x<internalBufferXSize; x++) {
					if (x%xDiff==0) xofs++;
					ofs=xofs+yofs*PIXELNR;
					this.internalBuffer[dst++]=this.images[currentImage].pixels[ofs];
				}				
			}
			
			//images[currentImage].loadPixels();
			//try {
				//todo resize
/*		 		PImage p = Collector.getInstance().getPapplet().createImage( internalBufferXSize, internalBufferYSize, PApplet.RGB );
				p.loadPixels();
				System.arraycopy(buffer, 0, p.pixels, 0, getBufferXSize()*getBufferYSize());

				//copy(x, y, width, height, dx, dy, dwidth, dheight)
				p.copy(xStart, yStart, xWidth, yWidth, 0, 0, internalBufferXSize, internalBufferYSize);
				p.updatePixels();
*/
/*				PImage img = (PImage)images[currentImage].clone();
				img.loadPixels();
				img.resize(internalBufferXSize, internalBufferYSize);
				System.arraycopy(img.pixels, 0, this.internalBuffer, 0, internalBufferXSize*internalBufferYSize);
				img.updatePixels();
			} catch (Exception e) {}
			images[currentImage].updatePixels();
			*/

		}
	}

	@Override
	public void close() {	}


}
