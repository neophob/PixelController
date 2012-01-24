/**
 * $Id: SimpleColorPicker.java,v 1.1 2010/10/01 15:59:01 mvogt Exp $
 *
 * @Copyright: United Security Providers., Switzerland, 2011, All Rights Reserved.
 */
package com.neophob.sematrix.output.gui.elements;

/**
 * controlP5 is a processing gui library.
 * 
 * rip from http://controlp5.googlecode.com/svn/trunk/src/controlP5/ColorPicker.java
 * 
 * 2006-2011 by Andreas Schlegel
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * @author Andreas Schlegel (http://www.sojamo.de)
 * @modified ##date##
 * @version ##version##
 * 
 */

import processing.core.PApplet;
import controlP5.ControlEvent;
import controlP5.ControlGroup;
import controlP5.ControlP5;
import controlP5.ControlWindowCanvas;
import controlP5.ControllerGroup;
import controlP5.Slider;

/**
 * A simple color picker using sliders to adjust RGBA values.
 * 
 * @example controllers/ControlP5colorPicker
 */
public class SimpleColorPicker extends ControlGroup {

    protected Slider sliderRed;
    protected Slider sliderGreen;
    protected Slider sliderBlue;    
    protected ControlWindowCanvas currentColor;

    public SimpleColorPicker(ControlP5 theControlP5, ControllerGroup theParent, String theName, int theX, int theY, int theWidth, int theHeight) {
        super(theControlP5, theParent, theName, theX, theY, theWidth, theHeight);
        isBarVisible = false;
        isCollapse = false;
        _myArrayValue = new float[] { 255, 255, 255, 255 };

        currentColor = addCanvas(new ColorField());
        

        sliderRed = cp5.addSlider(theName + "_RED", 0, 255, 0, 0, theWidth, 10);
        cp5.removeProperty(sliderRed);
        sliderRed.setId(0);
        sliderRed.setBroadcast(false);
        sliderRed.addListener(this);
        sliderRed.setValue(255);
        sliderRed.moveTo(this);
        sliderRed.setMoveable(false);
        sliderRed.setColorBackground(0xff660000);
        sliderRed.setColorForeground(0xffaa0000);
        sliderRed.setColorActive(0xffff0000);
        sliderRed.getCaptionLabel().setVisible(true);
        sliderRed.setCaptionLabel("Tint Red");
        sliderRed.setDecimalPrecision(0);
        sliderRed.setHeight(14);        

        sliderGreen = cp5.addSlider(theName + "_GREEN", 0, 255, 0, 14, theWidth, 10);
        cp5.removeProperty(sliderGreen);
        sliderGreen.setId(1);
        sliderGreen.setBroadcast(false);
        sliderGreen.addListener(this);
        sliderGreen.setValue(255);
        sliderGreen.moveTo(this);
        sliderGreen.setMoveable(false);
        sliderGreen.setColorBackground(0xff006600);
        sliderGreen.setColorForeground(0xff00aa00);
        sliderGreen.setColorActive(0xff00ff00);
        sliderGreen.getCaptionLabel().setVisible(true);
        sliderGreen.setCaptionLabel("Tint Green");
        sliderGreen.setDecimalPrecision(0);
        sliderGreen.setHeight(14);
        
        sliderBlue = cp5.addSlider(theName + "_BLUE", 0, 255, 0, 28, theWidth, 10);
        cp5.removeProperty(sliderBlue);
        sliderBlue.setId(2);
        sliderBlue.setBroadcast(false);
        sliderBlue.addListener(this);
        sliderBlue.setValue(255);
        sliderBlue.moveTo(this);
        sliderBlue.setMoveable(false);
        sliderBlue.setColorBackground(0xff000066);
        sliderBlue.setColorForeground(0xff0000aa);
        sliderBlue.setColorActive(0xff0000ff);
        sliderBlue.getCaptionLabel().setVisible(true);
        sliderBlue.setCaptionLabel("Tint Blue");
        sliderBlue.setDecimalPrecision(0);
        sliderBlue.setHeight(14);
    }

    /**
     * @exclude {@inheritDoc}
     */
    @Override
    public void controlEvent(ControlEvent theEvent) {
        _myArrayValue[theEvent.getId()] = theEvent.getValue();
        //_myValue = getColorValue();
    }

    /**
     * Requires an array of size 4 for RGBA
     * 
     * @return ColorPicker
     */
    @Override
    public SimpleColorPicker setArrayValue(float[] theArray) {
        sliderRed.setValue(theArray[0]);
        sliderGreen.setValue(theArray[1]);
        sliderBlue.setValue(theArray[2]);        
        _myArrayValue = theArray;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleColorPicker setColorValue(int theColor) {
        setArrayValue(new float[] { theColor >> 16 & 0xff, theColor >> 8 & 0xff, theColor >> 0 & 0xff, theColor >> 24 & 0xff });
        return this;
    }

    /**
     * @return int
     */
    public int getColorValue() {
        int cc = 0xffffffff;
        return cc & (int) (_myArrayValue[3]) << 24 | (int) (_myArrayValue[0]) << 16 | (int) (_myArrayValue[1]) << 8 | (int) (_myArrayValue[2]) << 0;
    }

    private class ColorField extends ControlWindowCanvas {
        public void draw(PApplet theApplet) {
            theApplet.fill(_myArrayValue[0], _myArrayValue[1], _myArrayValue[2], _myArrayValue[3]);
            theApplet.rect(0, 44, getWidth(), 15);
        }
    }

    /**
     * @exclude
     * {@inheritDoc}
     */
    @Override
    public String getInfo() {
        return "type:\tSimpleColorPicker\n" + super.toString();
    }
}
