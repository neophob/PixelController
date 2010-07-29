package com.neophob.sematrix.generator;


public class SimpleColors extends Generator {

	private int rotate = 0;

	public SimpleColors() {
		super(GeneratorName.SIMPLECOLORS);
	}
	
	@Override
	public void update() {
		int col, ofs=0;
		for (int y=0; y<this.getInternalBufferYSize(); y++) {
			for (int x=0; x<this.getInternalBufferXSize(); x++) {			
				col = (int)((4*((y+rotate)%255)) << 16) | ((2*((x+rotate)%255)) << 8)  | ((x+y+rotate>>1)%255);
				ofs = y*this.getInternalBufferXSize()+x;				
				this.internalBuffer[ofs++]=col;
			}
		}
		rotate+=5;
	}

	
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
}
