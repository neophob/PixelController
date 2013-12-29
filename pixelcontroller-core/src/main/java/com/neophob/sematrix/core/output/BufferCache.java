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
