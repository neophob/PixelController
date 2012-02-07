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

import java.util.List;
import java.util.Random;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.ShufflerOffset;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * The Class ColorScroll.
 *
 * @author McGyver
 */
public class ColorScroll extends ColorMapAwareGenerator {

    /** The fade. */
    private int fade;
    
    /** The scroll mode. */
    private ScrollMode scrollMode;
    
    /** The frame count. */
    private int frameCount;
    
    /** The internal buffer x size2. */
    private int internalBufferXSize2;
    
    /** The internal buffer y size2. */
    private int internalBufferYSize2;

    
    /**
     * The Enum ScrollMode.
     */
    public enum ScrollMode{
	    LEFT_TO_RIGHT(0),
	    RIGHT_TO_LEFT(1),
	    TOP_TO_BOTTOM(2),
	    BOTTOM_TO_TOP(3),
	    RIGHTBOTTOM_TO_LEFTTOP(4),
	    LEFTBOTTOM_TO_RIGHTTOP(5),
	    RIGHTTOP_TO_LEFTBOTTOM(6),
	    LEFTTOP_TO_RIGHTBOTTOM(7),
	    MIDDLE_TO_SIDES_VERT(8),
	    SIDES_TO_MIDDLE_VERT(9),
	    MIDDLE_TO_SIDES_HORIZ(10),
	    SIDES_TO_MIDDLE_HORIZ(11),
	    EXPLODE_CIRCLE(12),
	    IMPLODE_CIRCLE(13);
    	
    	/** The mode. */
	    private int mode;
    	
    	/**
	     * Instantiates a new scroll mode.
	     *
	     * @param mode the mode
	     */
	    private ScrollMode(int mode) {
    		this.mode = mode;
    	}
    	
    	/**
	     * Gets the mode.
	     *
	     * @return the mode
	     */
	    public int getMode() {
    		return mode;
    	}
    	 
    	/**
	     * Gets the scroll mode.
	     *
	     * @param nr the nr
	     * @return the scroll mode
	     */
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
     * Instantiates a new colorscroll.
     *
     * @param controller the controller
     * @param colorList the color list
     */
    public ColorScroll(PixelControllerGenerator controller, List<Integer> colorList) {
        super(controller, GeneratorName.COLOR_SCROLL, ResizeName.QUALITY_RESIZE, colorList);

        fade = 30;
        scrollMode = ScrollMode.EXPLODE_CIRCLE;

        internalBufferXSize2 = internalBufferXSize/2;
        internalBufferYSize2 = internalBufferYSize/2;
    }

    
    /* (non-Javadoc)
     * @see com.neophob.sematrix.generator.Generator#update()
     */
    @Override
    public void update() {

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
            case RIGHTBOTTOM_TO_LEFTTOP:
                rightBottomToLeftTop();
                break;
            case LEFTBOTTOM_TO_RIGHTTOP:
                leftBottomToRightTop();
                break;
            case RIGHTTOP_TO_LEFTBOTTOM:
                rightTopToLeftBottom();
                break;
            case LEFTTOP_TO_RIGHTBOTTOM:
                leftTopToRightBottom();
                break;
            case MIDDLE_TO_SIDES_VERT:
                middleToSidesVertical();
                break;
            case SIDES_TO_MIDDLE_VERT:
                sidesToMiddleVertical();
                break;
            case MIDDLE_TO_SIDES_HORIZ:
                middleToSidesHorizontal();
                break;
            case SIDES_TO_MIDDLE_HORIZ:
                sidesToMiddleHorizontal();
                break;
            case EXPLODE_CIRCLE:
                explodeCircle();
                break;
            case IMPLODE_CIRCLE:
                implodeCircle();
                break;
        }
        
        frameCount++;
    }

    /**
     * Sets the scroll mode.
     *
     * @param scrollMode the new scroll mode
     */
    void setScrollMode(int scrollMode) {
        this.scrollMode = ScrollMode.getScrollMode(scrollMode);
    }

    /**
     * Sets the fade length.
     *
     * @param fadeLength the new fade length
     */
    void setFadeLength(int fadeLength) {
        this.fade = fadeLength;
    }
    
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.generator.Generator#shuffle()
	 */
	@Override
	public void shuffle() {
		if (Collector.getInstance().getShufflerSelect(ShufflerOffset.COLOR_SCROLL)) {
			Random rand = new Random();
			int nr = rand.nextInt(ScrollMode.values().length);
			this.setScrollMode(nr);
			this.fade = rand.nextInt(150);
		}
	}
    
    /**
     * Gets the color.
     *
     * @param val the val
     * @return the color
     */
    private int getColor(int val) {
    	int saveFade = this.fade;
    	if (saveFade==0) {
    		saveFade = 1;
    	}
    	
        int colornumber = (int) (Math.round(Math.floor((val + frameCount) / saveFade)));
        colornumber = colornumber % colorMap.size();
        int nextcolornumber = (colornumber + 1) % colorMap.size();
        float ratio = ((val + frameCount) % saveFade) / (float)saveFade;
        return super.getColor(colornumber, nextcolornumber, ratio);
    }
    

    /**
     * Left to right.
     */
    private void leftToRight() {
        for (int x = 0; x < internalBufferXSize; x++) {	
        	int col = getColor(x);
            for (int y = 0; y < internalBufferYSize; y++) {
                this.internalBuffer[y * internalBufferXSize + x] = col;
            }
        }
    }

    /**
     * Right to left.
     */
    private void rightToLeft() {
        for (int x = 0; x < internalBufferXSize; x++) {
            int xRev = internalBufferXSize - x - 1;
            
            int col = getColor(x);
            for (int y = 0; y < internalBufferYSize; y++) {
                this.internalBuffer[y * internalBufferXSize + xRev] = col;
            }
        }
    }

    /**
     * Top to bottom.
     */
    private void topToBottom() {
        for (int y = 0; y < internalBufferYSize; y++) {
            int yRev = internalBufferYSize - y - 1;
            int col = getColor(y);
            for (int x = 0; x < internalBufferXSize; x++) {
                this.internalBuffer[yRev * internalBufferXSize + x] = col;
            }
        }
    }

    /**
     * Bottom to top.
     */
    private void bottomToTop() {
        for (int y = 0; y < internalBufferYSize; y++) {
            int col = getColor(y);
            for (int x = 0; x < internalBufferXSize; x++) {
                this.internalBuffer[y * internalBufferXSize + x] = col;
            }
        }
    }

    /**
     * Right bottom to left top.
     */
    private void rightBottomToLeftTop() {
        int bigSide = Math.max(internalBufferXSize, internalBufferYSize);
        for (int diagStep = 0; diagStep < 2 * bigSide; diagStep++) {

            int col = getColor(diagStep);

            int diagPixelCount = diagStep;
            int diagOffset = 0;
            if (diagStep >= bigSide) {
                diagPixelCount = (2 * bigSide) - diagStep;
                diagOffset = diagStep - bigSide;
            }

            for (int diagCounter = 0; diagCounter < diagPixelCount; diagCounter++) {
                int x = diagOffset + diagCounter;
                int y = diagPixelCount - diagCounter - 1 + diagOffset;
                setPixel(x, y, col);
            }
        }
    }

    /**
     * Left bottom to right top.
     */
    private void leftBottomToRightTop() {
        int bigSide = Math.max(internalBufferXSize, internalBufferYSize);
        for (int diagStep = 0; diagStep < 2 * bigSide; diagStep++) {
        	int col = getColor(diagStep);

            int diagPixelCount = diagStep;
            int diagOffset = 0;
            if (diagStep >= bigSide) {
                diagPixelCount = (2 * bigSide) - diagStep;
                diagOffset = diagStep - bigSide;
            }

            for (int diagCounter = 0; diagCounter < diagPixelCount; diagCounter++) {
                int x = internalBufferXSize - 1 - (diagOffset + diagCounter);
                int y = diagPixelCount - diagCounter - 1 + diagOffset;
                setPixel(x, y, col);
            }
        }
    }

    /**
     * Right top to left bottom.
     */
    private void rightTopToLeftBottom() {
        int bigSide = Math.max(internalBufferXSize, internalBufferYSize);
        for (int diagStep = 0; diagStep < 2 * bigSide; diagStep++) {
        	int col = getColor(diagStep);

            int diagPixelCount = diagStep;
            int diagOffset = 0;
            if (diagStep >= bigSide) {
                diagPixelCount = (2 * bigSide) - diagStep;
                diagOffset = diagStep - bigSide;
            }

            for (int diagCounter = 0; diagCounter < diagPixelCount; diagCounter++) {
                int x = diagOffset + diagCounter;
                int y = internalBufferYSize - 1 - (diagPixelCount - diagCounter - 1 + diagOffset);
                setPixel(x, y, col);
            }
        }
    }

    /**
     * Left top to right bottom.
     */
    private void leftTopToRightBottom() {
        int bigSide = Math.max(internalBufferXSize, internalBufferYSize);
        for (int diagStep = 0; diagStep < 2 * bigSide; diagStep++) {
        	int col = getColor(diagStep);

        	int diagPixelCount = diagStep;
            int diagOffset = 0;
            if (diagStep >= bigSide) {
                diagPixelCount = (2 * bigSide) - diagStep;
                diagOffset = diagStep - bigSide;
            }

            for (int diagCounter = 0; diagCounter < diagPixelCount; diagCounter++) {
                int x = internalBufferXSize - 1 - (diagOffset + diagCounter);
                int y = internalBufferYSize - 1 - (diagPixelCount - diagCounter - 1 + diagOffset);
                setPixel(x, y, col);
            }
        }
    }

    /**
     * Middle to sides vertical.
     */
    private void middleToSidesVertical() {
        int ySize = internalBufferYSize;

        for (int x = 0; x < internalBufferXSize2; x++) {
        	int col = getColor(x);

            for (int y = 0; y < ySize; y++) {
                this.internalBuffer[y * internalBufferXSize + x] = col;
                this.internalBuffer[y * internalBufferXSize + internalBufferXSize - x - 1] = col;
            }
        }
    }

    /**
     * Sides to middle vertical.
     */
    private void sidesToMiddleVertical() {
        int ySize = internalBufferYSize;

        for (int x = 0; x < internalBufferXSize2; x++) {

            int xRev = (internalBufferXSize2) - x - 1;
            int col = getColor(x);
            for (int y = 0; y < ySize; y++) {
                this.internalBuffer[y * internalBufferXSize + xRev] = col;
                this.internalBuffer[y * internalBufferXSize + internalBufferXSize - xRev - 1] = col;
            }
        }
    }

    /**
     * Middle to sides horizontal.
     */
    private void middleToSidesHorizontal() {
        int xSize = internalBufferXSize;

        for (int y = 0; y < internalBufferYSize2; y++) {

        	int col = getColor(y);

            for (int x = 0; x < xSize; x++) {
                this.internalBuffer[y * internalBufferXSize + x] = col;
                this.internalBuffer[(internalBufferYSize - y - 1) * internalBufferXSize + x] = col;
            }
        }
    }

    /**
     * Sides to middle horizontal.
     */
    private void sidesToMiddleHorizontal() {
        int xSize = internalBufferXSize;

        for (int y = 0; y < internalBufferYSize2; y++) {

            int yRev = internalBufferYSize2 - y - 1;
            int col = getColor(y);

            for (int x = 0; x < xSize; x++) {
                this.internalBuffer[yRev * internalBufferXSize + x] = col;
                this.internalBuffer[(internalBufferYSize - yRev - 1) * internalBufferXSize + x] = col;
            }
        }
    }

    /**
     * Implode circle.
     */
    private void implodeCircle() {

        int upToValue = (int)(Math.max(internalBufferXSize, internalBufferYSize) * 1.42f);
        for (int r = 0; r < upToValue; r++) {
        	int col = getColor(r);

            int f = 1 - r;
            int ddFx = 1;
            int ddFy = -2 * r;
            int x = 0;
            int y = r;

            setPixel(internalBufferXSize2, internalBufferYSize2 + r, col);
            setPixel(internalBufferXSize2, internalBufferYSize2 - r, col);
            setPixel(internalBufferXSize2 + r, internalBufferYSize2, col);
            setPixel(internalBufferXSize2 - r, internalBufferYSize2, col);

            while (x < y) {
                if (f >= 0) {
                    y--;
                    ddFy += 2;
                    f += ddFy;
                }
                x++;
                ddFx += 2;
                f += ddFx;
                setPixel(internalBufferXSize2 + x, internalBufferYSize2 + y, col);
                setPixel(internalBufferXSize2 - x, internalBufferYSize2 + y, col);
                setPixel(internalBufferXSize2 + x, internalBufferYSize2 - y, col);
                setPixel(internalBufferXSize2 - x, internalBufferYSize2 - y, col);
                setPixel(internalBufferXSize2 + y, internalBufferYSize2 + x, col);
                setPixel(internalBufferXSize2 - y, internalBufferYSize2 + x, col);
                setPixel(internalBufferXSize2 + y, internalBufferYSize2 - x, col);
                setPixel(internalBufferXSize2 - y, internalBufferYSize2 - x, col);

                //double line to mind gaps
                setPixel(internalBufferXSize2 + x + 1, internalBufferYSize2 + y, col);
                setPixel(internalBufferXSize2 - x + 1, internalBufferYSize2 + y, col);
                setPixel(internalBufferXSize2 + x + 1, internalBufferYSize2 - y, col);
                setPixel(internalBufferXSize2 - x + 1, internalBufferYSize2 - y, col);
                setPixel(internalBufferXSize2 + y + 1, internalBufferYSize2 + x, col);
                setPixel(internalBufferXSize2 - y + 1, internalBufferYSize2 + x, col);
                setPixel(internalBufferXSize2 + y + 1, internalBufferYSize2 - x, col);
                setPixel(internalBufferXSize2 - y + 1, internalBufferYSize2 - x, col);
                
                setPixel(internalBufferXSize2 + x + 1, internalBufferYSize2 + y + 1, col);
                setPixel(internalBufferXSize2 - x + 1, internalBufferYSize2 + y + 1, col);
                setPixel(internalBufferXSize2 + x + 1, internalBufferYSize2 - y + 1, col);
                setPixel(internalBufferXSize2 - x + 1, internalBufferYSize2 - y + 1, col);
                setPixel(internalBufferXSize2 + y + 1, internalBufferYSize2 + x + 1, col);
                setPixel(internalBufferXSize2 - y + 1, internalBufferYSize2 + x + 1, col);
                setPixel(internalBufferXSize2 + y + 1, internalBufferYSize2 - x + 1, col);
                setPixel(internalBufferXSize2 - y + 1, internalBufferYSize2 - x + 1, col);
                
            }
        }
    }
    
    /**
     * Explode circle.
     */
    private void explodeCircle() {

        int upToValue = (int)(Math.max(internalBufferXSize, internalBufferYSize) * 1.42f);
        for (int r = 0; r < upToValue; r++) {            
            int rRev = upToValue - r;
            int col = getColor(rRev);

            int f = 1 - r;
            int ddFx = 1;
            int ddFy = -2 * r;
            int x = 0;
            int y = r;

            setPixel(internalBufferXSize2, internalBufferYSize2 + r, col);
            setPixel(internalBufferXSize2, internalBufferYSize2 - r, col);
            setPixel(internalBufferXSize2 + r, internalBufferYSize2, col);
            setPixel(internalBufferXSize2 - r, internalBufferYSize2, col);

            while (x < y) {
                if (f >= 0) {
                    y--;
                    ddFy += 2;
                    f += ddFy;
                }
                x++;
                ddFx += 2;
                f += ddFx;
                setPixel(internalBufferXSize2 + x, internalBufferYSize2 + y, col);
                setPixel(internalBufferXSize2 - x, internalBufferYSize2 + y, col);
                setPixel(internalBufferXSize2 + x, internalBufferYSize2 - y, col);
                setPixel(internalBufferXSize2 - x, internalBufferYSize2 - y, col);
                setPixel(internalBufferXSize2 + y, internalBufferYSize2 + x, col);
                setPixel(internalBufferXSize2 - y, internalBufferYSize2 + x, col);
                setPixel(internalBufferXSize2 + y, internalBufferYSize2 - x, col);
                setPixel(internalBufferXSize2 - y, internalBufferYSize2 - x, col);

                //double line to mind gaps
                setPixel(internalBufferXSize2 + x + 1, internalBufferYSize2 + y, col);
                setPixel(internalBufferXSize2 - x + 1, internalBufferYSize2 + y, col);
                setPixel(internalBufferXSize2 + x + 1, internalBufferYSize2 - y, col);
                setPixel(internalBufferXSize2 - x + 1, internalBufferYSize2 - y, col);
                setPixel(internalBufferXSize2 + y + 1, internalBufferYSize2 + x, col);
                setPixel(internalBufferXSize2 - y + 1, internalBufferYSize2 + x, col);
                setPixel(internalBufferXSize2 + y + 1, internalBufferYSize2 - x, col);
                setPixel(internalBufferXSize2 - y + 1, internalBufferYSize2 - x, col);
                
                setPixel(internalBufferXSize2 + x + 1, internalBufferYSize2 + y + 1, col);
                setPixel(internalBufferXSize2 - x + 1, internalBufferYSize2 + y + 1, col);
                setPixel(internalBufferXSize2 + x + 1, internalBufferYSize2 - y + 1, col);
                setPixel(internalBufferXSize2 - x + 1, internalBufferYSize2 - y + 1, col);
                setPixel(internalBufferXSize2 + y + 1, internalBufferYSize2 + x + 1, col);
                setPixel(internalBufferXSize2 - y + 1, internalBufferYSize2 + x + 1, col);
                setPixel(internalBufferXSize2 + y + 1, internalBufferYSize2 - x + 1, col);
                setPixel(internalBufferXSize2 - y + 1, internalBufferYSize2 - x + 1, col);
            }
        }
    }

    /**
     * Sets the pixel.
     *
     * @param x the x
     * @param y the y
     * @param col the col
     */
    private void setPixel(int x, int y, int col) {
        if (y >= 0 && y < internalBufferYSize && x >= 0 && x < internalBufferXSize) {
            this.internalBuffer[y * internalBufferXSize + x] = col;
        }
    }


    /**
     * @return the fade
     */
    public int getFade() {
        return fade;
    }


    /**
     * @param fade the fade to set
     */
    public void setFade(int fade) {
        this.fade = fade;
    }


    /**
     * @return the scrollMode
     */
    public ScrollMode getScrollMode() {
        return scrollMode;
    }


    /**
     * @param scrollMode the scrollMode to set
     */
    public void setScrollMode(ScrollMode scrollMode) {
        this.scrollMode = scrollMode;
    }
    
    
    

}
