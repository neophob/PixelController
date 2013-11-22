package com.neophob.sematrix.gui.model;

import com.neophob.sematrix.core.glue.MatrixData;
import com.neophob.sematrix.core.layout.Layout;

public class LedSimulatorOutputWindow {

  /** The Constant RAHMEN_SIZE. */
  private static final int RAHMEN_SIZE = 2;

  /** The led size. */
  private int ledSize;
  private int rahmenSize;

  private MatrixData matrixData;

  public LedSimulatorOutputWindow(MatrixData matrixData, int ledSize) {
    this.matrixData = matrixData;
    this.ledSize = ledSize;
    this.rahmenSize = RAHMEN_SIZE;
  }

  public WindowSize getWindowsSize(Layout layout) {
    int x, y;

    switch (layout.getLayoutName()) {
    case HORIZONTAL:
      do {
        x = getOneMatrixXSize() * layout.getRow1Size() + layout.getRow2Size();
        y = getOneMatrixYSize();
      } while ((x > 1000 || y > 1000) && ledSize-- > 2);
      break;

    default: // AKA BOX
      do {
        int xsize = (layout.getRow1Size() + layout.getRow2Size()) / 2;
        x = getOneMatrixXSize() * xsize;
        y = getOneMatrixYSize() * 2; // 2 rows
      } while ((x > 1000 || y > 1000) && ledSize-- > 2);
      break;
    }

    return new WindowSize(x, y);
  }

  /**
   * Gets the one matrix x size.
   * 
   * @return the one matrix x size
   */
  private int getOneMatrixXSize() {
    return rahmenSize + matrixData.getDeviceXSize() * (rahmenSize + ledSize);
  }

  /**
   * Gets the one matrix y size.
   * 
   * @return the one matrix y size
   */
  private int getOneMatrixYSize() {
    return rahmenSize + matrixData.getDeviceYSize() * (rahmenSize + ledSize);
  }
}
