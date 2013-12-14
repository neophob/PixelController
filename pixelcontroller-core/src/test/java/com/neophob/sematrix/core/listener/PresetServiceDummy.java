package com.neophob.sematrix.core.listener;

import java.util.List;

import com.neophob.sematrix.core.preset.PresetService;
import com.neophob.sematrix.core.preset.PresetSettings;

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
	public void savePresents() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PresetSettings getSelectedPresetSettings() {
		// TODO Auto-generated method stub
		return null;
	}

}
