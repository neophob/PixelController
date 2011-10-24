/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
 *
 * This file is part of PixelController.
 *
 * PixelController is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PixelController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PixelController.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.neophob.sematrix.generator;

import java.awt.Color;
import java.awt.Point;

import com.neophob.sematrix.resize.Resize.ResizeName;


/**
 * The Class Particle System.
 * 
 * Ripped from 
 * http://code.google.com/p/cocos2d-android/source/browse/trunk/src/org/cocos2d/particlesystem/ParticleSystem.java
 */
public class ParticleSystem extends Generator {	

    public static final int kPositionTypeFree = 1;
    public static final int kPositionTypeGrouped = 2;
 
   
	// Gravity of the particles
    private Point gravity;

    // position is from "superclass" CocosNode Emitter source position
    private Point source;

    // Position variance
    private Point posVar;

    // The angle (direction) of the particles measured in degrees
    private float angle;
    // Angle variance measured in degrees;
    private float angleVar;

    // The speed the particles will have.
    private float speed;
    // The speed variance
    private float speedVar;

    // Tangential acceleration
    private float tangentialAccel;
    // Tangential acceleration variance
    private float tangentialAccelVar;

    // Radial acceleration
    private float radialAccel;
    // Radial acceleration variance
    private float radialAccelVar;

    // Size of the particles
    private float size;
    // Size variance
    private float sizeVar;

    // How many seconds will the particle live
    private float life;
    // Life variance
    private float lifeVar;

    // Start color of the particles
    private Color startColor;
    // Start color variance
    private Color startColorVar;

    // End color of the particles
    private Color endColor;
    // End color variance
    private Color endColorVar;

    // Array of particles
    private Particle particles[];

    // Maximum particles
    private int totalParticles;

    // Count of active particles
    private int particleCount;

    // additive color or blend
    private boolean blendAdditive;
    // color modulate
    private boolean colorModulate;

    // How many particles can be emitted per second
    private float emissionRate;
    private float emitCounter;

    // colors buffer id
    private int colorsID;

    //  particle idx
    private int particleIdx;

    // is the particle system active ?
    private boolean active;
    
    // duration in seconds of the system. -1 is infinity
    private float duration;

    // time elapsed since the start of the system (in seconds)
    private float elapsed;

    private long lastTimestamp;
    
    // movement type: free or grouped
    private int positionType_;

    
	/**
	 * Instantiates a new Particle System.
	 *
	 * @param controller the controller
	 */
	public ParticleSystem(PixelControllerGenerator controller) {
		super(controller, GeneratorName.BLINKENLIGHTS, ResizeName.QUALITY_RESIZE);
/*		super(controller, GeneratorName.PARTICLE_SYSTEM, ResizeName.QUALITY_RESIZE);
		
		//Test ParticleFire
		totalParticles = 250;
		
		particles = new Particle[totalParticles];
        for (int i = 0; i < totalParticles; i++) {
            particles[i] = new Particle();
        }
        
        
        
		gravity = new Point(0,0);
		
        angle = 90;
        angleVar = 10;
        
        radialAccel = 0;
        radialAccelVar = 0;
        
        // emitter position
        //TODO setPosition(160, 60);
        posVar = new Point(40,20);
        
        life = 3;
        lifeVar = 0.25f;

        speed = 60;
        speedVar = 20;
        
        size = 100.0f;
        sizeVar = 10.0f;
        
        emissionRate = totalParticles / life;
        System.out.println(emissionRate);
        
        startColor = new Color(0.76f, 0.25f, 0.12f, 1f);
        startColorVar = new Color(0);
        
        endColor = new Color(0, 0, 0, 1f);
        endColorVar = new Color(0);
        
        elapsed = 0;
        
        active = true;
        blendAdditive = true;
        positionType_ = kPositionTypeFree;
        
        lastTimestamp = System.currentTimeMillis();*/
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.generator.Generator#update()
	 */
	@Override
	public void update() {
/*		
		long dt = System.currentTimeMillis()-lastTimestamp;			
		lastTimestamp=System.currentTimeMillis();
		
		if (active && emissionRate != 0) {			
            float rate = 1.0f / emissionRate;
            emitCounter += dt;
            while (particleCount < totalParticles && emitCounter > rate) {
                addParticle();
                emitCounter -= rate;
            }

            elapsed += dt;
            if (duration != -1 && duration < elapsed)
                stopSystem();			
		}
		
		particleIdx = 0;
		
        while (particleIdx < particleCount) {
            Particle p = particles[particleIdx];

            if (p.life > 0) {

                Point tmp, radial, tangential;

                radial = new Point(0,0);
                // radial acceleration
                if (p.pos.x != 9 || p.pos.y != 0)
                    radial = CCPoint.ccpNormalize(p.pos);
                tangential = radial;
                radial = CCPoint.ccpMult(radial, p.radialAccel);

                // tangential acceleration
                float newy = tangential.x;
                tangential.x = -tangential.y;
                tangential.y = newy;
                tangential = CCPoint.ccpMult(tangential, p.tangentialAccel);

                // (gravity + radial + tangential) * dt
                tmp = CCPoint.ccpAdd(CCPoint.ccpAdd(radial, tangential), gravity);
                tmp = CCPoint.ccpMult(tmp, dt);
                p.dir = CCPoint.ccpAdd(p.dir, tmp);
                tmp = CCPoint.ccpMult(p.dir, dt);
                p.pos = CCPoint.ccpAdd(p.pos, tmp);

                p.color.r += (p.deltaColor.r * dt);
                p.color.g += (p.deltaColor.g * dt);
                p.color.b += (p.deltaColor.b * dt);
                p.color.a += (p.deltaColor.a * dt);

                p.life -= dt;

                // place vertices and colos in array
                vertices[particleIdx].x = p.pos.x;
                vertices[particleIdx].y = p.pos.y;

                // TODO: Remove when glPointSizePointerOES is fixed
                vertices[particleIdx].size = p.size;
                vertices[particleIdx].colors = new CCColorF(p.color);

                // update particle counter
                particleIdx++;

            } else {
                // life < 0
                if (particleIdx != particleCount - 1)
                    particles[particleIdx] = particles[particleCount - 1];
                particleCount--;
            }
        }*/
	}
	
	
    //! stop emitting particles. Running particles will continue to run until they die
    public void stopSystem() {
        active = false;
        elapsed = duration;
        emitCounter = 0;
    }
    
    //! whether or not the system is full
    private boolean isFull() {
        return particleCount == totalParticles;
    }
    
    //! Add a particle to the emitter
    public boolean addParticle() {
        if (isFull())
            return false;

        Particle particle = particles[particleCount];

        initParticle(particle);
        particleCount++;

        return true;
    }
    
    /**
     * returns a random float between -1 and 1
     * 
     * @return
     */
    private static float CCRANDOM_MINUS1_1() {
        return (float) Math.random() * 2.0f - 1.0f;
    }
    
    /**
     * 
     * @param angle
     * @return
     */
    private static float CC_DEGREES_TO_RADIANS(float angle) {
        return (angle / 180.0f * (float) Math.PI);
    }


    private void initParticle(Particle particle) {
        Point v = new Point(0,0);

        // position
/*        particle.pos.x = (int) (source.x + posVar.x * CCRANDOM_MINUS1_1());
        particle.pos.y = (int) (source.y + posVar.y * CCRANDOM_MINUS1_1());

        // direction
        float a = CC_DEGREES_TO_RADIANS(angle + angleVar * CCRANDOM_MINUS1_1());
        v.y = Math.sin(a);
        v.x = (float)Math.cos(a);
        float s = speed + speedVar * CCRANDOM_MINUS1_1();
        particle.dir = CCPoint.ccpMult(v, s);

        // radial accel
        particle.radialAccel = radialAccel + radialAccelVar * CCRANDOM_MINUS1_1();

        // tangential accel
        particle.tangentialAccel = tangentialAccel + tangentialAccelVar * CCRANDOM_MINUS1_1();

        // life
        particle.life = life + lifeVar * CCRANDOM_MINUS1_1();

        // Color
        float startR = startColor.getRed() + startColorVar.getRed() * CCRANDOM_MINUS1_1();
        float startG = startColor.getBlue() + startColorVar.getBlue() * CCRANDOM_MINUS1_1();
        float startB = startColor.getGreen() + startColorVar.getGreen() * CCRANDOM_MINUS1_1();
        float startA = startColor.getAlpha() + startColorVar.getAlpha() * CCRANDOM_MINUS1_1();
        //Color start = new Color(r,g,b,al);

        float endR = endColor.getRed() + endColorVar.getRed() * CCRANDOM_MINUS1_1();
        float endG = endColor.getBlue() + endColorVar.getBlue() * CCRANDOM_MINUS1_1();
        float endB = endColor.getGreen() + endColorVar.getGreen() * CCRANDOM_MINUS1_1();
        float endA = endColor.getAlpha() + endColorVar.getAlpha() * CCRANDOM_MINUS1_1();
        //Color end = new Color(r,g,b,al);
        
        float rr = (endR - startR) / particle.life;
        float gg = (endG - startG) / particle.life;
        float bb = (endB - startB) / particle.life;
        float aa = (endA - startA) / particle.life;
        particle.color = new Color(rr,gg,bb,aa);
        
        // size
        particle.size = size + sizeVar * CCRANDOM_MINUS1_1();

        // position
        if( positionType_ == kPositionTypeFree ) {
            particle.startPos = convertToWorldSpace(0, 0);
        } else {
            particle.startPos = CCPoint.make(getPositionX(), getPositionY());
        }*/
    }
    
    
	/**
	 * 
	 * @author michu
	 *
	 */
	public static class Particle {
		Point pos;
		Point startPos;
		Point dir;
		float radialAccel;
		float tangentialAccel;
		Color color;
		Color deltaColor;
		float size;
		float life;		
	}
}
