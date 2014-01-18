import oscP5.*;
import netP5.*;

//BW Colorspace
int BPP = 1;

OscP5 oscP5 = new OscP5(this, 12000);
NetAddress myRemoteLocation = new NetAddress("pixelcontroller.local", 9876);

OscMessage myMessage = new OscMessage("OSC_GENERATOR1");
byte[] bfr = new byte[DATA_SIZE*BPP];

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

