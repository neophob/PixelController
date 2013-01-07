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

import org.junit.Test;

import com.neophob.sematrix.output.misc.MD5;

/**
 * verify the rotate buffer code
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
    public void speedTestOld() throws NoSuchAlgorithmException {
        byte[] b = new byte[192];

        long pre = System.currentTimeMillis();
        MessageDigest md = MessageDigest.getInstance("MD5");

        for (int i=0; i<ROUNDS; i++) {
            MD5SpeedTest.getMD5(md, b);   
        }               
        long post = System.currentTimeMillis();

        long time = post-pre;
        float avg = (float)time / (float)ROUNDS;
        LOG.log(Level.INFO,"rainbowduino needed {0}ms, avg: {1}", new Object[] {time, avg});
    }
    
    @Test
    public void speedTestNew() {
        byte[] b = new byte[192];

        long pre = System.currentTimeMillis();

        for (int i=0; i<ROUNDS; i++) {
            MD5.asHex(b);   
        }               
        long post = System.currentTimeMillis();

        long time = post-pre;
        float avg = (float)time / (float)ROUNDS;        
        LOG.log(Level.INFO,"MD5.asHex needed {0}ms, avg: {1}", new Object[] {time, avg});
    }


}
