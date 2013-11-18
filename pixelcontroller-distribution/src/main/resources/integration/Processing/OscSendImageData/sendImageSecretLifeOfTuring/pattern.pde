// this is where the magic happens ...
 
void pattern() {
   
  // random angular offset
  float R = random(TWO_PI);
 
  // copy chemicals
  float[] pnew = new float[s];
  for(int i=0; i<s; i++) pnew[i] = pat[i];
 
  // create matrices
  float[][] pmedian = new float[s][scl];
  float[][] prange = new float[s][scl];
  float[][] pvar = new float[s][scl];
 
  // iterate over increasing distances
  for(int i=0; i<scl; i++) {
    float d = (2<<i) ;
     
    // update median matrix
    for(int j=0; j<dirs; j++) {
      float dir = j*TWO_PI/dirs + R;
      int dx = int (d * cos(dir));
      int dy = int (d * sin(dir));
      for(int l=0; l<s; l++) { 
        // coordinates of the connected cell
        int x1 = l%w + dx, y1 = l/w + dy;
        // skip if the cell is beyond the border or wrap around
        if(x1<0) if(border) continue; else x1 = w-1-(-x1-1)% w; else if(x1>=w) if(border) continue; else x1 = x1%w;
        if(y1<0) if(border) continue; else y1 = h-1-(-y1-1)% h; else if(y1>=h) if(border) continue; else y1 = y1%h;
        // update median
        pmedian[l][i] += pat[x1+y1*w] / dirs;
         
      }
    }
     
    // update range and variance matrix
    for(int j=0; j<dirs; j++) {
      float dir = j*TWO_PI/dirs + R;
      int dx = int (d * cos(dir));
      int dy = int (d * sin(dir));
      for(int l=0; l<s; l++) { 
        // coordinates of the connected cell
        int x1 = l%w + dx, y1 = l/w + dy;
        // skip if the cell is beyond the border or wrap around
        if(x1<0) if(border) continue; else x1 = w-1-(-x1-1)% w; else if(x1>=w) if(border) continue; else x1 = x1%w;
        if(y1<0) if(border) continue; else y1 = h-1-(-y1-1)% h; else if(y1>=h) if(border) continue; else y1 = y1%h;
        // update variance
        pvar[l][i] += abs( pat[x1+y1*w]  - pmedian[l][i] ) / dirs;
        // update range
         
        prange[l][i] += pat[x1+y1*w] > (lim + i*10) ? +1 : -1;   
    
      }
    }    
  }
 
  for(int l=0; l<s; l++) { 
     
    // find min and max variation
    int imin=0, imax=scl;
    float vmin = MAX_FLOAT;
    float vmax = -MAX_FLOAT;
    for(int i=0; i<scl; i+=1) {
      if (pvar[l][i] <= vmin) { vmin = pvar[l][i]; imin = i; }
      if (pvar[l][i] >= vmax) { vmax = pvar[l][i]; imax = i; }
    }
     
    // turing pattern variants
    switch(pattern) {
      case 0: for(int i=0; i<=imin; i++)    pnew[l] += prange[l][i]; break;
      case 1: for(int i=imin; i<=imax; i++) pnew[l] += prange[l][i]; break;
      case 2: for(int i=imin; i<=imax; i++) pnew[l] += prange[l][i] + pvar[l][i]/2; break;
    }
       
  }
 
  // rescale values
  float vmin = MAX_FLOAT;
  float vmax = -MAX_FLOAT;
  for(int i=0; i<s; i++)  {
    vmin = min(vmin, pnew[i]);
    vmax = max(vmax, pnew[i]);
  }      
  float dv = vmax - vmin;
  for(int i=0; i<s; i++)
    pat[i] = (pnew[i] - vmin) * 255 / dv;
    
}

