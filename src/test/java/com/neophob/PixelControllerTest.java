package com.neophob;

import static org.junit.Assert.*;

import org.junit.Test;

import processing.core.PApplet;

import com.neophob.sematrix.glue.Collector;

/**
 * test start
 * @author michu
 *
 */
public class PixelControllerTest extends PApplet {

	@Test
	public void testMain() {
		PApplet.main(new String[] { "com.neophob.PixelControllerTest" });
		
		assertTrue(Collector.getInstance().getPresent().size() > 0);		
		assertTrue(Collector.getInstance().getPixelControllerGenerator().getSize() > 0);
		assertTrue(Collector.getInstance().getPixelControllerEffect().getSize() > 0);
		assertTrue(Collector.getInstance().getPixelControllerMixer().getSize() > 0);
		assertTrue(Collector.getInstance().getPixelControllerResize().getAllResizers().size() > 0);
	}
	
	public void setup() { 
		Collector.getInstance().init(this, 10, 8, 8);			
	}
	
	public void draw() {
		
	}
	
}
