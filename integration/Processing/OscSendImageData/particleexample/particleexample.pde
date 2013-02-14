//example based on processing examples

ParticleSystem ps;

final int MAX_PARTICLE = 50;
final int PARTICLE_SIZE_Y = 4*2;
final int PARTICLE_SIZE_X = 2*2;

void setup() {
  size(64,64);
  noSmooth();
  ps = new ParticleSystem();
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

class ParticleSystem {
  ArrayList<Particle> particles;

  ParticleSystem() {
    //origin = location.get();
    particles = new ArrayList<Particle>();
  }

  void addParticle() {
    if (particles.size() < MAX_PARTICLE) {
      particles.add(new Particle(new PVector(mouseX, mouseY)));
    }
  }

  void run() {
    Iterator<Particle> it = particles.iterator();
    while (it.hasNext()) {
      Particle p = it.next();
      p.run();
      if (p.isDead()) {
        it.remove(); 
      }
    }
  }
}



// A simple Particle class
int colr=0;
class Particle {
  PVector location;
  PVector velocity;
  PVector acceleration;
  float lifespan;
  int col;

  Particle(PVector l) {
    velocity = new PVector(random(-1,1),random(-1,1));
    acceleration = new PVector(velocity.x/100f, velocity.y/100f);
    location = l.get();
    lifespan = 255.0;
    //col = int(random(255));
    col = colr%255;
    colr+=2;
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
    fill(col,lifespan);
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
