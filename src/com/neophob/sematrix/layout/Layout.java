/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
 *
 * This file is part of PixelController.
 *
 * PixelController is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PixelController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PixelController.  If not, see <http://www.gnu.org/licenses/>.
 */

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
	
	public abstract LayoutModel getDataForScreen(int screenNr);
	
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
