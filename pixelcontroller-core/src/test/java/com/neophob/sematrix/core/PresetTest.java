package com.neophob.sematrix.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.glue.FileUtilsJunit;
import com.neophob.sematrix.core.listener.MessageProcessor;
import com.neophob.sematrix.core.output.JunitColorSet;
import com.neophob.sematrix.core.preset.PresetFactory;
import com.neophob.sematrix.core.preset.PresetService;
import com.neophob.sematrix.core.preset.PresetServiceImpl;
import com.neophob.sematrix.core.preset.PresetSettings;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.properties.ValidCommand;
import com.neophob.sematrix.core.sound.SoundDummy;
import com.neophob.sematrix.core.visual.VisualState;
import com.neophob.sematrix.core.visual.color.IColorSet;

public class PresetTest {

    private static final Logger LOG = Logger.getLogger(PresetTest.class.getName());

    @Test
    public void loadAllPresetTest() {
        FileUtils fu = new FileUtilsJunit();
        List<PresetSettings> presets = PresetFactory.loadPresetsFile(fu.getDataDir());
        PresetService ps = new PresetServiceImpl(presets);
        Assert.assertTrue(ps.getPresets().size() > 0);

        List<IColorSet> col = new ArrayList<IColorSet>();
        col.add(new JunitColorSet());
        VisualState.getInstance().init(fu, new ApplicationConfigurationHelper(new Properties()),
                new SoundDummy(), col, ps);

        MessageProcessor.INSTANCE.init(ps, fu);
        for (int i = 0; i < ps.getPresets().size(); i++) {
            LOG.info("Load Preset " + i);
            ps.setSelectedPreset(i);
            MessageProcessor.INSTANCE.processMsg(
                    new String[] { ValidCommand.LOAD_PRESET.toString() }, false, null);
        }
    }
}
