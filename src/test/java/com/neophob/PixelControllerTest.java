package com.neophob;

import static org.junit.Assert.assertTrue;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import processing.core.PApplet;

import com.neophob.sematrix.fader.PixelControllerFader;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.properties.ConfigConstant;
import com.neophob.sematrix.properties.PropertiesHelper;

/**
 * test start
 * @author michu
 *
 */
public class PixelControllerTest {

	class TestProcessingclass extends PApplet {
		public void setup() {
		    Properties config = new Properties();
	        config.put(ConfigConstant.NULLOUTPUT_ROW1, "1");
	        config.put(ConfigConstant.NULLOUTPUT_ROW2, "0");
		    PropertiesHelper ph = new PropertiesHelper(config);
			Collector.getInstance().init(this, ph);
		}
	}
	
    private static final Logger LOG = Logger.getLogger(PixelControllerTest.class.getName());
    
	@Test
	public void testMain() {
		if (!java.awt.GraphicsEnvironment.isHeadless()) {
			PApplet pa = new TestProcessingclass();

			//Jenkins is headless, so this test would not work
		    LOG.log(Level.INFO,"public void setup");			
			PApplet.main(new String[] { "com.neophob.PixelController" });			
		}
		
		assertTrue(Collector.getInstance().getPresent().size() > 0);		
		assertTrue(Collector.getInstance().getPixelControllerGenerator().getSize() > 0);
		assertTrue(Collector.getInstance().getPixelControllerEffect().getSize() > 0);
		assertTrue(Collector.getInstance().getPixelControllerMixer().getSize() > 0);
		assertTrue(Collector.getInstance().getPixelControllerResize().getAllResizers().size() > 0);
		
		assertTrue(PixelControllerFader.getFaderCount()>3);
	}
	
	@Test
	public void testSetup() {
		PixelController pc = new PixelController();
		pc.setup();
	}

}
