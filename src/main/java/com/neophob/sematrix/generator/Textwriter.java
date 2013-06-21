/**
 * Copyright (C) 2011-2013 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.generator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * The Class Textwriter.
 *
 * @author mvogt
 */
public class Textwriter extends Generator {
    
    public static final String INITIAL_TEXT = "initial.text";
    public static final String FONT_FILENAME = "font.filename";
    public static final String FONT_SIZE = "font.size";
    

	/** The Constant TEXT_BUFFER_X_SIZE. */
	private static final int TEXT_BUFFER_X_SIZE=128;
	
	/** The Constant CHANGE_SCROLLING_DIRECTION_TIMEOUT. */
	private static final int CHANGE_SCROLLING_DIRECTION_TIMEOUT=12;
	
	/** The Constant SCROLL_AMMOUNT. */
	private static final int SCROLL_AMMOUNT = 4;
	
	/** The log. */
	private static final Logger LOG = Logger.getLogger(Textwriter.class.getName());

	/** The ypos. */
	private int xpos,ypos;
	
	/** The font. */
	private Font font;
	
	/** The xofs. */
	private int xofs;
	
	/** The max x pos. */
	private int maxXPos;
	
	/** The scroll right. */
	private boolean scrollRight=true;
	
	/** The wait. */
	private int wait;

	/** The text stored as bitmap. */
	private int[] textAsImage;
	
	/** The text. */
	private String text;

	/**
	 * Instantiates a new textwriter.
	 *
	 * @param controller the controller
	 * @param fontName the font name
	 * @param fontSize the font size
	 * @param text the text
	 */
	public Textwriter(PixelControllerGenerator controller, String fontName, int fontSize, String text) {
		super(controller, GeneratorName.TEXTWRITER, ResizeName.PIXEL_RESIZE);
		xpos=0;
		ypos=internalBufferYSize;
		try {
			InputStream is = Collector.getInstance().getPapplet().createInput(fontName);
			textAsImage = new int[internalBuffer.length];
			font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.BOLD, (float)fontSize);
			LOG.log(Level.INFO, "Loaded font "+fontName+", size: "+fontSize);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Failed to load font "+fontName+":", e);
			throw new IllegalArgumentException("Failed to load font "+fontName+". Check your config.properties file.");
		}
		createTextImage(text);			
	}

	/**
	 * create image.
	 *
	 * @param text the text
	 */
	public void createTextImage(String text) {	
		//only load if needed
		if (StringUtils.equals(text, this.text)) {
			return;
		}

		this.text = text;
		
		BufferedImage img = 
			new BufferedImage( TEXT_BUFFER_X_SIZE, internalBufferYSize, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2 = img.createGraphics();
		FontRenderContext frc = g2.getFontRenderContext(); 
		TextLayout layout = new TextLayout(text, font, frc); 
		Rectangle2D rect = layout.getBounds();

		int h = (int)(0.5f+rect.getHeight());
		maxXPos=(int)(0.5f+rect.getWidth())+5;
		ypos=internalBufferYSize-(internalBufferYSize-h)/2;

		img = new BufferedImage(maxXPos, internalBufferYSize, BufferedImage.TYPE_BYTE_GRAY);
		g2 = img.createGraphics();

		g2.setColor(new Color(128));
		g2.setFont(font);		
		g2.setClip(0, 0, maxXPos, internalBufferYSize);

		g2.drawString(text, xpos, ypos);
		DataBufferByte dbi = (DataBufferByte)img.getRaster().getDataBuffer();
		byte[] textBuffer=dbi.getData();
		g2.dispose();

		wait = 0;
		xofs = 0;
		scrollRight = false;
		
		textAsImage = new int[maxXPos*internalBufferYSize];
		for (int i=0; i<textAsImage.length; i++) {
		    if (textBuffer[i]>10) {
		        textAsImage[i] = 127;
		    } else {
		        textAsImage[i] = 0;
		    }
		}
		
		//clear internalbuffer
		Arrays.fill(this.internalBuffer, 0);
	}


	/* (non-Javadoc)
	 * @see com.neophob.sematrix.generator.Generator#update()
	 */
	@Override
	public void update() {
		
		//calculate pingpong scrolling
		if (wait>0) {
			wait--;
		} else {
			if (maxXPos < getInternalBufferXSize()) {
				//no need to scroll
				xofs = (getInternalBufferXSize()-maxXPos)/2;
				wait=99999;
			} else {
				//todo, if image < buffer
				if (scrollRight) {
					xofs+=SCROLL_AMMOUNT;
					if (xofs>maxXPos-internalBufferXSize) {
						scrollRight=false;
						xofs=maxXPos-internalBufferXSize;
						wait=CHANGE_SCROLLING_DIRECTION_TIMEOUT;
					}			
				} else {
					xofs-=SCROLL_AMMOUNT;
					if (xofs<1) {
						scrollRight=true;
						xofs=0;
						wait=CHANGE_SCROLLING_DIRECTION_TIMEOUT;
					}
				}			
			}
		}
		
		int srcOfs=xofs;
		int dstOfs=0;
		
		try {
			if (maxXPos < getInternalBufferXSize()) {
				//text image smaller than internal buffer
				srcOfs=0;
				dstOfs=xofs;				
				for (int y=0; y<internalBufferYSize; y++) {				    
				    System.arraycopy(textAsImage, srcOfs, this.internalBuffer, dstOfs, maxXPos);
					dstOfs+=internalBufferXSize;
					srcOfs+=maxXPos;
				}				
			} else {
				for (int y=0; y<internalBufferYSize; y++) {
				    System.arraycopy(textAsImage, srcOfs, this.internalBuffer, dstOfs, internalBufferXSize);
					dstOfs+=internalBufferXSize;
					srcOfs+=maxXPos;
				}				
			}
		} catch (Exception e) {
			//if the image is resized, this could lead to an arrayoutofboundexception!
		}
		
	}	

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return text;
	}


}