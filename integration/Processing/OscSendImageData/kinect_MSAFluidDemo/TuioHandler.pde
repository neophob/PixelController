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

PVector currentHand;

void initTUIO() {
  kinect = new SimpleOpenNI(this, SimpleOpenNI.RUN_MODE_MULTI_THREADED);
  kinect.setMirror(true);

  kinect.enableDepth();
  kinect.enableGesture();
  kinect.enableHands();

  kinect.addGesture(gesture);
  handPositions = new ConcurrentHashMap();
  previousHandPositions = new ConcurrentHashMap();
}




float maxX=0, maxY=0;
void updateTUIO() {
  kinect.update();

  s.image(kinect.depthImage(), 0, 0);

  //old and ugly iterate style
  Set<Integer> set = handPositions.keySet();
  for (Integer handId: set) {
    //fill(255,0,0);
    PVector currentHandPos = handPositions.get(handId);
    if (currentHandPos.x < 0.0001f) {
      //println("X is small "+currentHandPos.x);
      continue;
    }
    if (currentHandPos.y < 0.0001f) {
      //println("Y is small "+currentHandPos.y);
      continue;
    }
    //draw current pos
    //ellipse(currentHandPos.x, currentHandPos.y, 15, 15);

    PVector oldPos = new PVector(0f, 0f);
    if (previousHandPositions.containsKey(handId)) {
      oldPos = previousHandPositions.get(handId);
      oldPos.sub(currentHandPos);

      boolean printMax=false;      
      if (oldPos.x > maxX) {
        maxX = oldPos.x;
        printMax = true;
      }
      if (oldPos.y > maxY) {
        maxY = oldPos.y;
        printMax = true;
      }

      if (printMax) {
        println(handId+" max x: "+maxX+" max y: "+maxY);
      }
    }

    s.fill(128, 255, 64);
    s.ellipse(currentHandPos.x, currentHandPos.y, 15, 15);

    float mouseNormX = currentHandPos.x * invWidth;
    float mouseNormY = currentHandPos.y * invHeight;
    float mouseVelX = (oldPos.x) * -invWidth;
    float mouseVelY = (oldPos.y) * -invHeight;

println(mouseNormX+" "+ invWidth+" "+currentHandPos.x);


    float maxThreshold = 0.13f;
    //println(mouseVelX+"\t"+mouseVelY);
    if (Math.abs(mouseVelX)>maxThreshold) {
      println("huge x value filtered out! "+mouseVelX);
      while (Math.abs (mouseVelX)>maxThreshold) {
        mouseVelX/=2;
      }
    }
    if (Math.abs(mouseVelY)>maxThreshold) {
      println("huge y value filtered out! "+mouseVelY);
      while (Math.abs (mouseVelY)>maxThreshold) {
        mouseVelY/=2;
      }
    }
    addForce(mouseNormX, mouseNormY, mouseVelX, mouseVelY);

    previousHandPositions.put(handId, currentHandPos);
  }
}


void onCreateHands(int handId, PVector position, float time) {
  println("onCreateHands: "+handId+", time: "+time);
  //kinect.convertRealWorldToProjective(position,position);
  //handPositions.add(position);
}

void onUpdateHands(int handId, PVector position, float time) {
  //println("onUpdateHands: "+handId+", time: "+time+" pos:"+position);
  kinect.convertRealWorldToProjective(position, position);
  handPositions.put(handId, position);
}

void onDestroyHands(int handId, float time) {
  println("onDestroyHands: "+handId+", time: "+time);
  handPositions.remove(handId);
  previousHandPositions.remove(handId);
  kinect.addGesture(gesture);
}

void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition) {
  //println("onRecognizeGesture: "+strGesture+", idPosition: "+idPosition);
  kinect.startTrackingHands(endPosition);
  kinect.removeGesture(gesture);
}

