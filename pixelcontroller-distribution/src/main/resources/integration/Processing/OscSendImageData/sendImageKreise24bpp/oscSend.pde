import oscP5.*;
import netP5.*;

//RGB Colorspace
int BPP = 3;

OscP5 oscP5 = new OscP5(this, 12000);
NetAddress myRemoteLocation = new NetAddress("pixelcontroller.local", 9876);

OscMessage myMessage = new OscMessage("OSC_GENERATOR1");
byte[] bfr = new byte[DATA_SIZE*BPP];

void sendOsc() {
  
  myMessage.clearArguments();

  int ofs=0;
  loadPixels();
  for (int pxl: pixels) {
    bfr[ofs++] = (byte)((pxl>>16) & 0xff);    
    bfr[ofs++] = (byte)((pxl>>8) & 0xff);
    bfr[ofs++] = (byte)(pxl & 0xff);
  }
  updatePixels();

  //send the 24bit buffer to PixelController
  myMessage.add(bfr); // add an int array to the osc message 

  // send the message 
  oscP5.send(myMessage, myRemoteLocation);
}

