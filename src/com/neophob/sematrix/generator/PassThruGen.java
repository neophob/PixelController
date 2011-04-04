package com.neophob.sematrix.generator;

import java.util.logging.Logger;

import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * @author mvogt
 *
 */
public class PassThruGen extends Generator {

	static Logger log = Logger.getLogger(PassThruGen.class.getName());
	
	public PassThruGen() {
		super(GeneratorName.PASSTHRU, ResizeName.QUALITY_RESIZE);
	}
	
	@Override
	public void update() {	
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
}
