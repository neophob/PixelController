package com.neophob.sematrix.layout;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.OutputMapping;

public class BoxLayout extends Layout {

	private int ioMappingSize;
	
	/**
	 * 
	 * @param row1Size
	 * @param row2Size
	 */
	public BoxLayout(int row1Size, int row2Size) {
		super(LayoutName.BOX, row1Size, row2Size);
		ioMappingSize = Collector.getInstance().getAllOutputMappings().size();
	}

	/**
	 * 
	 * @param screenNr
	 * @return
	 */
	private boolean isInTopRow(int screenNr) {
		return screenNr<(ioMappingSize/2);
	}
	
	/**
	 * 
	 * @param fxInput
	 * @param screenNr
	 * @return
	 */
	private int howManyScreensShareThisFxOnTheXAxis(int fxInput, int screenNr) {
		int ofs=0;
		int ret=0;
		boolean checkLineOne=false;
		int l1=0;
		
		//we check only our x line
		if (!isInTopRow(screenNr)) {
			ofs=ioMappingSize/2;
			//checkLineOne=true;
		}
		OutputMapping o;
		for (int i=ofs; i<ofs+ioMappingSize/2; i++) {
			o = Collector.getInstance().getOutputMappings(i);
			if (o.getVisualId()==fxInput) {
				ret++;
			}
		}
		
		if (checkLineOne) {
			for (int i=0; i<(ioMappingSize/2); i++) {
				if (Collector.getInstance().getOutputMappings(i).getVisualId()==fxInput) {
					l1++;
				}
			}			
		}

		//System.out.println(screenNr+":share x "+Math.max(ret, l1));

		return ret;
	}
	
	/**
	 * 
	 * @param fxInput
	 * @param screenNr
	 * @return
	 */
	private int howManyScreensShareThisFxOnTheYAxis(int fxInput, int screenNr) {
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
		
		//System.out.println(screenNr+":share y "+ret);

		return ret;
	}

	
	/**
	 * return y offset of screen position
	 * (0=first row, 1=second row...)
	 * 
	 */
	private int getXOffsetForScreen(int fxInput,int screenNr) {
		int ret=0;
		int ofs=0;
		int max=screenNr;
		boolean checkLineOne=false;
		int l1=0;
		
		if (!isInTopRow(screenNr)) { 
			ofs=(ioMappingSize/2);
			//checkLineOne=true;
		}

		for (int i=ofs; i<max; i++) {
			if (Collector.getInstance().getOutputMappings(i).getVisualId()==fxInput) {
				ret++;
			}
		}
		
		if (checkLineOne) {
			for (int i=0; i<(ioMappingSize/2); i++) {
				if (Collector.getInstance().getOutputMappings(i).getVisualId()==fxInput) {
					l1++;
				}
			}			
		}
		System.out.println(screenNr+" ofs: "+ret);
		return ret;
	}

	/**
	 * return y offset of screen position if a visual is spread
	 * acros MULTIPLE outputs.
	 * 
	 * return 0 if the visuial is only shown on one screen 
	 * 
	 * (0=first row, 1=second row...)
	 * 
	 */
	private int getYOffsetForScreen(int fxInput, int screenNr) {
		int ret=0;
		int ofs=0;
		int max=screenNr;
		if (isInTopRow(screenNr)) {
			ofs=(ioMappingSize/2)-1;
		} else {
			max-=(ioMappingSize/2)-1;
		}

		//for (int i=ofs; i<max; i++) {
		for (int i=ofs; i<max; i+=(ioMappingSize/2)) {
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

		int fxOnHowMayScreensX=this.howManyScreensShareThisFxOnTheXAxis(fxInput, screenNr);
		int fxOnHowMayScreensY=this.howManyScreensShareThisFxOnTheYAxis(fxInput, screenNr);
		
		if (fxOnHowMayScreensX>0 && fxOnHowMayScreensY>0) {
			int fxOnHowMayScreens = Math.max(fxOnHowMayScreensX, fxOnHowMayScreensY);
			fxOnHowMayScreensX=fxOnHowMayScreens;
			fxOnHowMayScreensY=fxOnHowMayScreens;
		} 
		
		return new LayoutModel(
				fxOnHowMayScreensX, 
				fxOnHowMayScreensY,
				this.getXOffsetForScreen(fxInput, screenNr),
				this.getYOffsetForScreen(fxInput, screenNr),
				fxInput);
	}

}
