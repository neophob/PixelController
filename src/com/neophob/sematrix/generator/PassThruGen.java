package com.neophob.sematrix.generator;

import java.util.logging.Logger;

/**
 * @author mvogt
 *
 */
public class PassThruGen extends Generator {

	static Logger log = Logger.getLogger(PassThruGen.class.getName());
	
	public PassThruGen() {
		super(GeneratorName.PASSTHRU);
	}
	
	@Override
	public void update() {	
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
}
