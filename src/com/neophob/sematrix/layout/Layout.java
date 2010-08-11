package com.neophob.sematrix.layout;

import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class Layout {

	public enum LayoutName {
		HORIZONTAL(0),
		BOX(1);
		
		private int id;
		
		LayoutName(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
	}
	
	private static Logger log = Logger.getLogger(Layout.class.getName());
	private LayoutName layoutName;
	
	protected int row1Size;
	protected int row2Size;
	
	public Layout(LayoutName layoutName, int row1Size, int row2Size) {
		this.layoutName = layoutName;
		this.row1Size = row1Size;
		this.row2Size = row2Size;
		
		log.log(Level.INFO,
				"Layout created: {0}, size row 1: {1}, row 2: {2}"
				, new Object[] { layoutName.toString(), row1Size, row2Size });
	}
	
	public abstract int howManyScreensShareThisFxOnTheXAxis(int fxInput, int screenNr);
	public abstract int howManyScreensShareThisFxOnTheYAxis(int fxInput, int screenNr);
	
	public abstract int getXOffsetForScreen(int screenOutput);
	public abstract int getYOffsetForScreen(int screenOutput);

	
	public int getRow1Size() {
		return row1Size;
	}

	public int getRow2Size() {
		return row2Size;
	}

	public LayoutName getLayoutName() {
		return layoutName;
	}
	
}
