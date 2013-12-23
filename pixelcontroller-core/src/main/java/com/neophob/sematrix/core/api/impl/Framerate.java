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
package com.neophob.sematrix.core.api.impl;

import java.util.logging.Logger;

/**
 * blocking framerate limiter
 * 
 * @author michu
 * 
 */
public class Framerate {

    private static final Logger LOG = Logger.getLogger(Framerate.class.getName());

    private static final float MINIMAL_FPS = 0.001f;

    private long nextRepaintDue = 0;
    private long startTime;
    private long delay;
    private long frameCount;

    public Framerate(float targetFps) {
        this.setFps(targetFps);
    }

    public void setFps(float targetFps) {
        if (targetFps < MINIMAL_FPS) {
            targetFps = MINIMAL_FPS;
        }
        this.delay = (long) (1000f / targetFps);
        LOG.info("Target fps: " + targetFps + ", delay: " + delay + "ms");

        this.startTime = System.currentTimeMillis();
        this.frameCount = 1;
    }

    public float getFps() {
        return frameCount / (float) ((System.currentTimeMillis() - startTime) / 1000);
    }

    public long getFrameCount() {
        return frameCount;
    }

    public long getDelay() {
        return delay;
    }

    public long getFrameDelay() {
        long now = System.currentTimeMillis();
        nextRepaintDue = System.currentTimeMillis() + delay;
        frameCount++;
        return (nextRepaintDue - now);
    }
}