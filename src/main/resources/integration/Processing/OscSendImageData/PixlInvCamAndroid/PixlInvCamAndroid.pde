/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>Ketai Camera Features:
 * <ul>
 * <li>Interface for built-in camera</li>
 * <li></li>
 * </ul>
 * <p>Updated: 2012-10-21 Daniel Sauter/j.duran</p>
 */

import ketai.camera.*;
import oscP5.*;
import netP5.*;
import android.os.Bundle;
import android.view.WindowManager;

static String IP = "192.168.111.21";
OscP5 oscP5;
NetAddress myRemoteLocation = new NetAddress(IP, 9876);

OscMessage myMessage = new OscMessage("OSC_GENERATOR1");
byte[] bfr = new byte[4096];

boolean sendData = true;

KetaiCamera cam;
PImage pimgTmp;

void setup() {
//  orientation(PORTRAIT);
  orientation(LANDSCAPE);
  imageMode(CENTER);
  cam = new KetaiCamera(this, 320, 240, 24);
  pimgTmp = createImage(64,64,RGB);
  oscP5 = new OscP5(this, 0);
  
  background(0);
  textSize(32);
  text("PixelController OSC Cam Example, send to "+IP, 30, 30); 
}

void draw() {
  image(cam, width/2, height/2);
  sendOsc();
//  image(pimgTmp, width/2, height/2);
}

void onCameraPreviewEvent()
{
  cam.read();
}

// start/stop camera preview by tapping the screen
void mousePressed()
{
  if (cam.isStarted())
  {
    //cam.stop();
  }
  else
    cam.start();
}
void keyPressed() {
/*  if (key == CODED) {
    if (keyCode == MENU) {
      if (cam.isFlashEnabled())
        cam.disableFlash();
      else
        cam.enableFlash();
    }
  }*/
}

void sendOsc() {
  
  if (!sendData || cam == null || cam.pixels == null || cam.pixels.length == 0) {
    return;
  }
  
  myMessage.clearArguments();
  
  pimgTmp.copy(cam, 0,0,cam.width,cam.height, 0,0,64,64);

  pimgTmp.loadPixels();
  
  //invalid buffer size, return
  if (pimgTmp.pixels.length!=4096) {
    println("disable OSC Send, size: "+pimgTmp.pixels.length);
    sendData = false;
    pimgTmp.updatePixels();
    return;
  }
  
  int ofs=0;
  for (int pxl: pimgTmp.pixels) {
    bfr[ofs++] = (byte)(pxl & 0xff);
  }

  pimgTmp.updatePixels();
  
  image(pimgTmp, width/2, height/2);
    
  //send the buffer to PixelController
  myMessage.add(bfr); // add an int array to the osc message 

  // send the message 
  oscP5.send(myMessage, myRemoteLocation);
}

//dont go to sleep!
void onCreate(Bundle bundle) {
  super.onCreate(bundle);
  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
}

