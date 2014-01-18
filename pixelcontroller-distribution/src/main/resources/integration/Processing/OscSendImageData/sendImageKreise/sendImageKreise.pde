//source http://www.openprocessing.org/sketch/7475

float i;

//define the size of your matrix here. the maximal size is 16x16!
final int PIXELS_X = 64;
final int PIXELS_Y = 64;
int DATA_SIZE = PIXELS_X * PIXELS_Y;

//the maximal UDP packet size
int MAXIMAL_UDP_PACKET_SIZE = 65507;
 
int kreise = 23;
 
float ersterdurchmesser = 300;
float durchmesserdifferenz = 13;
 
float xVersatz = 13;
float yVersatz;
 
float geschwindigkeit = 0.002;
 
void setup()
{
  frameRate(30);
  size(PIXELS_X, PIXELS_Y);
  println("\nOSC Packet size: "+DATA_SIZE*BPP);
  if (DATA_SIZE*BPP > MAXIMAL_UDP_PACKET_SIZE) {
    println("ERROR, you use OSC to send that much data (UDP Packet size limit is 64k). Lower your resolution.");
    System.exit(1);
  }
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


