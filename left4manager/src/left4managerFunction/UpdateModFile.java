package left4managerFunction;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;

public class UpdateModFile {
	private File addonsPath;
	
	/********
	 * 
	 * @param config
	 */
	public UpdateModFile(Config config) {
		this.addonsPath = config.getL4d2AddonFile();
	}
	
	public void createFile() {
		try {
		    if (addonsPath.createNewFile()) {
		      System.out.println("File created: " + addonsPath.getName());
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
			FileWriter myWriter = new FileWriter(addonsPath);
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
