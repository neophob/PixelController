package com.neophob.sematrix.core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.neophob.sematrix.core.properties.ConfigConstant;

public class ConfigImpl implements Config {

    private static final transient Logger LOG = Logger.getLogger(ConfigImpl.class.getName());

    private static final transient String FAILED_TO_PARSE = "Failed to parse {0}";

    private Properties config;

    public ConfigImpl(Properties config) {
        this.config = config;
        LOG.log(Level.INFO, "Config initialized, config size: {0} keys", this.config.size());
    }

    @Override
    public String getString(String key, String defaultValue) {
        String rawConfig = config.getProperty(key);
        if (StringUtils.isNotBlank(rawConfig)) {
            return rawConfig;
        }
        return defaultValue;
    }

    @Override
    public List<String> getStrings(String key) {
        List<String> ret = new ArrayList<String>();
        String rawConfig = config.getProperty(key);
        if (StringUtils.isNotBlank(rawConfig)) {
            for (String s : rawConfig.split(ConfigConstant.DELIM)) {
                ret.add(s);
            }
        }
        return ret;
    }

    @Override
    public int getInt(String key, int defaultValue) {
        String rawConfig = config.getProperty(key);
        if (StringUtils.isNotBlank(rawConfig)) {
            try {
                int val = Integer.parseInt(StringUtils.strip(rawConfig));
                if (val >= 0) {
                    return val;
                } else {
                    LOG.log(Level.WARNING, "Ignored negative value {0}", rawConfig);
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, FAILED_TO_PARSE, rawConfig);
            }
        }
        return defaultValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        String rawConfig = config.getProperty(key);
        if (StringUtils.isNotBlank(rawConfig)) {
            try {
                return Boolean.parseBoolean(rawConfig);
            } catch (Exception e) {
                LOG.log(Level.WARNING, FAILED_TO_PARSE, rawConfig);
            }
        }
        return defaultValue;
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        String rawConfig = config.getProperty(key);
        if (StringUtils.isNotBlank(rawConfig)) {
            try {
                float val = Float.parseFloat(StringUtils.strip(rawConfig));
                if (val >= 0) {
                    return val;
                } else {
                    LOG.log(Level.WARNING, "Ignored negative value {0}", rawConfig);
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, FAILED_TO_PARSE, rawConfig);
            }
        }
        return defaultValue;
    }

}
