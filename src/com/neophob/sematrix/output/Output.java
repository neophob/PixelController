package com.neophob.sematrix.output;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.MatrixData;
import com.neophob.sematrix.glue.OutputMapping;
import com.neophob.sematrix.layout.Layout;
import com.neophob.sematrix.properties.PropertiesHelper;

/**
 * parent output class
 * 
 * 
 * @author michu
 *
 */
public abstract class Output {

	private static Logger log = Logger.getLogger(Output.class.getName());

	private String name;
	
	protected MatrixData matrixData;
	
	protected Layout layout;
	
	public Output(String name) {
		this.name = name;
				
		this.matrixData = Collector.getInstance().getMatrix();
		this.layout = PropertiesHelper.getLayout();

		log.log(Level.INFO,
				"Output created: {0}, Layout: {1}"
				, new Object[] { this.name, layout.getLayoutName() });
	
		//add to list
		Collector.getInstance().addOutput(this);
	}
	
	public abstract void update();
	
	public abstract void close(); 

	/**
	 * get buffer for a output, this method respect the mapping
	 * 
	 * @param screenNr
	 * @return
	 */
	public int[] getBufferForScreen(int screenNr) {
		Collector c = Collector.getInstance();
		int fxInput = c.getFxInputForScreen(screenNr);
		int fxOnHowMayScreensX = layout.howManyScreensShareThisFxOnTheXAxis(fxInput, screenNr);
		int fxOnHowMayScreensY = layout.howManyScreensShareThisFxOnTheYAxis(fxInput, screenNr);
		OutputMapping map = c.getOutputMappings(screenNr);
		
		if (fxOnHowMayScreensX==1 && fxOnHowMayScreensY==1) {
			return matrixData.getScreenBufferForDevice(c.getVisual(fxInput), map);
		} else {
			return matrixData.getScreenBufferForDevice(
					c.getVisual(fxInput),
					layout.getXOffsetForScreen(screenNr), 	//xoffset
					layout.getYOffsetForScreen(screenNr), 	//yoffset
					fxOnHowMayScreensX, 					//
					fxOnHowMayScreensY, 					//
					map 									//total
			);
		}
	}
	
	public String toString() {
		return name;
	}

}
