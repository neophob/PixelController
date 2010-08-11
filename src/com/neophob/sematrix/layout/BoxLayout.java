package com.neophob.sematrix.layout;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.OutputMapping;

public class BoxLayout extends Layout {

	private int ioMappingSize;
	
	private boolean isInTopRow(int screenNr) {
		return screenNr<(ioMappingSize/2);
	}
	
	public BoxLayout(int row1Size, int row2Size) {
		super(LayoutName.BOX, row1Size, row2Size);
		ioMappingSize = Collector.getInstance().getAllOutputMappings().size();
	}
	
	public int howManyScreensShareThisFxOnTheXAxis(int fxInput, int screenNr) {
		int ofs=0;
		int ret=0;
		//we check only our x line
		if (!isInTopRow(screenNr)) {
			ofs=ioMappingSize/2;
		}
		OutputMapping o;
		for (int i=ofs; i<ofs+ioMappingSize/2; i++) {
			o = Collector.getInstance().getOutputMappings(i);
			if (o.getVisualId()==fxInput) {
				ret++;
			}
		}
		return ret;
	}
	
	public int howManyScreensShareThisFxOnTheYAxis(int fxInput, int screenNr) {
		int ofs=0;
		int ret=0;
		//we check only the opposite line
		if (isInTopRow(screenNr)) {
			ofs=ioMappingSize/2;
		}
		OutputMapping o;
		for (int i=ofs; i<ofs+ioMappingSize/2; i++) {
			o = Collector.getInstance().getOutputMappings(i);
			if (o.getVisualId()==fxInput) {
				ret++;
			}
		}
		return ret;
	}

	
	/**
	 * return y offset of screen position
	 * (0=first row, 2=second row...)
	 * 
	 */
	public int getXOffsetForScreen(int screenNr) {
		int ret=0;
		int ofs=0;
		int max=screenNr;
		//we check only our x line
		if (!isInTopRow(screenNr)) {
			ofs=(ioMappingSize/2)-1;
			max-=(ioMappingSize/2)-1;
		}

		int fxInput = Collector.getInstance().getOutputMappings(screenNr).getVisualId();
		for (int i=ofs; i<max; i++) {
			if (Collector.getInstance().getOutputMappings(i).getVisualId()==fxInput) {
				ret++;
			}
		}
		System.out.println(screenNr+": x "+ret);
		
		return ret;
	}
/*
0: x 0
0: y 0
1: x 1
1: y 0
2: x 0
2: y 0 << error
3: x 0 << error
3: y 1
 */
	/**
	 * return y offset of screen position if a visual is spread
	 * acros MULTIPLE outputs.
	 * 
	 * return 0 if the visuial is only shown on one screen 
	 * 
	 * (0=first row, 1=second row...)
	 * 
	 */
	public int getYOffsetForScreen(int screenNr) {
		int ret=0;
		int ofs=0;
		int max=screenNr;
		if (isInTopRow(screenNr)) {
			ofs=(ioMappingSize/2)-1;
		} else {
			max-=(ioMappingSize/2)-1;
		}

		int fxInput = Collector.getInstance().getOutputMappings(screenNr).getVisualId();
		//for (int i=ofs; i<max; i++) {
		for (int i=ofs; i<max; i+=(ioMappingSize/2)) {
			if (Collector.getInstance().getOutputMappings(i).getVisualId()==fxInput) {
				ret++;
			}
		}
System.out.println(screenNr+": y "+ret);
		return ret;
	}

}
