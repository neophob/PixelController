
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

class ParticleSystemOne implements KinectFeedback {

  final static int maxParticles = 5000;
  int curIndex;

  ParticleOne[] particles;

  ParticleSystemOne() {
    particles = new ParticleOne[maxParticles];
    for (int i=0; i<maxParticles; i++) particles[i] = new ParticleOne();
    curIndex = 0;
  }

  void update() {
    for (int i=0; i<maxParticles; i++) {
      if (particles[i].alpha > 0) {
        particles[i].update();
      }
    }
  }

  int range = 15;
  void addParticles(float x, float y, int count ) {
    for (int i=0; i<count; i++) addParticle(x + random(-range, range), y + random(-range, range));
  }

  void addParticle(float x, float y) {
    particles[curIndex].init(x, y);
    curIndex++;
    if (curIndex >= maxParticles) curIndex = 0;
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

      particleSystemOne.addParticles(x * width, y * height, 10);
      fluidSolver.uOld[index] += dx * velocityMult;
      fluidSolver.vOld[index] += dy * velocityMult;
    }
  }


  void feedback(float x, float y, float speedx, float speedy) {
    addForce(x, y, speedx, speedy);
  }
}



class ParticleOne {
  final static float MOMENTUM = 0.5;
  final static float FLUID_FORCE = 0.6;

  float x, y;
  float vx, vy;
  float radius;       // particle's size
  float alpha;
  float mass;

  void init(float x, float y) {
    this.x = x;
    this.y = y;
    vx = 0;
    vy = 0;
    radius = 5;
    alpha  = random(0.3, 1);
    mass = random(0.1, 0.5);
    //        mass = random(0.1, 1);
  }


  void update() {
    // only update if particle is visible
    if (alpha == 0) return;

    // read fluid info and add to velocity
    int fluidIndex = fluidSolver.getIndexForNormalizedPosition(x * invWidth, y * invHeight);
    vx = fluidSolver.u[fluidIndex] * width * mass * FLUID_FORCE + vx * MOMENTUM;
    vy = fluidSolver.v[fluidIndex] * height * mass * FLUID_FORCE + vy * MOMENTUM;

    // update position
    x += vx;
    y += vy;

    // bounce of edges
    if (x<0) {
      x = 0;
      vx *= -1;
    }
    else if (x > width) {
      x = width;
      vx *= -1;
    }

    if (y<0) {
      y = 0;
      vy *= -1;
    }
    else if (y > height) {
      y = height;
      vy *= -1;
    }

    // hackish way to make particles glitter when the slow down a lot
    //     if(vx * vx + vy * vy < 1) {
    //         vx = random(-1, 1);
    //         vy = random(-1, 1);
    //     }

    // fade out a bit (and kill if alpha == 0);
    alpha *= 0.999;
    if (alpha < 0.01) alpha = 0;
  }
}







