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
package com.neophob.sematrix.jmx;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * 
 * @author michu
 *
 */
public class PixelControllerStatus implements PixelControllerStatusMBean {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(PixelControllerStatus.class.getName());

	public static final String JMX_BEAN_NAME = PixelControllerStatus.class.getCanonicalName()+":type=PixelControllerStatusMBean";
	
	private static final float VERSION = 1.04f;
	
	private float currentFps;
	private float generatorUpdateTime;
	private float effectUpdateTime;
	private float outputUpdateTime;

	/**
	 * Register the JMX Bean
	 */
	public PixelControllerStatus() {
		LOG.log(Level.INFO, "Initialize the PixelControllerStatus JMX Bean");
		// register MBean
		try {
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			ObjectName name = new ObjectName(JMX_BEAN_NAME);
			// check if the MBean is already registered
			if (!server.isRegistered(name)) {
				server.registerMBean(this, name);
			}
		} catch (JMException ex) {
			LOG.log(Level.WARNING, "Error while registering class as JMX Bean.", ex);
		}
	}
	
	@Override
	public float getVersion() {
		return VERSION;
	}

	@Override
	public float getCurrentFps() {
		return currentFps;
	}

	@Override
	public float getGeneratorUpdateTime() {
		return generatorUpdateTime;
	}

	@Override
	public float getEffectUpdateTime() {
		return effectUpdateTime;
	}

	@Override
	public float getOutputUpdateTime() {
		return outputUpdateTime;
	}

	public void setCurrentFps(float currentFps) {
		this.currentFps = currentFps;
	}

	public void setGeneratorUpdateTime(float generatorUpdateTime) {
		this.generatorUpdateTime = generatorUpdateTime;
	}

	public void setEffectUpdateTime(float effectUpdateTime) {
		this.effectUpdateTime = effectUpdateTime;
	}

	public void setOutputUpdateTime(float outputUpdateTime) {
		this.outputUpdateTime = outputUpdateTime;
	}

	
}
