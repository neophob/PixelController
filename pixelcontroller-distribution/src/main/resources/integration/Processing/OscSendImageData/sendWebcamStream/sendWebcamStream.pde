/**
 * Framingham
 * by Ben Fry.
 *  
 * Show subsequent frames from video input as a grid. Also fun with movie files.
 */


import processing.video.*;

Capture video;

void setup() {
  size(64, 64, P2D);

  // Uses the default video input, see the reference if this causes an error
  video = new Capture(this, 64, 64);
  // Also try with other video sizes
  
  background(0);
}


void draw() {
  // By using video.available, only the frame rate need be set inside setup()
  if (video.available()) {
    video.read();
    set(0, 0, video);
    filter(GRAY);
    filter(BLUR, 1);
//    filter(POSTERIZE, 16);
    sendOsc();
  }
  
  
}
