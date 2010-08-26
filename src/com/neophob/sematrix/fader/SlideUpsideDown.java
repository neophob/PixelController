package com.neophob.sematrix.fader;

import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.glue.Collector;

public class SlideUpsideDown extends Fader {

	public SlideUpsideDown() {
		super(FaderName.SLIDE_UPSIDE_DOWN, 1500);
	}

	@Override
	public int[] getBuffer(int[] buffer) {
		currentStep++;		

		try {
			int[] newBuffer = getNewBuffer();
			if (super.isDone()) {
				return newBuffer;
			}

			int[] ret = new int[buffer.length];

			float f = getCurrentStep();

			Generator g = Collector.getInstance().getGenerator(0);
			int ammount=(int)(g.getInternalBufferYSize()*f)*g.getInternalBufferXSize();
			int totalSize=g.getInternalBufferYSize()*g.getInternalBufferXSize();
			for (int y=0; y<ammount; y++) {
				ret[y]=newBuffer[totalSize-ammount+y];
			}
			int idx=0;
			for (int y=ammount; y<totalSize; y++) {
				ret[y]=buffer[idx++];
			}
			return ret;
			
		} catch (Exception e) {
			super.setDone();
			return buffer;
		}

	}

}
