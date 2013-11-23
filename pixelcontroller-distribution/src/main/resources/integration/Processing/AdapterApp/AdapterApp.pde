import oscP5.*;
import netP5.*;

OscP5 oscP5;
NetAddress myRemoteLocation;
ActionFactory factory;
LastAction events;

final long ICON_DELAY = 4000;
final long GESTURE_ANIMATION_DURATION = 20000;
final long DO_SOMETHING_AFTER_LONG_TIME_INACTIVITY = GESTURE_ANIMATION_DURATION*3;

final String SENDING_HOST = "127.0.0.1";
final int LISTENING_PORT = 8000;
final int SENDING_PORT = 9876;

int oscEvents = 0;
int startedActions = 0;
int cnt=0;
//String lastState="";
//long lastStateTs;

void setup() {
  size(380, 170);
  frameRate(15);
  /* start oscP5, listening for incoming messages at port 8000 */
  oscP5 = new OscP5(this, LISTENING_PORT);
  myRemoteLocation = new NetAddress(SENDING_HOST, SENDING_PORT);
  
  events = new LastAction();
  
  factory = new ActionFactory();
  factory.generateActions();
  
  displayStatistics();
}

void displayStatistics() {
    background(0,0,128);
    fill(255,255,255);
    text("KinecticSpace to PixelController Adapter v0.2", 10,20);
    text("Listening on Port: "+LISTENING_PORT+", sending to "+SENDING_HOST+":"+SENDING_PORT, 20,40);
    text("Actions in Library "+factory.oscActions.size(), 20,60);
    text("OSC Events: "+oscEvents, 20,80);  
    text("Actions started: "+startedActions, 20,100);    
    text("Last Action: "+events.getLastAction(), 20,120);
    long l=(events.getLastActionTs())/1000;
    text("executed: "+l+"s ago", 20,140);    
    rect(0, height-10, (cnt%100)/100f*width, height);
}

void draw() {
  cnt++;
  if (cnt%3==0) {
    displayStatistics();
  }
  factory.update();
  
  //TODO if last action is long time ago (5 minutes?) make a random preset selection
}


void oscEvent(OscMessage theOscMessage) {
  oscEvents++;
  
  if (theOscMessage==null || theOscMessage.addrPattern()==null) {
    return;
  }
  
  String pattern = theOscMessage.addrPattern().trim().toUpperCase();
  if (factory.currentAction==null) {
    if (factory.executeAction(pattern)) {
      startedActions++;
    }
  } else {
    events.updateLastAction("Event ignored as Action is still running: "+pattern);    
    println("Event ignored as Action is still running!");
  }
  
}

//simulate action, use key a..z
void keyPressed() {
  int keyIndex = -1;
  if (key >= 'A' && key <= 'Z') {
    keyIndex = key - 'A';
  } else if (key >= 'a' && key <= 'z') {
    keyIndex = key - 'a';
  }
  
  if (keyIndex>=0 && keyIndex<factory.oscActions.size()) {
    //factory.executeAction(factory.oscActions.get(keyIndex).keyName);    
    OscMessage msg = new OscMessage(factory.oscActions.get(keyIndex).keyName);
    oscEvent(msg);
  }
  
  //test bad argument
  if (key=='1') {
    oscEvent(null);
  }
}
