package com.neophob.sematrix.output.lpd6803;

import processing.core.PApplet;

/**
 * simply test class, only used to test the lib<br>
 * <br>
 * part of the neorainbowduino library
 * 
 * @author Michael Vogt / neophob.com
 *
 */
public class TestRoundtrip extends PApplet {

	Lpd6803 r;
	int [] frame1;
	byte [] frame2;
	 
	/** 
	 * ss
	 */
	public void setup() {		 
		frameRate(500);
		
		try {
			r = new Lpd6803(this, 9600);
			long l1 = System.currentTimeMillis();
			r.ping();
			long l2= System.currentTimeMillis()-l1;
			System.out.println("need "+l2+"ms to send ping");
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		frame1 = new int[64];
		frame2 = new byte[128];
		
	}
	
	public void draw() {
		long l1 = System.currentTimeMillis();
		//r.sendRgbFrame((byte)1, frame1);
		r.sendFrame((byte)0, frame2);
		//r.ping();
		long l2= System.currentTimeMillis()-l1;
		System.out.println("need "+l2+"ms to send data");
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "com.neophob.sematrix.output.lpd6803.TestRoundtrip" });
	}

}
