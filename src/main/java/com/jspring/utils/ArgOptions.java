package com.jspring.utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

public class ArgOptions extends com.jspring.collections.BasicTypesMap4String {
	private final Options options = new Options();
	private CommandLine commandLine;

	@Override
	public String getStringNullable(String key) {
		if (commandLine.hasOption(key)) {
			return commandLine.getOptionValue(key);
		}
		return null;
	}

	public void load(String[] args) throws Exception {
		CommandLineParser parser = new GnuParser();
		commandLine = parser.parse(options, args);
	}

	public void printHelp(String appName) {
		new HelpFormatter().printHelp(appName, options);
	}

	public void set(String name, String format, String description) {
		OptionBuilder.withArgName(format);
		OptionBuilder.hasArg();
		OptionBuilder.withDescription(description);
		options.addOption(OptionBuilder.create(name));
	}
}