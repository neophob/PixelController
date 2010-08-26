package com.neophob.sematrix.fader;

import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.glue.Collector;

public class SlideLeftRight extends Fader {

	public SlideLeftRight() {
		super(FaderName.SLIDE_LEFT_RIGHT, 1500);
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
			int ammount=(int)(g.getInternalBufferXSize()*f);
			int ofs,x,idx=0;

			int linesize=g.getInternalBufferXSize();
			for (int y=0; y<g.getInternalBufferYSize(); y++) {
				ofs=g.getInternalBufferXSize()*y;
				for (x=0; x<ammount; x++) {
					ret[idx++] = newBuffer[ofs+(linesize-ammount+x)];
				}
				for (x=ammount; x<g.getInternalBufferXSize(); x++) {
					ret[idx++] = buffer[ofs+x];
				}
			}
			return ret;
		} catch (Exception e) {
			super.setDone();
			return buffer;
		}

	}

}
