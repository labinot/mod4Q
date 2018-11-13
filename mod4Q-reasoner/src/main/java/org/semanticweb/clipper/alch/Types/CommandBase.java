package org.semanticweb.clipper.alch.Types;

import com.beust.jcommander.JCommander;

public abstract class CommandBase {

	protected JCommander jc;

	public CommandBase(JCommander jc) {
		this.jc = jc;
		jc.addCommand(this);
	}

	abstract boolean validate();

	abstract String getErrorMessage();

	abstract void exec();
}
