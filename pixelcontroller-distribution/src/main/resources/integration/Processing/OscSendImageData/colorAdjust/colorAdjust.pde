//pimitive sketch to check color adjustment of different pixelinvaders panels

//key 0 and 1 select the panel
//keys rRgGbB increase/decrease the color value

//start pixelcontroller
//select visual 1 generator OSC GEN 1
//select visual 2 generator OSC GEN 2
//map visual 1 to output 1
//map visual 2 to output 2

int STEP = 15;
final int XRES = 64;
final int YRES = 64;

int activePanel=0;
int[] r,g,b;
int[][] bfr = new int[2][4096];

void setup()
{
  frameRate(25);
  size(XRES, YRES);
  smooth();
  noStroke();
  
  r=new int[2];
  g=new int[2];
  b=new int[2];
  r[0]=255;
  g[0]=255;
  b[0]=255;
  r[1]=255;
  g[1]=255;
  b[1]=255;
}
 
void draw()
{
  background(color(r[activePanel], g[activePanel], b[activePanel]));  
  sendOsc();
}
 
void keyPressed()
{
  println("."+key);
  if(key == '1') {
    activePanel=0;
  }
  if(key == '2') {
    activePanel=1;
  }

  if(key == 'r') {
    r[activePanel]-=STEP;
    if (r[activePanel]<0) r[activePanel]=0;
  }
  if(key == 'R') {
    r[activePanel]+=STEP;
    if (r[activePanel]>255) r[activePanel]=255;
  }
  if(key == 'g') {
    g[activePanel]-=STEP;
    if (g[activePanel]<0) g[activePanel]=0;
  }
  if(key == 'G') {
    g[activePanel]+=STEP;
    if (g[activePanel]>255) g[activePanel]=255;
  }
  if(key == 'b') {
    b[activePanel]-=STEP;
    if (b[activePanel]<0) b[activePanel]=0;
  }
  if(key == 'B') {
    b[activePanel]+=STEP;
    if (b[activePanel]>255) b[activePanel]=255;
  }
  
  String s1 = "";
  String s2 = "";
  if (activePanel==0) {
    s1 = " *";
  } else {
    s2 = " *";
  }
  println("panel: 0, R: "+r[0]+", G: "+g[0]+", B: "+b[0]+s1);
  println("panel: 1, R: "+r[1]+", G: "+g[1]+", B: "+b[1]+s2);
}

