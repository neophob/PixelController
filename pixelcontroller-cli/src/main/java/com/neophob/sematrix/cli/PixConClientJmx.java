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
package com.neophob.sematrix.cli;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang3.time.DurationFormatUtils;

import com.neophob.sematrix.core.jmx.PixelControllerStatus;
import com.neophob.sematrix.core.jmx.PixelControllerStatusMBean;
import com.neophob.sematrix.core.jmx.TimeMeasureItemGlobal;
import com.neophob.sematrix.core.jmx.TimeMeasureItemOutput;

/**
 * JMX Helper
 * 
 * @author michu
 *
 */
public final class PixConClientJmx {
	
	private static final NumberFormat PERCENT_FORMAT = DecimalFormat.getPercentInstance();

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
		
		JMXServiceURL url;
		JMXConnector jmxc;
		hostname=hostname+":"+port;
		System.out.println("Create an RMI connector client and connect it to the RMI connector server "+hostname);
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
		
		// print general status information
		System.out.println("\nGeneric:");
		System.out.printf("%-25s: %s\n",
				"server version",
				mbeanProxy.getVersion()
		);
		System.out.printf("%-25s: %3.3f (%s of configured fps: %2.0f)\n",
				"current fps",
				mbeanProxy.getCurrentFps(),
				PERCENT_FORMAT.format(mbeanProxy.getCurrentFps() / mbeanProxy.getConfiguredFps()),
				mbeanProxy.getConfiguredFps()
		);
		System.out.printf("%-25s: %d\n",
				"frame count",
				mbeanProxy.getFrameCount()
		);
		System.out.printf("%-25s: %s\n",
				"running since",
				DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - mbeanProxy.getStartTime())
		);
        System.out.printf("%-25s: %s/%s\n",
                "OSC Packets/Bytes Recieved",
                mbeanProxy.getRecievedOscPakets(),
                mbeanProxy.getRecievedOscBytes()
        );
		
		// print average timing information
		System.out.println("\nThe following average times have been collected during the last " 
		+ DurationFormatUtils.formatDuration(mbeanProxy.getRecordedMilliSeconds(), "ss.SSS") + " seconds:");
		for (TimeMeasureItemGlobal valueEnum : TimeMeasureItemGlobal.values()) {
			System.out.printf("   %-22s: %3.3fms\n",
					valueEnum.getReadableName(),
					mbeanProxy.getAverageTime(valueEnum)
			);
		}
		
		// print output specific timing information
		for (int output = 0; output < mbeanProxy.getNumberOfOutputs(); output++) {
			System.out.println("\nOuput-specific average times for output #" + (output+1) + ": " + mbeanProxy.getOutputType(output).getReadableName());
			for (TimeMeasureItemOutput outputValueEnum : TimeMeasureItemOutput.values()) {
				System.out.printf("   %-22s: %3.3fms\n",
						outputValueEnum.getReadableName(),
						mbeanProxy.getOutputAverageTime(output, outputValueEnum)
				);
			}
		}
	}
}