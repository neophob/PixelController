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
import com.neophob.sematrix.statistics.Statistics;

/**
 * The Class PixelControllerOutput.
 */
public class PixelControllerOutput implements PixelControllerElement {
	
	/** The log. */
	private static final Logger LOG = Logger.getLogger(PixelControllerOutput.class.getName());

	/** The all outputs. */
	private List<Output> allOutputs;

	private final ExecutorService executorService;
	private Statistics statistics;
	
	/**
	 * Instantiates a new pixel controller output.
	 */
	public PixelControllerOutput() {
		allOutputs = new CopyOnWriteArrayList<Output>();
		this.executorService = Executors.newCachedThreadPool();
		this.statistics = Statistics.getInstance();
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
		long init = System.nanoTime();
		final CountDownLatch startGate = new CountDownLatch(1);
		final CountDownLatch endGate = new CountDownLatch(this.allOutputs.size());
		for (final Output output: this.allOutputs) {
			
			// create runnable instance
			Runnable outputRunnable = new Runnable() {
				@Override
				public void run() {
					try {
						startGate.await();
						try {
							output.update();
						} finally {
							endGate.countDown();
						}
					} catch (InterruptedException e) {
						LOG.log(Level.SEVERE, "waiting for start gate of output: " + output.getClass().getSimpleName()  + " got interrupted!", e);
					}
				}
			};
			// schedule runnable for execution
			this.executorService.execute(outputRunnable);
		}
		// track time needed to execute all runnable instances
		long start = System.nanoTime();
		startGate.countDown();
		try {
			endGate.await();
		} catch (InterruptedException e) {
			LOG.log(Level.SEVERE, "waiting for all outputs to finish their update() method got interrupted!", e);
		}
		this.statistics.trackOutputsUpdateTime(init, start, System.nanoTime());
	}

	/*
	 * OUTPUT ======================================================
	 */

	/**
	 * Gets the all outputs.
	 *
	 * @return the all outputs
	 */
	public List<Output> getAllOutputs() {
		return allOutputs;
	}

	/**
	 * Adds the output.
	 *
	 * @param output the output
	 */
	public void addOutput(Output output) {
		allOutputs.add(output);
	}
}
