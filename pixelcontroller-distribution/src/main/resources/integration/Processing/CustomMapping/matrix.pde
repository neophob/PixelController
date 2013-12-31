
void drawMatrix(int w, int h) {
  int yofs = 30, xofs = 0, ofs = 0;
  int highlight = mouseCoordToIndex();
  int msex = mouseX / rectX;
  int msey = mouseY / rectY;
  for (int y=0; y<h; y++) {
    xofs = 0;
    
    for (int x=0; x<w; x++) {
      int sel = containsCurrentPosition(ofs);            
      if (highlight == ofs) {
        fill(192,64,64);  
      } else if (sel>=0) {
        fill(192,192,64);
      } else {
        fill(128);
      }
      rect(xofs, yofs, rectX, rectY);
      if (sel>-1) {
        fill(0);        
        text(""+sel, xofs+rectX/3, yofs+rectY-8);                
      }

      xofs += rectX;
      ofs++;
    }
    yofs += rectY;
  }
}

int mouseCoordToIndex() {
  int xx = (int)(mouseX / rectX);
  int yy = (int)((mouseY-30) / rectY);
  
  return (int)(xx + yy*mX);
}

int containsCurrentPosition(int pos) {
  for (int i=0; i<matrixOfs; i++) {
    if (matrix[i] == pos) {
      return i;
    }
  }
  return -1;
}
