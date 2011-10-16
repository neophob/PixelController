/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neophob.sematrix.generator;

import com.neophob.sematrix.resize.Resize.ResizeName;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author McGyver
 */
public class ColorFade extends Generator {

    private int fade;
    private int maxFrames;
    private ArrayList<Color> colorMap;
    private int frameCount;

    /**
     * Instantiates a new colorscroll
     *
     * @param controller the controller
     */
    public ColorFade(PixelControllerGenerator controller) {
        super(controller, GeneratorName.COLOR_FADE, ResizeName.QUALITY_RESIZE);

        colorMap = new ArrayList<Color>();
        //put some colors for testing
        colorMap.add(new Color(0, 0, 128));
        colorMap.add(new Color(0, 0, 0));

        fade = 30;

        maxFrames = colorMap.size() * fade;
    }

    @Override
    public void update() {
        frameCount = (frameCount + 1) % maxFrames;

        int colornumber = (int) ((Math.round(Math.floor(frameCount / fade))) % colorMap.size());
        int nextcolornumber = (colornumber + 1) % colorMap.size();
        double ratio = (frameCount % fade);
        ratio = ratio / fade;

        int Rthis = colorMap.get(colornumber).getRed();
        int Rnext = colorMap.get(nextcolornumber).getRed();
        int Gthis = colorMap.get(colornumber).getGreen();
        int Gnext = colorMap.get(nextcolornumber).getGreen();
        int Bthis = colorMap.get(colornumber).getBlue();
        int Bnext = colorMap.get(nextcolornumber).getBlue();

        int R = Rthis - (int) Math.round((Rthis - Rnext) * (ratio));
        int G = Gthis - (int) Math.round((Gthis - Gnext) * (ratio));
        int B = Bthis - (int) Math.round((Bthis - Bnext) * (ratio));
        
        Arrays.fill(this.internalBuffer, (R << 16) | (G << 8) | (B));
    }

    void setFadeTime(int colorFadeTime) {
        this.fade = colorFadeTime;
    }
}
