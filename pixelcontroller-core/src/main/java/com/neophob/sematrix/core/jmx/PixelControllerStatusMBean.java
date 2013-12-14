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
package com.neophob.sematrix.core.jmx;

import com.neophob.sematrix.core.output.IOutput;
import com.neophob.sematrix.core.output.OutputDeviceEnum;

/**
 * JMX Interface to provide basic statistics
 * @author michu
 *
 */
public interface PixelControllerStatusMBean {

	/**
	 * 
	 * @return version of the application
	 */
	float getVersion();
	
	/**
	 * 
	 * @return current frames per second
	 */
	float getCurrentFps();
	
	/**
	 * configured frames per second
	 * @return
	 */
	float getConfiguredFps();

	/**
	 * How many frames we displayed?
	 * @return
	 */
	long getFrameCount();
	
	/**
	 * how long does it take to update the component defined by the given valueEnum instance?
	 * @return average time in ms for the duration of the getRecordedMilliSeconds() method
	 */
	float getAverageTime(TimeMeasureItemGlobal valueEnum);
	
	/**
	 * how long does it take to update the output aspect defined by the given outputValueEnum instance?
	 * @return average time in ms for the duration of the getRecordedMilliSeconds() method
	 */
	float getOutputAverageTime(int output, TimeMeasureItemOutput timeMeasureItem);
	
	/**
	 * when was the app started?
	 * @return time in ms
	 */
	long getStartTime();
	
	/**
	 * @return the number of milliseconds from that the average values will be calculated
	 */
	long getRecordedMilliSeconds();

	/**
	 * @return returns the number of registered output instances
	 */
	int getNumberOfOutputs();
	
	/**
	 * @return return the number of osc pakets recieved
	 */
	int getRecievedOscPakets();
	
    /**
     * @return return the number of osc pakets bytes recieved
     */
	long getRecievedOscBytes();

	/**
	 * @return returns the type of the given output instance position
	 */
	OutputDeviceEnum getOutputType(int output);
	
	/**
	 * update tracking time
	 * @param valueEnum
	 * @param time
	 */
	void trackTime(TimeMeasureItemGlobal valueEnum, long time);
	
	/**
	 * 
	 * @param output
	 * @param timeMeasureItem
	 * @param time
	 */
	void trackOutputTime(IOutput output, TimeMeasureItemOutput timeMeasureItem, long time);
	
	void setCurrentFps(float currentFps);
	
	void setFrameCount(long frameCount);
	//List<String> getCurrentState();
	
	void setOscServerStatistics(PacketAndBytesStatictics oscServerStatistics);
}
