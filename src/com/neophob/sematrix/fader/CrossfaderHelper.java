package com.neophob.sematrix.fader;

public class CrossfaderHelper {

	public static int[] getBuffer(float f, int[] oldBuffer, int[] newBuffer) {

		int[] ret = new int[oldBuffer.length];
		int oTmp, nTmp;
		short or,og,ob;
		short nr,ng,nb;

		for (int i=0; i<oldBuffer.length; i++){
			oTmp = oldBuffer[i];
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

	}
}
