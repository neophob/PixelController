import oscP5.*;
import netP5.*;

OscP5 oscP5 = new OscP5(this, 12000);
NetAddress myRemoteLocation = new NetAddress("pixelcontroller.local", 9876);

OscMessage myMessage1 = new OscMessage("OSC_GENERATOR1");
OscMessage myMessage2 = new OscMessage("OSC_GENERATOR2");
byte[] bfr1 = new byte[XRES*YRES*3];
byte[] bfr2 = new byte[XRES*YRES*3];

void sendOsc() {
  
  myMessage1.clearArguments();
  myMessage2.clearArguments();

  loadPixels();

  int ofs1=0;
  int ofs2=0;
  for (int i=0; i<4096; i++) {
    bfr1[ofs1++] = (byte)(r[0]);    
    bfr1[ofs1++] = (byte)(g[0]);
    bfr1[ofs1++] = (byte)(b[0]);
    bfr2[ofs2++] = (byte)(r[1]);    
    bfr2[ofs2++] = (byte)(g[1]);
    bfr2[ofs2++] = (byte)(b[1]);
  }
  updatePixels();

  //send the 24bit buffer to PixelController
  myMessage1.add(bfr1); // add an int array to the osc message
  myMessage2.add(bfr2); // add an int array to the osc message 

  // send the message 
  oscP5.send(myMessage1, myRemoteLocation);
  oscP5.send(myMessage2, myRemoteLocation);
}

