package com.neophob.sematrix.output;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.MatrixData;
import com.neophob.sematrix.glue.OutputMapping;

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
	
	public Output(String name) {
		this.name = name;
				
		matrixData = Collector.getInstance().getMatrix();

		log.log(Level.INFO,
				"Output created: {0}"
				, new Object[] { this.name });
		
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
		int fxOnHowMayScreens = c.howManyScreensShareThisFx(fxInput);
		//Fader f = Collector.getInstance().getAllOutputMappings().get(screenNr).getFader();

		OutputMapping map = Collector.getInstance().getAllOutputMappings().get(screenNr);
		int buffer[];
		if (fxOnHowMayScreens==1) {
			buffer = matrixData.getScreenBufferForDevice(Collector.getInstance().getAllVisuals().get(fxInput), map);
		} else {
			buffer = matrixData.getScreenBufferForDevice(
					Collector.getInstance().getAllVisuals().get(fxInput),
					c.getOffsetForScreen(screenNr), //offset
					fxOnHowMayScreens, map //total
			);
		}
		return buffer;
	}
	
	public String toString() {
		return name;
	}

}
