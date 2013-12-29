package com.neophob.sematrix.core.output;

import java.util.zip.Adler32;

public class BufferCache {

    private static Adler32 adler;
    private long lastDataMap;

    public BufferCache() {
        adler = new Adler32();
    }

    public boolean didFrameChange(byte[] data) {
        if (data == null) {
            return false;
        }

        adler.reset();
        adler.update(data);
        long l = adler.getValue();

        if (lastDataMap == l) {
            // last frame was equal current frame, do not send it!
            return false;
        }
        // update new hash
        lastDataMap = l;
        return true;
    }

}
