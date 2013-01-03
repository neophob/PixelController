/**
 * $Id: GuiCallbackAction.java,v 1.1 2010/10/01 15:59:01 mvogt Exp $
 *
 * @Copyright: United Security Providers., Switzerland, 2012, All Rights Reserved.
 */
package com.neophob.sematrix.output.gui;

/**
 * this interface is used to define gui actions are needed because of 
 * keyboard input device
 *  
 * @author mvogt
 *
 */
public interface GuiCallbackAction {

    /**
     * select a visual
     * 
     * @param n
     */
    void activeVisual(int n);
    
}
