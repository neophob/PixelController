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
package com.neophob.sematrix.cli;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang.time.DurationFormatUtils;

import com.neophob.sematrix.jmx.PixelControllerStatus;
import com.neophob.sematrix.jmx.PixelControllerStatusMBean;

/**
 * JMX Helper
 * 
 * @author michu
 *
 */
public final class PixConClientJmx {
	private static final NumberFormat PERCENT_FORMAT = DecimalFormat.getPercentInstance();
	private static final NumberFormat NUMBER_FORMAT = DecimalFormat.getNumberInstance();


	private static final String SUFFIX = "ms";
	
	private PixConClientJmx() {
		// avoid instantiation
	}

	/**
	 * 
	 * @param hostname
	 * @param port
	 */
	public static void queryJmxServer(String hostname, int port) {
		System.setSecurityManager(new java.rmi.RMISecurityManager());

		System.out.println("Create an RMI connector client and connect it to the RMI connector server");
		
		JMXServiceURL url;
		JMXConnector jmxc;
		hostname=hostname+":"+port;
		try {
			url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+hostname+"/jmxrmi");
			jmxc = JMXConnectorFactory.connect(url, null);

			System.out.println("Get an MBeanServerConnection...");
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			printJmxStatus(mbsc);
			
			System.out.println("\nClose the connection to the server");
			jmxc.close();
			
		} catch (Exception e) {
			System.out.println("Error: JMX Error!");
			e.printStackTrace();
		}	
	}
	
	/**
	 * 
	 * @param mbsc
	 * @throws Exception
	 */
	private static void printJmxStatus(MBeanServerConnection mbsc) throws Exception {
		ObjectName mbeanName = new ObjectName(PixelControllerStatus.JMX_BEAN_NAME);
		
		PixelControllerStatusMBean mbeanProxy = JMX.newMBeanProxy(mbsc, mbeanName, PixelControllerStatusMBean.class, true);		
		
		System.out.println("\nGeneric:");
		System.out.println("Server Version:\t" + mbeanProxy.getVersion());
		System.out.println("Current FPS:\t" + NUMBER_FORMAT.format(mbeanProxy.getCurrentFps()) + " (" + PERCENT_FORMAT.format(mbeanProxy.getCurrentFps() / mbeanProxy.getConfiguredFps()) + " of configured FPS: " + NUMBER_FORMAT.format(mbeanProxy.getConfiguredFps()) + ")");
		System.out.println("Frame count:\t" + mbeanProxy.getFrameCount());
		
		System.out.println("\nAverage update times collected during the last " + DurationFormatUtils.formatDuration(mbeanProxy.getRecordedMilliSeconds(), "ss:SSS") + " seconds:");
		System.out.println("Generator: \t" + NUMBER_FORMAT.format(mbeanProxy.getGeneratorUpdateTime())+SUFFIX);
		System.out.println("Effect:    \t" + NUMBER_FORMAT.format(mbeanProxy.getEffectUpdateTime())+SUFFIX);
		System.out.println("Output:    \t" + NUMBER_FORMAT.format(mbeanProxy.getOutputUpdateTime())+SUFFIX);
		System.out.println("Fader:     \t" + NUMBER_FORMAT.format(mbeanProxy.getFaderUpdateTime())+SUFFIX);
		System.out.println("Dbg Window:\t" + NUMBER_FORMAT.format(mbeanProxy.getInternalWindowUpdateTime())+SUFFIX);
		
		long runTime = System.currentTimeMillis() - mbeanProxy.getStartTime();
		runTime /= 60000;
		System.out.println("Runtime:\t" + runTime+"min");
		
	}

}
