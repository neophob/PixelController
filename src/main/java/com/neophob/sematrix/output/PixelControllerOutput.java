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

package com.neophob.sematrix.output;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.PixelControllerElement;

/**
 * The Class PixelControllerOutput.
 */
public class PixelControllerOutput implements PixelControllerElement {
	
	private static final Logger LOG = Logger.getLogger(PixelControllerOutput.class.getName());

	/** The all outputs. */
	private List<Output> allOutputs;
	
	private ExecutorService executorService;
	private CountDownLatch update_endGate;
	private CountDownLatch prepare_endGate;
	
	/**
	 * Instantiates a new pixel controller output.
	 */
	public PixelControllerOutput() {
		this.allOutputs = new CopyOnWriteArrayList<Output>();
		this.executorService = Executors.newCachedThreadPool();
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.glue.PixelControllerElement#initAll()
	 */
	public void initAll() {
		//nothing to init here
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.glue.PixelControllerElement#getCurrentState()
	 */
	public List<String> getCurrentState() {
		//no status to store
		return new ArrayList<String>();
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.glue.PixelControllerElement#update()
	 */
	@Override
	public void update() {
		// check if this is the first call of this method
		if (this.prepare_endGate == null && this.update_endGate == null) {
			// we have to prepare the int[] buffers manually the first time. to not mess up this method
			// even more the prepare() methods will be called directly without any additional threading
			// overhead. for the first frame it shouldn't really matter that the outputs have to wait 
			// until the int[] buffers preparation is done.
			for (Output output : this.allOutputs) {
				output.prepare();
			}
		}
		
		// wait for the outputs to finish their prepare() methods from the previous call of this method
		if (this.prepare_endGate != null) {
			try {
				this.prepare_endGate.await();
			} catch (InterruptedException e) {
				LOG.log(Level.SEVERE, "waiting for all outputs to finish their prepare() method got interrupted!", e);
			}
		}
		
		// wait for the outputs to finish their update() methods from the previous call of this method
		if (this.update_endGate != null) {
			try {
				this.update_endGate.await();
			} catch (InterruptedException e) {
				LOG.log(Level.SEVERE, "waiting for all outputs to finish their update() method got interrupted!", e);
			}
		}
		
		// after the prepare() and update() methods call of all outputs are done we have in every
		// output the currentBufferMap instance that contains all int[] buffer that just have been
		// written to the output instances and can therefore be cleaned. also we have the preparedBufferMap
		// instance containing the new set of int[] buffers to be written to the output. therefore we have
		// switch both map instances to be ready for the next call of this method
		for (Output output : this.allOutputs) {
			output.switchBuffers();
		}
		
		// create countDownLatches used to call all update() and prepare() methods simultaneously
		// and to block until all calls have been finished via the end gate instances
		final CountDownLatch update_startGate = new CountDownLatch(1);
		this.update_endGate = new CountDownLatch(this.allOutputs.size());
		final CountDownLatch prepare_startGate = new CountDownLatch(1);
		this.prepare_endGate = new CountDownLatch(this.allOutputs.size());
		
		// construct two runnable instance for each output and schedule them
		for (final Output output: allOutputs) {
			// create runnable instance for updating an output instance
			Runnable update_runnable = new Runnable() {
				@Override
				public void run() {
					try {
						update_startGate.await();
						try {
							output.update();
						} finally {
							update_endGate.countDown();
						}
					} catch (InterruptedException e) {
						LOG.log(Level.SEVERE, "waiting for start gate of output: " + output.getClass().getSimpleName()  + " got interrupted!", e);
					}
				}
			};
			this.executorService.execute(update_runnable);
			// create runnable instance for preparing an output instance
			Runnable prepare_runnable = new Runnable() {
				@Override
				public void run() {
					try {
						prepare_startGate.await();
						try {
							output.prepare();
						} finally {
							prepare_endGate.countDown();
						}
					} catch (InterruptedException e) {
						LOG.log(Level.SEVERE, "waiting for start gate of output: " + output.getClass().getSimpleName()  + " got interrupted!", e);
					}
				}
			};
			this.executorService.execute(prepare_runnable);
		}
		
		// trigger output update() methods and write the 
		// current int[] buffers to the output instances
		update_startGate.countDown();
		
		// trigger output prepare() methods to be ready for
		// the next run in parallel to the running update() methods
		prepare_startGate.countDown();
	}
	
	/**
	 * Gets the all outputs.
	 * @return the all outputs
	 */
	public List<Output> getAllOutputs() {
		return allOutputs;
	}

	/**
	 * Adds the output.
	 * @param output the output
	 */
	public void addOutput(Output output) {
		allOutputs.add(output);
	}
}
