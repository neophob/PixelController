import controlP5.*;
import oscP5.*;
import netP5.*;
import java.util.Properties;

ControlP5 cp5;
OscP5 oscP5;
NetAddress myRemoteLocation;
boolean initDone = false;

final int SENDING_PORT = 9876;

Textlabel tget;

void setup() {
  size(400, 400);
  frameRate(50);
  
  String targetip = getTargetIp();
  
  /* start oscP5, listening for incoming messages at port 8000 */
  oscP5 = new OscP5(this, 10000);
  myRemoteLocation = new NetAddress(targetip, SENDING_PORT);

  cp5 = new ControlP5(this);
  
  tget = new Textlabel(cp5, "PixelController REMOTE, target IP: <"+targetip+">",100,20,400,50);
  
  for (int i=0; i<8; i++) {
    cp5.addButton("Preset"+i)
     .setValue(0)
     .setPosition(100,50+i*25)
     .setSize(200,20)
     .setLabel("Preset "+(i+10))
     .setId(10+i);
   
  }  
  initDone = true;
  println("---");
}

void draw() {
  background(0);
  tget.draw(this);
}

// function controlEvent will be invoked with every value change 
// in any registered controller
public void controlEvent(ControlEvent theEvent) {
  if (!initDone) {
    return;
  }
  println("got a control event from controller with id "+theEvent.getId());
  println(theEvent.getController().getName());
  
  if (theEvent.getId()>0) {
    loadPreset(theEvent.getId());
    println("preset "+theEvent.getId());
  }
}

String getTargetIp() {
  try {
    Properties p = new Properties();  
    p.load(openStream("config.properties"));
    return p.getProperty("targetip","127.0.0.1");    
  } catch (IOException e) {
    return "FAILED TO LOAD FILE config.properties";
  }   
}


void loadPreset(int nr) {
  oscP5.send("/CHANGE_PRESENT", new Object[] { ""+nr }, myRemoteLocation);
  oscP5.send("/LOAD_PRESENT", new Object[] { }, myRemoteLocation);
  println("load "+nr); 
}
