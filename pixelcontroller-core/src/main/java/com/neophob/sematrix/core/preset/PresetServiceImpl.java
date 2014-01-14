package com.neophob.sematrix.core.preset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.neophob.sematrix.core.visual.VisualState;

/**
 * 
 * @author michu
 * 
 */
public class PresetServiceImpl implements PresetService {

    private static final Logger LOG = Logger.getLogger(PresetServiceImpl.class.getName());

    private static final String PRESETS_FILENAME = "presets.properties";

    private String filename;

    private List<PresetSettings> presets;

    private int selectedPreset;

    /**
     * 
     * @param fileUtils
     */
    public PresetServiceImpl(String filePath) {
        this.filename = filePath + File.separator + PRESETS_FILENAME;
        selectedPreset = 0;
        loadPresetsFile();
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

    /**
     * Load presents.
     */
    private List<PresetSettings> loadPresetsFile() {
        Properties props = new Properties();

        presets = new ArrayList<PresetSettings>(NR_OF_PRESET_SLOTS);
        for (int i = 0; i < NR_OF_PRESET_SLOTS; i++) {
            presets.add(new PresetSettings());
        }

        InputStream input = null;
        try {
            input = new FileInputStream(filename);
            props.load(input);
            String s;
            int count = 0;
            for (int i = 0; i < NR_OF_PRESET_SLOTS; i++) {
                s = props.getProperty("" + i);
                if (StringUtils.isNotBlank(s)) {
                    presets.get(i).setPreset(s.split(";"));
                    count++;
                }
            }
            LOG.log(Level.INFO, "Loaded {0} presets from file {1}", new Object[] { count,
                    PRESETS_FILENAME });
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to load {0}, Error: {1}", new Object[] {
                    PRESETS_FILENAME, e });
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Failed to close input stream", e);
            }
        }

        return presets;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.preset.PresetService#savePresents()
     */
    @Override
    public void writePresetFile() {
        long t1 = System.currentTimeMillis();
        Properties props = new Properties();
        int idx = 0;
        for (PresetSettings p : presets) {
            props.setProperty("" + idx, p.getSettingsAsString());
            idx++;
        }

        OutputStream output = null;
        try {
            output = new FileOutputStream(filename);
            props.store(output, "Visual Daemon presets file");
            LOG.log(Level.INFO, "Presets saved as {0}, time needed: {1}ms", new Object[] {
                    PRESETS_FILENAME, (System.currentTimeMillis() - t1) });
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to save {0}, Error: {1}", new Object[] {
                    PRESETS_FILENAME, e });
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Failed to close output stream", e);
            }
        }
    }

    @Override
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
    public void loadActivePreset(VisualState visualState) {
        visualState.setLoadingPresent(true);
        // save current selections
        int currentVisual = visualState.getCurrentVisual();
        int currentOutput = visualState.getCurrentOutput();

        List<String> preset = presets.get(selectedPreset).getPreset();
        if (preset != null) {
            preset = removeObsoleteCommands(new ArrayList<String>(preset));

            // load preset
            visualState.setCurrentStatus(preset);

            // Restore current Selection
            visualState.setCurrentVisual(currentVisual);
            visualState.setCurrentOutput(currentOutput);
        }
        visualState.setLoadingPresent(false);
    }

    @Override
    public void saveActivePreset(String name, List<String> presetString) {
        PresetSettings currentPreset = getSelectedPresetSettings();
        currentPreset.setName(name);
        currentPreset.setPreset(presetString);
    }
}
