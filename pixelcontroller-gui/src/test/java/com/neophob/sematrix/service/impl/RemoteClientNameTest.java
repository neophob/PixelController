package com.neophob.sematrix.service.impl;

import java.util.Observable;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.neophob.sematrix.core.api.CallbackMessageInterface;
import com.neophob.sematrix.gui.service.impl.RemoteClientName;

public class RemoteClientNameTest implements CallbackMessageInterface<String> {

    @Test
    public void testDefault() {
        RemoteClientName rcn = new RemoteClientName(this, null);
        rcn.queryRemoteName();
        Assert.assertEquals(rcn.getTargetHost(), RemoteClientName.DEFAULT_TARGET_HOST);
    }

    @Test
    public void testManual() {
        final String targetValue = "1.2.3.4";
        final String targetKey = "remote.client.host";
        Properties p = new Properties();
        p.put(targetKey, targetValue);
        RemoteClientName rcn = new RemoteClientName(this, p);
        rcn.queryRemoteName();
        Assert.assertEquals(rcn.getTargetHost(), targetValue);
    }

    @Override
    public void update(Observable paramObservable, Object paramObject) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleMessage(String msg) {
        // TODO Auto-generated method stub

    }
}
