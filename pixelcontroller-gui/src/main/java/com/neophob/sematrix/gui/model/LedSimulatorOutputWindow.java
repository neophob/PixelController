package com.neophob.sematrix.gui.model;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.glue.MatrixData;
import com.neophob.sematrix.core.layout.Layout;

/**
 * calculate the size of the matrix simulation window
 * @author michu
 *
 */
public class LedSimulatorOutputWindow {

	private static final Logger LOG = Logger.getLogger(LedSimulatorOutputWindow.class.getName());
	
	/** The Constant RAHMEN_SIZE. */
	private static final int RAHMEN_SIZE = 2;

	/** The led size. */
	private int ledSize;
	private int rahmenSize;

	private MatrixData matrixData;
	
	private Point windowSize;

	public LedSimulatorOutputWindow(MatrixData matrixData, int ledSize, Layout layout) {
		this.matrixData = matrixData;
		this.ledSize = ledSize;
		this.rahmenSize = RAHMEN_SIZE;
		calculateWindowsSize(layout);
	}

	/**
	 * 
	 * @param layout
	 * @return
	 */
	public void calculateWindowsSize(Layout layout) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int)screenSize.getWidth();
		int screenHeight = (int)screenSize.getHeight();

		boolean firstRun = true;
		int sanityCheck=0;
		do {
			if (firstRun) {
				firstRun=false;
			} else {
				if (ledSize>2) {
					ledSize--;
				}
				
				if (rahmenSize>0) {
					rahmenSize--;
				}
			}
			
			if (sanityCheck++ > 200) {
				LOG.log(Level.SEVERE, "Infitie loop detected, fail here, current point: "+windowSize);
				throw new IllegalStateException("Infinite loop detected!");
			}
			windowSize = getWindowSize(layout);
			
		} while ((windowSize.getX() > screenWidth || windowSize.getY() > screenHeight));
	}

	/**
	 * 
	 * @param layout
	 * @return
	 */
	private Point getWindowSize(Layout layout) {		
		int x, y;

		switch (layout.getLayoutName()) {
		case HORIZONTAL:
			x = getOneMatrixXSize() * layout.getRow1Size() + layout.getRow2Size();
			y = getOneMatrixYSize();
			break;

		default: // AKA BOX
			int xsize = (layout.getRow1Size() + layout.getRow2Size()) / 2;
			x = getOneMatrixXSize() * xsize;
			y = getOneMatrixYSize() * 2; // 2 rows
			break;
		}

		return new Point(x+rahmenSize,y+20+2*rahmenSize);
	}

	/**
	 * Gets the one matrix x size.
	 * 
	 * @return the one matrix x size
	 */
	public int getOneMatrixXSize() {
		return rahmenSize + matrixData.getDeviceXSize() * (rahmenSize + ledSize);
	}

	/**
	 * Gets the one matrix y size.
	 * 
	 * @return the one matrix y size
	 */
	public int getOneMatrixYSize() {
		return rahmenSize + matrixData.getDeviceYSize() * (rahmenSize + ledSize);
	}

	/**
	 * @return the windowSize
	 */
	public Point getWindowSize() {
		return windowSize;
	}

	/**
	 * @return the rahmenSize
	 */
	public int getRahmenSize() {
		return rahmenSize;
	}

	
	/**
	 * @return the ledSize
	 */
	public int getLedSize() {
		return ledSize;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("LedSimulatorOutputWindow [ledSize=%s, rahmenSize=%s, matrixData=%s, windowSize=%s]",
						ledSize, rahmenSize, matrixData, windowSize);
	}
	
	
}
