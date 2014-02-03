/**
 * Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
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

import java.util.HashMap;
import java.util.Map;
import java.util.zip.Adler32;

public class BufferCache {

    private static Adler32 adler;
    protected transient Map<Byte, Long> lastDataMap = new HashMap<Byte, Long>();

    public BufferCache() {
        adler = new Adler32();
    }

    public boolean didFrameChange(byte[] data) {
        return didFrameChange((byte) 0, data);
    }

    public boolean didFrameChange(byte ofs, byte[] data) {
        if (data == null) {
            return false;
        }

        adler.reset();
        adler.update(data);
        long l = adler.getValue();

        if (!lastDataMap.containsKey(ofs)) {
            // first run
            lastDataMap.put(ofs, l);
            return true;
        }

        if (lastDataMap.get(ofs) == l) {
            // last frame was equal current frame, do not send it!
            return false;
        }
        // update new hash
        lastDataMap.put(ofs, l);
        return true;
    }

    public void resetHash(byte ofs) {
        lastDataMap.put(ofs, 0L);
    }

}
