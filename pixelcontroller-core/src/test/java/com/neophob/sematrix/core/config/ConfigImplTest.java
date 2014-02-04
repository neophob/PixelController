package com.neophob.sematrix.core.config;

import java.util.Arrays;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.neophob.sematrix.core.properties.ConfigConstant;

public class ConfigImplTest {

    private static final String INTKEY = "intKey";
    private static final int INTVALUE = 55;

    private static final String FLOATKEY = "floatKey";
    private static final float FLOATVALUE = 7.55f;

    private static final String BOOLEANKEY = "booleanKey";
    private static final boolean BOOLEANVALUE = true;

    private static final String STRINGKEY = "stringKey";
    private static final String STRINGVALUE = "aaaa";

    private static final String STRINGSKEY = "stringsKey";
    private static final String STRINGSVALUE = "a,b,c,d,";

    @Test
    public void testBasic() {
        Properties p = new Properties();
        p.setProperty(INTKEY, "" + INTVALUE);
        p.setProperty(FLOATKEY, "" + FLOATVALUE);
        p.setProperty(BOOLEANKEY, "" + BOOLEANVALUE);
        p.setProperty(STRINGKEY, STRINGVALUE);
        p.setProperty(STRINGSKEY, STRINGSVALUE);

        Config cfg = new ConfigImpl(p);

        Assert.assertEquals(STRINGVALUE, cfg.getString(STRINGKEY, ""));
        Assert.assertEquals(Arrays.asList(STRINGSVALUE.split(ConfigConstant.DELIM)),
                cfg.getStrings(STRINGSKEY));
        Assert.assertEquals(FLOATVALUE, cfg.getFloat(FLOATKEY, 0), 0.002f);
        Assert.assertEquals(BOOLEANVALUE, cfg.getBoolean(BOOLEANKEY, false));
        Assert.assertEquals(INTVALUE, cfg.getInt(INTKEY, 0));

        Assert.assertEquals(4, cfg.getInt("does not work", 4));
        Assert.assertEquals(4, cfg.getInt("", 4));
    }
}
