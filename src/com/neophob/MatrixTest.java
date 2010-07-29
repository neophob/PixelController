package com.neophob;

import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.serial.Serial;

import com.neophob.lib.rainbowduino.Rainbowduino;
import com.neophob.sematrix.output.RainbowduinoDevice;

public class MatrixTest extends PApplet {
	
	public static final int FPS = 10;
	
	private static Logger log = Logger.getLogger(RainbowduinoDevice.class.getName());

	private static int[] data =
	  {//green 
	    0xFF,0xFF,0xFF,0x4b,
	    0xFF,0xFF,0xF4,0xbf,
	    0x00,0x00,0x4b,0xff,
	    0x00,0x04,0xbf,0xff,
	    0x00,0x4b,0xff,0xff,
	    0x04,0xbf,0xff,0xff,
	    0x4b,0xff,0xff,0xff,
	    0xbf,0xff,0xff,0xfd,

	    0xff,0xfd,0x71,0x00,
	    0xff,0xd7,0x10,0x00,
	    0xfd,0xf1,0x00,0x00,
	    0xda,0x10,0x00,0x00,
	    0x71,0x00,0x00,0x01,
	    0x10,0x00,0x00,0x17,
	    0x00,0x00,0x01,0x7e,
	    0x00,0x00,0x17,0xef,

	    0x06,0xef,0xff,0xff,
	    0x6e,0xff,0xff,0xff,
	    0xef,0xff,0xff,0xfa,
	    0xff,0xff,0xff,0xa3,
	    0xff,0xff,0xfa,0x30,
	    0xff,0xfa,0xa3,0x00,
	    0xff,0xfa,0x30,0x00,
	    0xff,0xa3,0x00,0x00
	  };
	
	Rainbowduino rainbowduino;

	public void setup() {
		rainbowduino = new Rainbowduino( this );
		rainbowduino.initPort();
		
//		rainbowduino.slaveActiv(4);		
//		boolean ping = rainbowduino.ping((byte)0);
		

	//	rainbowduino.slaveActiv(5);		
		boolean ping = rainbowduino.ping((byte)0);
		log.log(Level.INFO, "ping result: "+ ping);
		
	frameRate(5);	
		//noLoop();
	}
	
	public void draw() { 
		//rainbowduino.ping((byte)0);
		//rainbowduino.sendFrame((byte)1,fromUnsignedInt(data), false);
		//sim.update();
	}

	public static byte fromUnsignedInt(int value) { 
		return (byte)value; 
	} 

	public static byte[] fromUnsignedInt(int[] value) {
		byte b[] = new byte[value.length];
		
		int ofs=0;
		for (int i: value) {
			b[ofs++] = (byte)i;
		}
		return b; 
	} 

	public static int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}
	  
	public static void main(String args[]) {
		PApplet.main(new String[] { /*"--present", */"com.neophob.MatrixTest" });
	}
}
