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
    	MessageProcessor.processMsg(str, false);
    	
    	str = new String[2];
    	str[0] = "AAAAAAAAAA";
    	str[1] = "ALSOINVALID";
    	MessageProcessor.processMsg(str, false);
    	
    	str[0] = "CURRENT_VISUAL";
    	str[1] = "23323223";
    	MessageProcessor.processMsg(str, false);
    	
    	str[0] = "CURRENT_OUTPUT";
    	str[1] = "99999";
    	MessageProcessor.processMsg(str, false);
    	
    	str[0] = "COLOR_FADE_LENGTH";
    	str[1] = "0";
    	MessageProcessor.processMsg(str, false);
    	
    	str[0] = "CHANGE_GENERATOR_A";
    	str[1] = "9999990";    	
    	MessageProcessor.processMsg(str, false);

    	str = new String[1];
    	str[0] = "CHANGE_GENERATOR_A";
    	MessageProcessor.processMsg(str, false);

    }


}
