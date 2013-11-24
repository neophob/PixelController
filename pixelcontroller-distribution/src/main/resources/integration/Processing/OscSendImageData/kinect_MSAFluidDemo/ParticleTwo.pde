import java.util.*;
//example based on processing examples

// A class to describe a group of Particles
// An ArrayList is used to manage the list of Particles 

class ParticleSystemTwo implements KinectFeedback {
  ArrayList<ParticleTwo> particlesTwo;

  ParticleSystemTwo() {
    //origin = location.get();
    particlesTwo = new ArrayList<ParticleTwo>();
  }

  void addParticle() {
    if (particlesTwo.size() < MAX_PARTICLE) {
      particlesTwo.add(new ParticleTwo(new PVector(mouseX, mouseY)));
    }
  }

  void run() {
    Iterator<ParticleTwo> it = particlesTwo.iterator();
    while (it.hasNext()) {
      ParticleTwo p = it.next();
      p.run();
      if (p.isDead()) {
        it.remove(); 
      }
    }
  }
  
  void feedback(float x, float y, float speedx, float speedy) {
    if (particlesTwo.size() < MAX_PARTICLE) {
      particlesTwo.add(new ParticleTwo(new PVector(x*width, y*height), new PVector(speedx*width, speedy*height) ));
    }    
  }
  
}



// A simple Particle class
class ParticleTwo {
  PVector location;
  PVector velocity;
  PVector acceleration;
  float lifespan;
  int col;

  ParticleTwo(PVector l, PVector speed) {    
    velocity = speed.get();
    while (Math.abs(velocity.x) < 0.1f && Math.abs(velocity.y) < 0.1f) {
      velocity = new PVector(random(-1,1),random(-1,1));
    }
    acceleration = new PVector(velocity.x/100f, velocity.y/100f);
    location = l.get();
    lifespan = 255.0;
    col=192;
  }

  ParticleTwo(PVector l) {
    this(l, new PVector(random(-1,1),random(-1,1)));
  }

  void run() {
    update();
    display();
  }

  // Method to update location
  void update() {
    velocity.add(acceleration);
    location.add(velocity);
    lifespan -= 1.0;
  }

  // Method to display
  void display() {
    noStroke();
    float colr = 255f / float(Math.abs((col--)%255));
    fill(colr, 255);
    rect(location.x,location.y,PARTICLE_SIZE_X,PARTICLE_SIZE_Y);
  }
  
  // Is the particle still useful?
  boolean isDead() {
    if (lifespan < 0.0 || location.x>width || location.x<0 || location.y>height || location.y<0) {
      return true;
    } else {
      return false;
    }
  }
}
