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
package com.neophob.sematrix.output;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Adler32;

import org.junit.Test;

/**
 * speed test, results on my test machine:
 * 
 *   05.09.2013 09:02:45 com.neophob.sematrix.output.MD5SpeedTest speedTestMd5
 *   INFO: rainbowduino needed 704'219'613ns, avg: 7'042.196
 *   05.09.2013 09:02:45 com.neophob.sematrix.output.MD5SpeedTest speedTestAdler
 *   INFO: Adler32.asHex needed 38'249'882ns, avg: 382.499
 *
 * @author michu
 *
 */
public class MD5SpeedTest {

    private static final Logger LOG = Logger.getLogger(MD5SpeedTest.class.getName());

    private static final int ROUNDS = 100000;

    /**
     * get md5 checksum of an byte array
     * @param input
     * @return
     */
    private static String getMD5(MessageDigest md, byte[] input) {
            try {
                    byte[] messageDigest = md.digest(input);
                    BigInteger number = new BigInteger(1, messageDigest);
                    String hashtext = number.toString(16);
                    // Now we need to zero pad it if you actually want the full 32 chars.
                    while (hashtext.length() < 32) {
                            hashtext = "0" + hashtext;
                    }
                    return hashtext;
            }
            catch (Exception e) {
                    LOG.log(Level.WARNING, "Failed to calculate MD5 sum: {0}", e);
                    return "";
            }
    }
    
    @Test
    public void speedTestMd5() throws NoSuchAlgorithmException {
    	byte[] b = new byte[512];        
        for (int i=0; i<b.length; i++) b[i] = (byte)(Math.random()*255);  

        long pre = System.nanoTime();
        MessageDigest md = MessageDigest.getInstance("MD5");
        for (int i=0; i<ROUNDS; i++) {
            MD5SpeedTest.getMD5(md, b);   
        }               
        long post = System.nanoTime();

        long time = post-pre;
        float avg = (float)time / (float)ROUNDS;
        LOG.log(Level.INFO,"rainbowduino needed {0}ns, avg: {1}", new Object[] {time, avg});
    }
    
    @Test
    public void speedTestAdler() {
        byte[] b = new byte[512];
        for (int i=0; i<b.length; i++) b[i] = (byte)(Math.random()*255);
        
        Adler32 ad = new Adler32();
        
        long pre = System.nanoTime();
        for (int i=0; i<ROUNDS; i++) {
        	ad.update(b);   
        }               
        long post = System.nanoTime();

        long time = post-pre;
        float avg = (float)time / (float)ROUNDS;        
        LOG.log(Level.INFO,"Adler32.asHex needed {0}ns, avg: {1}", new Object[] {time, avg});
    }


}
