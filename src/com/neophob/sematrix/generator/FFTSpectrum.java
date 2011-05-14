package com.neophob.sematrix.generator;

import com.neophob.sematrix.input.Sound;
import com.neophob.sematrix.resize.Resize.ResizeName;


/**
 * moving cell
 * 
 * @author mvogt
 * 
 */
public class FFTSpectrum extends Generator {

	private Sound sound;
	private int[] fftHold = new int[32];
	private float[] fftSmooth = new float[32];
	
	private int yBlock;

	public FFTSpectrum() {
		super(GeneratorName.FFT, ResizeName.QUALITY_RESIZE);
		sound = Sound.getInstance();
		yBlock = this.internalBufferYSize / 32;
	}


	@Override
	public void update() {
		int avg = sound.getFftAvg();
		int col;
		
		for (int i = 0; i < avg; i++) {
			fftSmooth[i] = 0.6f * fftSmooth[i] + 0.4f * sound.getFftAvg(i);
			
		    int h = (int)(Math.log(fftSmooth[i]*3.0f)*30);		    
		    if (fftHold[i] < h) {
		      fftHold[i] = h;
		    }

		    h=255+h;
		    if (h>255) h=255;

		    col = (h << 16) | (h << 8) | h;
		    rect(col, 0, i*yBlock, this.internalBufferXSize, i*yBlock+yBlock);
		    
		    fftHold[i] = fftHold[i] - 4;
		    if (fftHold[i] < 0) fftHold[i] = 0;			
		}		
	}
	
	
	private void rect(int col, int x1, int y1, int x2, int y2) {
		int ofs;
		for (int y=y1; y<y2; y++) {
			ofs = y*this.internalBufferXSize;
			for (int x=x1; x<x2; x++) {		
				this.internalBuffer[ofs++] = col;
			}
		}
	}

	
	@Override
	public void close() {
	}


}
