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
