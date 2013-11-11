package com.neophob.sematrix.gui.callback;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.gui.GuiCallbackAction;

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
			callBackAction.updateGuiElements(diff);
			LOG.log(Level.INFO, "{0} settings updated.", diff.size());
			
        } else {
        	LOG.log(Level.WARNING, "Ignored notification of unknown type: "+arg);
        }
	}

/*
	private void updateGuiElements() {
		for (Map.Entry<String, String> s: state.getDiff().entrySet()) {
			System.out.println("UPDATE GUI ELEMENT>> "+s.getKey()+": "+s.getValue());
		}
	}
*/
}
