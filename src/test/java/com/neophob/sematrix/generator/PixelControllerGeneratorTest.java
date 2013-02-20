package com.neophob.sematrix.generator;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import processing.core.PApplet;

import com.neophob.sematrix.color.ColorSet;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.properties.ConfigConstant;
import com.neophob.sematrix.setup.InitApplication;

public class PixelControllerGeneratorTest {

	private PApplet applet;
	
	@Before
	public void initPapplet() {
		applet = new PApplet();
        applet.init();
        applet.setVisible(false); 
	}
	
    @Test
    public void testInitGenerator() {
    	//prepare config
    	Properties config = new Properties();
        config.put(ConfigConstant.NULLOUTPUT_ROW1, "2");
        config.put(ConfigConstant.NULLOUTPUT_ROW2, "2");
    	ApplicationConfigurationHelper ac = new ApplicationConfigurationHelper(config);

    	//init app
		List<ColorSet> colorSets = InitApplication.getColorPalettes(applet);
		Collector.getInstance().setColorSets(colorSets);
    	Collector.getInstance().init(applet, ac);

    	int stateEntries = Collector.getInstance().getPixelControllerGenerator().getCurrentState().size();
    	assertTrue(stateEntries>5);    	
	}

}
