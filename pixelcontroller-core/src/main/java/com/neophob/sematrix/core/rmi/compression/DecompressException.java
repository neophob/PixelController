/**
 * Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.core.rmi.compression;

public class DecompressException extends Exception {

	private static final long serialVersionUID = 7586636618269125537L;

	public DecompressException() {
		super();
	}

	public DecompressException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DecompressException(String arg0) {
		super(arg0);
	}

	public DecompressException(Throwable arg0) {
		super(arg0);
	}

}
