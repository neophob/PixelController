package com.neophob.sematrix.generator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * 
 * 
 * @author michu
 *
 */
public abstract class ColorMapAwareGenerator extends Generator {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(ColorMapAwareGenerator.class.getName());

	/** The color map. */
	protected List<Color> colorMap;
	
	/** the color map */
	protected String colorMapAsString;

	/**
	 * 
	 * @param controller
	 * @param name
	 * @param resizeOption
	 * @param colorList
	 */
	public ColorMapAwareGenerator(PixelControllerGenerator controller, GeneratorName name, ResizeName resizeOption,
			List<Integer> colorList) {
		super(controller, name, resizeOption);
		this.colorMap = new ArrayList<Color>();
		
		LOG.log(Level.INFO, "add {0} colors to map", colorList.size());
        for (int i: colorList) {
        	colorMap.add(new Color(i));
        }
        
		//add default value if nothing is configured
		if (colorMap.isEmpty()) {
			colorMap.add(new Color(255, 128, 128));
			colorMap.add(new Color(255, 255, 128));
			colorMap.add(new Color(128, 255, 128));
			colorMap.add(new Color(128, 255, 255));
			colorMap.add(new Color(128, 128, 255));
			colorMap.add(new Color(255, 128, 255));
		}

	}

	/**
	 * 
	 * @param colornumber
	 * @param nextcolornumber
	 * @param ratio
	 * @return
	 */
	protected int getColor(int colornumber, int nextcolornumber, float ratio) {
		Color currentColor = colorMap.get(colornumber);
		Color nextColor = colorMap.get(nextcolornumber);
		
		int rThis = currentColor.getRed();
		int rNext = nextColor.getRed();
		int gThis = currentColor.getGreen();
		int gNext = nextColor.getGreen();
		int bThis = currentColor.getBlue();
		int bNext = nextColor.getBlue();

		int r = rThis - (int) Math.round((rThis - rNext) * ratio);
		int g = gThis - (int) Math.round((gThis - gNext) * ratio);
		int b = bThis - (int) Math.round((bThis - bNext) * ratio);

		return (r << 16) | (g << 8) | b;		
	}
	
	/**
	 * Update the color map, example parameter (6 Colors):
	 * 		"0xff8080_0xffff80_0x80ff80_0x80ffff_0x8080ff_0x000000"
	 * 
	 * @param colorMap
	 */
	public void setColorMap(String colorMap) {
	    this.colorMapAsString =  colorMap;
		String[] tmp = colorMap.trim().split("_");
		if (tmp==null || tmp.length==0) {
			LOG.log(Level.WARNING, "Invalid Parameter: "+colorMap);
			return;
		}

		List<Color> list = new ArrayList<Color>();
		for (String s: tmp) {
			try {
				list.add( new Color(Integer.decode(s.trim())) );
			} catch (Exception e) {
				LOG.log(Level.WARNING, "Failed to parse color {0}", s);
			}	
		}
		this.colorMap = list;
	}

    /**
     * @return the colorMapAsString
     */
    public String getColorMapAsString() {
        return colorMapAsString;
    }

	
	
}
