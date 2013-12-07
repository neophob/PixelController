package com.neophob.sematrix.core.api.impl;

import java.util.Observable;

import com.neophob.sematrix.core.api.CallbackMessageInterface;
import com.neophob.sematrix.core.api.PixelController;

/**
 * abstract class, implements observer class
 * 
 * @author michu
 *
 */
public abstract class PixelControllerServer extends Observable implements PixelController, Runnable {

	/**
	 * 
	 * @param handler
	 */
	public PixelControllerServer(CallbackMessageInterface<String> handler) {
		//register the caller as observer
		addObserver(handler);		
	}

	protected synchronized void clientNotification(final String msg) {
		setChanged();
        notifyObservers(msg);
	}
	
}
