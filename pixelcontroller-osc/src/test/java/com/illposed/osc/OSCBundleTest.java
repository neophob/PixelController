/*
 * Copyright (C) 2003, C. Ramakrishnan / Illposed Software.
 * All rights reserved.
 *
 * This code is licensed under the BSD 3-Clause license.
 * See file LICENSE (or LICENSE.html) for more information.
 */

package com.illposed.osc;

import java.util.Date;
import java.util.GregorianCalendar;

import com.illposed.osc.utility.OSCByteArrayToJavaConverter;

/**
 * @author Chandrasekhar Ramakrishnan
 * @see OSCBundle
 */
public class OSCBundleTest extends junit.framework.TestCase {

	public void testSendBundle() {
		Date timestamp = GregorianCalendar.getInstance().getTime();
		OSCBundle bundle =
			new OSCBundle(
				new OSCPacket[] { new OSCMessage("/dummy") },
				timestamp);
		byte[] byteArray = bundle.getByteArray();
		OSCByteArrayToJavaConverter converter = new OSCByteArrayToJavaConverter();
		OSCBundle packet = (OSCBundle) converter.convert(byteArray, byteArray.length);
		if (!packet.getTimestamp().equals(timestamp)) {
			fail("Send Bundle did not receive the correct timestamp " + packet.getTimestamp()
				+ "(" + packet.getTimestamp().getTime() +
				") (should be " + timestamp +"( " + timestamp.getTime() + ")) ");
		}
		OSCPacket[] packets = packet.getPackets();
		OSCMessage msg = (OSCMessage) packets[0];
		if (!msg.getAddress().equals("/dummy")) {
			fail("Send Bundle's message did not receive the correct address");
		}
	}

	public void testSendBundleImmediate() {
		OSCBundle bundle =
			new OSCBundle(new OSCPacket[] { new OSCMessage("/dummy") });
		byte[] byteArray = bundle.getByteArray();
		OSCByteArrayToJavaConverter converter = new OSCByteArrayToJavaConverter();
		OSCBundle packet = (OSCBundle) converter.convert(byteArray, byteArray.length);
		if (!packet.getTimestamp().equals(OSCBundle.TIMESTAMP_IMMEDIATE)) {
			fail("Timestamp should have been immediate, not " + packet.getTimestamp()
				+ "(" + packet.getTimestamp().getTime() + ")");
		}
		OSCPacket[] packets = packet.getPackets();
		OSCMessage msg = (OSCMessage) packets[0];
		if (!msg.getAddress().equals("/dummy")) {
			fail("Send Bundle's message did not receive the correct address");
		}
	}
}
