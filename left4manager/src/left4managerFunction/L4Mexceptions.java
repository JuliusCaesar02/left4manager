package left4managerFunction;

import java.io.File;

public class L4Mexceptions {
	static class ConfigValueException extends Exception {
	      public ConfigValueException(String param, String value) {
	    	  super("Config value \"" +value +"\" for parameter \"" +param +"\" doesn't exist");
	      }
	 }
	
	static class InfoSourceException extends Exception {
	      public InfoSourceException(short source) {
	    	  super("Info source \"" +source +"\" is to low for selected mode");
	      }
	 }
	
	static class ModInfoNotFoundException extends Exception {
		public ModInfoNotFoundException(String code, File file) {
	    	  super("ModInfo with code \"" +code +"\" has not been found in json (" +file.getAbsolutePath() +")");
	    }
		public ModInfoNotFoundException(File file) {
	    	  super("Json file \"" +file.getAbsolutePath() +"\" doesn't exist");
	    }
	}
	
	static class NoConnectionException extends Exception {
		public NoConnectionException(String url) {
	    	  super("Cannot connect to to \"" +url +"\"");
	    }
	}
	
}
