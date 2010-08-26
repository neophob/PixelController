package com.neophob.sematrix.fader;



public class Crossfader extends Fader {

	public Crossfader() {
		super(FaderName.CROSSFADE, 2500);
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
			int oTmp, nTmp;
			short or,og,ob;
			short nr,ng,nb;

			float f = getCurrentStep();
			for (int i=0; i<buffer.length; i++){
				oTmp = buffer[i];
				nTmp = newBuffer[i];
				
	    		or=(short) (((oTmp>>16)&255)* (1.0f-f));
	    		og=(short) (((oTmp>>8)&255)* (1.0f-f));
	    		ob=(short) (( oTmp&255)* (1.0f-f));

	    		nr=(short) (((nTmp>>16)&255)* f);
	    		ng=(short) (((nTmp>>8)&255)* f);
	    		nb=(short) (( nTmp&255)* f);

				ret[i] = ((or << 16) | (og << 8) | ob) + ((nr << 16) | (ng << 8) | nb);
			}
			return ret;
			
		} catch (Exception e) {
			super.setDone();
			return buffer;
		}
	}

}
