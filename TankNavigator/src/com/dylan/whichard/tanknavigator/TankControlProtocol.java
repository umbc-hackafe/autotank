package com.dylan.whichard.tanknavigator;

public class TankControlProtocol {
	public static enum Command {
		SINGLE_DATAGRAM("GIMME", "Sends a single data point for each of the chosen sensors."),
		CLOSE_FIREHOSE("STAHP", "Stops sending data."),
		OPEN_FIREHOSE("MOAR", "Starts sending the chosen sensor data continuously."),
		ADD_TYPES("WANT", "[ACC|ROT|GPS]: Start receiving data from the specified sensor."),
		REMOVE_TYPES("NO", "[ACC|ROT|GPS]: Stops receiving data from the specified sensor."),
		HELP("HALP", "Displays this help message"),
		EXIT("BAI", "Closes the connection.");
		
		public String commandString;
		public String helpString;
		
		Command(String command) {
			this.commandString = command;
		}
		
		Command(String command, String help) {
			this.commandString = command;
			this.helpString = help;
		}
		
		public static Command fromString(String command) {
			for (Command c : values()) {
				if (c.commandString.equals(command)) {
					return c;
				}
			}
			
			throw new IllegalArgumentException("Invalid Command " + command);
		}
	}
	
	public static enum DataType {
		ROTATION_VECTOR("ROT"),
		GPS("GPS"),
		ACCELEROMETER("ACC");
		
		public String typeString;
		
		DataType(String command) {
			this.typeString = command;
		}
		
		public static DataType fromString(String command) {
			for (DataType c : values()) {
				if (c.typeString.equals(command)) {
					return c;
				}
			}
			
			throw new IllegalArgumentException("Invalid data type " + command);
		}		
	}
}
