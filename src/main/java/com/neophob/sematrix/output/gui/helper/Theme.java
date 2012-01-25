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
package com.neophob.sematrix.output.gui.helper;

import controlP5.DropdownList;

/**
 * @author mvogt
 *
 */
public final class Theme {

    public static final int DROPBOXLIST_LENGTH = 110;
    public static final int DROPBOX_XOFS = DROPBOXLIST_LENGTH + 23;

    /**
     * 
     */
    private Theme() {
        //Util Class, no instance allowed
    }
        

    @SuppressWarnings("deprecation")
    public static void themeDropdownList(DropdownList ddl) {
        // a convenience function to customize a DropdownList
        ddl.setItemHeight(12); //height of a element in the dropdown list
        ddl.setBarHeight(15);  //size of the list
        ddl.actAsPulldownMenu(true); //close menu after a selection was done
        ddl.setBackgroundColor(0);

        ddl.captionLabel().style().marginTop = 3;
        ddl.captionLabel().style().marginLeft = 3;
        ddl.valueLabel().style().marginTop = 3;
    }
}
