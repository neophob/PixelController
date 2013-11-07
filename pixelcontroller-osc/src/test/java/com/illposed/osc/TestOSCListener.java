/*
 * Copyright (C) 2001, C. Ramakrishnan / Illposed Software.
 * All rights reserved.
 *
 * This code is licensed under the BSD 3-Clause license.
 * See file LICENSE (or LICENSE.html) for more information.
 */

package com.illposed.osc;

import java.util.Date;
import org.junit.Ignore;

@Ignore
public class TestOSCListener implements OSCListener {

	private boolean messageReceived = false;
	private Date receivedTimestamp = null;

	public Date getReceivedTimestamp() {
		return receivedTimestamp;
	}

	public boolean isMessageReceived() {
		return messageReceived;
	}

	@Override
	public void acceptMessage(Date time, OSCMessage message) {
		messageReceived = true;
		receivedTimestamp = time;
	}
}
