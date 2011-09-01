package com.neophob.sematrix.output;

public abstract class ArduinoOutput extends Output {
	protected boolean initialized;
	protected long needUpdate;
	protected long noUpdate;
	
	public ArduinoOutput(PixelControllerOutput controller, String name) {
		super(controller, name);
	}
	
	public abstract int getArduinoErrorCounter();

	public abstract int getArduinoBufferSize();
	
	public abstract long getLatestHeartbeat();
}
