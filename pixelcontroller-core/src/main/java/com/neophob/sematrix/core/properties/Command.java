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
package com.neophob.sematrix.core.properties;

import java.io.Serializable;
import java.util.Arrays;

public class Command implements Serializable {
	
	private ValidCommand validCommand;
	private String[] parameter;

	public Command(ValidCommand validCommand) {
		this.validCommand = validCommand;
		this.parameter = null;
	}

	public Command(ValidCommand validCommand, String[] parameter) {
		this.validCommand = validCommand;
		this.parameter = parameter;
	}

	public Command(String[] parameter) {
		this.validCommand = ValidCommand.valueOf(parameter[0]);
		this.parameter = Arrays.copyOfRange(parameter, 1, parameter.length);
	}

	public ValidCommand getValidCommand() {
		return validCommand;
	}

	public String[] getParameter() {
		return parameter;
	}
	
}
