package com.neophob.sematrix.core.listener;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.glue.impl.FileUtilsLocalImpl;
import com.neophob.sematrix.core.preset.PresetService;
import com.neophob.sematrix.core.preset.PresetServiceImpl;
import com.neophob.sematrix.core.preset.PresetSettings;
import com.neophob.sematrix.core.properties.Configuration;
import com.neophob.sematrix.core.sound.SoundDummy;
import com.neophob.sematrix.core.visual.VisualState;
import com.neophob.sematrix.core.visual.color.ColorSet;
import com.neophob.sematrix.core.visual.color.IColorSet;

public class RandomModeTest {

    @Before
    public void setUp() {
        FileUtils fileUtils = new FileUtilsLocalImpl();
        PresetService presetService = new PresetServiceImpl(new ArrayList<PresetSettings>());
        List<IColorSet> colorsets = new ArrayList<IColorSet>();
        colorsets.add(new ColorSet("aa", new int[] { 1, 100, 1000 }));
        colorsets.add(new ColorSet("bb", new int[] { 999, 555, 0xffffff }));

        VisualState.getInstance().init(fileUtils, new Configuration(new Properties()),
                new SoundDummy(), colorsets, presetService);

        MessageProcessor.INSTANCE.init(presetService, fileUtils);
    }

    @Test
    public void testRandomModeOffInt() throws Exception {
        String[] str = new String[2];
        str[0] = "RANDOM";
        str[1] = "" + 0;
        VisualState col = VisualState.getInstance();
        col.setRandomMode(true);
        col.setRandomPresetMode(true);
        MessageProcessor.INSTANCE.processMsg(str, false, null);
        assertEquals(false, col.isRandomMode());
        assertEquals(false, col.isRandomPresetMode());
    }

    @Test
    public void testRandomModeOffFloat() throws Exception {
        String[] str = new String[2];
        str[0] = "RANDOM";
        str[1] = "" + 0f;
        VisualState col = VisualState.getInstance();
        col.setRandomMode(true);
        col.setRandomPresetMode(true);
        MessageProcessor.INSTANCE.processMsg(str, false, null);
        assertEquals(false, col.isRandomMode());
        assertEquals(false, col.isRandomPresetMode());
    }

    @Test
    public void testRandomModeOffString() throws Exception {
        String[] str = new String[2];
        str[0] = "RANDOM";
        str[1] = "OFF";
        VisualState col = VisualState.getInstance();
        col.setRandomMode(true);
        col.setRandomPresetMode(true);
        MessageProcessor.INSTANCE.processMsg(str, false, null);
        assertEquals(false, col.isRandomMode());
        assertEquals(false, col.isRandomPresetMode());
    }

    @Test
    public void testRandomModeOnFloat() throws Exception {
        String[] str = new String[2];
        str[0] = "RANDOM";
        str[1] = "" + 1f;
        VisualState col = VisualState.getInstance();
        col.setRandomMode(false);
        col.setRandomPresetMode(false);
        MessageProcessor.INSTANCE.processMsg(str, false, null);
        assertEquals(true, col.isRandomMode());
        assertEquals(false, col.isRandomPresetMode());
    }

    @Test
    public void testRandomModeOnInt() throws Exception {
        String[] str = new String[2];
        str[0] = "RANDOM";
        str[1] = "" + 1;
        VisualState col = VisualState.getInstance();
        col.setRandomMode(false);
        col.setRandomPresetMode(false);
        MessageProcessor.INSTANCE.processMsg(str, false, null);
        assertEquals(true, col.isRandomMode());
        assertEquals(false, col.isRandomPresetMode());
    }

    @Test
    public void testRandomModeOnString() throws Exception {
        String[] str = new String[2];
        str[0] = "RANDOM";
        str[1] = "ON";
        VisualState col = VisualState.getInstance();
        col.setRandomMode(false);
        col.setRandomPresetMode(false);
        MessageProcessor.INSTANCE.processMsg(str, false, null);
        assertEquals(true, col.isRandomMode());
        assertEquals(false, col.isRandomPresetMode());
    }

    @Test
    public void testRandomPresetModeOnString() throws Exception {
        String[] str = new String[2];
        str[0] = "RANDOM_PRESET_MODE";
        str[1] = "ON";
        VisualState col = VisualState.getInstance();
        col.setRandomMode(false);
        col.setRandomPresetMode(false);
        MessageProcessor.INSTANCE.processMsg(str, false, null);
        assertEquals(false, col.isRandomMode());
        assertEquals(true, col.isRandomPresetMode());
    }

    @Test
    public void testRandomPresetModeOnInt() throws Exception {
        String[] str = new String[2];
        str[0] = "RANDOM_PRESET_MODE";
        str[1] = "" + 1;
        VisualState col = VisualState.getInstance();
        col.setRandomMode(false);
        col.setRandomPresetMode(false);
        MessageProcessor.INSTANCE.processMsg(str, false, null);
        assertEquals(false, col.isRandomMode());
        assertEquals(true, col.isRandomPresetMode());
    }

    @Test
    public void testRandomPresetModeOnFloat() throws Exception {
        String[] str = new String[2];
        str[0] = "RANDOM_PRESET_MODE";
        str[1] = "" + 1f;
        VisualState col = VisualState.getInstance();
        col.setRandomMode(false);
        col.setRandomPresetMode(false);
        MessageProcessor.INSTANCE.processMsg(str, false, null);
        assertEquals(false, col.isRandomMode());
        assertEquals(true, col.isRandomPresetMode());
    }

    @Test
    public void testRandomPresetModeOffString() throws Exception {
        String[] str = new String[2];
        str[0] = "RANDOM_PRESET_MODE";
        str[1] = "OFF";
        VisualState col = VisualState.getInstance();
        col.setRandomMode(true);
        col.setRandomPresetMode(true);
        MessageProcessor.INSTANCE.processMsg(str, false, null);
        assertEquals(false, col.isRandomMode());
        assertEquals(false, col.isRandomPresetMode());
    }

    @Test
    public void testRandomPresetModeOffInt() throws Exception {
        String[] str = new String[2];
        str[0] = "RANDOM_PRESET_MODE";
        str[1] = "" + 0;
        VisualState col = VisualState.getInstance();
        col.setRandomMode(true);
        col.setRandomPresetMode(true);
        MessageProcessor.INSTANCE.processMsg(str, false, null);
        assertEquals(false, col.isRandomMode());
        assertEquals(false, col.isRandomPresetMode());
    }

    @Test
    public void testRandomPresetModeOffFloat() throws Exception {
        String[] str = new String[2];
        str[0] = "RANDOM_PRESET_MODE";
        str[1] = "" + 0f;
        VisualState col = VisualState.getInstance();
        col.setRandomMode(true);
        col.setRandomPresetMode(true);
        MessageProcessor.INSTANCE.processMsg(str, false, null);
        assertEquals(false, col.isRandomMode());
        assertEquals(false, col.isRandomPresetMode());
    }

}
