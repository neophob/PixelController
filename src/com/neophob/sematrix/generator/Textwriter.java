package com.neophob.sematrix.generator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * @author mvogt
 *
 */
public class Textwriter extends Generator {

	private String text = "VTG!";
	private String fontName = "";
	private int fontSize = 54;
	private int xpos=0,ypos=64;
	private Font font;
	private Color color;
	
	/**
	 * 
	 * @param filename
	 */
	public Textwriter() {
		super(GeneratorName.TEXTWRITER);
		color = new Color(255,255,255);
		font=new Font(fontName, Font.PLAIN, fontSize);
		
		BufferedImage img = getBufferedImage();
		Graphics2D g2 = img.createGraphics();
		g2.setColor(color);
		g2.setFont(font);		
		g2.setClip(0, 0, getInternalBufferXSize(), getInternalBufferYSize());
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.drawString(text, xpos, ypos);
		DataBufferInt dbi = (DataBufferInt)img.getRaster().getDataBuffer();
		
		System.arraycopy(dbi.getData(), 0, internalBuffer, 0, internalBuffer.length);
		g2.dispose();
	}
	
	/**
	 * create a BufferedImage from a Texture Layer
	 * @param nr
	 * @return BufferedImage 
	 */
	private BufferedImage getBufferedImage() {
		BufferedImage image = new BufferedImage( getInternalBufferXSize(), getInternalBufferYSize(), BufferedImage.TYPE_INT_RGB); 
		return image;
	}
	
	@Override
	public void update() {
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
}
