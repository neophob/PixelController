import oscP5.*;
import netP5.*;

OscP5 oscP5 = new OscP5(this, 12000);
NetAddress myRemoteLocation = new NetAddress("127.0.0.1", 9876);

OscMessage myMessage = new OscMessage("OSC_GENERATOR2");
byte[] bfr = new byte[4096];

void sendOsc() {
  
  myMessage.clearArguments();

  loadPixels();

  int ofs=0;
  for (int pxl: pixels) {
    bfr[ofs++] = (byte)(pxl & 0xff);
  }

  updatePixels();

  //send the 8bit buffer to PixelController
  myMessage.add(bfr); // add an int array to the osc message 

  // send the message 
  oscP5.send(myMessage, myRemoteLocation);
}

