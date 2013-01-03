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

package com.neophob.sematrix.listener;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.Shuffler;
import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.output.gui.GuiCallbackAction;

/**
 * 
 * PixelController Keyboard handler
 * 
 * @author mvogt
 *
 */
public abstract class KeyboardHandler {

    public static GuiCallbackAction registerGuiClass;
    
    
    
    /**
     * 
     * @param key
     */
    public static void keyboardHandler(char key) {
        Collector col = Collector.getInstance();
        switch (key) {
        
        //change current Colorset
        case 'C':
            Visual v = col.getVisual(col.getCurrentVisual());
            if (v!=null) {
                int currentColorSet = v.getColorSetIndex();
                int colorSetsNrs = col.getColorSets().size();
                
                if (currentColorSet++>=colorSetsNrs-1) {
                    currentColorSet=0;
                }
                v.setColorSet(currentColorSet);
                registerGuiClass.refreshGui();
            }
            break;

        //randomize
        case 'R':
            Shuffler.manualShuffleStuff();
            break;
            
        //change open tabs
        case 'T':
            //TODO
            break;          
            
        default:
            break;
        }   

        
        if(key>='1' && key<'9') {
            if (registerGuiClass!=null) {
                // convert a key-number (48-52) to an int between 0 and 4
                int n = (int)key-49;
                registerGuiClass.activeVisual(n);
            }
        }           
    }



    /**
     * @param registerGuiClass the registerGuiClass to set
     */
    public static void setRegisterGuiClass(GuiCallbackAction registerGuiClass) {
        KeyboardHandler.registerGuiClass = registerGuiClass;
    }
    
    
}
