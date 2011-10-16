package com.neophob.sematrix.generator;

import com.neophob.sematrix.resize.Resize.ResizeName;
import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author McGyver
 */
public class ColorScroll extends Generator {

    private int fade;
    private int scrollDir;
    private ArrayList<Color> colorMap;
    private int frameCount;
    private int maxFrames;
    
    private static final byte LEFT_TO_RIGHT = 1;
    private static final byte RIGHT_TO_LEFT = 2;
    private static final byte TOP_TO_BOTTOM = 3;
    private static final byte BOTTOM_TO_TOP = 4;
    
    private static final byte RIGHT_BOTTOM_TO_LEFT_TOP = 5;
    private static final byte LEFT_BOTTOM_TO_RIGHT_TOP = 6;
    private static final byte RIGHT_TOP_TO_LEFT_BOTTOM = 7;
    private static final byte LEFT_TOP_TO_RIGHT_BOTTOM = 8;
    
    private static final byte MIDDLE_TO_SIDES_VERTICAL = 9;
    private static final byte SIDES_TO_MIDDLE_VERTICAL = 10;
    private static final byte MIDDLE_TO_SIDES_HORIZONTAL = 11;
    private static final byte SIDES_TO_MIDDLE_HORIZONTAL = 12;
    
    private static final byte EXPLODE_CIRCLE = 13;
    private static final byte IMPLODE_CIRCLE = 14;
    

    /**
     * Instantiates a new colorscroll
     *
     * @param controller the controller
     */
    public ColorScroll(PixelControllerGenerator controller) {
        super(controller, GeneratorName.COLOR_SCROLL, ResizeName.QUALITY_RESIZE);

        colorMap = new ArrayList<Color>();
        //put some colors for testing
        colorMap.add(new Color(255, 128, 128));
        colorMap.add(new Color(255, 255, 128));
        colorMap.add(new Color(128, 255, 128));
        colorMap.add(new Color(128, 255, 255));
        colorMap.add(new Color(128, 128, 255));
        colorMap.add(new Color(255, 128, 255));

        fade = 30;
        scrollDir = EXPLODE_CIRCLE;

        maxFrames = colorMap.size() * fade;
    }

    /* (non-Javadoc)
     * @see com.neophob.sematrix.generator.Generator#update()
     */
    @Override
    public void update() {
        frameCount = (frameCount + 1) % maxFrames;
        // scrol colors on x axis
        switch (scrollDir) {
            case LEFT_TO_RIGHT:
                leftToRight();
                break;
            case RIGHT_TO_LEFT:
                rightToLeft();
                break;
            case TOP_TO_BOTTOM:
                topToBottom();
                break;
            case BOTTOM_TO_TOP:
                bottomToTop();
                break;
            case RIGHT_BOTTOM_TO_LEFT_TOP:
                rightBottomToLeftTop();
                break;
            case LEFT_BOTTOM_TO_RIGHT_TOP:
                leftBottomToRightTop();
                break;
            case RIGHT_TOP_TO_LEFT_BOTTOM:
                rightTopToLeftBottom();
                break;
            case LEFT_TOP_TO_RIGHT_BOTTOM:
                leftTopToRightBottom();
                break;
            case MIDDLE_TO_SIDES_VERTICAL:
                middleToSidesVertical();
                break;
            case SIDES_TO_MIDDLE_VERTICAL:
                sidesToMiddleVertical();
                break;
            case MIDDLE_TO_SIDES_HORIZONTAL:
                middleToSidesHorizontal();
                break;
            case SIDES_TO_MIDDLE_HORIZONTAL:
                sidesToMiddleHorizontal();
                break;
            case EXPLODE_CIRCLE:
                explodeCircle();
                break;
            case IMPLODE_CIRCLE:
                implodeCircle();
                break;
        }
    }

    void setColorScrollDir(int colorScrollDir) {
        this.scrollDir = colorScrollDir;
        this.frameCount = 0;
    }

    void setFadeLength(int fadeLength) {
        this.fade = fadeLength;
        maxFrames = colorMap.size() * fade;
    }

    private void leftToRight() {
        int ySize = internalBufferYSize;

        for (int x = 0; x < internalBufferXSize; x++) {

            int colornumber = (int) ((Math.round(Math.floor((x + frameCount) / fade))) % colorMap.size());
            int nextcolornumber = (colornumber + 1) % colorMap.size();
            double ratio = ((x + frameCount) % fade);
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

            for (int y = 0; y < ySize; y++) {
                this.internalBuffer[y * internalBufferXSize + x] = (R << 16) | (G << 8) | (B);
            }
        }
    }

    private void rightToLeft() {
        int ySize = internalBufferYSize;

        for (int x = 0; x < internalBufferXSize; x++) {

            int x_rev = internalBufferXSize - x - 1;
            int colornumber = (int) ((Math.round(Math.floor((x + frameCount) / fade))) % colorMap.size());
            int nextcolornumber = (colornumber + 1) % colorMap.size();
            double ratio = ((x + frameCount) % fade);
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

            for (int y = 0; y < ySize; y++) {
                this.internalBuffer[y * internalBufferXSize + x_rev] = (R << 16) | (G << 8) | (B);
            }
        }
    }

    private void topToBottom() {
        int ySize = internalBufferYSize;

        for (int y = 0; y < internalBufferXSize; y++) {

            int y_rev = internalBufferXSize - y - 1;
            int colornumber = (int) ((Math.round(Math.floor((y + frameCount) / fade))) % colorMap.size());
            int nextcolornumber = (colornumber + 1) % colorMap.size();
            double ratio = ((y + frameCount) % fade);
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

            for (int x = 0; x < ySize; x++) {
                this.internalBuffer[y_rev * internalBufferXSize + x] = (R << 16) | (G << 8) | (B);
            }
        }
    }

    private void bottomToTop() {
        int ySize = internalBufferYSize;

        for (int y = 0; y < internalBufferXSize; y++) {

            int colornumber = (int) ((Math.round(Math.floor((y + frameCount) / fade))) % colorMap.size());
            int nextcolornumber = (colornumber + 1) % colorMap.size();
            double ratio = ((y + frameCount) % fade);
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

            for (int x = 0; x < ySize; x++) {
                this.internalBuffer[y * internalBufferXSize + x] = (R << 16) | (G << 8) | (B);
            }
        }
    }

    private void rightBottomToLeftTop() {
        for (int diagStep = 0; diagStep < internalBufferXSize + internalBufferYSize; diagStep++) {

            int colornumber = (int) ((Math.round(Math.floor((diagStep + frameCount) / fade))) % colorMap.size());
            int nextcolornumber = (colornumber + 1) % colorMap.size();
            double ratio = ((diagStep + frameCount) % fade);
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

            int diagPixelCount = diagStep;
            int diagOffset = 0;
            if (diagStep >= internalBufferXSize) {
                diagPixelCount = (2 * internalBufferXSize) - diagStep;
                diagOffset = diagStep - internalBufferXSize;
            }

            for (int diagCounter = 0; diagCounter < diagPixelCount; diagCounter++) {
                int x = diagOffset + diagCounter;
                int y = diagPixelCount - diagCounter - 1 + diagOffset;
                this.internalBuffer[y * internalBufferXSize + x] = (R << 16) | (G << 8) | (B);
            }
        }
    }

    private void leftBottomToRightTop() {
        for (int diagStep = 0; diagStep < internalBufferXSize + internalBufferYSize; diagStep++) {

            int colornumber = (int) ((Math.round(Math.floor((diagStep + frameCount) / fade))) % colorMap.size());
            int nextcolornumber = (colornumber + 1) % colorMap.size();
            double ratio = ((diagStep + frameCount) % fade);
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

            int diagPixelCount = diagStep;
            int diagOffset = 0;
            if (diagStep >= internalBufferXSize) {
                diagPixelCount = (2 * internalBufferXSize) - diagStep;
                diagOffset = diagStep - internalBufferXSize;
            }

            for (int diagCounter = 0; diagCounter < diagPixelCount; diagCounter++) {
                int x = internalBufferXSize - 1 - (diagOffset + diagCounter);
                int y = diagPixelCount - diagCounter - 1 + diagOffset;
                this.internalBuffer[y * internalBufferXSize + x] = (R << 16) | (G << 8) | (B);
            }
        }
    }

    private void rightTopToLeftBottom() {
        for (int diagStep = 0; diagStep < internalBufferXSize + internalBufferYSize; diagStep++) {

            int colornumber = (int) ((Math.round(Math.floor((diagStep + frameCount) / fade))) % colorMap.size());
            int nextcolornumber = (colornumber + 1) % colorMap.size();
            double ratio = ((diagStep + frameCount) % fade);
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

            int diagPixelCount = diagStep;
            int diagOffset = 0;
            if (diagStep >= internalBufferXSize) {
                diagPixelCount = (2 * internalBufferXSize) - diagStep;
                diagOffset = diagStep - internalBufferXSize;
            }

            for (int diagCounter = 0; diagCounter < diagPixelCount; diagCounter++) {
                int x = diagOffset + diagCounter;
                int y = internalBufferXSize - 1 - (diagPixelCount - diagCounter - 1 + diagOffset);
                this.internalBuffer[y * internalBufferXSize + x] = (R << 16) | (G << 8) | (B);
            }
        }
    }

    private void leftTopToRightBottom() {
        for (int diagStep = 0; diagStep < internalBufferXSize + internalBufferYSize; diagStep++) {

            int colornumber = (int) ((Math.round(Math.floor((diagStep + frameCount) / fade))) % colorMap.size());
            int nextcolornumber = (colornumber + 1) % colorMap.size();
            double ratio = ((diagStep + frameCount) % fade);
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

            int diagPixelCount = diagStep;
            int diagOffset = 0;
            if (diagStep >= internalBufferXSize) {
                diagPixelCount = (2 * internalBufferXSize) - diagStep;
                diagOffset = diagStep - internalBufferXSize;
            }

            for (int diagCounter = 0; diagCounter < diagPixelCount; diagCounter++) {
                int x = internalBufferXSize - 1 - (diagOffset + diagCounter);
                int y = internalBufferYSize - 1 - (diagPixelCount - diagCounter - 1 + diagOffset);
                this.internalBuffer[y * internalBufferXSize + x] = (R << 16) | (G << 8) | (B);
            }
        }
    }

    private void middleToSidesVertical() {
        int ySize = internalBufferYSize;

        for (int x = 0; x < internalBufferXSize / 2; x++) {

            int colornumber = (int) ((Math.round(Math.floor((x + frameCount) / fade))) % colorMap.size());
            int nextcolornumber = (colornumber + 1) % colorMap.size();
            double ratio = ((x + frameCount) % fade);
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

            for (int y = 0; y < ySize; y++) {
                this.internalBuffer[y * internalBufferXSize + x] = (R << 16) | (G << 8) | (B);
                this.internalBuffer[y * internalBufferXSize + internalBufferXSize - x - 1] = (R << 16) | (G << 8) | (B);
            }
        }
    }

    private void sidesToMiddleVertical() {
        int ySize = internalBufferYSize;

        for (int x = 0; x < internalBufferXSize / 2; x++) {

            int x_rev = (internalBufferXSize / 2) - x - 1;
            int colornumber = (int) ((Math.round(Math.floor((x + frameCount) / fade))) % colorMap.size());
            int nextcolornumber = (colornumber + 1) % colorMap.size();
            double ratio = ((x + frameCount) % fade);
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

            for (int y = 0; y < ySize; y++) {
                this.internalBuffer[y * internalBufferXSize + x_rev] = (R << 16) | (G << 8) | (B);
                this.internalBuffer[y * internalBufferXSize + internalBufferXSize - x_rev - 1] = (R << 16) | (G << 8) | (B);
            }
        }
    }

    private void middleToSidesHorizontal() {
        int xSize = internalBufferXSize;

        for (int y = 0; y < internalBufferYSize / 2; y++) {

            int colornumber = (int) ((Math.round(Math.floor((y + frameCount) / fade))) % colorMap.size());
            int nextcolornumber = (colornumber + 1) % colorMap.size();
            double ratio = ((y + frameCount) % fade);
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

            for (int x = 0; x < xSize; x++) {
                this.internalBuffer[y * internalBufferXSize + x] = (R << 16) | (G << 8) | (B);
                this.internalBuffer[(internalBufferYSize - y - 1) * internalBufferXSize + x] = (R << 16) | (G << 8) | (B);
            }
        }
    }

    private void sidesToMiddleHorizontal() {
        int xSize = internalBufferXSize;

        for (int y = 0; y < internalBufferYSize / 2; y++) {

            int y_rev = (internalBufferYSize / 2) - y - 1;
            int colornumber = (int) ((Math.round(Math.floor((y + frameCount) / fade))) % colorMap.size());
            int nextcolornumber = (colornumber + 1) % colorMap.size();
            double ratio = ((y + frameCount) % fade);
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

            for (int x = 0; x < xSize; x++) {
                this.internalBuffer[y_rev * internalBufferXSize + x] = (R << 16) | (G << 8) | (B);
                this.internalBuffer[(internalBufferYSize - y_rev - 1) * internalBufferXSize + x] = (R << 16) | (G << 8) | (B);
            }
        }
    }

    private void implodeCircle() {

        for (int r = 0; r < Math.max(internalBufferXSize, internalBufferYSize) * 1.42; r++) {

            int colornumber = (int) ((Math.round(Math.floor((r + frameCount) / fade))) % colorMap.size());
            int nextcolornumber = (colornumber + 1) % colorMap.size();
            double ratio = ((r + frameCount) % fade);
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


            int x0 = internalBufferXSize / 2;
            int y0 = internalBufferYSize / 2;

            int f = 1 - r;
            int ddF_x = 1;
            int ddF_y = -2 * r;
            int x = 0;
            int y = r;


            setPixel(x0, y0 + r, R, G, B);
            setPixel(x0, y0 - r, R, G, B);
            setPixel(x0 + r, y0, R, G, B);
            setPixel(x0 - r, y0, R, G, B);

            while (x < y) {
                // ddF_x == 2 * x + 1;
                // ddF_y == -2 * y;
                // f == x*x + y*y - radius*radius + 2*x - y + 1;
                if (f >= 0) {
                    y--;
                    ddF_y += 2;
                    f += ddF_y;
                }
                x++;
                ddF_x += 2;
                f += ddF_x;
                setPixel(x0 + x, y0 + y, R, G, B);
                setPixel(x0 - x, y0 + y, R, G, B);
                setPixel(x0 + x, y0 - y, R, G, B);
                setPixel(x0 - x, y0 - y, R, G, B);
                setPixel(x0 + y, y0 + x, R, G, B);
                setPixel(x0 - y, y0 + x, R, G, B);
                setPixel(x0 + y, y0 - x, R, G, B);
                setPixel(x0 - y, y0 - x, R, G, B);

                //double line to mind gaps
                setPixel(x0 + x + 1, y0 + y, R, G, B);
                setPixel(x0 - x + 1, y0 + y, R, G, B);
                setPixel(x0 + x + 1, y0 - y, R, G, B);
                setPixel(x0 - x + 1, y0 - y, R, G, B);
                setPixel(x0 + y + 1, y0 + x, R, G, B);
                setPixel(x0 - y + 1, y0 + x, R, G, B);
                setPixel(x0 + y + 1, y0 - x, R, G, B);
                setPixel(x0 - y + 1, y0 - x, R, G, B);
                
                setPixel(x0 + x + 1, y0 + y + 1, R, G, B);
                setPixel(x0 - x + 1, y0 + y + 1, R, G, B);
                setPixel(x0 + x + 1, y0 - y + 1, R, G, B);
                setPixel(x0 - x + 1, y0 - y + 1, R, G, B);
                setPixel(x0 + y + 1, y0 + x + 1, R, G, B);
                setPixel(x0 - y + 1, y0 + x + 1, R, G, B);
                setPixel(x0 + y + 1, y0 - x + 1, R, G, B);
                setPixel(x0 - y + 1, y0 - x + 1, R, G, B);
                
            }
        }
    }
    
    private void explodeCircle() {

        for (int r = 0; r < Math.max(internalBufferXSize, internalBufferYSize) * 1.42; r++) {
            
            int rRev = (int) (Math.max(internalBufferXSize, internalBufferYSize) * 1.42) - r;

            int colornumber = (int) ((Math.round(Math.floor((rRev + frameCount) / fade))) % colorMap.size());
            int nextcolornumber = (colornumber + 1) % colorMap.size();
            double ratio = ((rRev + frameCount) % fade);
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


            int x0 = internalBufferXSize / 2;
            int y0 = internalBufferYSize / 2;

            int f = 1 - r;
            int ddF_x = 1;
            int ddF_y = -2 * r;
            int x = 0;
            int y = r;


            setPixel(x0, y0 + r, R, G, B);
            setPixel(x0, y0 - r, R, G, B);
            setPixel(x0 + r, y0, R, G, B);
            setPixel(x0 - r, y0, R, G, B);

            while (x < y) {
                // ddF_x == 2 * x + 1;
                // ddF_y == -2 * y;
                // f == x*x + y*y - radius*radius + 2*x - y + 1;
                if (f >= 0) {
                    y--;
                    ddF_y += 2;
                    f += ddF_y;
                }
                x++;
                ddF_x += 2;
                f += ddF_x;
                setPixel(x0 + x, y0 + y, R, G, B);
                setPixel(x0 - x, y0 + y, R, G, B);
                setPixel(x0 + x, y0 - y, R, G, B);
                setPixel(x0 - x, y0 - y, R, G, B);
                setPixel(x0 + y, y0 + x, R, G, B);
                setPixel(x0 - y, y0 + x, R, G, B);
                setPixel(x0 + y, y0 - x, R, G, B);
                setPixel(x0 - y, y0 - x, R, G, B);

                //double line to mind gaps
                setPixel(x0 + x + 1, y0 + y, R, G, B);
                setPixel(x0 - x + 1, y0 + y, R, G, B);
                setPixel(x0 + x + 1, y0 - y, R, G, B);
                setPixel(x0 - x + 1, y0 - y, R, G, B);
                setPixel(x0 + y + 1, y0 + x, R, G, B);
                setPixel(x0 - y + 1, y0 + x, R, G, B);
                setPixel(x0 + y + 1, y0 - x, R, G, B);
                setPixel(x0 - y + 1, y0 - x, R, G, B);
                
                setPixel(x0 + x + 1, y0 + y + 1, R, G, B);
                setPixel(x0 - x + 1, y0 + y + 1, R, G, B);
                setPixel(x0 + x + 1, y0 - y + 1, R, G, B);
                setPixel(x0 - x + 1, y0 - y + 1, R, G, B);
                setPixel(x0 + y + 1, y0 + x + 1, R, G, B);
                setPixel(x0 - y + 1, y0 + x + 1, R, G, B);
                setPixel(x0 + y + 1, y0 - x + 1, R, G, B);
                setPixel(x0 - y + 1, y0 - x + 1, R, G, B);
                
            }
        }
    }

    private void setPixel(float x, float y, int R, int G, int B) {
        if ((int) y >= 0 && (int) y < internalBufferYSize && (int) x >= 0 && (int) x < internalBufferXSize) {
            this.internalBuffer[(int) y * internalBufferXSize + (int) x] = (R << 16) | (G << 8) | (B);
        }
    }
}
