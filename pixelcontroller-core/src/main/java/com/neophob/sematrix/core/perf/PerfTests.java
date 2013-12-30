package com.neophob.sematrix.core.perf;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Adler32;

import net.jpountz.xxhash.XXHash32;
import net.jpountz.xxhash.XXHashFactory;

import com.neophob.sematrix.core.resize.IResize;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.Visual;
import com.neophob.sematrix.core.visual.color.ColorSet;
import com.neophob.sematrix.core.visual.color.IColorSet;
import com.neophob.sematrix.core.visual.effect.Effect;
import com.neophob.sematrix.core.visual.effect.Inverter;
import com.neophob.sematrix.core.visual.generator.Generator;
import com.neophob.sematrix.core.visual.generator.Plasma2;
import com.neophob.sematrix.core.visual.mixer.AddSat;
import com.neophob.sematrix.core.visual.mixer.Mixer;

public class PerfTests {

    private static final Logger LOG = Logger.getLogger(PerfTests.class.getName());

    private int pixelSize;
    private int rounds;
    private int smallRound;
    private byte[] data;
    private long t1;

    public PerfTests(int rounds, int pixelSize) {
        this.rounds = rounds;
        this.smallRound = rounds / 200;
        if (smallRound == 0) {
            smallRound = 1;
        }
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

        MatrixData matrix = new MatrixData(pixelSize, pixelSize);
        Generator g = new Plasma2(matrix);
        Effect e = new Inverter(matrix);
        Mixer m = new AddSat();
        IColorSet c = new ColorSet("pillepalle", new int[] { 0, 0x0000ff, 0x00ff00, 0xff0000,
                0xffffff });
        Visual v = new Visual(g, e, m, c);
        for (int i = 0; i < smallRound; i++) {
            v.getBuffer();
        }

        return postTest();
    }

    private void measureResize() {
        int largeSize = pixelSize * 8;
        int smallSize = pixelSize;
        LOG.log(Level.INFO,
                "Pixel Resize: {0} rounds, output buffer: {1} bytes, visual buffer: {2} bytes",
                new Object[] { smallRound, smallSize * smallSize, largeSize * largeSize });

        PixelControllerResize pcr = new PixelControllerResize();
        pcr.initAll();
        IResize res = pcr.getResize(ResizeName.SIMPLE_RESIZE);
        preTest();
        int[] buffer = new int[largeSize * largeSize];
        for (int i = 0; i < smallRound; i++) {
            res.resizeImage(buffer, largeSize, largeSize, smallSize, smallSize);
        }
        long time = postTest();
        long timePerResize = time * 1000L / smallRound;
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

        t = measureVisual();
        long timePerResize = t * 1000L / smallRound;
        LOG.log(Level.INFO, ">>> Visual needed {0}ms - {1}ns per getBuffer call", new Object[] { t,
                timePerResize });

        measureResize();

    }
}
