package com.neophob.sematrix.generator;

import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.MatrixData;

/**
 * matrix display buffer class
 * 
 * the internal buffer is much larger than the actual device. the buffer for the matrix is recalculated
 * each frame. reason: better display quality 
 * 
 * TODO: Layout (horiz/vertical/cube)
 * 
 * @author mvogt
 *
 */
public abstract class Generator {

	public enum GeneratorName {
		PASSTHRU(0),
		BLINKENLIGHTS(1),
		IMAGE(2),
		PLASMA(3),
		SIMPLECOLORS(4),
		VOLUMEDISPLAY(5);
		
		private int id;
		
		GeneratorName(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
	}
	
	/** the internal buffer is 8 times larger than the output buffer */
	public static final int INTERNAL_BUFFER_SIZE = 8;
	
	private static Logger log = Logger.getLogger(Generator.class.getName());

	private GeneratorName name;
	
	//internal, larger buffer
	public int[] internalBuffer;
	private int internalBufferXSize;
	private int internalBufferYSize;
	
	/**
	 * 
	 * @param name
	 */
	public Generator(GeneratorName name) {
		this.name = name;
		MatrixData matrix = Collector.getInstance().getMatrix();

		this.internalBufferXSize = matrix.getDeviceXSize()*INTERNAL_BUFFER_SIZE;
		this.internalBufferYSize = matrix.getDeviceYSize()*INTERNAL_BUFFER_SIZE;
		this.internalBuffer = new int[internalBufferXSize*internalBufferYSize];

		log.log(Level.INFO,
				"SeMAtrixParent: internalBufferSize: {0} name: {1} "
				, new Object[] { internalBuffer.length, name });
		
		//add to list
		Collector.getInstance().addInput(this);
	}

	public abstract void update();
	
	public abstract void close();

	public int getInternalBufferXSize() {
		return internalBufferXSize;
	}

	public int getInternalBufferYSize() {
		return internalBufferYSize;
	}

	public int getInternalBufferSize() {
		return internalBuffer.length;
	}

	public GeneratorName getName() {
		return name;
	}

	public int[] getBuffer() {
		return internalBuffer;
	}
	
	public PImage getBufferAsImage() {
		PImage pImage = Collector.getInstance().getPapplet().createImage
							( internalBufferXSize, internalBufferYSize, PApplet.RGB );
		pImage.loadPixels();
		System.arraycopy(internalBuffer, 0, pImage.pixels, 0, internalBuffer.length);
		pImage.updatePixels();
		return pImage;
	}

	public int getId() {
		return this.name.getId();
	}
}
