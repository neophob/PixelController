/***********************************************************************
 
 Demo of the MSAFluid library (www.memo.tv/msafluid_for_processing)
 Move mouse to add dye and forces to the fluid.
 Click mouse to turn off fluid rendering seeing only particles and their paths.
 Demonstrates feeding input into the fluid and reading data back (to update the particles).
 Also demonstrates using Vertex Arrays for particle rendering.
 
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

import msafluid.*;
import javax.swing.JFrame;
import SimpleOpenNI.*;
import java.util.concurrent.*;

SimpleOpenNI kinect;
final int FLUID_WIDTH = 16;

float invWidth, invHeight;    // inverse of screen dimensions
float aspectRatio, aspectRatio2;

MSAFluidSolver2D fluidSolver;

ParticleSystem particleSystem;

PImage imgFluid;

PFrame f;
secondApplet s;

boolean showDepthMap = false;
boolean kinectConnected= false;

void setup() {
  frameRate(30);
  size(64, 64);    // use OPENGL rendering for bilinear filtering on texture    

  invWidth = 1.0f/width;
  invHeight = 1.0f/height;
  aspectRatio = width * invHeight;
  aspectRatio2 = aspectRatio * aspectRatio;

  // create fluid and set options
//  fluidSolver = new MSAFluidSolver2D((int)(FLUID_WIDTH), (int)(FLUID_WIDTH * height/width));
  fluidSolver = new MSAFluidSolver2D(FLUID_WIDTH, FLUID_WIDTH);
//  fluidSolver.enableRGB(false).setFadeSpeed(0.003).setDeltaT(0.8).setVisc(0.000001);
  fluidSolver.enableRGB(false).setFadeSpeed(0.03).setDeltaT(0.7).setVisc(0.0000000000000001);
  //    fluidSolver.enableRGB(true).setFadeSpeed(0.003).setDeltaT(0.5).setVisc(0.0001);    

  // create image to hold fluid picture
//  imgFluid = createImage(fluidSolver.getWidth(), fluidSolver.getHeight(), RGB);
  imgFluid = createImage(FLUID_WIDTH, FLUID_WIDTH, RGB);
  println("created image x:"+fluidSolver.getWidth()+", y:"+fluidSolver.getHeight());
  
  // create particle system
  particleSystem = new ParticleSystem();

  // init TUIO
  kinectConnected = initTUIO();
  
  PFrame f = new PFrame();  
//WAS RGB, 2
  colorMode(RGB, 1); 
//  colorMode(RGB, 0.5); 

  initOsc();
}


void mouseMoved() {
  float mouseNormX = mouseX * invWidth;
  float mouseNormY = mouseY * invHeight;
  float mouseVelX = (mouseX - pmouseX) * invWidth;
  float mouseVelY = (mouseY - pmouseY) * invHeight;
  
  //println(mouseVelX+"\t"+mouseVelY);
  addForce(mouseNormX, mouseNormY, mouseVelX, mouseVelY);
}

void draw() {
  if (kinectConnected) {
    updateTUIO();
  }

  fluidSolver.update();

  int i=0;
  imgFluid.loadPixels();
  
  //do not copy boarder - its duplicate
  for (int y=1; y<fluidSolver.getHeight()-1; y++) {
    for (int x=1; x<fluidSolver.getWidth()-1; x++) {
      int ofs = fluidSolver.getWidth()*y+x;
      //we only use the red channel, as we don't need rgb data
      imgFluid.pixels[i++] = color(fluidSolver.r[ofs]);      
    }
  }  

  imgFluid.updatePixels();//  fastblur(imgFluid, 2);
  image(imgFluid, 0, 0, width, height);
  //copy(imgFluid, 2, 2, imgFluid.width-4, imgFluid.height-4, 0, 0, width, height);
  
  particleSystem.update();
  
  sendOsc();
}



// add force and dye to fluid, and create particles
void addForce(float x, float y, float dx, float dy) {
  float speed = dx * dx  + dy * dy * aspectRatio2;    // balance the x and y components of speed with the screen aspect ratio

  if (speed > 0) {
    if (x<0) x = 0; 
    else if (x>1) x = 1;
    if (y<0) y = 0; 
    else if (y>1) y = 1;

    float velocityMult = 30.0f;

    int index = fluidSolver.getIndexForNormalizedPosition(x, y);

    fluidSolver.rOld[index]  += 9.4f;

    particleSystem.addParticles(x * width, y * height, 10);
    fluidSolver.uOld[index] += dx * velocityMult;
    fluidSolver.vOld[index] += dy * velocityMult;
  }
}

void keyPressed() {
  if (key == 's') {
    if (showDepthMap) showDepthMap=false; 
    else showDepthMap=true;
  }
}



public class PFrame extends JFrame {
    public PFrame() {
        setBounds(0,0,640,480);
        s = new secondApplet();
        add(s);
        s.init();
        show();
    }
}


public class secondApplet extends PApplet {
    public void setup() {
       // size(400, 300);
       // noLoop();
    }
    public void draw() {
    }
}

