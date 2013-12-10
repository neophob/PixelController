package com.neophob.sematrix.core.glue.helper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.neophob.sematrix.core.visual.Visual;
import com.neophob.sematrix.core.visual.color.ColorSet;

public class ScreenshotHelper {

    private static final Logger LOG = Logger.getLogger(ScreenshotHelper.class.getName());

	private ScreenshotHelper() {
		// no instance
	}

	public static void saveImage(Visual v, String filename, int[] data) {
		try {
			int w = v.getGenerator1().getInternalBufferXSize();
			int h = v.getGenerator1().getInternalBufferYSize();
			BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			bi.setRGB(0, 0, w, h, data, 0, w);
			File outputfile = new File(filename);
			ImageIO.write(bi, "png", outputfile);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Failed to save screenshot "+filename, e);
		}
	}
	
	/**
	 * create screenshot
	 */
	public static void saveScreenshot(int frames, List<Visual> allVisuals) {
		int ofs=0;		
		String suffix = ".png";
		File f = new File("screenshot"+File.separator);
		if (!f.exists()) {
			LOG.log(Level.INFO, "Create directory "+f.getAbsolutePath());
			boolean result = f.mkdir();
			LOG.log(Level.INFO, "Result: "+result);
		}
		for (Visual v: allVisuals) {
			String prefix = "screenshot"+File.separator+frames+"-"+ofs+"-";
			ColorSet cs = v.getColorSet();			
			saveImage(v, prefix+"gen1"+suffix, cs.convertToColorSetImage(v.getGenerator1().internalBuffer));
			saveImage(v, prefix+"gen2"+suffix, cs.convertToColorSetImage(v.getGenerator2().internalBuffer));

			saveImage(v, prefix+"fx1"+suffix, cs.convertToColorSetImage(v.getEffect1Buffer()));
			saveImage(v, prefix+"fx2"+suffix, cs.convertToColorSetImage(v.getEffect2Buffer()));

			saveImage(v, prefix+"mix"+suffix, v.getMixerBuffer());
			ofs++;
		}
	}
}
