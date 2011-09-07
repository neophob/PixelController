package com.neophob;

import static org.junit.Assert.assertTrue;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import processing.core.PApplet;

import com.neophob.sematrix.fader.PixelControllerFader;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.properties.PropertiesHelper;

/**
 * test start
 * @author michu
 *
 */
public class PixelControllerTest extends PApplet {

    private static final Logger LOG = Logger.getLogger(PixelControllerTest.class.getName());
    
	@Test
	public void testMain() {
		PApplet.main(new String[] { "com.neophob.PixelControllerTest" });
		
		assertTrue(Collector.getInstance().getPresent().size() > 0);		
		assertTrue(Collector.getInstance().getPixelControllerGenerator().getSize() > 0);
		assertTrue(Collector.getInstance().getPixelControllerEffect().getSize() > 0);
		assertTrue(Collector.getInstance().getPixelControllerMixer().getSize() > 0);
		assertTrue(Collector.getInstance().getPixelControllerResize().getAllResizers().size() > 0);
		
		assertTrue(PixelControllerFader.getFaderCount()>3);
	}
	

	
	public void setup() { 
	    LOG.log(Level.INFO,"public void setup");
	    Properties config = new Properties();     
	    PropertiesHelper ph = new PropertiesHelper(config);
		Collector.getInstance().init(this, 10, ph);
	}
	
	public void draw() {
		
	}
	
}
