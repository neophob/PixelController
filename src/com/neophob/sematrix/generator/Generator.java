package com.neophob.sematrix.generator;

import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.MatrixData;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * matrix display buffer class
 * 
 * the internal buffer is much larger than the actual device. the buffer for the matrix is recalculated
 * each frame. reason: better display quality 
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
		FIRE(5),
		METABALLS(6),
		PIXELIMAGE(7),
		TEXTURE_DEFORMATION(8),
		TEXTWRITER(9),
		IMAGE_ZOOMER(10),
		CELL(11);
		
		private int id;
		
		GeneratorName(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
	}
	
	private static Logger log = Logger.getLogger(Generator.class.getName());

	private GeneratorName name;
	
	private ResizeName resizeOption;
	
	//internal, larger buffer
	public int[] internalBuffer;
	protected int internalBufferXSize;
	protected int internalBufferYSize;
	
	/**
	 * 
	 * @param name
	 */
	public Generator(GeneratorName name, ResizeName resizeOption) {
		this.name = name;
		this.resizeOption = resizeOption;
		MatrixData matrix = Collector.getInstance().getMatrix();
		this.internalBufferXSize = matrix.getBufferXSize();
		this.internalBufferYSize = matrix.getBufferYSize();
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
	
	public ResizeName getResizeOption() {
		return resizeOption;
	}

	public int[] getBuffer() {
		return internalBuffer;
	}
	
	/**
	 * used for debug output
	 * @return
	 */
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
