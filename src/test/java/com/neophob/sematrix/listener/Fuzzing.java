package com.neophob.sematrix.listener;

import java.util.Random;
import java.util.UUID;

import org.junit.Test;

import processing.core.PApplet;

import com.neophob.sematrix.properties.ValidCommands;

/**
 * manual fuzzing test
 * 
 * @author michu
 *
 */
public class Fuzzing {
    
    //@Test
    public void processMessages() {
                
        PApplet.main(new String[] { "com.neophob.PixelController" });
        
        ValidCommands[] allCommands = ValidCommands.values();
        Random r = new Random();
        
        for (int i=0; i<10000; i++) {
            ValidCommands cmd = allCommands[r.nextInt(allCommands.length)];
            String[] param = new String[2];
            param[0] = cmd.toString();
            
            if (i%3==0) {
                param[1] = ""+r.nextInt(512);
            } else if (i%3==1) {
                param[1] = ""+r.nextFloat();
            } else {
                param[1] = UUID.randomUUID().toString();
            }
            
            MessageProcessor.processMsg(param, false);
        }
    }


}
