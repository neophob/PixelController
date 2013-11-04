import java.util.*;

class OscAction {
  String keyName;
  List<Action> actions;
  long timeStamp;
  boolean finished;
  int currentAction;

  public OscAction(String keyName) {
    this.keyName = keyName;
    this.actions = new ArrayList<Action>();
    finished = true;
  }

  void addAction(Action a) {
    this.actions.add(a);
  }
  
  void clearActions() {
    this.actions.clear();
  }

  void start() {
    timeStamp = System.currentTimeMillis();
    currentAction = 0;
    finished = false;
    sendMessage();
  }

  void sendMessage() {
    do {
      String name = actions.get(currentAction).command;
      Object[] param = actions.get(currentAction).parameter;
      oscP5.send(name, param, myRemoteLocation);
      println("sent "+name+" to PixelController, currentAction: "+currentAction+" of "+actions.size());
      events.updateLastAction("sent "+name+" to PixelController");      
      currentAction++;
    } while (currentAction<actions.size() && actions.get(currentAction).delayInMs==0);
  }

  void update() {
    if (finished==false && currentAction >= actions.size()) {
      finished = true;
      println("OscAction "+keyName+" is finished!");
      events.updateLastAction("OscAction "+keyName+" is finished!");      
    }

    if (finished) {
      return;
    }

    if ((System.currentTimeMillis()-timeStamp) > actions.get(currentAction).delayInMs) {      
      println("next action "+(System.currentTimeMillis()-timeStamp)+" > "+actions.get(currentAction).delayInMs);           
      sendMessage();
    }
  }
}




class Action {
  String command;
  Object[] parameter;
  long delayInMs;

  public Action(String command, long delayInMs) {
    this.command = command;
    this.delayInMs = delayInMs;
    this.parameter = new Object[] {};
  }

  public Action(String command) {
    this(command, 0);
  }

  public Action(String command, Object[] parameter, long delayInMs) {
    this.command = command;
    this.parameter = parameter;
    this.delayInMs = delayInMs;
  }  

  public Action(String command, Object[] parameter) {
    this(command, parameter, 0);
  }
}

