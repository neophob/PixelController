package com.neophob.sematrix.core.preset;

import java.io.BufferedInputStream;
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

public final class PresetFactory {

    private static final Logger LOG = Logger.getLogger(PresetFactory.class.getName());

    private static final String PRESETS_FILENAME = "presets.properties";

    private PresetFactory() {
        // no instance allowed
    }

    /**
     * Load presents.
     */
    public static List<PresetSettings> loadPresetsFile(String filePath) {
        Properties props = new Properties();
        String filename = filePath + File.separator + PRESETS_FILENAME;

        List<PresetSettings> presets = new ArrayList<PresetSettings>(
                PresetService.NR_OF_PRESET_SLOTS);
        for (int i = 0; i < PresetService.NR_OF_PRESET_SLOTS; i++) {
            presets.add(new PresetSettings());
        }

        InputStream input = null;
        try {
            long t1 = System.currentTimeMillis();
            input = new BufferedInputStream(new FileInputStream(filename));
            props.load(input);
            String s;
            int count = 0;
            for (int i = 0; i < PresetService.NR_OF_PRESET_SLOTS; i++) {
                s = props.getProperty("" + i);
                if (StringUtils.isNotBlank(s)) {
                    presets.get(i).setPreset(s.split(";"));
                    count++;
                }
            }
            long t2 = System.currentTimeMillis() - t1;
            LOG.log(Level.INFO, "Loaded {0} presets from file {1} in {2}ms", new Object[] { count,
                    PRESETS_FILENAME, t2 });
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

    /**
     * 
     * @param presets
     */
    public static void writePresetFile(List<PresetSettings> presets, String filePath) {
        String filename = filePath + File.separator + PRESETS_FILENAME;

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
}
