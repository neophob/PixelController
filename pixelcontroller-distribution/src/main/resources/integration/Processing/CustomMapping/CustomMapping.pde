int rectX, rectY;
int mX, mY;
int[] matrix;
int matrixOfs=0;

int resX = 10;
int resY = 5;

void setup() {
  println("PixelController custom mapping tool. Press 'd' to undo last action.");
  println("Copy and paste result to your config.properties file.");
  size(800,630);
  background(0);
  frameRate(20);
  smooth();
  calcRectSize(resX,resY);
}


void draw() {
  background(0);
  drawMatrix(mX, mY);
  fill(255);
  text("PixelController Custom Matrix Mapping, Matrix Resolution: "+mX+"/"+mY+". "+
  "Press a or y to change matrix width, press s or x to change matrix height.", 10, 20);
}

void calcRectSize(int w, int h) {
  mX = w;
  mY = h;
  rectX = (int)(width / w);
  rectY = (int)((height-30) / h);
  matrix = new int[mX*mY];
  matrixOfs = 0;  
}

void keyPressed() {
  if (key == 'd') {
    deleteLastEntry();
  }
  if (key == 'a') {
    if (resX>1) {
      resX--;
      calcRectSize(resX,resY);
    }
  }
  if (key == 'y') {
    resX++;
    calcRectSize(resX,resY);
  }
  if (key == 's') {
    if (resY>1) {
      resY--;
      calcRectSize(resX,resY);
    }
  }
  if (key == 'x') {
    resY++;
    calcRectSize(resX,resY);
  }
}

void deleteLastEntry() {
  if (matrixOfs<1) {
    return;
  }
  matrixOfs--;
}

void mousePressed() {
  if (frameCount<20) {
    return;
  }
  
  int pos = mouseCoordToIndex();
  if (containsCurrentPosition(pos)>-1) {
    return;
  }
  matrix[matrixOfs++] = pos;
  print("output.mapping=");
  for (int i=0; i<matrixOfs; i++) {
    print(matrix[i]+",");
  }
  println();
}


