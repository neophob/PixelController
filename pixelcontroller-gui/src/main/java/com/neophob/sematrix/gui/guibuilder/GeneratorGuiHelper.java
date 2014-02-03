/**
 * Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.gui.guibuilder;

import com.neophob.sematrix.core.visual.effect.Effect.EffectName;
import com.neophob.sematrix.core.visual.fader.Fader.FaderName;

import controlP5.ControlP5;
import controlP5.DropdownList;

/**
 * 
 * 
 * @author michu
 */
public class GeneratorGuiHelper {

	private GeneratorGuiHelper() {
		//no instance allowed
	}
	
	/**
	 * 
	 * @param cp5
	 * @param yPosStartDrowdown
	 * @return
	 */
	public static DropdownList createFaderDropdown(ControlP5 cp5, String name, int yPosStartDrowdown) {
		DropdownList dropdownOutputFader = cp5.addDropdownList(name,
        		35+Theme.DROPBOX_XOFS*2, 45+yPosStartDrowdown, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(dropdownOutputFader);
        int i=0;
        for (FaderName fn: FaderName.values()) {
            dropdownOutputFader.addItem(fn.name(), i++);
        }
        dropdownOutputFader.setLabel(dropdownOutputFader.getItem(0).getName());        
        dropdownOutputFader.setHeight(70);

        return dropdownOutputFader;
	}
	
	/**
	 * 
	 * @param cp5
	 * @param yPosStartDrowdown
	 * @return
	 */
	public static DropdownList createEffectDropdown(ControlP5 cp5, String name, int yPosStartDrowdown) {
		DropdownList dropdownOutputEffect = cp5.addDropdownList(name, 
        		35+Theme.DROPBOX_XOFS, 45+yPosStartDrowdown, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(dropdownOutputEffect);
        int i=0;
        for (EffectName gn: EffectName.values()) {
            dropdownOutputEffect.addItem(gn.name(), i++);
        }
        dropdownOutputEffect.setLabel(dropdownOutputEffect.getItem(0).getName());
        dropdownOutputEffect.setHeight(70);
        
        return dropdownOutputEffect;
	}
		
	/**
	 * 
	 * @param cp5
	 * @param yPosStartDrowdown
	 * @param nrOfVisuals
	 * @return
	 */
	public static DropdownList createVisualDropdown(ControlP5 cp5, String name, int yPosStartDrowdown, int nrOfVisuals) {
		DropdownList dropdownOutputVisual = cp5.addDropdownList(name, 
				35, 45+yPosStartDrowdown, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(dropdownOutputVisual);
        int i=0;
        for (i=0; i<nrOfVisuals; i++) {
            dropdownOutputVisual.addItem("Visual #"+(1+i), i);
        }
        dropdownOutputVisual.setLabel(dropdownOutputVisual.getItem(0).getName());
       // dropdownOutputVisual.setHeight(70);

        return dropdownOutputVisual;
	}
}
