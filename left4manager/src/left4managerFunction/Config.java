package left4managerFunction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import left4managerFunction.Gui.GroupListModel;

public class Config {
	private String l4d2Dir = new String();
	final private String l4managerDir = new String(System.getProperty("user.home") +File.separator +".left4manager");
	final private String addonsFileName = new String("addonlist.txt");

	/************
	 * 
	 * @param l4d2Dir
	 * @param l4managerDir
	 */
	public Config(String l4d2Dir) {
		this.l4d2Dir = l4d2Dir;
	}
	
	public Config() {
		readFile();
	}
	
	public void setL4D2Dir(String dir) {
		this.l4d2Dir = dir;
	}
	public String getL4D2Dir() {
		return this.l4d2Dir;
	}
	public String getL4managerDir() {
		return this.l4managerDir;
	}
	public String getAddonsFileName() {
		return this.addonsFileName;
	}
	
	public void createFile(String name) {
		try {
		    File myObj = new File(this.l4managerDir +File.separator + name);
		    if (myObj.createNewFile()) {
		      System.out.println("File created: " + myObj.getName());
		    } else {
		      System.out.println("File already exists.");
		    }
	    } catch (IOException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
	}
	
	public void writeFile() {
		createFile("config.txt");
		try {
			FileWriter fw = new FileWriter(this.l4managerDir +File.separator + "config.txt");
			fw.write(buildString());
			fw.close();
			//System.out.println(output);
			
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	
	public void readFile() {
		try {
			List<String> content = Files.readAllLines(Paths.get(this.l4managerDir +File.separator + "config.txt"));
			Pattern l4d2DirPattern = Pattern.compile("\"l4d2Dir\":\\s*\"(.*?)\"");
			
			for(int i = 0; i < content.size(); i++) {
				Matcher l4d2DirMatcher = l4d2DirPattern.matcher(content.get(i));

				if(l4d2DirMatcher.find()) {
					this.l4d2Dir = l4d2DirMatcher.group(1);
				}			
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String buildString(){
		String output = new String();
		output = "\"l4d2Dir\":\t\"" +this.l4d2Dir +"\"\n";
		return output;
	}
	
	public void writeModGroupFile(GroupListModel listModel) {
		createFile("modGroup.json");
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();         
    	FileWriter fw;
		try {
			fw = new FileWriter(getL4managerDir() +File.separator +"modGroup.json");
			gson.toJson(listModel.get(), fw);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
