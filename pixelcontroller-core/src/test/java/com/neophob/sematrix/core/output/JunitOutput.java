package com.neophob.sematrix.core.output;

import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.visual.MatrixData;

public class JunitOutput extends OnePanelResolutionAwareOutput {

    public JunitOutput(MatrixData matrix, PixelControllerResize resize,
            ApplicationConfigurationHelper ph) {
        super(matrix, resize, OutputDeviceEnum.ARTNET, ph, 8);
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
