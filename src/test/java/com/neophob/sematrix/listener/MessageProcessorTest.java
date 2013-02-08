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

import org.junit.Test;

/**
 * verify the rotate buffer code
 * @author michu
 *
 */
public class MessageProcessorTest {
    
    @Test
    public void processMessages() {
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

    }


}
