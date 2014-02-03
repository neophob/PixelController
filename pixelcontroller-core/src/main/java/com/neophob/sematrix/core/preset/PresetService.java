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