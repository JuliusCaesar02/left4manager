package left4managerFunction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Config {
	private File l4d2Dir;
	private File l4d2AddonFile;
	private File l4d2VPKDir;
	private File l4managerDir;
	private File l4managerJSONDir;
	private File l4managerIconDir;
	private String[][] configs;
	
	public Config() {
		this.l4managerDir = new File(System.getProperty("user.dir"));
		this.l4managerIconDir = new File(this.l4managerDir +File.separator +"icon");
		this.l4managerJSONDir = new File(this.l4managerDir +File.separator +"json");
	}
	
	public void setL4D2Dir(File l4d2Dir) {
		this.l4d2Dir = l4d2Dir;
		this.l4d2AddonFile = new File(this.l4d2Dir +File.separator +"left4dead2" +File.separator +"addonlist.txt");
		this.l4d2VPKDir = new File(this.l4d2Dir +File.separator +"left4dead2" +File.separator +"addons");
		configs[0][1] = l4d2Dir.getAbsolutePath();
	}
	public void setL4D2Dir() {
		this.l4d2Dir = new File(configs[0][1]);
		this.l4d2AddonFile = new File(this.l4d2Dir +File.separator +"left4dead2" +File.separator +"addonlist.txt");
		this.l4d2VPKDir = new File(this.l4d2Dir +File.separator +"left4dead2" +File.separator +"addons");
	}

	public File getL4D2Dir() {
		return l4d2Dir;
	}
	public File getL4d2AddonFile() {
		return l4d2AddonFile;
	}
	public File getL4d2VPKDir() {
		return l4d2VPKDir;
	}
	public File getL4managerDir() {
		return l4managerJSONDir;
	}
	public File getL4managerJSONDir() {
		return l4managerJSONDir;
	}
	public File getL4managerIconDir() {
		return l4managerIconDir;
	}
	public String[][] getConfigs() {
		return configs;
	}
	public void setConfigs(int config, String value){
		this.configs[config][1] = value;
	}
	
	public void readConfig() throws IOException {
		String[][] result = new String[5][2];
		
		String text;
		text = Utilities.fileReader(new File(this.l4managerJSONDir +File.separator +"config.txt"));

		result[0][0] = "l4d2Dir";
		result[0][1] = Utilities.regexParser(Pattern.compile("\""+result[0][0]+"\":\\s*\"([\\s\\S]*?)\""), text);
		
		result[1][0] = "mode";
		result[1][1] = Utilities.regexParser(Pattern.compile("\""+result[1][0]+"\":\\s*\"([\\s\\S]*?)\""), text);
		
		File l4d2DirTemp = new File(result[0][1]);
		if(!l4d2DirTemp.getName().equals("Left 4 Dead 2")) {
			throw new IOException();
		}
		this.configs = result;
		setL4D2Dir();
	}

	public void writeConfig() throws IOException {
		
		File configFile = new File(l4managerJSONDir +File.separator +"config.txt");
		Utilities.createFile(configFile);
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < configs.length; i++) {
			if(configs[i][0] != null) {
				sb.append("\""+configs[i][0]+"\":");
				sb.append("\t");
				sb.append("\""+configs[i][1]+"\"");
				sb.append(System.getProperty("line.separator"));
			}
		}

		Utilities.fileWriter(configFile, sb.toString(), false);
	}
}