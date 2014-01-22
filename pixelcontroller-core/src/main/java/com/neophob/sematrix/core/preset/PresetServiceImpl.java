package com.neophob.sematrix.core.preset;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.neophob.sematrix.core.listener.MessageProcessor;

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
    public List<PresetSettings> getPresets() {
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
    public void loadActivePreset() {
        List<String> preset = presets.get(selectedPreset).getPreset();
        if (preset != null) {
            preset = removeObsoleteCommands(new ArrayList<String>(preset));

            // load preset
            this.setCurrentStatus(preset);
        }
    }

    /**
     * load a saved preset.
     * 
     * @param preset
     *            the new current status
     */
    private void setCurrentStatus(List<String> preset) {
        LOG.log(Level.FINEST, "--------------");
        long start = System.currentTimeMillis();
        // setLoadingPresent(true);
        for (String s : preset) {
            s = StringUtils.trim(s);
            s = StringUtils.removeEnd(s, ";");
            LOG.log(Level.FINEST, "LOAD PRESET: " + s);
            MessageProcessor.INSTANCE.processMsg(StringUtils.split(s, ' '), false, null);
        }
        // setLoadingPresent(false);
        long needed = System.currentTimeMillis() - start;
        LOG.log(Level.INFO, "Preset loaded in " + needed + "ms");
    }

    @Override
    public void saveActivePreset(String name, List<String> presetString) {
        PresetSettings currentPreset = getSelectedPresetSettings();
        currentPreset.setName(name);
        currentPreset.setPreset(presetString);
    }
}
