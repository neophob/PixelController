package com.neophob.sematrix.output.lpd6803;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.properties.DeviceConfig;

/**
 * this class will transform a buffer
 * @author michu
 *
 */
public class RotateBuffer {

	private static int deviceXSize = 8;
	
	private RotateBuffer() {
		//no instance
	}
	
	/**
	 * 
	 * @param buffer
	 * @return
	 */
	private static int[] rotate90(int[] buffer) {
		int[] ret = new int[deviceXSize*deviceXSize];
		int ofs=0;
		for (int x=0; x<deviceXSize; x++) {			
			for (int y=0; y<deviceXSize; y++) {
				ret[deviceXSize*y+deviceXSize-1-x] = buffer[ofs++];
			}
		}
		return ret;
	}

	/**
	 * 
	 * @param buffer
	 * @return
	 */
	private static int[] flipY(int[] buffer) {
		int[] ret = new int[deviceXSize*deviceXSize];
		int ofs=0;
		for (int x=0; x<deviceXSize; x++) {			
			for (int y=0; y<deviceXSize; y++) {
				//flipX
				//ret[deviceXSize-1-y+x*deviceXSize] = buffer[ofs++];
				ret[(deviceXSize-1-x)*deviceXSize+y] = buffer[ofs++];
			}
		}
		return ret;
	}

	private static int[] rotate180(int[] buffer) {
		int[] ret = new int[deviceXSize*deviceXSize];
		int ofs=0;
		for (int x=0; x<deviceXSize; x++) {			
			for (int y=0; y<deviceXSize; y++) {
				//flipX
				ret[deviceXSize-1-y+x*deviceXSize] = buffer[ofs++];
				//ret[(deviceXSize-1-x)*deviceXSize+y] = buffer[ofs++];
			}
		}
		return ret;
	}

	/*static void print(int[] buffer) {
		int ofs=0;
		for (int x=0; x<8; x++) {			
			for (int y=0; y<8; y++) {
				System.out.print(buffer[ofs++]+" ");
			}
			System.out.println();
		}
		System.out.println("---");
	}
	
	public static void main(String args[]) {
		deviceXSize=8;
		int aa[] = new int[64];
		for (int x=0; x<64; x++)
			aa[x] = x;
		
		print(aa);
		int ret[] = flipY(aa);
		print(ret);		
	}*/


	/**
	 * 
	 * @param buffer
	 * @return
	 */
	private static int[] rotate270(int[] buffer) {
		int[] ret = new int[deviceXSize*deviceXSize];
		int ofs=0;
		for (int x=0; x<deviceXSize; x++) {			
			for (int y=0; y<deviceXSize; y++) {
				ret[x+deviceXSize*(deviceXSize-1-y)] = buffer[ofs++];
				//flip at 0.0
				//ret[x+deviceXSize*y] = buffer[ofs++];
			}
		}
		return ret;
	}

	/**
	 * 
	 * @param buffer
	 * @param deviceConfig
	 * @return
	 */
	public static int[] transformImage(int[] buffer, DeviceConfig deviceConfig) {
		
		if (deviceXSize==0) {
			deviceXSize = Collector.getInstance().getMatrix().getDeviceXSize();
		}
		
		switch (deviceConfig) {
		case NO_ROTATE:
			return buffer;

		case ROTATE_90:
			return rotate90(buffer);			
		
		case ROTATE_90_FLIPPED:
			return flipY(
					rotate90(buffer)
				);

		case ROTATE_180:
			return flipY(buffer);
			
		case ROTATE_180_FLIPPED:
			return rotate180(
					flipY(buffer)
					//buffer
					);

		case ROTATE_270:
			return rotate270(buffer);

		case ROTATE_270_FLIPPED:
			return flipY(
						rotate270(buffer)
					);

		default:
			break;
		}
		return null;
	}
}
