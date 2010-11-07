package com.neophob.sematrix.mixer;

import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.input.Sound;

/**
 * mix src/dst accoring to volume of sound!
 * 
 * @author michu
 *
 */
public class Voluminizer extends Mixer {

	public Voluminizer() {
		super(MixerName.VOLUMINIZER);
	}

	public int[] getBuffer(Visual visual) {
		if (visual.getEffect2() == null) {
			return visual.getEffect1Buffer();
		}

		short cr_s,cg_s,cb_s;
		short cr_d,cg_d,cb_d;
		int col;

		Generator gen1 = visual.getGenerator1();		
		int[] src1 = visual.getEffect1Buffer();
		int[] src2 = visual.getEffect2Buffer();
		int[] dst = new int [gen1.internalBuffer.length];

		float sound = Sound.getInstance().getVolumeNormalized();
		for (int i=0; i<gen1.internalBuffer.length; i++){
			col = src1[i];
    		cr_s=(short) ((col>>16)&255);
    		cg_s=(short) ((col>>8) &255);
    		cb_s=(short) ( col     &255);
    		
			col = src2[i];
    		cr_d=(short) ((col>>16)&255);
    		cg_d=(short) ((col>>8) &255);
    		cb_d=(short) ( col     &255);
    		
    		cr_d = (short) (cr_d*sound + ((1.0f-sound)*cr_s) );
    		cg_d = (short) (cg_d*sound + ((1.0f-sound)*cg_s));
    		cb_d = (short) (cb_d*sound + ((1.0f-sound)*cb_s));
    		
    		dst[i]=(int) ((cr_d << 16) | (cg_d << 8) | cb_d);
          }
	
		return dst;
	}

}
