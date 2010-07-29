package com.neophob.sematrix.output.helper;

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
        Generator g = Collector.getInstance().getGenerator(0);
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
