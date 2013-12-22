package com.neophob.sematrix.core.perf;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Adler32;

import net.jpountz.xxhash.XXHash32;
import net.jpountz.xxhash.XXHashFactory;

import com.neophob.sematrix.core.resize.PixelResize;

public class PerfTests {

    private static final Logger LOG = Logger.getLogger(PerfTests.class.getName());

    private int pixelSize;
    private int rounds;
    private byte[] data;
    private long t1;

    public PerfTests(int rounds, int pixelSize) {
        this.rounds = rounds;
        this.pixelSize = pixelSize;
        this.data = new byte[pixelSize * pixelSize * 2];
        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = (byte) (i);
        }
        LOG.log(Level.INFO,
                "\n\nPerformance test using {0} rounds and pixelsize of {1} ({2} buffer size).",
                new Object[] { rounds, pixelSize, data.length });
    }

    private long measureAdler32Hash() {
        preTest();
        Adler32 adler = new Adler32();
        for (int i = 0; i < rounds; i++) {
            adler.reset();
            adler.update(data);
            adler.getValue();
        }
        return postTest();
    }

    private long measureXXHash() {
        preTest();
        XXHashFactory factory = XXHashFactory.fastestInstance();
        XXHash32 hash32 = factory.hash32();
        for (int i = 0; i < rounds; i++) {
            hash32.hash(data, 0, data.length, 0x9747b28c);
        }
        return postTest();
    }

    private long measureVisual() {
        preTest();

        return postTest();
    }

    private void measureResize() {
        int cnt = rounds / 200;
        int largeSize = pixelSize * 8;
        int smallSize = pixelSize;
        LOG.log(Level.INFO,
                "Pixel Resize: {0} rounds, output buffer: {1} bytes, visual buffer: {2} bytes",
                new Object[] { cnt, smallSize * smallSize, largeSize * largeSize });

        preTest();
        PixelResize res = new PixelResize();
        int[] buffer = new int[largeSize * largeSize];
        for (int i = 0; i < cnt; i++) {
            res.resizeImage(buffer, largeSize, largeSize, smallSize, smallSize);
        }
        long time = postTest();
        if (cnt == 0) {
            cnt = 1;
        }
        long timePerResize = time * 1000L / cnt;
        LOG.log(Level.INFO, ">>> PixelResize needed {0}ms - {1}ns per resize", new Object[] { time,
                timePerResize });
    }

    private void preTest() {
        this.t1 = System.currentTimeMillis();
    }

    private long postTest() {
        return System.currentTimeMillis() - t1;
    }

    public void startPerfTest() {
        long t = measureAdler32Hash();
        long timePerHash = t * 1000L / rounds;
        LOG.log(Level.INFO, ">>> Adler32 Hash: {0}ms - {1}ns per hash", new Object[] { t,
                timePerHash });

        t = measureXXHash();
        timePerHash = t * 1000L / rounds;
        LOG.log(Level.INFO, ">>> XXHash: {0}ms - {1}ns per hash", new Object[] { t, timePerHash });

        measureVisual();
        measureResize();

    }
}
