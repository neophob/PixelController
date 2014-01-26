/**
 * Copyright (C) 2011-2013 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.core.glue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.PixelControllerElement;
import com.neophob.sematrix.core.listener.MessageProcessor;
import com.neophob.sematrix.core.properties.ValidCommand;
import com.neophob.sematrix.core.sound.ISound;
import com.neophob.sematrix.core.visual.VisualState;

/**
 * The Class PixelControllerShufflerSelect.
 */
public class PixelControllerShufflerSelect implements PixelControllerElement {

    private static final Logger LOG = Logger.getLogger(PixelControllerShufflerSelect.class
            .getName());

    private List<Boolean> shufflerSelect;

    private ISound sound;
    private long randomLifetime;
    private long lastRandomHit;

    /**
     * Instantiates a new pixel controller shuffler select.
     */
    public PixelControllerShufflerSelect(ISound sound, long randomLifetime) {
        this.sound = sound;
        this.randomLifetime = randomLifetime * 1000L;
        shufflerSelect = new CopyOnWriteArrayList<Boolean>();
        for (int n = 0; n < ShufflerOffset.values().length; n++) {
            shufflerSelect.add(true);
        }
        LOG.log(Level.INFO, "Random Mode Lifetime: " + this.randomLifetime + "ms. ");

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.glue.PixelControllerElement#initAll()
     */
    public void initAll() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.neophob.sematrix.core.glue.PixelControllerElement#getCurrentState()
     */
    public List<String> getCurrentState() {
        List<String> ret = new ArrayList<String>();

        ret.add(ValidCommand.CHANGE_SHUFFLER_SELECT + " " + getShufflerStatus());

        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.glue.PixelControllerElement#update()
     */
    @Override
    public void update() {
        VisualState visualState = VisualState.getInstance();

        if (visualState.isRandomMode()) {
            if (RandomModeShuffler.shuffleStuff(shufflerSelect, sound.isKick(), sound.isHat(),
                    sound.isSnare())) {
                visualState.notifyGuiUpdate();
            }

            long now = System.currentTimeMillis();
            if (randomLifetime > 0 && now - lastRandomHit > randomLifetime) {
                lastRandomHit = now;
                LOG.log(Level.INFO, "Random Mode lifetime is over, fire manual shuffler");
                String[] msg = new String[] { "" + ValidCommand.RANDOMIZE };
                MessageProcessor.INSTANCE.processMsg(msg, true, null);
            }
        } else if (visualState.isRandomPresetMode()) {
            if (Shuffler.randomPresentModeShuffler(sound)) {
                LOG.log(Level.INFO, "Fire Preset Random");
                String[] msg = new String[] { "" + ValidCommand.PRESET_RANDOM };
                MessageProcessor.INSTANCE.processMsg(msg, true, null);
            }
        }
    }

    /**
     * returns string for current status. the order is fix and defined by gui
     * 
     * @return the shuffler status
     */
    private String getShufflerStatus() {
        StringBuilder sb = new StringBuilder();
        int value;

        for (int i = 0; i < shufflerSelect.size(); i++) {
            value = 0;
            if (shufflerSelect.get(i)) {
                value = 1;
            }
            sb.append(' ');
            sb.append(value);
        }
        return sb.toString();
    }

    /**
     * Gets the shuffler select.
     * 
     * @return the shuffler select
     */
    public List<Boolean> getShufflerSelect() {
        return shufflerSelect;
    }

    /**
     * Gets the shuffler select.
     * 
     * @param ofs
     *            the ofs
     * @return the shuffler select
     */
    public boolean getShufflerSelect(ShufflerOffset ofs) {
        return shufflerSelect.get(ofs.getOffset());
    }

    /**
     * Sets the shuffler select.
     * 
     * @param ofs
     *            the ofs
     * @param value
     *            the value
     */
    public void setShufflerSelect(int ofs, Boolean value) {
        this.shufflerSelect.set(ofs, value);
    }

}
