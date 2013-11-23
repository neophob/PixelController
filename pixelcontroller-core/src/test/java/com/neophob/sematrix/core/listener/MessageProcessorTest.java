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
package com.neophob.sematrix.core.listener;

import static org.junit.Assert.assertEquals;

import com.neophob.sematrix.core.glue.Collector;
import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.listener.MessageProcessor;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.properties.ConfigConstant;
import com.neophob.sematrix.core.properties.ValidCommands;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

/**
 * verify the rotate buffer code
 * @author michu
 *
 */
public class MessageProcessorTest {

	private static final Logger LOG = Logger.getLogger(MessageProcessorTest.class.getName());

	/*public static Unsafe getUnsafe() {
		try {
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			return (Unsafe)f.get(null);
		} catch (Exception e) { 
 		}
		return null;
	}*/

	static void setFinalStatic(Field field, Object newValue) throws Exception {
		field.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(null, newValue);
	}

	@Test
	public void processMessages() throws Exception {
		if (java.awt.GraphicsEnvironment.isHeadless()) {
			/**
			 * Initialize PApplet on headless machines will fail, the
			 * reason for this error is this field:
			 * 
			 * static public final int MENU_SHORTCUT = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
			 * 
			 * -> there is not Toolkit on headless machines. 
			 * 
			 * I try to subclass, overwrite with reflection and set it with the Unsafe method
			 * but I couldn't get this to work if the unit test is executed on a machine with
			 * the option "-Djava.awt.headless=true" 
			 */
			return;
		}

/*		try {
			Unsafe u = getUnsafe();
			System.out.println(u.addressSize());
			final long ofs = u.objectFieldOffset(PApplet.class.getDeclaredField("MENU_SHORTCUT"));
			//u.putInt(ofs, 0);  			
		} catch (Throwable e) {
			e.printStackTrace();
		}*/

//		PApplet papplet = mock(PApplet.class);

		Properties config = new Properties();
		String rootDir = System.getProperty("buildDirectory");
		LOG.log(Level.INFO, "Test Root Directory: "+rootDir);

		config.put(ConfigConstant.RESOURCE_PATH, rootDir);
		ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

		Collector.getInstance().init(new FileUtils(), ph);

		String[] str = null;
		MessageProcessor.processMsg(str, false, null);

		str = new String[2];
		str[0] = "AAAAAAAAAA";
		str[1] = "ALSOINVALID";
		MessageProcessor.processMsg(str, false, null);

		str[0] = "CURRENT_VISUAL";
		str[1] = "23323223";
		MessageProcessor.processMsg(str, false, null);

		str[0] = "CURRENT_OUTPUT";
		str[1] = "99999";
		MessageProcessor.processMsg(str, false, null);

		str[0] = "COLOR_FADE_LENGTH";
		str[1] = "0";
		MessageProcessor.processMsg(str, false, null);

		str[0] = "CHANGE_GENERATOR_A";
		str[1] = "9999990";    	
		MessageProcessor.processMsg(str, false, null);

		str = new String[1];
		str[0] = "CHANGE_GENERATOR_A";
		MessageProcessor.processMsg(str, false, null);

		//test real life use case
		str = new String[2];
		str[0] = "CURRENT_VISUAL";
		str[1] = "0";    	
		MessageProcessor.processMsg(str, false, null);

		str[0] = "CHANGE_GENERATOR_A";
		str[1] = "2";    	
		MessageProcessor.processMsg(str, false, null);

		str[0] = "CHANGE_EFFECT_B";
		str[1] = "5";    	
		MessageProcessor.processMsg(str, false, null);

		str[0] = "CHANGE_MIXER";
		str[1] = "1";    	
		MessageProcessor.processMsg(str, false, null);

		assertEquals(2, Collector.getInstance().getVisual(0).getGenerator1Idx());
		assertEquals(5, Collector.getInstance().getVisual(0).getEffect2Idx());
		assertEquals(1, Collector.getInstance().getVisual(0).getMixerIdx());
	}


}
