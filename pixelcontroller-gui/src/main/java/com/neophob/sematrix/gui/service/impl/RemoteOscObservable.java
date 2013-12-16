package com.neophob.sematrix.gui.service.impl;

import java.util.List;
import java.util.Observable;

/**
 * observer class, update gui if new state arrives
 * @author michu
 *
 */
public class RemoteOscObservable extends Observable {

	public RemoteOscObservable() {

	}

	public void notifyGuiUpdate(List<String> guiState) {
		setChanged();
		notifyObservers(guiState);
	}

}
