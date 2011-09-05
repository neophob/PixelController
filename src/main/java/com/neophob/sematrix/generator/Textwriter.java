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

package com.neophob.sematrix.generator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * @author mvogt
 *
 */
public class Textwriter extends Generator {

	private static final int TEXT_BUFFER_X_SIZE=128;
	private static final int CHANGE_SCROLLING_DIRECTION_TIMEOUT=12;
	
	private static final int SCROLL_AMMOUNT = 4;
	
	private static Logger log = Logger.getLogger(Textwriter.class.getName());

	private int xpos,ypos;
	private Font font;
	private Color color;

	private int xofs;
	private int maxXPos;
	private boolean scrollRight=true;
	private int wait;

	private int[] textBuffer;
	private int[] tmp;
	
	private String text;

	/**
	 * 
	 * @param filename
	 */
	public Textwriter(PixelControllerGenerator controller, String fontName, int fontSize, String text) {
		super(controller, GeneratorName.TEXTWRITER, ResizeName.PIXEL_RESIZE);
		color = new Color(255,255,255);
		xpos=0;
		ypos=internalBufferYSize;
		try {
			InputStream is = Collector.getInstance().getPapplet().createInput(fontName);
			tmp = new int[internalBuffer.length];
			font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.BOLD, (float)fontSize);
			log.log(Level.INFO, "Loaded font "+fontName+", size: "+fontSize);
			createTextImage(text);			
		} catch (Exception e) {
			log.log(Level.WARNING, "Failed to load font "+fontName+"!", e);
		}
	}

	/**
	 * create image
	 * @return
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

		img = new BufferedImage(maxXPos, internalBufferYSize, BufferedImage.TYPE_INT_RGB);
		g2 = img.createGraphics();

		g2.setColor(color);
		g2.setFont(font);		
		g2.setClip(0, 0, maxXPos, internalBufferYSize);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.drawString(text, xpos, ypos);
		DataBufferInt dbi = (DataBufferInt)img.getRaster().getDataBuffer();
		textBuffer=dbi.getData();
		g2.dispose();

		wait = 0;
		xofs = 0;
		scrollRight = false;
	}


	@Override
	public void update() {
		
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
				//we need to clear the buffer first!
				Arrays.fill(tmp, 0);

				for (int y=0; y<internalBufferYSize; y++) {
					System.arraycopy(textBuffer, srcOfs, tmp, dstOfs, maxXPos);
					dstOfs+=internalBufferXSize;
					srcOfs+=maxXPos;
				}				
			} else {
				for (int y=0; y<internalBufferYSize; y++) {
					System.arraycopy(textBuffer, srcOfs, tmp, dstOfs, internalBufferXSize);
					dstOfs+=internalBufferXSize;
					srcOfs+=maxXPos;
				}				
			}

			this.internalBuffer = tmp;			
		} catch (Exception e) {
			//if the image is resized, this could lead to an arrayoutofboundexception!
		}
		
	}	

	public String getText() {
		return text;
	}


}
