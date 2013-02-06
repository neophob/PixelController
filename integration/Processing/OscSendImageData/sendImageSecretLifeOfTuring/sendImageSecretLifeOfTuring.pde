//http://www.openprocessing.org/sketch/17043

/////////////////////////////////////////////////
//                                             //
//    The Secret Life of Turing Patterns       //
//                                             //
/////////////////////////////////////////////////
 
// Inspired by the work of Jonathan McCabe
// (c) Martin Schneider 2010
 
 
int scl = 4, dirs = 9, rdrop = 8, lim = 128;
int res = 2, palette = 0, pattern = 2, soft = 2;
int dx, dy, w, h, s;
boolean border, invert;
float[] pat;
PImage img;
  
void setup() {
  size(64, 64);
  colorMode(HSB);
  reset();
}
 
void reset() {
  w = width/res;
  h = height/res;
  s = w*h;
  img = createImage(w, h, RGB);
  pat = new float[s];
  // random init
  for(int i=0; i<s; i++) 
    pat[i] = floor(random(256));
}
 
void draw() {
   
  // constrain the mouse position
  if(border) {
    mouseX = constrain(mouseX,0,width-1);
    mouseY = constrain(mouseY,0,height-1);
  }
     
  // add a circular drop of chemical
  if(mousePressed) {
      if(mouseButton != CENTER) {
      int x0 = mod((mouseX-dx)/res, w);
      int y0 = mod((mouseY-dy)/res, h);
      int r = rdrop * scl / res ;
      for(int y=y0-r; y<y0+r;y++)
        for(int x=x0-r; x<x0+r;x++) {
          int xwrap = mod(x,w), ywrap = mod(y,w);
          if(border && (x!=xwrap || y!=ywrap)) continue;         
          if(dist(x,y,x0,y0) < r)
            pat[xwrap+w*ywrap] = mouseButton == LEFT ? 255 : 0;
        }
    }
  }
 
  // calculate a single pattern step
  pattern();
   
  // draw chemicals to the canvas
  img.loadPixels();
  for(int x=0; x<w; x++)
    for(int y=0; y<h; y++) {
      int c = (x+dx/res)%w + ((y+dy/res)%h)*w;
      int i = x+y*w;
      float val = invert ? 255-pat[i]: pat[i];
      switch(palette) {
        case 0: img.pixels[c] = color(0, 0, val); break;
        case 1: img.pixels[c] = color(64+val/4, val, val); break;
        case 2: img.pixels[c] = color(val,val,255-val); break;
        case 3: img.pixels[c] = color(val,128,255); break;
      }
    }
  img.updatePixels();
   
  // display the canvas
  if(soft>0) smooth(); else noSmooth();
  image(img, 0, 0, res*w, res*h);
  if(soft==2) filter(BLUR);
  
  filter(GRAY); 
  sendOsc();
  
  println(frameRate);
     
}
 
void keyPressed() {
  switch(key) {
    case 'r': reset(); break;
    case 'p': pattern = (pattern + 1) % 3; break;
    case 'c': palette = (palette + 1) % 4; break;
    case 'b': border = !border; dx=0; dy=0; break;
    case 'i': invert = !invert; break;
    case 's': soft = (soft + 1) % 3; break;
    case '+': lim = min(lim+8, 255); break;
    case '-': lim = max(lim-8, 0); break;
    case CODED:
      switch(keyCode) {
        case LEFT: scl = max(scl-1, 2); break;
        case RIGHT:scl = min(scl+1, 6); break;
        case UP:   res = min(res+1, 5); reset(); break;
        case DOWN: res = max(res-1, 1); reset(); break;
      }
      break;
  }
}
 
// moving the canvas
void mouseDragged() {
  if(mouseButton == CENTER && !border) {
    dx = mod(dx + mouseX - pmouseX, width);
    dy = mod(dy + mouseY - pmouseY, height);
  }
}
 
// floor modulo
final int mod(int a, int n) {
  return a>=0 ? a%n : (n-1)-(-a-1)%n;
}

