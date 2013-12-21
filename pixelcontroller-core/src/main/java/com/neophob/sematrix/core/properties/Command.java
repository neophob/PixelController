package com.neophob.sematrix.core.properties;

import java.io.Serializable;
import java.util.Arrays;

public class Command implements Serializable {
	
	private ValidCommands validCommand;
	private String[] parameter;

	public Command(ValidCommands validCommand) {
		this.validCommand = validCommand;
		this.parameter = null;
	}

	public Command(ValidCommands validCommand, String[] parameter) {
		this.validCommand = validCommand;
		this.parameter = parameter;
	}

	public Command(String[] parameter) {
		this.validCommand = ValidCommands.valueOf(parameter[0]);
		this.parameter = Arrays.copyOfRange(parameter, 1, parameter.length);
	}

	public ValidCommands getValidCommand() {
		return validCommand;
	}

	public String[] getParameter() {
		return parameter;
	}
	
}
