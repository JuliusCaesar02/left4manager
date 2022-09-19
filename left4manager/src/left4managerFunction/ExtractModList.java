package left4managerFunction;

import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//String bash = "extractVPK.sh";
//Process process = Runtime.getRuntime().exec("ciao");
public class ExtractModList {
    //static String patternString = "\"workshop(\\d+)\\.vpk\"\\s*\"(\\d{1})\"";
	private String patternString = "(\\d+).vpk\"\\s*\"(\\d*)";
	private Pattern pattern = Pattern.compile(patternString);
	private List<ModInfo> modList = new ArrayList<ModInfo>();
    
    public List<ModInfo> getModList() {
    	return modList;
    }
    
    public void populateModList() {
    	try {
			System.out.println(this.patternString);
			List<String> content = Files.readAllLines(Paths.get("C:\\Program Files (x86)\\Steam\\steamapps\\common\\Left 4 Dead 2\\left4dead2\\addonlist.txt"));
			content.forEach(i->{
				System.out.println(i);
				Matcher checkedLine = pattern.matcher(i);
				if (checkedLine.find()) {
			         boolean enabled = false;
			         if(checkedLine.group(2) == "1") {
			        	 enabled = true;
			         }
			         modList.add(new ModInfo(checkedLine.group(1), enabled));
			      }
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
//System.out.println(modList.get(0).getCode());