package com.neophob.sematrix.output;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import com.neophob.lib.rainbowduino.RainbowduinoHelper;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.misc.MD5;

/**
 * verify the rotate buffer code
 * @author michu
 *
 */
public class MD5SpeedTest {

    private static final Logger LOG = Logger.getLogger(MD5SpeedTest.class.getName());

    private static final int ROUNDS = 5000;

    @Test
    public void speedTestOld() {
        byte[] b = new byte[192];

        long pre = System.currentTimeMillis();

        for (int i=0; i<ROUNDS; i++) {
            RainbowduinoHelper.getMD5(b);   
        }	    	    
        long post = System.currentTimeMillis();

        LOG.log(Level.INFO,"RainbowduinoHelper.getMD5 needed {0}ms", (post-pre));
    }


    @Test
    public void speedTestNew() {
        byte[] b = new byte[192];

        long pre = System.currentTimeMillis();

        for (int i=0; i<ROUNDS; i++) {
            MD5.asHex(b);   
        }               
        long post = System.currentTimeMillis();

        LOG.log(Level.INFO,"MD5.asHex needed {0}ms", (post-pre));
    }

    @Test
    public void speedTestNexw() {
        long pre = System.currentTimeMillis();
        for (int i=0; i<ROUNDS; i++) {
            Collector.getInstance().getCurrentVisual();
        }               
        long post = System.currentTimeMillis();

        LOG.log(Level.INFO,"111111 {0}ms", (post-pre));
    }
    
    @Test
    public void speedTestNexw2() {
        Collector c = Collector.getInstance();
        long pre = System.currentTimeMillis();
        for (int i=0; i<ROUNDS; i++) {
            c.getCurrentVisual();
        }               
        long post = System.currentTimeMillis();

        LOG.log(Level.INFO,"2222222 {0}ms", (post-pre));
    }
}
