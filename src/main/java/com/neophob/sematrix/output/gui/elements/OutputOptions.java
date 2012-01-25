/**
 * $Id: SimpleColorPicker.java,v 1.1 2010/10/01 15:59:01 mvogt Exp $
 *
 * @Copyright: United Security Providers., Switzerland, 2011, All Rights Reserved.
 */
package com.neophob.sematrix.output.gui.elements;


import com.neophob.sematrix.effect.Effect.EffectName;
import com.neophob.sematrix.fader.Fader.FaderName;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.gui.helper.Theme;

import controlP5.ControlEvent;
import controlP5.ControlGroup;
import controlP5.ControlP5;
import controlP5.ControllerGroup;
import controlP5.DropdownList;

/**
 * A simple color picker using sliders to adjust RGBA values.
 * 
 * @example controllers/ControlP5colorPicker
 */
public class OutputOptions extends ControlGroup {

    protected DropdownList dropdownVisual;
    protected DropdownList dropDownEffect;
    protected DropdownList dropDownFader;    

    /**
     * 
     * @param theControlP5
     * @param theParent
     * @param theName
     * @param theX
     * @param theY
     * @param theWidth
     * @param theHeight
     */
    public OutputOptions(ControlP5 theControlP5, ControllerGroup theParent, String theName, int theX, int theY, int theWidth, int theHeight) {
        super(theControlP5, theParent, theName, theX, theY, theWidth, theHeight);
        isBarVisible = false;
        isCollapse = false;

        int nrOfVisuals = Collector.getInstance().getAllVisuals().size();
        
        
        //Visuals
        dropdownVisual = cp5.addDropdownList(theName+"ddlVisual", 0, 0, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(dropdownVisual);
        int i=0;
        for (i=0; i<nrOfVisuals; i++) {
            dropdownVisual.addItem("Visual #"+(1+i), i);
        }
        dropdownVisual.setLabel(dropdownVisual.getItem(0).getName());
        dropdownVisual.addListener(this);
        dropdownVisual.moveTo(this);
        dropdownVisual.setId(0);

        
        //Effect 
        dropDownEffect = cp5.addDropdownList(theName+"ddlEffect", 14, 14, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(dropDownEffect);
        i=0;
        for (EffectName gn: EffectName.values()) {
            dropDownEffect.addItem(gn.name(), i++);
        }
        dropDownEffect.setLabel(dropDownEffect.getItem(0).getName());
        dropDownEffect.addListener(this);
        dropDownEffect.moveTo(this);
        dropdownVisual.setId(1);
        

        //Fader 
        dropDownFader = cp5.addDropdownList(theName+"ddlFader", 28, 28, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(dropDownFader);
        i=0;
        for (FaderName fn: FaderName.values()) {
            dropDownFader.addItem(fn.name(), i++);
        }
        dropDownFader.setLabel(dropDownFader.getItem(0).getName());
        dropDownFader.addListener(this);
        dropDownFader.moveTo(this);
        dropdownVisual.setId(2);
    }

    /**
     * @exclude {@inheritDoc}
     */
    @Override
    public void controlEvent(ControlEvent theEvent) {
        //_myArrayValue[theEvent.getId()] = theEvent.getValue();
        System.out.println(theEvent.getId()+" SAYS: "+theEvent.getValue());

        //maybe check if event is active?
        //cp5.getControlBroadcaster().broadcast(new ControlEvent(this), ControlP5Constants.STRING);
    }
    


    /**
     * @exclude
     * {@inheritDoc}
     */
    @Override
    public String getInfo() {
        return "type:\tOutputOptions\n" + super.toString();
    }
}
