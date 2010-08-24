package com.neophob.sematrix.output.helper;

import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.Visual;


public class InternalBuffer extends PApplet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2344499301021L;

	private static final int BFR_X = 128;
	private static final int BFR_Y = 128;
	
	static Logger log = Logger.getLogger(InternalBuffer.class.getName());

	private boolean displayHoriz;
	private int x,y,frame;
	private int[] buffer;
	private PImage pImage;

	public InternalBuffer(boolean displayHoriz, int x, int y) {
		this.displayHoriz = displayHoriz;
		this.x = x;
		this.y = y;
	}
	
    public void setup() {
    	log.log(Level.INFO, "create frame with size "+x+"/"+y);
        size(x,y);
        frameRate(Collector.getInstance().getFps());
        background(0,0,0);
    }

	public void draw() {
		frame++;
		
		if (frame%2==1) return;

		int x=0, y=0;
		for (Visual v: Collector.getInstance().getAllVisuals()) {
			//get image
			buffer = Collector.getInstance().getMatrix().resizeBufferForDevice(v.getBuffer(), BFR_X, BFR_Y);
			
			//create an image out of the buffer
	 		pImage = Collector.getInstance().getPapplet().createImage( BFR_X, BFR_Y, PApplet.RGB );
			pImage.loadPixels();
			System.arraycopy(buffer, 0, pImage.pixels, 0, BFR_X*BFR_Y);
			pImage.updatePixels();

			//display it
			image(pImage,x,y);
			if (displayHoriz) {
				x += pImage.width;
			} else {
				y += pImage.height;
			}			
		}

		//redraw();
	}


}
