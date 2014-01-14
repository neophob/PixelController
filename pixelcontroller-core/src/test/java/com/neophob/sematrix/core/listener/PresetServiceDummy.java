package com.neophob.sematrix.core.listener;

import java.util.List;

import com.neophob.sematrix.core.preset.PresetService;
import com.neophob.sematrix.core.preset.PresetSettings;
import com.neophob.sematrix.core.visual.VisualState;

public class PresetServiceDummy implements PresetService {

    @Override
    public int getSelectedPreset() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setSelectedPreset(int selectedPreset) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<PresetSettings> getPresets() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PresetSettings getSelectedPresetSettings() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void writePresetFile() {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadActivePreset(VisualState visualState) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveActivePreset(String name, List<String> presetString) {
        // TODO Auto-generated method stub

    }

}
