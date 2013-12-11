package com.neophob.sematrix.core.preset;

import java.util.List;

public interface PresetService {

	/**
	 * @return the selectedPreset
	 */
	int getSelectedPreset();

	/**
	 * @param selectedPreset the selectedPreset to set
	 */
	void setSelectedPreset(int selectedPreset);

	/**
	 * @return the presets
	 */
	List<PresetSettings> getPresets();

	/**
	 * Save presents.
	 */
	void savePresents();

}