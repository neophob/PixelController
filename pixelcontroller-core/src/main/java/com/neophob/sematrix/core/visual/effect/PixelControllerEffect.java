/**
 * Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.core.visual.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.PixelControllerElement;
import com.neophob.sematrix.core.properties.ValidCommand;
import com.neophob.sematrix.core.resize.IResize;
import com.neophob.sematrix.core.sound.ISound;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.effect.Effect.EffectName;

/**
 * The Class PixelControllerEffect.
 */
public class PixelControllerEffect implements PixelControllerElement {

    private static final Logger LOG = Logger.getLogger(PixelControllerEffect.class.getName());

    /** The all effects. */
    private List<Effect> allEffects;

    /** The threshold. */
    private Threshold threshold;
    private RotoZoom rotoZoom;
    private Zoom zoom;
    private TextureDeformation textureDeformation;
    private MatrixData matrix;
    private ISound sound;
    private IResize resize;

    /**
     * Instantiates a new pixel controller effect.
     */
    public PixelControllerEffect(MatrixData matrix, ISound sound, IResize resize) {
        allEffects = new CopyOnWriteArrayList<Effect>();
        this.matrix = matrix;
        this.sound = sound;
        this.resize = resize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.glue.PixelControllerElement#update()
     */
    @Override
    public void update() {
        for (Effect e : allEffects) {
            e.update();
        }
    }

    /**
     * initialize all effects.
     */
    @Override
    public void initAll() {
        // create effects
        allEffects.add(new Inverter(matrix));
        allEffects.add(new PassThru(matrix));

        rotoZoom = new RotoZoom(matrix, 1.5f, 2.3f);
        allEffects.add(rotoZoom);

        allEffects.add(new BeatVerticalShift(matrix, sound));
        allEffects.add(new BeatHorizShift(matrix, sound));
        allEffects.add(new Voluminize(matrix, sound));

        threshold = new Threshold(matrix);
        allEffects.add(threshold);

        zoom = new Zoom(matrix);
        allEffects.add(zoom);

        allEffects.add(new FlipY(matrix));
        allEffects.add(new FlipX(matrix));
        allEffects.add(new Strobo(matrix));
        allEffects.add(new Rotate90(matrix, resize));
        allEffects.add(new Posterize(matrix));
        allEffects.add(new Darken(matrix));

        textureDeformation = new TextureDeformation(matrix);
        allEffects.add(textureDeformation);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.neophob.sematrix.core.glue.PixelControllerElement#getCurrentState()
     */
    @Override
    public List<String> getCurrentState() {
        List<String> ret = new ArrayList<String>();

        ret.add(ValidCommand.CHANGE_ROTOZOOM + " "
                + ((RotoZoom) getEffect(EffectName.ROTOZOOM)).getAngle());
        ret.add(ValidCommand.CHANGE_THRESHOLD_VALUE + " " + threshold.getThreshold());
        ret.add(ValidCommand.TEXTDEF + " " + textureDeformation.getLut());
        ret.add(ValidCommand.ZOOMOPT + " " + zoom.getZoomMode());

        return ret;
    }

    /*
     * EFFECT ======================================================
     */

    /**
     * Gets the size.
     * 
     * @return the size
     */
    public int getSize() {
        return allEffects.size();
    }

    /**
     * Gets the effect.
     * 
     * @param name
     *            the name
     * @return the effect
     */
    public Effect getEffect(EffectName name) {
        for (Effect fx : allEffects) {
            if (fx.getId() == name.getId()) {
                return fx;
            }
        }
        LOG.log(Level.WARNING, "Invalid Effect name selected: {0}", name);
        return null;
    }

    /**
     * Gets the all effects.
     * 
     * @return the all effects
     */
    public List<Effect> getAllEffects() {
        return allEffects;
    }

    /**
     * Gets the effect.
     * 
     * @param index
     *            the index
     * @return the effect
     */
    public Effect getEffect(int index) {
        for (Effect fx : allEffects) {
            if (fx.getId() == index) {
                return fx;
            }
        }
        LOG.log(Level.WARNING, "Invalid Effect index selected: {0}", index);
        return null;
    }

    /**
     * Sets the threshold value.
     * 
     * @param val
     *            the new threshold value
     */
    public void setThresholdValue(int val) {
        this.threshold.setThreshold(val);
    }

    /**
     * Gets the threshold value.
     * 
     * @return the threshold value
     */
    public int getThresholdValue() {
        return this.threshold.getThreshold();
    }

    /**
     * 
     * @return
     */
    public int getRotoZoomAngle() {
        return this.rotoZoom.getAngle();
    }

    /**
     * 
     * @param angle
     */
    public void setRotoZoomAngle(int angle) {
        this.rotoZoom.setAngle(angle);
    }

    /**
     * Sets the texture deformation lut.
     * 
     * @param textureDeformationLut
     *            the new texture deformation lut
     */
    public void setTextureDeformationLut(int textureDeformationLut) {
        textureDeformation.changeLUT(textureDeformationLut);
    }

    /**
     * Gets the texture deformation lut.
     * 
     * @return the texture deformation lut
     */
    public int getTextureDeformationLut() {
        return textureDeformation.getLut();
    }

    /**
     * 
     * @param option
     */
    public void setZoomOption(int option) {
        zoom.setZoomMode(option);
    }

    /**
     * 
     * @return
     */
    public int getZoomOption() {
        return zoom.getZoomMode();
    }

}
