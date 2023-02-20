package left4managerFunction;

import java.util.*;
import java.lang.reflect.Type;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
//String bash = "extractVPK.sh";
//Process process = Runtime.getRuntime().exec("ciao");
public class ExtractModList {
	private List<ModInfo> modList = new ArrayList<ModInfo>();
	private String addonsPath = new String();
	private String jsonFile = new String();
	private String fileName = new String();
    
	public ExtractModList(Config config) {
		this.addonsPath = config.getL4D2Dir() +File.separator +"left4dead2" +File.separator;
		this.jsonFile = config.getL4managerDir() +File.separator +"json" +File.separator +"modJson.json";
		this.fileName = config.getAddonsFileName();
	}
    public List<ModInfo> getModList() {
    	return this.modList;
    }
    
    public ModInfo getModInfoByCode(String code) {
    	for(int i = 0; i < this.modList.size(); i++) {
    		if(this.modList.get(i).getCode().equals(code)) {
    			return this.modList.get(i);
    		}
    	}
		return null;
    }
    
    public int getModIndexByCode(String code) {
    	for(int i = 0; i < this.modList.size(); i++) {
    		if(this.modList.get(i).getCode().equals(code)) {
    			return i;
    		}
    	}
		return -1;
    }
    
    public List<ModInfo> readL4d2ModList() throws IOException{
    	List<ModInfo> l4d2ModList = new ArrayList<ModInfo>();
    	
		BufferedReader fr = new BufferedReader(new InputStreamReader(new FileInputStream(addonsPath +fileName)));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = fr.readLine()) != null) {
            sb.append(line);
        }
        String fileText = sb.toString();

        Pattern pattern = Pattern.compile("(\\d+).vpk\"\\s*\"(\\d*)");
        Matcher matcher = pattern.matcher(fileText);
        while (matcher.find()) {
        	boolean isEnabled = false;
        	if(matcher.group(2).equals("1")) {
        		isEnabled = true;
        	}
        	System.out.println(matcher.group(1) + isEnabled);
        	l4d2ModList.add(new ModInfo(matcher.group(1), isEnabled));
        }
    	fr.close();
    	
    	return l4d2ModList;
    }
    
    public void swapPosition(int first, int second) {
    	Collections.swap(modList, first, second);
    }
    
    public void moveToTop(int index) {
    	 modList.add(0, modList.remove(index));
    }
    
    public void moveToBottom(int index) {
   	 	modList.add(modList.size() - 1, modList.remove(index));
    }
    
    public void moveToIndex(int elementIndex, int position) {
    	modList.add(position, modList.remove(elementIndex));
    }
    
    public String getHtml(String code) throws IOException {
    	String html = null;
    	String url = "https://steamcommunity.com/sharedfiles/filedetails/?id=" +code;
    	URLConnection connection = null;

  	  connection =  new URL(url).openConnection();
  	  InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
  	  Scanner scanner = new Scanner(reader);
  	  scanner.useDelimiter("<div class=\"detailBox\"><script type=\"text/javascript\">");
  	  html = scanner.next();
  	  scanner.close();

    	//System.out.println(html);
    	return html;
    }
    
    public String[] getAdditionalInfo(String html) {
    	String[] result = new String[3];
    	
		Pattern titleRegex = Pattern.compile("<div class=\"workshopItemTitle\">(.*?)\\n*</div>");
    	//System.out.println(regexParser(titleRegex, html));
    	result[0] = regexParser(titleRegex, html);
    	
    	Pattern authorRegex = Pattern.compile("<div class=\"friendBlockContent\">\\n*(.*?)<br>", Pattern.DOTALL);
    	result[1] = regexParser(authorRegex, html);
    	//System.out.println(regexParser(authorRegex, html));
    	
    	Pattern descriptionRegex = Pattern.compile("<div class=\"workshopItemDescription\" id=\"highlightContent\">(.+?)<script>", Pattern.DOTALL);
    	//System.out.println(regexParser(descriptionRegex, html));
    	result[2] = regexParser(descriptionRegex, html);
    	Pattern removeImg = Pattern.compile("\\u003cimg[\\s\\S]*?\\u003e");
    	Matcher removeImgMatcher = removeImg.matcher(result[2]);
    	result[2] = removeImgMatcher.replaceAll("");
    		
    	return result;
    }
    
    public List<Tags> getTags(String html){  	
    	List<Tags> tags = new ArrayList<Tags>();   
    	Pattern bigCutRegex = Pattern.compile("<div class=\"col_right responsive_local_menu\">([\\s\\S]*?)<div class=\"detailsStatsContainerLeft\">", Pattern.DOTALL);
    	String bigCutString = regexParser(bigCutRegex, html);	
    	//System.out.println(bigCutString);
    	
    	Pattern tagGroupRegex = Pattern.compile("<div data-panel=[\\s\\S]*?class=\"workshopTags\">([\\s\\S]*?)<\\/div>", Pattern.DOTALL);
    	Matcher tagGroupMatcher = tagGroupRegex.matcher(bigCutString);
    	
    	while (tagGroupMatcher.find()) {
    		//System.out.println(tagGroupMatcher.group(i) +i +"/" +tagGroupMatcher.groupCount());
			String tagGroupString = tagGroupMatcher.group();
    		
			Pattern primaryTagRegex = Pattern.compile("<span class=\"workshopTagsTitle\">([\\s\\S]*?):&nbsp;", Pattern.DOTALL);
	    	String primaryTagsString = regexParser(primaryTagRegex, tagGroupString);
	    	
			List<String> secondaryTags = new ArrayList<String>();   
			Pattern secondaryTagRegex = Pattern.compile("<a[\\s\\S]*?>([\\s\\S]*?)<\\/a>", Pattern.DOTALL);
	    	Matcher secondaryTagsMatcher = secondaryTagRegex.matcher(tagGroupString);
	    	//System.out.println(regexParser(secondaryTagRegex, bigCutString));
	    	
	    	while (secondaryTagsMatcher.find()) { 		
	    		secondaryTags.add(secondaryTagsMatcher.group(1));
	    	}
			tags.add(new Tags(primaryTagsString, secondaryTags));
    	}
    	return tags;
    }
    
    /******
     * 
     * @param pattern
     * @param text
     * @return
     */
    public String regexParser(Pattern pattern, String text) {
    	Matcher matcher = pattern.matcher(text);
    	if(matcher.find()) {
    		return matcher.group(1);
    	}
    	else return "nothing found";
    }
    
    public void createJsonFile() {
    	try {
		    File file = new File(jsonFile);
		    if (file.createNewFile()) {
		      System.out.println("File created: " + file.getName());
		    } else {
		      System.out.println("File already exists.");
		    }
	    } catch (IOException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
    }
    
    /***********
     * 
     * @param objectList
     * @throws Exception
     */
    public void addObjectToJson(List<ModInfo> objectList) throws Exception {
    	Gson gson = new GsonBuilder().setPrettyPrinting().create(); 	
    	FileWriter fw = new FileWriter(jsonFile);
    	fw.close();
    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonFile, true), StandardCharsets.UTF_8));
    	gson.toJson(objectList, bw);
    	bw.close();
    }
    
    /**********
     * 
     * @param object
     * @throws Exception
     */
    public void addObjectToJson(ModInfo object) throws Exception {
    	Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
    	FileReader fr = new FileReader(jsonFile);
    	
        Type modInfoListType = new TypeToken<List<ModInfo>>(){}.getType();
        List<ModInfo> oldJson = gson.fromJson(fr, modInfoListType);
        fr.close();
        
        try {
	        boolean exist = false;
	        
	        for(int i = 0; i < oldJson.size(); i++) {
	        	if(object.getCode() == oldJson.get(i).getCode()) {
	        		exist = true;
	        	}
	        }
	        
	        if(!exist) {    	  	
	        	BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonFile, true), StandardCharsets.UTF_8));
        		oldJson.add(object);
        		gson.toJson(oldJson, fw);
        		fw.close();   			
	        }
        } catch(Exception e) {
        	e.printStackTrace();
        }
	        
    }
    
    public ModInfo getObjectFromJson(String code) throws IOException {
    	Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8));

        Type modInfoListType = new TypeToken<List<ModInfo>>(){}.getType();
        List<ModInfo> oldJson = gson.fromJson(br, modInfoListType);
        br.close();
        
        ModInfo oldModInfo;
        for(int i = 0; i < oldJson.size(); i++) {
        	oldModInfo = oldJson.get(i);
        	if(code.equals(oldModInfo.getCode()) && !oldModInfo.getName().isEmpty()) {
        		return oldJson.get(i); 	
        	}
        }
		throw new IOException();
    }
    
    public boolean checkIfEmpty() {
    	try {
	    	BufferedReader br = new BufferedReader(new FileReader(jsonFile));
	    	if (br.readLine() == null) {
	    		br.close();
	    		return true;
	    	}
	    	br.close();
			return false;
    	} catch(Exception e) {
        	e.printStackTrace();
        }
    	return true;
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