package left4managerFunction;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;

public class UpdateModFile {
	private String directory = new String();
	private String fileName = new String();
	
	public void createFile() {
		try {
			System.out.println(directory + fileName);
		    File myObj = new File(directory + fileName);
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
			FileWriter myWriter = new FileWriter(directory + fileName);
			myWriter.write(output);
			myWriter.close();
			//System.out.println(output);
			
			
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	
	public String buildString(List<ModInfo> modList){
		String output = new String();
		String bool;
		output = "AddonList \n" + "{ \n";
		for(int i=0; i < modList.size(); i++) {
			bool = "0";
			if(modList.get(i).getEnabled() == true) {
				bool = "1";
			}
			output += "\"workshop\\" 
			+ modList.get(i).getCode() 
			+ ".vpk\"\t\"" 
			+ bool 
			+ "\"\n";
		}
		output += "}";
		return output;
	}
	
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	public String getDirectory() {
		return this.directory;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileName() {
		return this.fileName;
	}
}
