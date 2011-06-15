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

package com.neophob.sematrix.output.emulatorhelper;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;

import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.glue.Collector;

public class NewWindowHelper extends Frame {

	private static final long serialVersionUID = 2946906663946781980L;

	static Logger log = Logger.getLogger(NewWindowHelper.class.getName());

	public NewWindowHelper(boolean displayHoriz) {
        super("debug buffer");
        int nrOfScreens = Collector.getInstance().getAllVisuals().size();
        //MatrixData matrix = Collector.getInstance().getMatrix();
        Generator g = Collector.getInstance().getPixelControllerGenerator().getGenerator(0);
        int x = g.getInternalBufferXSize()*2;
        int y = g.getInternalBufferYSize()*2;
        
        if (displayHoriz) {
        	x*=nrOfScreens;
        } else {
        	y*=nrOfScreens;
        }
        x+=20;y+=40;
        
        log.log(Level.INFO, "create frame with size "+x+"/"+y);
        setBounds(0, 0, x, y);
        this.setResizable(false);
        this.setSize(x, y);

        setLayout(new BorderLayout());
        PApplet embed = new InternalBuffer(displayHoriz, x, y);
        
        add(embed, BorderLayout.CENTER);

        // important to call this whenever embedding a PApplet.
        // It ensures that the animation thread is started and
        // that other internal variables are properly set.
        embed.init();
        
        setVisible(true); 
		
	}
}
