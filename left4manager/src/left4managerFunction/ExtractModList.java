package left4managerFunction;

import java.util.*;
import java.lang.reflect.Type;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		this.jsonFile = config.getL4managerDir() +File.separator +"modJson.json";
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
    
    /*public void populateModList() throws Exception {
    	createJsonFile();
    	
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
			         
			         if(checkIfEmpty()) {
			        	 try {
			        		 addObjectToJson(new ModInfo(additionalInfo[0], checkedLine.group(1), additionalInfo[1], additionalInfo[2], enabled));
			        	 } catch (Exception e) {
			        		 // TODO Auto-generated catch block
			        		 e.printStackTrace();
			        	 }
			         } else {
						 try {
							 addObjectToJson(modList);
						 } catch (Exception e) {
							 // TODO Auto-generated catch block
							 e.printStackTrace();
						 }
			         }
			    }
			});
		} catch (IOException e) {
			File file = new File(addonsPath +fileName);
		}
    }*/
    
    public void populateModList() {
    	createJsonFile();
    	
    	try {
    		Pattern pattern = Pattern.compile("(\\d+).vpk\"\\s*\"(\\d*)");
			List<String> content = Files.readAllLines(Paths.get(addonsPath +fileName), Charset.forName("UTF-8"));
			System.out.println("Is json file empty?" +checkIfEmpty());
	    	if(!checkIfEmpty()) {
	    		content.forEach(i->{
	    			System.out.println(i);
	    			Matcher checkedLine = pattern.matcher(i);
	    			if (checkedLine.find()) {
	    				boolean enabled = false;
				         if(checkedLine.group(2).equals("1")) {
				        	 enabled = true;
				         }
				         
				         ModInfo objectFromJson = new ModInfo("NULL", false);
						 try {
							 objectFromJson = getObjectFromJson(checkedLine.group(1));
						 } catch (Exception e) {
							 // TODO Auto-generated catch block
							 e.printStackTrace();
						 }
				         if(objectFromJson.getCode() == "NULL") {
				        	 System.out.println("Object not found in json");
				        	 String[] additionalInfo = getAdditionalInfo(checkedLine.group(1));
					         modList.add(new ModInfo(additionalInfo[0], checkedLine.group(1), additionalInfo[1], additionalInfo[2], enabled));
					         try {
								addObjectToJson(new ModInfo(additionalInfo[0], checkedLine.group(1), additionalInfo[1], additionalInfo[2], enabled));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				         }
				         else {
				        	 System.out.println("Object found in json");
				        	 modList.add(objectFromJson);
				        	 modList.get(modList.size() - 1).setEnabled(enabled);
				         }
	    			}
				});
	    	}
	    	else {
	    		content.forEach(i->{
	    			System.out.println(i);
	    			Matcher checkedLine = pattern.matcher(i);
	    			if (checkedLine.find()) {
	    				System.out.println("Empty file, adding the entire list to json at the end of the operation");
	    				boolean enabled = false;
				         if(checkedLine.group(2).equals("1")) {
				        	 enabled = true;
				         }
				         String[] additionalInfo = getAdditionalInfo(checkedLine.group(1));
				         modList.add(new ModInfo(additionalInfo[0], checkedLine.group(1), additionalInfo[1], additionalInfo[2], enabled));
	    			}  
	    		});
	    		try {
	    			addObjectToJson(modList);
	    		} catch (Exception e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    	}
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    	
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
    
    public String[] getAdditionalInfo(String code) {
    	String[] result = new String[3];
    	String html = null;
    	String url = "https://steamcommunity.com/sharedfiles/filedetails/?id=" +code;
    	URLConnection connection = null;
    	try {
    	  connection =  new URL(url).openConnection();
    	  InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
    	  Scanner scanner = new Scanner(reader);
    	  scanner.useDelimiter("<div class=\"detailBox\"><script type=\"text/javascript\">");
    	  html = scanner.next();
    	  scanner.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	//System.out.println(html);
    	
		Pattern titleRegex = Pattern.compile("<div class=\"workshopItemTitle\">(.*?)\\n*</div>");
    	//System.out.println(regexParser(titleRegex, html));
    	result[0] = regexParser(titleRegex, html);
    	
    	Pattern authorRegex = Pattern.compile("<div class=\"friendBlockContent\">\\n*(.*?)<br>", Pattern.DOTALL);
    	result[1] = regexParser(authorRegex, html);
    	//System.out.println(regexParser(authorRegex, html));
    	
    	Pattern descriptionRegex = Pattern.compile("<div class=\"workshopItemDescription\" id=\"highlightContent\">(.+?)<script>", Pattern.DOTALL);
    	//System.out.println(regexParser(descriptionRegex, html));
    	result[2] = regexParser(descriptionRegex, html);
    	
    	return result;
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
    	gson.toJson(objectList, fw);
		fw.close();
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
        		FileWriter fw = new FileWriter(jsonFile);
        		oldJson.add(object);
        		gson.toJson(oldJson, fw);
        		fw.close();   			
	        }
        } catch(Exception e) {
        	e.printStackTrace();
        }
	        
    }
    
    public ModInfo getObjectFromJson(String code) throws Exception {
    	Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
    	FileReader fr = new FileReader(jsonFile);
        Type modInfoListType = new TypeToken<List<ModInfo>>(){}.getType();
        List<ModInfo> oldJson = gson.fromJson(fr, modInfoListType);
        fr.close();
        
        try {
	        for(int i = 0; i < oldJson.size(); i++) {
	        	if(code.equals(oldJson.get(i).getCode())) {
	        		return oldJson.get(i); 	
	        	}
	        }
        } catch(Exception e) {
        	e.printStackTrace();
        }
        
        return new ModInfo("NULL", false);
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