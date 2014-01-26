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
        pixelController.start();
        int delay = 0;
        while (!pixelController.isInitialized()) {
            Thread.sleep(100);
            delay += 100;
            if (delay > 6000) {
                throw new InterruptedException();
            }
        }
        Assert.assertTrue(updateHandled);
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
