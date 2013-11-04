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
package com.neophob.sematrix.cli;

import static org.junit.Assert.*;

import org.junit.Test;

import com.neophob.sematrix.properties.ValidCommands;

/**
 * verify the rotate buffer code
 * @author michu
 *
 */
public class PixConClientTest extends PixConClient {


    @Test(expected = InvalidParameterException.class)
    public void parserTestNoParameter() throws Exception {    	    	
    	parseArgument(new String[0]);
    }

    @Test
    public void parserTestRandomize1() throws Exception {
        String[] param = new String[4];
        param[0] = "-c";
        param[1] = "RANDOMIZE";
        param[2] = "-p";
        param[3] = "34234";

        ParsedArgument cmd = parseArgument(param);

        assertEquals("127.0.0.1", cmd.getHostname());
        assertEquals(34234, cmd.getPort());
        assertEquals(ValidCommands.RANDOMIZE, cmd.getCommand());
        assertEquals("", cmd.getParameter());
    }

    @Test
    public void parserTestRandomize2() throws Exception {
        String[] param = new String[2];
        param[0] = "-c";
        param[1] = "RANDOMIZE";

        ParsedArgument cmd = parseArgument(param);

        assertEquals("127.0.0.1", cmd.getHostname());
        assertEquals(3448, cmd.getPort());
        assertEquals(ValidCommands.RANDOMIZE, cmd.getCommand());
        assertEquals("", cmd.getParameter());
    }

    @Test(expected = InvalidParameterException.class)
    public void parserTestRandomize3() throws Exception {
        String[] param = new String[5];
        param[0] = "-c";
        param[1] = "RANDOMIZE";
        param[2] = "-p";
        param[3] = "2222";
        param[4] = "blah";
        
        ParsedArgument cmd = parseArgument(param);

        assertEquals("127.0.0.1", cmd.getHostname());
        assertEquals(2222, cmd.getPort());
        assertEquals(ValidCommands.RANDOMIZE, cmd.getCommand());
        assertEquals("blah", cmd.getParameter());
    }

    @Test(expected = InvalidParameterException.class)
    public void parserTestSimple3() throws Exception {
        String[] param = new String[8];
        param[0] = "-c";
        param[1] = "CHANGE_THRESHOLD_VALUE";
        param[2] = "";
        param[3] = "9";        
        param[4] = "-h";
        param[5] = "1.2.3.4";
        param[6] = "1";
        param[7] = "2";
        
        parseArgument(param);
    }
}
