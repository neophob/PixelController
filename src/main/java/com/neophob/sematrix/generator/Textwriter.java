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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.neophob.sematrix.glue.FileUtils;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * The text is created once and stored to a buffer. The Scroller just change the X offset of the display window.
 *
 * @author mvogt
 */
public class Textwriter extends Generator {
    
    public static final String INITIAL_TEXT = "initial.text";
    public static final String FONT_FILENAME = "font.filename";
    public static final String FONT_SIZE = "font.size";
    
	public enum TextwriterMode {
		PINGPONG,
		LEFT,
		//SINGLE_CHARACTERS
	}
	
	/** The Constant TEXT_BUFFER_X_SIZE. */
	private static final int TEXT_BUFFER_X_SIZE=128;
	
	/** The Constant CHANGE_SCROLLING_DIRECTION_TIMEOUT. */
	private static final int CHANGE_SCROLLING_DIRECTION_TIMEOUT=12;
	
	/** The Constant SCROLL_AMMOUNT. */
	private static final int SCROLL_AMMOUNT = 4;
	
	/** The log. */
	private static final Logger LOG = Logger.getLogger(Textwriter.class.getName());
	
	/** The font. */
	private Font font;
	
	/** The xofs. */
	private int xofs;
	
	/** The max x pos. */
	private int maxXPos;
	
	/** The text stored as bitmap. */
	private int[] textAsImage;
	
	/** The text. */
	private String text;

	private AbstractScroller scroller;
	private int scrollerNr = 0;
	
	/**
	 * Instantiates a new textwriter.
	 *
	 * @param controller the controller
	 * @param fontName the font name
	 * @param fontSize the font size
	 * @param text the text
	 */
	public Textwriter(PixelControllerGenerator controller, String fontName, int fontSize, String text, FileUtils fu) {
		super(controller, GeneratorName.TEXTWRITER, ResizeName.PIXEL_RESIZE);
		String filename = fu.getRootDirectory()+File.separator+"data"+File.separator+fontName;
		InputStream is = null;
		try {			
			is = new FileInputStream(filename);
			textAsImage = new int[internalBuffer.length];
			font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.BOLD, (float)fontSize);
			LOG.log(Level.INFO, "Loaded font "+fontName+", size: "+fontSize);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Failed to load font "+filename+":", e);
			throw new IllegalArgumentException("Failed to load font "+filename+". Check your config.properties file.");
		} finally {
			if(is!=null) {
				try {
					is.close();
				} catch (IOException e) {
					LOG.log(Level.WARNING, "Failed to close InputStream.", e);
				}	
			}	            		
		}
		createTextImage(text);
		scroller = new PingPongScroller();
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
		//head and tailing space
		maxXPos=(int)(0.5f+rect.getWidth())+2*internalBufferXSize;
		int ypos=internalBufferYSize-(internalBufferYSize-h)/2;

		img = new BufferedImage(maxXPos, internalBufferYSize, BufferedImage.TYPE_BYTE_GRAY);
		g2 = img.createGraphics();

		g2.setColor(new Color(128));
		g2.setFont(font);		
		g2.setClip(0, 0, maxXPos, internalBufferYSize);

		g2.drawString(text, internalBufferXSize, ypos);
		DataBufferByte dbi = (DataBufferByte)img.getRaster().getDataBuffer();
		byte[] textBuffer=dbi.getData();
		g2.dispose();

		xofs = 0;
		
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
		scroller.updateText();
	}	

	
	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * 
	 * @param nr
	 */
	public void setTextscroller(int nr) {	
		if (nr==0) {
			scroller = new PingPongScroller();
			scrollerNr = 0;
		} else {
			scroller = new LeftScroller();
			scrollerNr = 1;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public int getTextscroller() {		
		return scrollerNr;
	}
	
	/**
	 * this interface is used by all text scrollers
	 * 
	 * @author mvogt
	 *
	 */
	interface Textscroller {
		void updateText();
	}

	
	/**
	 * abstract class for text scrollers
	 * @author mvogt
	 *
	 */
	abstract class AbstractScroller implements Textscroller {
		/** The wait. */
		protected int wait=0;

		protected abstract void scroll();

		@Override
		public void updateText() {
			//is scrolling needed?
			if (wait>0) {
				wait--;
			} else {
				if (maxXPos < getInternalBufferXSize()) {
					//no need to scroll
					xofs = (getInternalBufferXSize()-maxXPos)/2;
					wait=99999;
				} else {
					scroll();
				}
			}
			
			copyDataToBuffer();
		}
		
		private void copyDataToBuffer() {
			int srcOfs=xofs;
			int dstOfs=0;	
			try {
				if (maxXPos < getInternalBufferXSize()) {
					//text image smaller than internal buffer
					srcOfs=0;
					dstOfs=xofs;				
					for (int y=0; y<internalBufferYSize; y++) {				    
					    System.arraycopy(textAsImage, srcOfs, internalBuffer, dstOfs, maxXPos);
						dstOfs+=internalBufferXSize;
						srcOfs+=maxXPos;
					}				
				} else {
					for (int y=0; y<internalBufferYSize; y++) {
					    System.arraycopy(textAsImage, srcOfs, internalBuffer, dstOfs, internalBufferXSize);
						dstOfs+=internalBufferXSize;
						srcOfs+=maxXPos;
					}				
				}
			} catch (Exception e) {
				//if the image is resized, this could lead to an arrayoutofboundexception!
			}
			
		}
	}
	
	
	/**
	 * scoll left, wait, scoll right, wait, repeat
	 * @author mvogt
	 *
	 */
	class PingPongScroller extends AbstractScroller {
		/** The scroll right. */
		private boolean scrollRight=false;

		public PingPongScroller() {
			xofs = internalBufferXSize;
		}
		
		protected void scroll() {
			if (scrollRight) {
				xofs+=SCROLL_AMMOUNT;
				if (xofs>maxXPos-internalBufferXSize*2) {
					scrollRight=false;
					xofs=maxXPos-internalBufferXSize*2;
					wait=CHANGE_SCROLLING_DIRECTION_TIMEOUT;
				}			
			} else {
				xofs-=SCROLL_AMMOUNT;
				if (xofs<1+internalBufferXSize) {
					scrollRight=true;
					xofs=internalBufferXSize;
					wait=CHANGE_SCROLLING_DIRECTION_TIMEOUT;
				}
			}						

		}

	}

	/**
	 * Scroll the text to the left, jump to left side if finished
	 * 
	 * @author mvogt
	 *
	 */
	class LeftScroller extends AbstractScroller {
		boolean flipNext = false;
		
		public void scroll() {	
			if (flipNext) {
				xofs=0;
				flipNext = false;
				return;
			}
			
			xofs+=SCROLL_AMMOUNT;
			if (xofs+internalBufferXSize>maxXPos) {				
				flipNext = true;
				wait=CHANGE_SCROLLING_DIRECTION_TIMEOUT;
			}						
		}
	}

	
	/**
	 * 
	 * @author mvogt
	 *
	 */
	class SingleCharScroller extends AbstractScroller {
		public void scroll() {
			
		}		
	}

}