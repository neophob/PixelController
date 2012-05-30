package com.neophob;

import java.util.List;

import org.junit.Test;

import com.neophob.sematrix.color.ColorSet;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;

/**
 * test start
 * @author michu
 *
 */
public class PixelControllerChildTest extends PixelController {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5991306744881489366L;
	
	@Test
	public void testSetup() {
		List<ColorSet> colorSets = super.getColorPalettes();
		
		ApplicationConfigurationHelper applicationConfig = super.getAppliactionConfiguration();
		
		super.getOutputDevice(applicationConfig);
		
	}

	

}
