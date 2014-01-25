package com.neophob.sematrix.core.preset;

import java.util.List;

public interface PresetService {

    int NR_OF_PRESET_SLOTS = 144;

    /**
     * @return the selectedPreset
     */
    int getSelectedPreset();

    /**
     * @param selectedPreset
     *            the selectedPreset to set
     */
    void setSelectedPreset(int selectedPreset);

    /**
     * 
     * @return the selected preset settings
     */
    PresetSettings getSelectedPresetSettings();

    /**
     * @return the presets
     */
    List<PresetSettings> getPresets();

    /**
     * get a list of entries to load a preset. the returned values are optimized
     * so not needed actions like loading a blinkenlights file while no
     * blinkenlights generator is active are removed.
     * 
     * @return
     */
    List<String> getActivePreset();

    void saveActivePreset(String name, List<String> presetString);

}