package com.neophob.sematrix.mixer;

import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * checkbox mixer
 * 
 * 
 * @author mvogt
 *
 */
public class Checkbox extends Mixer {

	private int pixelsPerLine;
	public Checkbox() {
		super(MixerName.CHECKBOX, ResizeName.PIXEL_RESIZE);
		pixelsPerLine = Collector.getInstance().getMatrix().getDeviceXSize();
	}

	public int[] getBuffer(Visual visual) {
		
		if (visual.getEffect2() == null) {
			return visual.getEffect1Buffer();
		}

		Generator gen1 = visual.getGenerator1();
		int[] src1 = visual.getEffect1Buffer();
		int[] src2 = visual.getEffect2Buffer();
		int[] dst = new int [gen1.internalBuffer.length];
		int flp = gen1.getInternalBufferXSize()*pixelsPerLine;
		
		boolean flip=true;
		for (int i=0; i<src1.length; i++) {
			if (i%pixelsPerLine==0) {
				flip=!flip;
			}
			if (i%flp==0) {
				flip=!flip;
			}
			
			if (flip) {
				dst[i] = src2[i];
			} else {
				dst[i] = src1[i];
			}
			
		}

		return dst;
	}

}
