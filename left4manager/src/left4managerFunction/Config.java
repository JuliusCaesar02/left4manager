package left4managerFunction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Config {
	private String l4d2Dir = new String();
	private String l4managerDir = new String();
	final private String addonsFileName = new String("addonlist2.txt");

	/************
	 * 
	 * @param l4d2Dir
	 * @param l4managerDir
	 */
	public Config(String l4d2Dir, String l4managerDir) {
		this.l4d2Dir = l4d2Dir;
		this.l4managerDir = l4managerDir;
	}
	
	public Config() {
	}
	public void setL4D2Dir(String dir) {
		this.l4d2Dir = dir;
	}
	public void setL4ManagerDir(String dir) {
		this.l4managerDir = dir;
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
	
	public void createFile() {
		try {
		    File myObj = new File(this.l4managerDir + "config.txt");
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
	
	public void writeFile(String output) {
		createFile();
		try {
			FileWriter myWriter = new FileWriter(this.l4managerDir + "config.txt");
			myWriter.write(output);
			myWriter.close();
			//System.out.println(output);
			
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	
	public String buildString(){
		String output = new String();
		output = "\"l4d2Dir\":\t\"" +this.l4d2Dir +"\"\n" +
				"\"l4managerDir\":\t\"" +this.l4managerDir +"\"\n";
		return output;
	}
}
