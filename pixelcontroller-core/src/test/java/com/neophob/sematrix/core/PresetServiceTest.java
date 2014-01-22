package com.neophob.sematrix.core;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.neophob.sematrix.core.preset.PresetService;
import com.neophob.sematrix.core.preset.PresetServiceImpl;
import com.neophob.sematrix.core.preset.PresetSettings;

public class PresetServiceTest {

    private static final String PRESET = "CURRENT_VISUAL 0;CHANGE_GENERATOR_A 8;CHANGE_GENERATOR_B 3;CHANGE_EFFECT_A 0;CHANGE_EFFECT_B 0;CHANGE_MIXER 0;CURRENT_COLORSET RGB;CURRENT_VISUAL 1;CHANGE_GENERATOR_A 2;CHANGE_GENERATOR_B 12;CHANGE_EFFECT_A 0;CHANGE_EFFECT_B 0;CHANGE_MIXER 0;CURRENT_COLORSET Cake;CURRENT_VISUAL 2;CHANGE_GENERATOR_A 4;CHANGE_GENERATOR_B 10;CHANGE_EFFECT_A 0;CHANGE_EFFECT_B 0;CHANGE_MIXER 3;CURRENT_COLORSET Wayyou;CURRENT_OUTPUT 0;CHANGE_OUTPUT_FADER 1;CHANGE_OUTPUT_VISUAL 0;CURRENT_OUTPUT 1;CHANGE_OUTPUT_FADER 1;CHANGE_OUTPUT_VISUAL 0;CHANGE_BRIGHTNESS 100;GENERATOR_SPEED 40;CHANGE_ROTOZOOM -31;CHANGE_THRESHOLD_VALUE 150;TEXTDEF 10;ZOOMOPT 3;BLINKEN torus.bml;IMAGE _logo.gif;TEXTWR PIXELINVADERS;TEXTWR_OPTION 0;COLOR_SCROLL_OPT 14;BEAT_WORKMODE 1;CHANGE_SHUFFLER_SELECT  1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1;CHANGE_PRESET 0;presetname=Default;";

    @Test
    public void loadPreset() {
        PresetSettings preset = new PresetSettings();
        preset.setPreset(PRESET.split(";"));
        Assert.assertEquals("Default", preset.getName());

        List<PresetSettings> presets = new ArrayList<PresetSettings>();
        presets.add(preset);

        PresetService presetService = new PresetServiceImpl(presets);

    }
}
