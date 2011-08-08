package com.neophob;

import org.junit.Test;

import com.neophob.sematrix.effect.PixelControllerEffect;
import com.neophob.sematrix.generator.PixelControllerGenerator;
import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.mixer.PixelControllerMixer;
import com.neophob.sematrix.output.PixelControllerOutput;
import com.neophob.sematrix.properties.PropertiesHelper;
import com.neophob.sematrix.resize.PixelControllerResize;

public class PixelControllerTest {

	@Test
	public void testMain() {
		PixelControllerResize pixelControllerResize = new PixelControllerResize();
		pixelControllerResize.initAll();

		//create generators
		PixelControllerGenerator pixelControllerGenerator = new PixelControllerGenerator();
		pixelControllerGenerator.initAll();
		
		PixelControllerEffect pixelControllerEffect = new PixelControllerEffect();
		pixelControllerEffect.initAll();

		PixelControllerMixer pixelControllerMixer = new PixelControllerMixer();
		pixelControllerMixer.initAll();
		
		//create 5 visuals
		Visual.initializeVisuals(5);
				
		PixelControllerOutput pixelControllerOutput = new PixelControllerOutput();
		pixelControllerOutput.initAll();
		
	}
}
