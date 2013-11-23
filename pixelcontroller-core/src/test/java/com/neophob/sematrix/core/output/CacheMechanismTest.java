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
package com.neophob.sematrix.core.output;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.neophob.sematrix.core.output.pixelinvaders.Lpd6803Common;
import com.neophob.sematrix.core.output.pixelinvaders.WriteDataException;

/**
 * verify the cache mechanism
 * @author michu
 *
 */
public class CacheMechanismTest {

	class DummyOutput extends Lpd6803Common {

		public DummyOutput() {
			super(8,8);
		}

		boolean ackReturnValue=false;
		
		@Override
		protected void writeData(byte[] cmdfull) throws WriteDataException {
			// TODO Auto-generated method stub			
		}

		@Override
		protected boolean waitForAck() {
			return ackReturnValue;
		}
		
		public long getCache(byte i) {
			return lastDataMap.get(i);
		}
		
		public boolean didFrameChange(byte ofs, int data[]) {
			return super.didFrameChange(ofs, data);
		}

		@Override
		protected byte[] getReplyFromController() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
    @Test
    public void speedAdler() {
    	DummyOutput out = new DummyOutput();
    	byte[] data = new byte[128];
    	int[] datai = new int[128];
    	byte ofs=(byte)0;
    	final int TPM2_HEADER_SIZE = 7;
    	
    	int sentFrames = out.sendFrame(ofs, data,1);
    	assertEquals(sentFrames, data.length+TPM2_HEADER_SIZE);
    	
    	//simulate successful data send
    	out.ackReturnValue=true;
    	sentFrames = out.sendFrame(ofs, data,1);
    	assertEquals(sentFrames, data.length+TPM2_HEADER_SIZE);
    	    	    	
    	data[0] = 20;
    	datai[0] = 20;
    	assertTrue(out.didFrameChange(ofs, datai));
    	assertFalse(out.didFrameChange(ofs, datai));
    	sentFrames = out.sendFrame(ofs, data, 1);
    	assertEquals(sentFrames, data.length+TPM2_HEADER_SIZE);
    	
    	data[1] = 120;
    	data[111] = 120;
    	datai[1] = 120;
    	datai[111] = 120;
    	assertTrue(out.didFrameChange(ofs, datai));
    	assertFalse(out.didFrameChange(ofs, datai));
    	sentFrames = out.sendFrame(ofs, data, 1);
    	assertEquals(sentFrames, data.length+TPM2_HEADER_SIZE);

    }
    
}
