package left4managerFunction;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;

public class UpdateModFile {
	private String addonsPath = new String();
	private String fileName = new String();
	
	/********
	 * 
	 * @param config
	 */
	public UpdateModFile(Config config) {
		this.addonsPath = config.getL4D2Dir() +File.separator +"left4dead2" +File.separator;
		this.fileName = config.getAddonsFileName();
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	public void createFile() {
		try {
			System.out.println(addonsPath + fileName);
		    File myObj = new File(addonsPath + fileName);
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
			FileWriter myWriter = new FileWriter(addonsPath + fileName);
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
}
