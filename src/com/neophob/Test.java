package com.neophob;

import processing.core.PApplet;

import com.neophob.sematrix.generator.Blinkenlights;
import com.neophob.sematrix.generator.Image;
import com.neophob.sematrix.glue.Collector;

public class Test extends PApplet {

	public void setup() {		
		Collector.getInstance().init(this, 1, 1, 8, 8);

		Blinkenlights blink = new Blinkenlights("torus.bml");
		
		for (int i=0; i<100; i++) {
			blink.loadFile("bb-rauten2.bml");
			System.out.println(i+" "+Runtime.getRuntime().freeMemory()/1024);
			
		}
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "com.neophob.Test" });
	}

}
