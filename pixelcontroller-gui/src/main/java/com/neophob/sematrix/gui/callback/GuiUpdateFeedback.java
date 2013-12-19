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
package com.neophob.sematrix.gui.callback;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.gui.guibuilder.GuiCallbackAction;

/**
 * observer pattern implementation for client callback
 * 
 * 
 * @author michu
 *
 */
public class GuiUpdateFeedback implements Observer {

	private static final Logger LOG = Logger.getLogger(GuiUpdateFeedback.class.getName());
	
	private GuiState state;
	private GuiCallbackAction callBackAction;
	
	/**
	 * 
	 * @param callBackAction
	 */
	public GuiUpdateFeedback(GuiCallbackAction callBackAction) {
		this.callBackAction = callBackAction;
		this.state = new GuiState();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof List<?>) {
			
			state.updateState((List<?>)arg);
			Map<String, String> diff = state.getDiff();
			if (diff.size()>0) {
				callBackAction.updateGuiElements(diff);
				LOG.log(Level.INFO, "{0} settings updated.", diff.size());				
			}			
        } else {
        	LOG.log(Level.WARNING, "Ignored notification of unknown type: "+arg);
        }
	}

}
