import oscP5.*;
import netP5.*;

OscP5 oscP5;
NetAddress myRemoteLocation;

void setup() {
  size(400, 400);
  frameRate(1);
  /* start oscP5, listening for incoming messages at port 12000 */
  oscP5 = new OscP5(this, 12000);

  //CHANGE to your ip
  myRemoteLocation = new NetAddress("pixelcontroller.local", 9876);

  long now=System.currentTimeMillis();  

  oscP5.send("/CURRENT_VISUAL", new Object[] { 0 }, myRemoteLocation);
  oscP5.send("/CHANGE_GENERATOR_A", new Object[] { 3 }, myRemoteLocation);
  oscP5.send("/CHANGE_EFFECT_A", new Object[] { 0 }, myRemoteLocation);
  oscP5.send("/CHANGE_MIXER", new Object[] { 0 }, myRemoteLocation);
  oscP5.send("/IMAGE", new Object[] { "does-not-exist" }, myRemoteLocation);

  long needed = System.currentTimeMillis()-now;
  println("Sendtime: "+needed+"ms");

  noLoop();
}

int ii=0;
void draw() {

}

