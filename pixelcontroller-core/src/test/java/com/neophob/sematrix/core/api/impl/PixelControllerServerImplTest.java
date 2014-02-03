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
package com.neophob.sematrix.core.api.impl;

import java.util.Observable;

import org.junit.Assert;
import org.junit.Test;

import com.neophob.sematrix.core.api.CallbackMessageInterface;
import com.neophob.sematrix.core.api.PixelController;

public class PixelControllerServerImplTest implements CallbackMessageInterface<String> {

    private boolean updateHandled = false;

    @Test
    public void testPixelControllerMain() throws Exception {
        PixelController pixelController = PixelControllerFactory.initialize(this);
        Assert.assertNotNull(pixelController.getVersion());
        /*
         * pixelController.start(); int delay = 0; while
         * (!pixelController.isInitialized()) { Thread.sleep(100); delay += 100;
         * if (delay > 6000) { throw new InterruptedException(); } }
         * 
         * Assert.assertTrue(updateHandled);
         */
    }

    @Override
    public void update(Observable paramObservable, Object paramObject) {
        updateHandled = true;
    }

    @Override
    public void handleMessage(String msg) {
        // TODO Auto-generated method stub
        System.out.println("handle");
    }
}
