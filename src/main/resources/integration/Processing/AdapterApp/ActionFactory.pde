class ActionFactory {

  int presetWithImage = 15;

  List<OscAction> oscActions;
  OscAction currentAction, switchbackAction;
  long actionEndedAt;   

  public ActionFactory() {
    oscActions = new ArrayList<OscAction>();
  }

  //kickit
  void generateActions() {
//Cold
    OscAction itsCold = new OscAction("/itscold");
    itsCold.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    itsCold.addAction(new Action("LOAD_PRESENT"));
    itsCold.addAction(new Action("IMAGE", new Object[] {"icn-skates.png"}));
    
    //Wait iconDelay
    itsCold.addAction(new Action("CHANGE_PRESENT", new Object[] {65}, ICON_DELAY));
    itsCold.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(itsCold);

//freeze
    OscAction freeze = new OscAction("/freeze");
    freeze.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    freeze.addAction(new Action("LOAD_PRESENT"));
    freeze.addAction(new Action("IMAGE", new Object[] {"icn-gun.png"}));    
    //Wait iconDelay
    freeze.addAction(new Action("CHANGE_PRESENT", new Object[] {72}, ICON_DELAY));
    freeze.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(freeze);

//headpan
    OscAction headpan = new OscAction("/headpan");
    headpan.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    headpan.addAction(new Action("LOAD_PRESENT"));
    headpan.addAction(new Action("IMAGE", new Object[] {"icn-skull.png"}));    
    //Wait iconDelay, 31=pixel heads 
    headpan.addAction(new Action("CHANGE_PRESENT", new Object[] {31}, ICON_DELAY));
    headpan.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(headpan);

//eks
    OscAction eks = new OscAction("/eks");
    eks.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    eks.addAction(new Action("LOAD_PRESENT"));
    eks.addAction(new Action("IMAGE", new Object[] {"icn-pattern.png"}));    
    //Wait iconDelay, 31=pixel heads 
    eks.addAction(new Action("CHANGE_PRESENT", new Object[] {28}, ICON_DELAY));
    eks.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(eks);

//spinner
    OscAction spinner = new OscAction("/spinner");
    spinner.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    spinner.addAction(new Action("LOAD_PRESENT"));
    spinner.addAction(new Action("IMAGE", new Object[] {"icn-pwr.png"}));    
    //Wait iconDelay
    spinner.addAction(new Action("CHANGE_PRESENT", new Object[] {60}, ICON_DELAY));
    spinner.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(spinner);

//yepee - drogen, auf dem saturn
    OscAction yepee = new OscAction("/yepee");
    yepee.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    yepee.addAction(new Action("LOAD_PRESENT"));
    yepee.addAction(new Action("IMAGE", new Object[] {"icn-saturn.png"}));    
    //Wait iconDelay
    yepee.addAction(new Action("CHANGE_PRESENT", new Object[] {76}, ICON_DELAY));
    yepee.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(yepee);

//djbobo
    OscAction djbobo = new OscAction("/djbobo");
    djbobo.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    djbobo.addAction(new Action("LOAD_PRESENT"));
    djbobo.addAction(new Action("IMAGE", new Object[] {"icn-skull.png"}));    
    //Wait iconDelay, 92=in hell
    djbobo.addAction(new Action("CHANGE_PRESENT", new Object[] {92}, ICON_DELAY));
    djbobo.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(djbobo);


//wave
    OscAction wave = new OscAction("/wave");
    wave.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    wave.addAction(new Action("LOAD_PRESENT"));
    wave.addAction(new Action("IMAGE", new Object[] {"icn-smiley.png"}));    
    //Wait iconDelay
    wave.addAction(new Action("CHANGE_PRESENT", new Object[] {79}, ICON_DELAY));
    wave.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(wave);

//hello, like wave
    OscAction hello = new OscAction("/hello");
    hello.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    hello.addAction(new Action("LOAD_PRESENT"));
    hello.addAction(new Action("IMAGE", new Object[] {"icn-smiley.png"}));
    //Wait iconDelay
    hello.addAction(new Action("CHANGE_PRESENT", new Object[] {79}, ICON_DELAY));
    hello.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(hello);

//Party
    OscAction party = new OscAction("/prrrty");
    party.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    party.addAction(new Action("LOAD_PRESENT"));
    party.addAction(new Action("IMAGE", new Object[] {"icn-play.png"}));
    //Wait iconDelay
    party.addAction(new Action("CHANGE_PRESENT", new Object[] {64}, ICON_DELAY));
    party.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(party);

//silence
    OscAction silence = new OscAction("/silence");
    silence.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    silence.addAction(new Action("LOAD_PRESENT"));
    silence.addAction(new Action("IMAGE", new Object[] {"icn-three-mokeys.png"}));
    //Wait iconDelay
    silence.addAction(new Action("CHANGE_PRESENT", new Object[] {75}, ICON_DELAY));
    silence.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(silence);

//Walking
    OscAction walk = new OscAction("/walking");
    walk.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    walk.addAction(new Action("LOAD_PRESENT"));
    walk.addAction(new Action("IMAGE", new Object[] {"icn-shoe.png"}));
    //Wait iconDelay
    walk.addAction(new Action("CHANGE_PRESENT", new Object[] {66}, ICON_DELAY));
    walk.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(walk);

//Drinking
    OscAction drink = new OscAction("/drinking");
    drink.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    drink.addAction(new Action("LOAD_PRESENT"));
    drink.addAction(new Action("IMAGE", new Object[] {"icn-glass.png"}));
    //Wait iconDelay
    drink.addAction(new Action("CHANGE_PRESENT", new Object[] {67}, ICON_DELAY));
    drink.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(drink);

//WTF aka puzzled
    OscAction wtf = new OscAction("/puzzled");
    wtf.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    wtf.addAction(new Action("LOAD_PRESENT"));
    wtf.addAction(new Action("IMAGE", new Object[] {"icn-question.png"}));
    //Wait iconDelay
    wtf.addAction(new Action("CHANGE_PRESENT", new Object[] {69}, ICON_DELAY));
    wtf.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(wtf);    

//flying
    OscAction fly = new OscAction("/flying");
    fly.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    fly.addAction(new Action("LOAD_PRESENT"));
    fly.addAction(new Action("IMAGE", new Object[] {"icn-moon1.png"}));
    //Wait iconDelay
    fly.addAction(new Action("CHANGE_PRESENT", new Object[] {81}, ICON_DELAY));
    fly.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(fly);    

//pointing
    OscAction point = new OscAction("/pointing");
    point.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    point.addAction(new Action("LOAD_PRESENT"));
    point.addAction(new Action("IMAGE", new Object[] {"icn-yourehere.png"}));
    //Wait iconDelay
    point.addAction(new Action("CHANGE_PRESENT", new Object[] {78}, ICON_DELAY));
    point.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(point); 

//throwing tdf, pranger
    OscAction throwing = new OscAction("/throwing");
    throwing.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    throwing.addAction(new Action("LOAD_PRESENT"));
    throwing.addAction(new Action("IMAGE", new Object[] {"icn-photo.png"}));
    //Wait iconDelay
    throwing.addAction(new Action("CHANGE_PRESENT", new Object[] {74}, ICON_DELAY));
    throwing.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(throwing); 

//handsup
    OscAction handsup = new OscAction("/handsup");
    handsup.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    handsup.addAction(new Action("LOAD_PRESENT"));
    handsup.addAction(new Action("IMAGE", new Object[] {"icn-gun.png"}));
    //Wait iconDelay
    handsup.addAction(new Action("CHANGE_PRESENT", new Object[] {72}, ICON_DELAY));
    handsup.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(handsup); 

//slowdown
    OscAction slowdown = new OscAction("/slowdown");
    slowdown.addAction(new Action("CHANGE_PRESENT", new Object[] {presetWithImage}));
    slowdown.addAction(new Action("LOAD_PRESENT"));
    slowdown.addAction(new Action("IMAGE", new Object[] {"icn-pause.png"}));
    //Wait iconDelay
    slowdown.addAction(new Action("CHANGE_PRESENT", new Object[] {77}, ICON_DELAY));
    slowdown.addAction(new Action("LOAD_PRESENT"));
    oscActions.add(slowdown); 

    //switch back action
    switchbackAction = new OscAction("SWITCHBACK");
    switchbackAction.addAction(new Action("PRESET_RANDOM"));

    println("Generated "+oscActions.size()+" OSC Actions");
    events.updateLastAction("Generated "+oscActions.size()+" OSC Actions");
  }


  boolean executeAction(String pattern) {
    //find osc action, assign it to currentAction
    for (OscAction actn: oscActions) {
      if (actn.keyName.equalsIgnoreCase(pattern)) {
        println("Start Action "+pattern);
        events.updateLastAction("Start Action "+pattern);        
        currentAction = actn;
        currentAction.start();
        println("Started Action for pattern "+pattern);
        events.updateLastAction("Started Action for pattern "+pattern);        
        return true;
      }
    }
    println("No Action found for pattern "+pattern);
    return false;
  }


  void update() {
    switchbackAction.update();

    if (currentAction==null) {
      
      //check if we should switch back to regular mode
      if (actionEndedAt > 0 && (System.currentTimeMillis()-actionEndedAt) > GESTURE_ANIMATION_DURATION) {
        //switch back to default reset after some time
        selectSwitchbackActionAndStart();
      }

      //After a long time of inactivity, do something!
      if (events.getLastActionTs() > DO_SOMETHING_AFTER_LONG_TIME_INACTIVITY) {
        println("long inactivity detected, so something!");
        selectSwitchbackActionAndStart();
      }

      return;
    }  

    currentAction.update();
    if (currentAction.finished) {
      println("Action is done, schedule switchback action\n");
      currentAction = null;     
      actionEndedAt = System.currentTimeMillis();
    }
  }


  //randomize switchback action
  void selectSwitchbackActionAndStart() {
    /*int r = int(random(10));
    if (r==4) {
      r = int(random(2));
      if (r==0) {
        switchbackAction.clearActions();
        switchbackAction.addAction(new Action("PRESET_RANDOM"));
        events.updateLastAction("New preset switchback action");
      } 
      else if (r==1) {
        switchbackAction.clearActions();
        switchbackAction.addAction(new Action("RANDOMIZE"));
        events.updateLastAction("New random switchback action");
      }
    }*/

    switchbackAction.start();        
    actionEndedAt = 0;
    println("Started switchback action");
    events.updateLastAction("Started switchback action");
  }
}

