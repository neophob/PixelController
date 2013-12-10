/**
 * Copyright (C) 2011-2013 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.core.visual.layout;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.visual.OutputMapping;

/**
 * this class defines how multiple panels are arranged.
 *
 * @author michu
 */
public abstract class Layout {

	/**
	 * The Enum LayoutName.
	 */
	public enum LayoutName {
		
		/** The HORIZONTAL. */
		HORIZONTAL(0),
		
		/** The BOX. */
		BOX(1);
		
		/** The id. */
		private int id;
		
		/**
		 * Instantiates a new layout name.
		 *
		 * @param id the id
		 */
		LayoutName(int id) {
			this.id = id;
		}
		
		/**
		 * Gets the id.
		 *
		 * @return the id
		 */
		public int getId() {
			return id;
		}
	}
	
	/** The log. */
	private static final Logger LOG = Logger.getLogger(Layout.class.getName());
	
	/** The layout name. */
	private LayoutName layoutName;
	
	/** The row1 size. */
	protected int row1Size;
	
	/** The row2 size. */
	protected int row2Size;
	
	/**
	 * Instantiates a new layout.
	 *
	 * @param layoutName the layout name
	 * @param row1Size the row1 size
	 * @param row2Size the row2 size
	 */
	public Layout(LayoutName layoutName, int row1Size, int row2Size) {
		this.layoutName = layoutName;
		this.row1Size = row1Size;
		this.row2Size = row2Size;
		
		LOG.log(Level.INFO,
				"Layout created: {0}, size row 1: {1}, row 2: {2}"
				, new Object[] { layoutName.toString(), row1Size, row2Size });
	}
	
	/**
	 * Gets the data for screen.
	 *
	 * @param screenNr the screen nr
	 * @return the data for screen
	 */
	public abstract LayoutModel getDataForScreen(int screenNr, List<OutputMapping> ioMapping);
	
	/**
	 * Gets the row1 size.
	 *
	 * @return the row1 size
	 */
	public int getRow1Size() {
		return row1Size;
	}

	/**
	 * Gets the row2 size.
	 *
	 * @return the row2 size
	 */
	public int getRow2Size() {
		return row2Size;
	}

	/**
	 * Gets the layout name.
	 *
	 * @return the layout name
	 */
	public LayoutName getLayoutName() {
		return layoutName;
	}
	
}
