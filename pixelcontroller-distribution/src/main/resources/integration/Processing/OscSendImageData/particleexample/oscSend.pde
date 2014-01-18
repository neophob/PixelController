import oscP5.*;
import netP5.*;

OscP5 oscP5;
NetAddress myRemoteLocation = new NetAddress("pixelcontroller.local", 9876);

OscMessage myMessage = new OscMessage("OSC_GENERATOR1");
byte[] bfr = new byte[XRES*YRES];

boolean sendData = true;

void initOsc() {
  oscP5 = new OscP5(this, 0);
}

void sendOsc() {
  
  if (!sendData) {
    return;
  }
  
  myMessage.clearArguments();

  loadPixels();

  //invalid buffer size, return
  if (pixels.length!=4096) {
    sendData = false;
    updatePixels();
    return;
  }
  
  int ofs=0;
  for (int pxl: pixels) {
    bfr[ofs++] = (byte)(pxl & 0xff);
  }

  updatePixels();
  
  //send the buffer to PixelController
  myMessage.add(bfr); // add an int array to the osc message 

  // send the message 
  oscP5.send(myMessage, myRemoteLocation);
}
