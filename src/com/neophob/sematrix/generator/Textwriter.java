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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.Collector;

/**
 * @author mvogt
 *
 */
public class Textwriter extends Generator {

	private static final int TEXT_BUFFER_X_SIZE=512;
	private static final int CHANGE_SCROLLING_DIRECTION_TIMEOUT=12;
	
	private static Logger log = Logger.getLogger(Textwriter.class.getName());
	
	private int xpos,ypos;
	private Font font;
	private Color color;
	
	private int xofs;
	private int maxXPos;
	private boolean scrollRight=true;
	private int wait;
	
	private int[] textBuffer;
	
	/**
	 * 
	 * @param filename
	 */
	public Textwriter(String fontName, int fontSize) {
		super(GeneratorName.TEXTWRITER);
		color = new Color(255,255,255);
		xpos=0;
		ypos=getInternalBufferYSize()-2;
		InputStream is = Collector.getInstance().getPapplet().createInput(fontName);
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont((float)fontSize);
			//font=new Font("", Font.PLAIN, fontSize);
			log.log(Level.INFO, "Loaded font "+fontName+", size: "+fontSize);
			createTextImage("ABcd!");			
		} catch (Exception e) {
			log.log(Level.WARNING, "Failed to load font "+fontName+"!", e);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public void createTextImage(String text) {
		BufferedImage img = getBufferedImage();

		Graphics2D g2 = img.createGraphics();
		FontRenderContext frc = g2.getFontRenderContext(); 
		TextLayout layout = new TextLayout(text, font, frc); 
		Rectangle2D rect = layout.getBounds();
		
		int h = (int)(0.5f+rect.getHeight());
		System.out.println(h);

		ypos=internalBufferYSize-(internalBufferYSize-h)/2;
		maxXPos=(int)(0.5f+rect.getWidth());
		
		g2.setColor(color);
		g2.setFont(font);		
		g2.setClip(0, 0, TEXT_BUFFER_X_SIZE, internalBufferYSize);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.drawString(text, xpos, ypos);
		DataBufferInt dbi = (DataBufferInt)img.getRaster().getDataBuffer();
		textBuffer=dbi.getData();

		System.out.println("maxXPos: "+maxXPos);
		g2.dispose();
	}
	
	/**
	 * create a BufferedImage from a Texture Layer
	 * @param nr
	 * @return BufferedImage 
	 */
	private BufferedImage getBufferedImage() {
		BufferedImage image = 
			new BufferedImage( TEXT_BUFFER_X_SIZE, internalBufferYSize, BufferedImage.TYPE_INT_RGB); 
		return image;
	}
	
	@Override
	public void update() {
		int srcOfs=xofs;
		int dstOfs=0;
		for (int y=0; y<internalBufferYSize; y++) {
			System.arraycopy(textBuffer, srcOfs, this.internalBuffer, dstOfs, internalBufferXSize);
			dstOfs+=internalBufferXSize;
			srcOfs+=TEXT_BUFFER_X_SIZE;
		}
		
		if (wait>0) {
			wait--;
		} else {
			if (scrollRight) {
				xofs+=4;
				if (xofs>maxXPos-internalBufferXSize) {
					scrollRight=false;
					xofs=maxXPos-internalBufferXSize;
					wait=CHANGE_SCROLLING_DIRECTION_TIMEOUT;
				}			
			} else {
				xofs-=4;
				if (xofs<1) {
					scrollRight=true;
					xofs=0;
					wait=CHANGE_SCROLLING_DIRECTION_TIMEOUT;
				}
			}			
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
}
