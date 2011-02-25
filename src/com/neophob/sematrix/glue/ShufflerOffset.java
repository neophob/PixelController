package com.neophob.sematrix.glue;

public enum ShufflerOffset {
	GENERATOR_A(0),
	GENERATOR_B(1),
	EFFECT_A(2),
	EFFECT_B(3),
	MIXER(4),
	MIXER_OUTPUT(5),
	FADER_OUTPUT(6),
	OUTPUT(7),
	BLINKEN(8),
	IMAGE(9),
	TINT(10),
	TEXTURE_DEFORMATION(11),
	THRESHOLD_VALUE(12);
	
	int ofs;
	ShufflerOffset(int ofs) {
		this.ofs = ofs;
	}
	
	int getOffset() {
		return ofs;
	}
}
