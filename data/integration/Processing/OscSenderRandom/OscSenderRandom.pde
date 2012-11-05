import oscP5.*;
import netP5.*;

OscP5 oscP5;
NetAddress myRemoteLocation;
Random r;
// roove of concept, send pixelinformation as osc message (OFFSET, PIXEL1, PIXEL2, PIXEL3, PIXEL4)
// take less than 5ms to send 320 32bit pixe data.


void setup() {
  size(400, 400);
  frameRate(1);
  /* start oscP5, listening for incoming messages at port 12000 */
  oscP5 = new OscP5(this, 12000);

  //CHANGE to your ip
//  myRemoteLocation = new NetAddress("192.168.43.111", 9876);
  myRemoteLocation = new NetAddress("127.0.0.1", 9876);

  r = new Random();
}

int ii=0;
void draw() {

  long now=System.currentTimeMillis();  
  for (int i=0; i<96; i+=4) {
    OscMessage myMessage = new OscMessage("/RANDOMIZE");
    oscP5.send(myMessage, myRemoteLocation);
  }
  long needed = System.currentTimeMillis()-now;
  println("Sendtime: "+needed+"ms");
}

