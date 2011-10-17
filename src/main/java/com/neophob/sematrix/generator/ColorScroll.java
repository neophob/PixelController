/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
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

package com.neophob.sematrix.generator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.ShufflerOffset;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 *
 * @author McGyver
 */
public class ColorScroll extends Generator {

    private int fade;
    private ScrollMode scrollMode;
    private List<Color> colorMap;
    private int frameCount;
    private int maxFrames;
    
    enum ScrollMode{
    	LEFT_TO_RIGHT(0),
    	RIGHT_TO_LEFT(1),
    	TOP_TO_BOTTOM(2),
    	BOTTOM_TO_TOP(3),
    	
    	RIGHT_BOTTOM_TO_LEFT_TOP(4),
    	LEFT_BOTTOM_TO_RIGHT_TOP(5),
    	RIGHT_TOP_TO_LEFT_BOTTOM(6),
    	LEFT_TOP_TO_RIGHT_BOTTOM(7),
    	
    	MIDDLE_TO_SIDES_VERTICAL(8),
    	SIDES_TO_MIDDLE_VERTICAL(9),
    	MIDDLE_TO_SIDES_HORIZONTAL(10),
    	SIDES_TO_MIDDLE_HORIZONTAL(11),
    	
    	EXPLODE_CIRCLE(12),
    	IMPLODE_CIRCLE(13);
    	
    	private int mode;
    	
    	ScrollMode(int mode) {
    		this.mode = mode;
    	}
    	
    	public int getMode() {
    		return mode;
    	}
    	 
    	public static ScrollMode getScrollMode(int nr) {
    		for (ScrollMode s: ScrollMode.values()) {
    			if (s.getMode() == nr) {
    				return s;
    			}
    		}
    		
    		return null;
    	}

    }
    
    /**
     * Instantiates a new colorscroll
     *
     * @param controller the controller
     */
    public ColorScroll(PixelControllerGenerator controller, List<Integer> colorList) {
        super(controller, GeneratorName.COLOR_SCROLL, ResizeName.QUALITY_RESIZE);

        colorMap = new ArrayList<Color>();
        for (Integer i: colorList) {
        	colorMap.add(new Color(i));
        }
        fade = 30;
        scrollMode = ScrollMode.EXPLODE_CIRCLE;

        maxFrames = colorMap.size() * fade;
    }

    
    /* (non-Javadoc)
     * @see com.neophob.sematrix.generator.Generator#update()
     */
    @Override
    public void update() {
        frameCount = (frameCount + 1) % maxFrames;
        // scroll colors on x axis
        switch (scrollMode) {
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

    /**
     * 
     * @param scrollMode
     */
    void setScrollMode(int scrollMode) {
        this.scrollMode = ScrollMode.getScrollMode(scrollMode);
        this.frameCount = 0;
    }

    /**
     * 
     * @param fadeLength
     */
    void setFadeLength(int fadeLength) {
        this.fade = fadeLength;
        maxFrames = colorMap.size() * fade;
    }
    
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.generator.Generator#shuffle()
	 */
	@Override
	public void shuffle() {
		if (Collector.getInstance().getShufflerSelect(ShufflerOffset.COLOR_SCROLL)) {
			Random rand = new Random();
			int nr = rand.nextInt(ScrollMode.values().length);
			setScrollMode(nr);	
		}
	}
    
    /**
     * 
     * @param val
     * @return
     */
    private int[] getColor(int val) {
        int colornumber = (int) ((Math.round(Math.floor((val + frameCount) / fade))) % colorMap.size());
        int nextcolornumber = (colornumber + 1) % colorMap.size();
        double ratio = ((val + frameCount) % fade) / (float)fade;

        int rThis = colorMap.get(colornumber).getRed();
        int rNext = colorMap.get(nextcolornumber).getRed();
        int gThis = colorMap.get(colornumber).getGreen();
        int gNext = colorMap.get(nextcolornumber).getGreen();
        int bThis = colorMap.get(colornumber).getBlue();
        int bNext = colorMap.get(nextcolornumber).getBlue();

        int[] ret = new int[3];
        ret[0] = rThis - (int) Math.round((rThis - rNext) * (ratio));
        ret[1] = gThis - (int) Math.round((gThis - gNext) * (ratio));
        ret[2] = bThis - (int) Math.round((bThis - bNext) * (ratio));
        
        return ret;
    }
    

    /**
     * 
     */
    private void leftToRight() {
        int ySize = internalBufferYSize;

        for (int x = 0; x < internalBufferXSize; x++) {
        	
        	int[] col = getColor(x);
            for (int y = 0; y < ySize; y++) {
                this.internalBuffer[y * internalBufferXSize + x] = (col[0] << 16) | (col[1] << 8) | col[2];
            }
        }
    }

    /**
     * 
     */
    private void rightToLeft() {
        int ySize = internalBufferYSize;

        for (int x = 0; x < internalBufferXSize; x++) {
            int xRev = internalBufferXSize - x - 1;
            
            int[] col = getColor(x);
            for (int y = 0; y < ySize; y++) {
                this.internalBuffer[y * internalBufferXSize + xRev] = (col[0] << 16) | (col[1] << 8) | col[2];
            }
        }
    }

    /**
     * 
     */
    private void topToBottom() {
        int ySize = internalBufferYSize;

        for (int y = 0; y < internalBufferXSize; y++) {
            int yRev = internalBufferXSize - y - 1;
            
            int[] col = getColor(y);            
            for (int x = 0; x < ySize; x++) {
                this.internalBuffer[yRev * internalBufferXSize + x] = (col[0] << 16) | (col[1] << 8) | col[2];
            }
        }
    }

    /**
     * 
     */
    private void bottomToTop() {
        int ySize = internalBufferYSize;

        for (int y = 0; y < internalBufferXSize; y++) {
            int[] col = getColor(y);
            for (int x = 0; x < ySize; x++) {
                this.internalBuffer[y * internalBufferXSize + x] = (col[0] << 16) | (col[1] << 8) | col[2];
            }
        }
    }

    /**
     * 
     */
    private void rightBottomToLeftTop() {
        for (int diagStep = 0; diagStep < internalBufferXSize + internalBufferYSize; diagStep++) {

        	int[] col = getColor(diagStep);

            int diagPixelCount = diagStep;
            int diagOffset = 0;
            if (diagStep >= internalBufferXSize) {
                diagPixelCount = (2 * internalBufferXSize) - diagStep;
                diagOffset = diagStep - internalBufferXSize;
            }

            for (int diagCounter = 0; diagCounter < diagPixelCount; diagCounter++) {
                int x = diagOffset + diagCounter;
                int y = diagPixelCount - diagCounter - 1 + diagOffset;
                this.internalBuffer[y * internalBufferXSize + x] = (col[0] << 16) | (col[1] << 8) | col[2];
            }
        }
    }

    /**
     * 
     */
    private void leftBottomToRightTop() {
        for (int diagStep = 0; diagStep < internalBufferXSize + internalBufferYSize; diagStep++) {
        	int[] col = getColor(diagStep);

            int diagPixelCount = diagStep;
            int diagOffset = 0;
            if (diagStep >= internalBufferXSize) {
                diagPixelCount = (2 * internalBufferXSize) - diagStep;
                diagOffset = diagStep - internalBufferXSize;
            }

            for (int diagCounter = 0; diagCounter < diagPixelCount; diagCounter++) {
                int x = internalBufferXSize - 1 - (diagOffset + diagCounter);
                int y = diagPixelCount - diagCounter - 1 + diagOffset;
                this.internalBuffer[y * internalBufferXSize + x] = (col[0] << 16) | (col[1] << 8) | col[2];
            }
        }
    }

    /**
     * 
     */
    private void rightTopToLeftBottom() {
        for (int diagStep = 0; diagStep < internalBufferXSize + internalBufferYSize; diagStep++) {
        	int[] col = getColor(diagStep);

            int diagPixelCount = diagStep;
            int diagOffset = 0;
            if (diagStep >= internalBufferXSize) {
                diagPixelCount = (2 * internalBufferXSize) - diagStep;
                diagOffset = diagStep - internalBufferXSize;
            }

            for (int diagCounter = 0; diagCounter < diagPixelCount; diagCounter++) {
                int x = diagOffset + diagCounter;
                int y = internalBufferXSize - 1 - (diagPixelCount - diagCounter - 1 + diagOffset);
                this.internalBuffer[y * internalBufferXSize + x] = (col[0] << 16) | (col[1] << 8) | col[2];
            }
        }
    }

    /**
     * 
     */
    private void leftTopToRightBottom() {
        for (int diagStep = 0; diagStep < internalBufferXSize + internalBufferYSize; diagStep++) {
        	int[] col = getColor(diagStep);

        	int diagPixelCount = diagStep;
            int diagOffset = 0;
            if (diagStep >= internalBufferXSize) {
                diagPixelCount = (2 * internalBufferXSize) - diagStep;
                diagOffset = diagStep - internalBufferXSize;
            }

            for (int diagCounter = 0; diagCounter < diagPixelCount; diagCounter++) {
                int x = internalBufferXSize - 1 - (diagOffset + diagCounter);
                int y = internalBufferYSize - 1 - (diagPixelCount - diagCounter - 1 + diagOffset);
                this.internalBuffer[y * internalBufferXSize + x] = (col[0] << 16) | (col[1] << 8) | col[2];
            }
        }
    }

    /**
     * 
     */
    private void middleToSidesVertical() {
        int ySize = internalBufferYSize;

        for (int x = 0; x < internalBufferXSize / 2; x++) {
        	int[] col = getColor(x);

            for (int y = 0; y < ySize; y++) {
                this.internalBuffer[y * internalBufferXSize + x] = (col[0] << 16) | (col[1] << 8) | col[2];
                this.internalBuffer[y * internalBufferXSize + internalBufferXSize - x - 1] = (col[0] << 16) | (col[1] << 8) | col[2];
            }
        }
    }

    /**
     * 
     */
    private void sidesToMiddleVertical() {
        int ySize = internalBufferYSize;

        for (int x = 0; x < internalBufferXSize / 2; x++) {

            int xRev = (internalBufferXSize / 2) - x - 1;
            int[] col = getColor(x);
            for (int y = 0; y < ySize; y++) {
                this.internalBuffer[y * internalBufferXSize + xRev] = (col[0] << 16) | (col[1] << 8) | col[2];
                this.internalBuffer[y * internalBufferXSize + internalBufferXSize - xRev - 1] = (col[0] << 16) | (col[1] << 8) | col[2];
            }
        }
    }

    /**
     * 
     */
    private void middleToSidesHorizontal() {
        int xSize = internalBufferXSize;

        for (int y = 0; y < internalBufferYSize / 2; y++) {

        	int[] col = getColor(y);

            for (int x = 0; x < xSize; x++) {
                this.internalBuffer[y * internalBufferXSize + x] = (col[0] << 16) | (col[1] << 8) | col[2];
                this.internalBuffer[(internalBufferYSize - y - 1) * internalBufferXSize + x] = (col[0] << 16) | (col[1] << 8) | col[2];
            }
        }
    }

    /**
     * 
     */
    private void sidesToMiddleHorizontal() {
        int xSize = internalBufferXSize;

        for (int y = 0; y < internalBufferYSize / 2; y++) {

            int yRev = (internalBufferYSize / 2) - y - 1;
            int[] col = getColor(y);

            for (int x = 0; x < xSize; x++) {
                this.internalBuffer[yRev * internalBufferXSize + x] = (col[0] << 16) | (col[1] << 8) | col[2];
                this.internalBuffer[(internalBufferYSize - yRev - 1) * internalBufferXSize + x] = (col[0] << 16) | (col[1] << 8) | col[2];
            }
        }
    }

    /**
     * 
     */
    private void implodeCircle() {

        int x0 = internalBufferXSize / 2;
        int y0 = internalBufferYSize / 2;

        for (int r = 0; r < Math.max(internalBufferXSize, internalBufferYSize) * 1.42; r++) {
        	int[] col = getColor(r);

            int f = 1 - r;
            int ddFx = 1;
            int ddFy = -2 * r;
            int x = 0;
            int y = r;

            setPixel(x0, y0 + r, col[0], col[1], col[2]);
            setPixel(x0, y0 - r, col[0], col[1], col[2]);
            setPixel(x0 + r, y0, col[0], col[1], col[2]);
            setPixel(x0 - r, y0, col[0], col[1], col[2]);

            while (x < y) {
                if (f >= 0) {
                    y--;
                    ddFy += 2;
                    f += ddFy;
                }
                x++;
                ddFx += 2;
                f += ddFx;
                setPixel(x0 + x, y0 + y, col[0], col[1], col[2]);
                setPixel(x0 - x, y0 + y, col[0], col[1], col[2]);
                setPixel(x0 + x, y0 - y, col[0], col[1], col[2]);
                setPixel(x0 - x, y0 - y, col[0], col[1], col[2]);
                setPixel(x0 + y, y0 + x, col[0], col[1], col[2]);
                setPixel(x0 - y, y0 + x, col[0], col[1], col[2]);
                setPixel(x0 + y, y0 - x, col[0], col[1], col[2]);
                setPixel(x0 - y, y0 - x, col[0], col[1], col[2]);

                //double line to mind gaps
                setPixel(x0 + x + 1, y0 + y, col[0], col[1], col[2]);
                setPixel(x0 - x + 1, y0 + y, col[0], col[1], col[2]);
                setPixel(x0 + x + 1, y0 - y, col[0], col[1], col[2]);
                setPixel(x0 - x + 1, y0 - y, col[0], col[1], col[2]);
                setPixel(x0 + y + 1, y0 + x, col[0], col[1], col[2]);
                setPixel(x0 - y + 1, y0 + x, col[0], col[1], col[2]);
                setPixel(x0 + y + 1, y0 - x, col[0], col[1], col[2]);
                setPixel(x0 - y + 1, y0 - x, col[0], col[1], col[2]);
                
                setPixel(x0 + x + 1, y0 + y + 1, col[0], col[1], col[2]);
                setPixel(x0 - x + 1, y0 + y + 1, col[0], col[1], col[2]);
                setPixel(x0 + x + 1, y0 - y + 1, col[0], col[1], col[2]);
                setPixel(x0 - x + 1, y0 - y + 1, col[0], col[1], col[2]);
                setPixel(x0 + y + 1, y0 + x + 1, col[0], col[1], col[2]);
                setPixel(x0 - y + 1, y0 + x + 1, col[0], col[1], col[2]);
                setPixel(x0 + y + 1, y0 - x + 1, col[0], col[1], col[2]);
                setPixel(x0 - y + 1, y0 - x + 1, col[0], col[1], col[2]);
                
            }
        }
    }
    
    /**
     * 
     */
    private void explodeCircle() {

        int x0 = internalBufferXSize / 2;
        int y0 = internalBufferYSize / 2;

        for (int r = 0; r < Math.max(internalBufferXSize, internalBufferYSize) * 1.42; r++) {            
            int rRev = (int) (Math.max(internalBufferXSize, internalBufferYSize) * 1.42) - r;

            int[] col = getColor(rRev);

            int f = 1 - r;
            int ddFx = 1;
            int ddFy = -2 * r;
            int x = 0;
            int y = r;

            setPixel(x0, y0 + r, col[0], col[1], col[2]);
            setPixel(x0, y0 - r, col[0], col[1], col[2]);
            setPixel(x0 + r, y0, col[0], col[1], col[2]);
            setPixel(x0 - r, y0, col[0], col[1], col[2]);

            while (x < y) {
                if (f >= 0) {
                    y--;
                    ddFy += 2;
                    f += ddFy;
                }
                x++;
                ddFx += 2;
                f += ddFx;
                setPixel(x0 + x, y0 + y, col[0], col[1], col[2]);
                setPixel(x0 - x, y0 + y, col[0], col[1], col[2]);
                setPixel(x0 + x, y0 - y, col[0], col[1], col[2]);
                setPixel(x0 - x, y0 - y, col[0], col[1], col[2]);
                setPixel(x0 + y, y0 + x, col[0], col[1], col[2]);
                setPixel(x0 - y, y0 + x, col[0], col[1], col[2]);
                setPixel(x0 + y, y0 - x, col[0], col[1], col[2]);
                setPixel(x0 - y, y0 - x, col[0], col[1], col[2]);

                //double line to mind gaps
                setPixel(x0 + x + 1, y0 + y, col[0], col[1], col[2]);
                setPixel(x0 - x + 1, y0 + y, col[0], col[1], col[2]);
                setPixel(x0 + x + 1, y0 - y, col[0], col[1], col[2]);
                setPixel(x0 - x + 1, y0 - y, col[0], col[1], col[2]);
                setPixel(x0 + y + 1, y0 + x, col[0], col[1], col[2]);
                setPixel(x0 - y + 1, y0 + x, col[0], col[1], col[2]);
                setPixel(x0 + y + 1, y0 - x, col[0], col[1], col[2]);
                setPixel(x0 - y + 1, y0 - x, col[0], col[1], col[2]);
                
                setPixel(x0 + x + 1, y0 + y + 1, col[0], col[1], col[2]);
                setPixel(x0 - x + 1, y0 + y + 1, col[0], col[1], col[2]);
                setPixel(x0 + x + 1, y0 - y + 1, col[0], col[1], col[2]);
                setPixel(x0 - x + 1, y0 - y + 1, col[0], col[1], col[2]);
                setPixel(x0 + y + 1, y0 + x + 1, col[0], col[1], col[2]);
                setPixel(x0 - y + 1, y0 + x + 1, col[0], col[1], col[2]);
                setPixel(x0 + y + 1, y0 - x + 1, col[0], col[1], col[2]);
                setPixel(x0 - y + 1, y0 - x + 1, col[0], col[1], col[2]);
            }
        }
    }

    /**
     * 
     * @param x
     * @param y
     * @param R
     * @param G
     * @param B
     */
    private void setPixel(int x, int y, int r, int g, int b) {
        if (y >= 0 && y < internalBufferYSize && x >= 0 && x < internalBufferXSize) {
            this.internalBuffer[y * internalBufferXSize + x] = (r << 16) | (g << 8) | b;
        }
    }

}
