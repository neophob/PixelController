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
package com.neophob.sematrix.glue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.Test;

import com.neophob.sematrix.color.ColorSet;
import com.neophob.sematrix.effect.Effect;
import com.neophob.sematrix.effect.PassThru;
import com.neophob.sematrix.fader.IFader;
import com.neophob.sematrix.fader.Switch;
import com.neophob.sematrix.generator.Fire;
import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.layout.LayoutModel;
import com.neophob.sematrix.mixer.Mixer;
import com.neophob.sematrix.mixer.PassThruMixer;
import com.neophob.sematrix.output.NullDevice;
import com.neophob.sematrix.output.Output;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.properties.ConfigConstant;

/**
 * test internal buffer size
 * 
 * @author michu
 *
 */
public class MatrixDataTest {
    
    @Test
    public void processMessages() throws Exception {
    	//verify the buffer get multiplied with 8
    	MatrixData matrix = new MatrixData(8,8);
    	assertEquals(64, matrix.getBufferXSize());
    	assertEquals(64, matrix.getBufferYSize());
    	
    	//verify the buffer get multiplied with 8
    	matrix = new MatrixData(16,16);
    	assertEquals(128, matrix.getBufferXSize());
    	
    	//verify the buffer get multiplied with 4
    	matrix = new MatrixData(32,32);
    	assertEquals(128, matrix.getBufferXSize());
    	
    	//verify the buffer get multiplied with 2
    	matrix = new MatrixData(64,64);
    	assertEquals(128, matrix.getBufferXSize());

    	//verify the buffer get multiplied with 1
    	matrix = new MatrixData(512, 512);
    	assertEquals(512, matrix.getBufferXSize());
    	
    	matrix = new MatrixData(24,18);
    }

    @Test
    public void testMatrixStretch() {
        Properties config = new Properties();
        config.put(ConfigConstant.NULLOUTPUT_ROW1, "2");
        config.put(ConfigConstant.NULLOUTPUT_ROW2, "2");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
        Output output = new NullDevice(ph);
    	
    	FileUtils fileUtils = new FileUtils();
    	Collector.getInstance().init(fileUtils, ph);

    	Mixer m = new PassThruMixer();
    	ColorSet c = new ColorSet("JUNIT", new int[]{123233,232323,100,200});

    	LayoutModel lmDefault = new LayoutModel(1, 1, 0, 0, 0);
    	LayoutModel lmBox1 = new LayoutModel(2, 1, 0, 0, 0);
    	LayoutModel lmBox2 = new LayoutModel(2, 1, 32, 32, 0);
    	LayoutModel lmBox4 = new LayoutModel(2, 2, 32, 32, 0);

    	for (int y=1; y<38; y++) {
        	for (int x=1; x<38; x++) {
            	MatrixData matrix = new MatrixData(x,y);

            	Generator g = new Fire(matrix);
            	Effect e = new PassThru(matrix);
                Visual visual = new Visual(g, e, m, c);        
            	    	
            	IFader fader = new Switch(matrix, 100);
            	OutputMapping map = new OutputMapping(fader, 0);
            	
                assertNotNull(matrix.getScreenBufferForDevice(visual, map));    	
                assertNotNull(matrix.getScreenBufferForDevice(visual, lmDefault, map, output));
                assertNotNull(matrix.getScreenBufferForDevice(visual, lmBox1, map, output));
                assertNotNull(matrix.getScreenBufferForDevice(visual, lmBox2, map, output));
                assertNotNull(matrix.getScreenBufferForDevice(visual, lmBox4, map, output));
            	
        	}    		
    	}
    }

}
