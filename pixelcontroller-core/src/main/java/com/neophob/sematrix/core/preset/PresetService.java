package com.neophob.sematrix.core.preset;

import java.util.List;

import com.neophob.sematrix.core.visual.VisualState;

public interface PresetService {

    final int NR_OF_PRESET_SLOTS = 144;

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
     * Save presents.
     */
    void writePresetFile();

    /**
     * load a preset
     * 
     * @param nr
     * @param visualState
     */
    void loadActivePreset(VisualState visualState);

    void saveActivePreset(String name, List<String> presetString);

}