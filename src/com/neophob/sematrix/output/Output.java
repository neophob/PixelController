package com.neophob.sematrix.output;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.MatrixData;
import com.neophob.sematrix.glue.OutputMapping;
import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.layout.Layout;
import com.neophob.sematrix.layout.LayoutModel;
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
		LayoutModel lm = layout.getDataForScreen(screenNr);
		OutputMapping map = c.getOutputMappings(screenNr);
		
		if (lm.screenDoesNotNeedStretching()) {
			Visual v = c.getVisual(lm.getFxInput());
			return matrixData.getScreenBufferForDevice(v, map);
		} else {
			Visual v = c.getVisual(lm.getFxInput());
			return matrixData.getScreenBufferForDevice(v, lm, map);
		}
	}
	
	public String toString() {
		return name;
	}

}
