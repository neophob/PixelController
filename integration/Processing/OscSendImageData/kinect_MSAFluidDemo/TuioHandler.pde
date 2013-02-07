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

String gesture = "RaiseHand";
boolean handsTrackFlag = false;
PVector currentHand;

float maxXSize = 0;
float minXSize = 1000;
float maxYSize = 0;
float minYSize = 1000;

float maxXSpeed = 0;
float minXSpeed = 1000;
float maxYSpeed = 0;
float minYSpeed = 1000;


void initTUIO() {
  kinect = new SimpleOpenNI(this, SimpleOpenNI.RUN_MODE_MULTI_THREADED);
  
  //flip x, this makes gestures much more intuitive
  kinect.setMirror(true);

  kinect.enableDepth();
  kinect.enableGesture();
  kinect.enableHands();

  kinect.addGesture(gesture);
  handPositions = new ConcurrentHashMap();
  previousHandPositions = new ConcurrentHashMap();
}


void updateTUIO() {
  kinect.update();

//s.image(kinect.depthImage(), 0, 0);

  //nothing todo!
  if (!handsTrackFlag) {
    return;
  }
  //old and ugly iterate style
  Set<Integer> set = handPositions.keySet();
  for (Integer handId: set) {
    //fill(255,0,0);
    PVector currentHandPos = handPositions.get(handId);
    if (currentHandPos.x == 0.0f) {
      continue;
    }
    if (currentHandPos.y == 0.0f) {
      continue;
    }

    //draw dot
    s.fill(128, 255, 64);
    s.ellipse(currentHandPos.x, currentHandPos.y, 15, 15);

    //calculate velocity
    PVector oldPos = new PVector(0f, 0f);
    if (previousHandPositions.containsKey(handId)) {
      oldPos = previousHandPositions.get(handId);
      oldPos.sub(currentHandPos);
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

    //dynamic adjust the moving speed
    if (oldPos.x > maxXSpeed) {
      maxXSpeed = oldPos.x;
    }
    if (minXSpeed > oldPos.x) {
      minXSpeed = oldPos.x;
    }
    if (oldPos.y > maxYSpeed) {
      maxYSpeed = oldPos.y;
    }
    if (minYSpeed > oldPos.y) {
      minYSpeed = oldPos.y;
    }    

    float mouseNormX = norm(currentHandPos.x, minXSize, maxXSize);
    float mouseNormY = norm(currentHandPos.y, minYSize, maxYSize);
    
    float calx = mouseNormX/currentHandPos.x;
    float mouseVelX = oldPos.x*calx*-1f;

    float caly = mouseNormY/currentHandPos.y;
    float mouseVelY = oldPos.y*caly*-1f;

    //println("mouseVel\t"+mouseVelX+" "+mouseVelY+"\t"+oldPos.x+" "+oldPos.y);
    
    //filter out possible errors
    float maxThreshold = 0.13f;    
    if (Math.abs(mouseVelX)>maxThreshold) {
      //println("huge x value filtered out! "+mouseVelX);
      while (Math.abs (mouseVelX)>maxThreshold) {
        mouseVelX/=2f;
      }
    }
    if (Math.abs(mouseVelY)>maxThreshold) {
      //println("huge y value filtered out! "+mouseVelY);
      while (Math.abs (mouseVelY)>maxThreshold) {
        mouseVelY/=2f;
      }
    }
    
    //add new force to create animation
    addForce(mouseNormX, mouseNormY, mouseVelX, mouseVelY);

    //Store previous position to calculate velocity
    previousHandPositions.put(handId, currentHandPos);
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

