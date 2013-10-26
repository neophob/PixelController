/**
 * Copyright (C) 2011-2013 Michael Vogt <michu@neophob.com>
 *
 * This file is part of PixelController.
 *
 * PixelController is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PixelController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PixelController.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.neophob.sematrix.listener;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;
import org.mockito.Mock;

import processing.core.PApplet;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.properties.ConfigConstant;
import com.neophob.sematrix.properties.ValidCommands;

/**
 * verify the rotate buffer code
 * @author michu
 *
 */
public class MessageProcessorTest {
    
	@Mock private PApplet papplet;

    @Test
    public void processMessages() {
		Properties config = new Properties();
		config.put(ConfigConstant.RESOURCE_PATH, "/Users/michu/_code/workspace/PixelController.github/PixelController");
		ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
    	
    	Collector.getInstance().init(papplet, ph);
   
    	
    	String[] str = null;
    	MessageProcessor.processMsg(str, false, null);
    	
    	str = new String[2];
    	str[0] = "AAAAAAAAAA";
    	str[1] = "ALSOINVALID";
    	MessageProcessor.processMsg(str, false, null);
    	
    	str[0] = "CURRENT_VISUAL";
    	str[1] = "23323223";
    	MessageProcessor.processMsg(str, false, null);
    	
    	str[0] = "CURRENT_OUTPUT";
    	str[1] = "99999";
    	MessageProcessor.processMsg(str, false, null);
    	
    	str[0] = "COLOR_FADE_LENGTH";
    	str[1] = "0";
    	MessageProcessor.processMsg(str, false, null);
    	
    	str[0] = "CHANGE_GENERATOR_A";
    	str[1] = "9999990";    	
    	MessageProcessor.processMsg(str, false, null);

    	str = new String[1];
    	str[0] = "CHANGE_GENERATOR_A";
    	MessageProcessor.processMsg(str, false, null);

    	str = new String[1];
    	str[0] = "STATUS";
    	assertEquals(ValidCommands.STATUS, MessageProcessor.processMsg(str, false, null));

    	//test real life use case
    	str = new String[2];
    	str[0] = "CURRENT_VISUAL";
    	str[1] = "0";    	
    	MessageProcessor.processMsg(str, false, null);

    	str[0] = "CHANGE_GENERATOR_A";
    	str[1] = "2";    	
    	MessageProcessor.processMsg(str, false, null);
    	
    	str[0] = "CHANGE_EFFECT_B";
    	str[1] = "5";    	
    	MessageProcessor.processMsg(str, false, null);

    	str[0] = "CHANGE_MIXER";
    	str[1] = "1";    	
    	MessageProcessor.processMsg(str, false, null);

    	assertEquals(2, Collector.getInstance().getVisual(0).getGenerator1Idx());
    	assertEquals(5, Collector.getInstance().getVisual(0).getEffect2Idx());
    	assertEquals(1, Collector.getInstance().getVisual(0).getMixerIdx());
    }


}
