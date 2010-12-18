package com.neophob.sematrix.layout;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.OutputMapping;

public class HorizontalLayout extends Layout {

	public HorizontalLayout(int row1Size, int row2Size) {
		super(LayoutName.HORIZONTAL, row1Size, row2Size);
	}

	/**
	 * 
	 * @param fxInput
	 * @return
	 */
	private int howManyScreensShareThisFxOnTheXAxis(int fxInput) {
		int ret=0;
		for (OutputMapping o: Collector.getInstance().getAllOutputMappings()) {
			if (o.getVisualId()==fxInput) {
				ret++;
			}
		}
		return ret;			
	}
	
	/**
	 * check which offset position the fx at this screen is
	 * @param screenOutput
	 * @return
	 */
	private int getXOffsetForScreen(int fxInput, int screenNr) {
		int ret=0;

		for (int i=0; i<screenNr; i++) {
			if (Collector.getInstance().getOutputMappings(i).getVisualId()==fxInput) {
				ret++;
			}
		}

		return ret;
	}
	

	/**
	 * 
	 */
	public LayoutModel getDataForScreen(int screenNr) {
		int fxInput = Collector.getInstance().getOutputMappings(screenNr).getVisualId();

		return new LayoutModel(
				this.howManyScreensShareThisFxOnTheXAxis(fxInput), 
				1,
				this.getXOffsetForScreen(fxInput, screenNr),
				0,
				fxInput);
	}

}
