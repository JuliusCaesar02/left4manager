package left4managerFunction;

import java.util.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//String bash = "extractVPK.sh";
//Process process = Runtime.getRuntime().exec("ciao");
public class ExtractModList {
	private List<ModInfo> modList = new ArrayList<ModInfo>();
	private String addonsPath = new String();
	private String fileName = new String();
    
	public ExtractModList(Config config) {
		this.addonsPath = config.getL4D2Dir() +File.separator +"left4dead2" +File.separator;
		this.fileName = config.getAddonsFileName();
	}
    public List<ModInfo> getModList() {
    	return this.modList;
    }
    
    public void populateModList() {
    	try {
    		Pattern pattern = Pattern.compile("(\\d+).vpk\"\\s*\"(\\d*)");
			List<String> content = Files.readAllLines(Paths.get(addonsPath +fileName));
			content.forEach(i->{
				System.out.println(i);
				Matcher checkedLine = pattern.matcher(i);
				if (checkedLine.find()) {
			         boolean enabled = false;
			         if(checkedLine.group(2).equals("1")) {
			        	 enabled = true;
			         }
			         String[] additionalInfo = getAdditionalInfo(checkedLine.group(1));
			         modList.add(new ModInfo(additionalInfo[0], checkedLine.group(1), additionalInfo[1], additionalInfo[2], enabled));
			    }
			});
		} catch (IOException e) {
			File file = new File(addonsPath +fileName);
		}
    }
    
    public String[] getAdditionalInfo(String code) {
    	String[] result = new String[3];
    	String html = null;
    	String url = "https://steamcommunity.com/sharedfiles/filedetails/?id=" +code;
    	URLConnection connection = null;
    	try {
    	  connection =  new URL(url).openConnection();
    	  Scanner scanner = new Scanner(connection.getInputStream());
    	  scanner.useDelimiter("<div class=\"detailBox\">");
    	  html = scanner.next();
    	  scanner.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	//System.out.println(html);
    	
		Pattern titleRegex = Pattern.compile("<div class=\"workshopItemTitle\">(.*?)\\n*</div>");
    	//System.out.println(regexParser(titleRegex, html));
    	result[0] = regexParser(titleRegex, html);
    	
    	Pattern authorRegex = Pattern.compile("<div class=\"friendBlockContent\">\\n(\\s*.*?)<br>");
    	result[1] = regexParser(authorRegex, html);
    	//System.out.println(regexParser(authorRegex, html));
    	
    	Pattern descriptionRegex = Pattern.compile("<div class=\"workshopItemDescription\" id=\"highlightContent\">(.*?)</div>");
    	//System.out.println(regexParser(descriptionRegex, html));
    	result[2] = regexParser(descriptionRegex, html);
    	
    	return result;
    }
    
    public String regexParser(Pattern pattern, String text) {
    	Matcher matcher = pattern.matcher(text);
    	if(matcher.find()) {
    		return matcher.group(1);
    	}
    	else return "nothing found";
    }
    
    /*public void runScript(String code, Config config) {
		Process process;
		String initialDir = config.getL4D2Dir() +File.separator +"left4dead2" + File.separator +"addons" +File.separator +"workshop";
		String finalDir = config.getL4managerDir() + File.separator + "temp" +"\"";
		String bashPath = config.getL4managerDir() + File.separator +"vpkExtractor.sh";
		System.out.println("-" + bashPath);
		String[] command = {"vpkExtractor.sh" + " -i \"" +initialDir +"\"-f \"" +finalDir +"\"-n " +code, config.getL4managerDir()};
		//String[] path = {"/bin/sh", config.getL4managerDir()};
		System.out.print(command);
		
	    try {
			process = Runtime.getRuntime()
			  .exec("C:\\Users\\Caesar\\.left4manager\\vpkExtractor.sh");
	    	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ProcessBuilder pb = new ProcessBuilder(
		    bashPath,
		    "-i " +initialDir,
		    "-f " +finalDir,
		    "-n " +code
			);
		
		ProcessBuilder pb = new ProcessBuilder(
				new String[]{
						"C:\\Users\\Caesar\\.left4manager\\vpkExtractor.sh"
						}
				);
		pb. redirectErrorStream(true);

		try {
			Process p = pb.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }*/

}