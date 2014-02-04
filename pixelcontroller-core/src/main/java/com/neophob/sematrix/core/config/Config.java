package com.neophob.sematrix.core.config;

import java.util.List;

public interface Config {

    String getString(String key, String defaultValue);

    List<String> getStrings(String key);

    int getInt(String key, int defaultValue);

    boolean getBoolean(String key, boolean defaultValue);

    float getFloat(String key, float defaultValue);
}
