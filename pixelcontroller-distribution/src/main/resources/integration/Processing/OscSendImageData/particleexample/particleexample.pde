//example based on processing examples
import java.util.*;

ParticleSystemTwo ps;

final int MAX_PARTICLE = 10;
final int PARTICLE_SIZE_Y = 4*2;
final int PARTICLE_SIZE_X = 2*2;

final int XRES = 64;
final int YRES = 64;

void setup() {
  size(XRES, YRES);
  noSmooth();
  ps = new ParticleSystemTwo();
  initOsc();
}

void draw() {
  filter(BLUR, 1);
 // background(0);
  ps.addParticle();
  ps.run();
  sendOsc();
}





// A class to describe a group of Particles
// An ArrayList is used to manage the list of Particles 

class ParticleSystemTwo {
  ArrayList<ParticleTwo> particles;

  ParticleSystemTwo() {
    //origin = location.get();
    particles = new ArrayList<ParticleTwo>();
  }

  void addParticle() {
    if (particles.size() < MAX_PARTICLE) {
      particles.add(new ParticleTwo(new PVector(mouseX, mouseY)));
    }
  }

  void run() {
    Iterator<ParticleTwo> it = particles.iterator();
    while (it.hasNext()) {
      ParticleTwo p = it.next();
      p.run();
      if (p.isDead()) {
        it.remove(); 
      }
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

  ParticleTwo(PVector l) {    
    velocity = new PVector(random(-1,1),random(-1,1));
    while (Math.abs(velocity.x) < 0.1f && Math.abs(velocity.y) < 0.1f) {
      velocity = new PVector(random(-1,1),random(-1,1));
    }
    acceleration = new PVector(velocity.x/100f, velocity.y/100f);
    location = l.get();
    lifespan = 255.0;
    col=192;
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
//    stroke(255,lifespan);
    noStroke();
    fill(Math.abs((col--)%255), 255);
//    fill(col,lifespan);    
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
