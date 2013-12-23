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
package com.neophob.sematrix.core.preset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * simple class to store present set's.
 * 
 * @author michu
 */
public class PresetSettings implements Serializable {

    private static final transient String NAME_MARKER = "presetname=";

    private static final transient char DELIM = ';';

    /** The present. */
    private List<String> present;
    private String name = "";

    /**
     * Gets the present.
     * 
     * @return the present
     */
    public List<String> getPresent() {
        return present;
    }

    /**
     * Sets the present.
     * 
     * @param present
     *            the new present
     */
    public void setPresent(List<String> present) {
        this.present = present;
    }

    /**
     * Sets the present.
     * 
     * @param present
     *            the new present
     */
    public void setPresent(String[] present) {
        List<String> list = new ArrayList<String>();
        for (String s : present) {
            if (StringUtils.startsWith(s, NAME_MARKER)) {
                String rawName = StringUtils.substring(s, NAME_MARKER.length());
                if (StringUtils.isNotBlank(rawName)) {
                    this.name = rawName;
                }
            } else {
                list.add(s);
            }
        }
        this.present = list;
    }

    /**
     * Gets the settings as string.
     * 
     * @return the settings as string
     */
    public String getSettingsAsString() {
        if (present == null) {
            return "";
        }

        StringBuilder ret = new StringBuilder();

        for (String s : present) {
            ret.append(s);
            ret.append(DELIM);
        }

        // add name
        if (StringUtils.isNotBlank(name)) {
            ret.append(NAME_MARKER);
            ret.append(name);
            ret.append(DELIM);
        }

        return ret.toString();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     */
    public boolean isSlotUsed() {
        if (present == null || present.isEmpty()) {
            return false;
        }
        return true;
    }

}
