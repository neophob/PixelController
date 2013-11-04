/***********************************************************************
 
 Copyright (c) 2008, 2009, Memo Akten, www.memo.tv
 *** The Mega Super Awesome Visuals Company ***
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of MSA Visuals nor the names of its contributors 
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS 
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 *
 * ***********************************************************************/

Map<Integer, PVector> handPositions;
Map<Integer, PVector> previousHandPositions;

List<KinectFeedback> kinectHandler = new ArrayList<KinectFeedback>();

String gesture = "RaiseHand";
boolean handsTrackFlag = false;
PVector currentHand;

float maxXSize = 0;
float minXSize = 1000;
float maxYSize = 0;
float minYSize = 1000;

boolean initTUIO() {
  kinect = new SimpleOpenNI(this, SimpleOpenNI.RUN_MODE_MULTI_THREADED);
  
  //flip x, this makes gestures much more intuitive
  kinect.setMirror(true);

  if (!kinect.enableDepth()) {
    println("failed to init Kinect");
    return false;
  }
  
  kinect.enableGesture();
  kinect.enableHands();

  kinect.addGesture(gesture);
  handPositions = new ConcurrentHashMap();
  previousHandPositions = new ConcurrentHashMap();
  
  return true;
}

void addHandler(KinectFeedback handler) {
  kinectHandler.add(handler);
}

float mouseVelX, mouseVelY;

void updateTUIO() {
  kinect.update();

  if (showDepthMap) {
    s.image(kinect.depthImage(), 0, 0);
  }
  
  //nothing todo!
  if (!handsTrackFlag) {
    return;
  }
  
  //old and ugly iterate style
  Set<Integer> setHandPosKeys = handPositions.keySet();
  for (Integer handId: setHandPosKeys) {
    //fill(255,0,0);
    PVector currentHandPos = handPositions.get(handId);
    if (currentHandPos.x < 0.000000001f || currentHandPos.y < 000000001f) {
      //println("ignore call, return");
      continue;
    }

    //draw dot
    s.fill(128, 255, 64);
    s.ellipse(currentHandPos.x, currentHandPos.y, 15, 15);

    //calculate velocity
    PVector oldPos = new PVector(0f, 0f);
    if (previousHandPositions.containsKey(handId)) {
      oldPos = previousHandPositions.get(handId);
      if (oldPos.x > 0.0f && oldPos.y > 0.0f) {
        oldPos.sub(currentHandPos);        
      }
    }

    //dynamic adjust the viewing window
    if (currentHandPos.x > maxXSize) {
      maxXSize = currentHandPos.x;
    }
    if (minXSize > currentHandPos.x && currentHandPos.x>0.0f) {
      minXSize = currentHandPos.x;
    }
    if (currentHandPos.y > maxYSize) {
      maxYSize = currentHandPos.y;
    }
    if (minYSize > currentHandPos.y && currentHandPos.y>0.0f) {
      minYSize = currentHandPos.y;
    }    

    float mouseNormX = norm(currentHandPos.x, minXSize, maxXSize);
    float mouseNormY = norm(currentHandPos.y, minYSize, maxYSize);
    
    if (currentHandPos.x==0) {
      mouseVelX = 0;
    } else {
      float calx = mouseNormX/currentHandPos.x;
      mouseVelX = oldPos.x*calx*-1f;
    }    
    
    if (currentHandPos.y==0) {
      mouseVelY = 0;
    } else {
      float caly = mouseNormY/currentHandPos.y;
      mouseVelY = oldPos.y*caly*-1f;
    }
    
    //filter out possible errors
    boolean addForce = true;
    float maxThreshold = 0.13f;    
    if (Math.abs(mouseVelX)>maxThreshold) {
      println("huge x value filtered out! "+mouseVelX+", oldpos.x: "+oldPos.x);
      addForce = false;
    }
    if (Math.abs(mouseVelY)>maxThreshold) {
      println("huge y value filtered out! "+mouseVelY+", oldpos.y: "+oldPos.y);
      addForce = false;
    } 
    
    //add new force to create animation
    if (addForce) {
      //addForce(mouseNormX, mouseNormY, 1.2*mouseVelX, 1.2*mouseVelY);
      for (KinectFeedback feedback: kinectHandler) {
        feedback.feedback(mouseNormX, mouseNormY, 1.2*mouseVelX, 1.2*mouseVelY);
      }
    }

    //Store previous position to calculate velocity
    if (currentHandPos.x == 0.0f || currentHandPos.y == 0.0f) {
      //println("current: "+currentHandPos);
    } else {
      previousHandPositions.put(handId, currentHandPos);
    }
  }
}


void onCreateHands(int handId, PVector position, float time) {
  println("onCreateHands: "+handId+", time: "+time);
  handsTrackFlag = true;
  //kinect.convertRealWorldToProjective(position,position);
  //handPositions.add(position);
}

void onUpdateHands(int handId, PVector position, float time) {
  //println("onUpdateHands: "+handId+", time: "+time+" pos:"+position);
  //TODO: make hand gestures only available in a specific range, for example between 2-4m away from the sensor
  
  kinect.convertRealWorldToProjective(position, position);
  if (position.x>0.0f && position.y>0.0f) {    
    handPositions.put(handId, position);
  } else {
    println("ignore wrong detected hand!");
  }
  
}

void onDestroyHands(int handId, float time) {
  println("onDestroyHands: "+handId+", time: "+time);
  handsTrackFlag = false;
  handPositions.remove(handId);
  previousHandPositions.remove(handId);
  kinect.addGesture(gesture);
}

void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition) {
  //println("onRecognizeGesture: "+strGesture+", idPosition: "+idPosition);
  kinect.startTrackingHands(endPosition);
  kinect.removeGesture(gesture);
}

