package com.neophob.sematrix.gui.service.impl;

import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * observer class, update gui if new state arrives
 * @author michu
 *
 */
public class RemoteOscObservable extends Observable {

	private static final Logger LOG = Logger.getLogger(RemoteOscObservable.class.getName());

	public RemoteOscObservable() {

	}

	public void notifyGuiUpdate(List<String> guiState) {
		LOG.log(Level.INFO, "Refresh GUI content");
		setChanged();
		notifyObservers(guiState);
	}

}
