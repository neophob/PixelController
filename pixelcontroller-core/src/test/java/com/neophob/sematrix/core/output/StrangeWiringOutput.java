package com.neophob.sematrix.core.output;

import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;

public class StrangeWiringOutput extends OnePanelResolutionAwareOutput {

    public StrangeWiringOutput(ApplicationConfigurationHelper ph) {
        super(OutputDeviceEnum.ARTNET, ph, 8);
    }

    public int[] getBuffer() {
        return super.getTransformedBuffer();
    }

    @Override
    public void update() {
    }

    @Override
    public void close() {

    }

}
