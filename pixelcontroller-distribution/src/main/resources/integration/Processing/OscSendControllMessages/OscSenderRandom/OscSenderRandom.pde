import oscP5.*;
import netP5.*;

OscP5 oscP5;
NetAddress myRemoteLocation;

//send OSC RANDOMIZE message to pixelcontroller

void setup() {
  size(400, 400);
  frameRate(4);
  /* start oscP5, listening for incoming messages at port 12000 */
  oscP5 = new OscP5(this,9999);

  myRemoteLocation = new NetAddress("pixelcontroller.local", 9876);
}

int ii=0;
void draw() {

  long now=System.currentTimeMillis();  
  for (int i=0; i<1; i++) {
    OscMessage myMessage = new OscMessage("/CURRENT_VISUAL");
    myMessage.add(1);
    oscP5.send(myMessage, myRemoteLocation);
    
    myMessage = new OscMessage("/RANDOMIZE");
    oscP5.send(myMessage, myRemoteLocation);
    println(".");
  }
  long needed = System.currentTimeMillis()-now;
  println("Sendtime: "+needed+"ms");
}

