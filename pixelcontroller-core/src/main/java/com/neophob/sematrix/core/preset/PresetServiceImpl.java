/**
 * Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.core.preset;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author michu
 * 
 */
public class PresetServiceImpl implements PresetService {

    private static final Logger LOG = Logger.getLogger(PresetServiceImpl.class.getName());

    private List<PresetSettings> presets;

    private int selectedPreset;

    /**
     * 
     * @param fileUtils
     */
    public PresetServiceImpl(List<PresetSettings> presets) {
        selectedPreset = 0;
        this.presets = presets;
        LOG.log(Level.INFO, "Preset Service initialized, contains {0} elements", presets.size());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.preset.PresetService#getSelectedPreset()
     */
    @Override
    public int getSelectedPreset() {
        return selectedPreset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.neophob.sematrix.core.preset.PresetService#setSelectedPreset(int)
     */
    @Override
    public void setSelectedPreset(int selectedPreset) {
        if (selectedPreset < 0 || selectedPreset > NR_OF_PRESET_SLOTS) {
            LOG.log(Level.WARNING, "Ignore invalid selected preset: " + selectedPreset);
            return;
        }
        this.selectedPreset = selectedPreset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.preset.PresetService#getPresets()
     */
    @Override
    public List<PresetSettings> getAllPresets() {
        return presets;
    }

    public PresetSettings getSelectedPresetSettings() {
        return presets.get(selectedPreset);
    }

    /**
     * remove obsolete commands form preset
     * 
     * @param preset
     * @return
     */
    private List<String> removeObsoleteCommands(List<String> preset) {
        /* Global output gain should not be affected by presets
           Yes, this is a little hacky; we could use a blacklist of
           settings that never get saved to or read from presets */
        if (preset.contains("CHANGE_OUTPUT_GAIN")) {
            int ofs = 0;
            for (String s : preset) {
                if (s.startsWith("CHANGE_OUTPUT_GAIN")) {
                    LOG.log(Level.INFO, "Removing Output gain from preset");
                    break;
                }
                ofs++;
            }
            preset.remove(ofs);
        }
        if (!preset.contains("CHANGE_GENERATOR_B 1") && !preset.contains("CHANGE_GENERATOR_A 1")) {
            LOG.log(Level.INFO, "No Blinkengenerator found, remove loading blinken resource");
            int ofs = 0;
            for (String s : preset) {
                if (s.startsWith("BLINKEN")) {
                    break;
                }
                ofs++;
            }
            preset.remove(ofs);
        }
        if (!preset.contains("CHANGE_GENERATOR_B 2") && !preset.contains("CHANGE_GENERATOR_A 2")) {
            LOG.log(Level.INFO, "No Imagegenerator found, remove loading image resource");
            int ofs = 0;
            for (String s : preset) {
                if (s.startsWith("IMAGE")) {
                    break;
                }
                ofs++;
            }
            preset.remove(ofs);
        }
        return preset;
    }

    @Override
    public List<String> getActivePreset() {
        List<String> preset = presets.get(selectedPreset).getPreset();
        if (preset != null) {
            // remove not needed commands (load image, load blinkenlight...)
            return removeObsoleteCommands(new ArrayList<String>(preset));
        }

        // else return empty array if no saved preset is found
        return new ArrayList<String>();
    }

    @Override
    public void saveActivePreset(String name, List<String> presetString) {
        PresetSettings currentPreset = getSelectedPresetSettings();
        currentPreset.setName(name);
        currentPreset.setPreset(presetString);
    }
}
