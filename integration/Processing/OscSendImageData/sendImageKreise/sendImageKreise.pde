//source http://www.openprocessing.org/sketch/7475

float i;
 
int kreise = 23;
 
float ersterdurchmesser = 300;
float durchmesserdifferenz = 13;
 
float xVersatz = 13;
float yVersatz;
 
float geschwindigkeit = 0.002;
 
void setup()
{
  frameRate(30);
  size(64, 64);
//  textFont(createFont("Sans", 11));
  smooth();
  noStroke();
}
 
void draw()
{
  background(255);
  translate(width/2, height/2);
  pushMatrix();
  rotate(i);
  for(int j = 0; j < kreise; j++)
  {
//    if(j % 2 == 0) fill(239, 239, 212); else fill(42, j * 185/kreise, 84);
    if(j % 2 == 0) fill(0); else fill(j * 185/kreise);
    rotate(i);
    ellipse(0, 0, ersterdurchmesser - j * durchmesserdifferenz, ersterdurchmesser - j * durchmesserdifferenz);
    translate(xVersatz, yVersatz);
  }
  popMatrix();
  i += geschwindigkeit;
  translate(-width/2, -height/2);
  
  filter(GRAY);
  sendOsc();
}
 
void keyPressed()
{
  if(key == '1')
  {
    kreise = 23;
    durchmesserdifferenz = 13;
    xVersatz = 13;
    geschwindigkeit = 0.002;
  }
  if(key == '2')
  {
    kreise = 30;
    durchmesserdifferenz = 10;
    xVersatz = 90;
    geschwindigkeit = 0.002;
  }
}


