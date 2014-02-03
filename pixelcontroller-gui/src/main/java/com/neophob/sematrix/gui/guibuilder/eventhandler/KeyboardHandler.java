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
package com.neophob.sematrix.gui.guibuilder.eventhandler;

import java.awt.event.KeyEvent;

import com.neophob.sematrix.core.properties.ValidCommand;
import com.neophob.sematrix.gui.guibuilder.GuiCallbackAction;
import com.neophob.sematrix.gui.service.PixConServer;

/**
 * 
 * PixelController Keyboard handler
 * 
 * @author mvogt
 *
 */
public final class KeyboardHandler {

	private static GuiCallbackAction registerGuiClass;
    private static PixConServer pixConServer;
    
    private KeyboardHandler() {
    	//no instance
    }
    
    /**
     * 
     * @param key
     */
    public static void keyboardHandler(char key, int keyCode) {
    	
    	//if a user press a key during the setup - ignore it!
    	if (registerGuiClass==null || pixConServer==null) {    		
    		return;
    	}
    	
    	//if we edit a textfield, ignore keyboard shortcuts
    	if (registerGuiClass.isTextfieldInEditMode()) {
    		return;
    	}
    	
        switch (key) {
        
        //change current Colorset
        case 'C':  
        	sendMsg(ValidCommand.ROTATE_COLORSET);
            break;

        //change current generator 1
        case 'F':
        	sendMsg(ValidCommand.ROTATE_GENERATOR_A);
        	break;

        //change current generator 2
        case 'G':
        	sendMsg(ValidCommand.ROTATE_GENERATOR_B);
            break;

        //change current effect 1
        case 'W':
        	sendMsg(ValidCommand.ROTATE_EFFECT_A);
            break;

        //change current effect 2
        case 'E':        	
        	sendMsg(ValidCommand.ROTATE_EFFECT_B);
            break;

        //change current mixer
        case 'M':
        	sendMsg(ValidCommand.ROTATE_MIXER);
            break;
            
        //randomize
        case 'R':
        	sendMsg(ValidCommand.RANDOMIZE);
            break;
            
        default:
            break;
        }   

        //select previous/next tab
        switch (keyCode) {
        	case KeyEvent.VK_LEFT:
        		registerGuiClass.selectPreviousTab();
        		break;                  
        	case KeyEvent.VK_RIGHT:
        		registerGuiClass.selectNextTab();
        		break;                  
        }
        
        if (key>='1' && key<'9') {
            if (registerGuiClass!=null) {
                // convert a key-number (48-52) to an int between 0 and 4
                int currentVisual = (int)key-49;
                registerGuiClass.activeVisual(currentVisual);
                pixConServer.refreshGuiState();
            }
        }
        
    }

    private static void sendMsg(ValidCommand command) {
        String[] msg = new String[1];		
        msg[0] = ""+command;
    	pixConServer.sendMessage(msg);
    }

    /**
     * @param registerGuiClass the registerGuiClass to set
     */
    public static void init(GuiCallbackAction registerGuiClass, PixConServer pixConServer) {
        KeyboardHandler.registerGuiClass = registerGuiClass;
        KeyboardHandler.pixConServer = pixConServer;
    }
    
    
}
