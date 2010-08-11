package com.neophob.sematrix.layout;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.OutputMapping;

public class HorizontalLayout extends Layout {

	public HorizontalLayout(int row1Size, int row2Size) {
		super(LayoutName.HORIZONTAL, row1Size, row2Size);
	}

	public int howManyScreensShareThisFxOnTheXAxis(int fxInput, int screenNr) {
		int ret=0;
		for (OutputMapping o: Collector.getInstance().getAllOutputMappings()) {
			if (o.getVisualId()==fxInput) {
				ret++;
			}
		}
		return ret;			
	}
	
	public int howManyScreensShareThisFxOnTheYAxis(int fxInput, int screenNr) {
		return 1;
	}
	
	/**
	 * check which offset position the fx at this screen is
	 * @param screenOutput
	 * @return
	 */
	public int getXOffsetForScreen(int screenOutput) {
		int ret=0;
		int fxInput = Collector.getInstance().getOutputMappings(screenOutput).getVisualId();

		for (int i=0; i<screenOutput; i++) {
			if (Collector.getInstance().getOutputMappings(i).getVisualId()==fxInput) {
				ret++;
			}
		}

		return ret;
	}
	
	public int getYOffsetForScreen(int screenOutput) {
		return 0;
	}

}
