package com.neophob.sematrix.output.helper;

import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.MatrixTest;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.Visual;


public class InternalBuffer extends PApplet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2344499301021L;

	static Logger log = Logger.getLogger(InternalBuffer.class.getName());

	private boolean displayHoriz;
	private int x,y;
	
	public InternalBuffer(boolean displayHoriz, int x, int y) {
		this.displayHoriz = displayHoriz;
		this.x = x;
		this.y = y;
	}
	
    public void setup() {
    	log.log(Level.INFO, "create frame with size "+x+"/"+y);
        size(x,y);
        frameRate(MatrixTest.FPS);
        background(0,0,0);
    }

	public void draw() {
		int x=0, y=0;
		
		for (Visual v: Collector.getInstance().getAllVisuals()) {
			//PImage pimage = v.getBufferAsImage();
			PImage pimage = Collector.getInstance().getImageFromBuffer(v.getBuffer(), 128, 128);
			image(pimage,x,y);
			
			if (displayHoriz) {
				x += pimage.width;
			} else {
				y += pimage.height;
			}			
		}

		redraw();
	}


}
