package left4managerFunction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import left4managerFunction.L4Mexceptions.ModInfoNotFoundException;
import left4managerFunction.L4Mexceptions.NoConnectionException;
import com.connorhaigh.javavpk.core.Archive;
import com.connorhaigh.javavpk.core.ArchiveEntry;
import com.connorhaigh.javavpk.core.Directory;
import com.connorhaigh.javavpk.exceptions.ArchiveException;
import com.connorhaigh.javavpk.exceptions.EntryException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Utilities {
	
	public static String fileReader(File file) throws IOException {
		String output = new String();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();			
		String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        output = sb.toString();
    	br.close();

		return output;
	}
	
	public static List<ModInfo> jsonReader(File file) throws ModInfoNotFoundException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
		
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
		} catch (FileNotFoundException e) {
			throw new ModInfoNotFoundException(file);
		}

        Type modInfoListType = new TypeToken<List<ModInfo>>(){}.getType();
        List<ModInfo> oldJson = gson.fromJson(br, modInfoListType);
        
        return oldJson;
    }
	
	public static void fileWriter(File file, String text, boolean append) throws IOException {
    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), StandardCharsets.UTF_8));
    	bw.write(text);
    	bw.close();
	}
	
	public static void jsonWriter(File file, List<?> object, boolean append) throws IOException {
    	Gson gson = new GsonBuilder().setPrettyPrinting().create(); 	
    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), StandardCharsets.UTF_8));
    	gson.toJson(object, bw);
    	bw.close();
	}
	
	public static void jsonAppend(File file, ModInfo object) throws IOException {
		List<ModInfo> oldJson = new ArrayList<ModInfo>();
		try {
			oldJson = jsonReader(file);
		} catch (ModInfoNotFoundException e) {
			e.printStackTrace();
		}
        boolean exist = false;
        
        for(int i = 0; i < oldJson.size(); i++) {
        	if(object.getCode() == oldJson.get(i).getCode()) {
        		exist = true;
        	}
        }
        
        if(!exist) {    	  	
        	BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8));
    		oldJson.add(object);
    		jsonWriter(file, oldJson, true); 			
    		fw.close();
        }
	}
	
	public static ModInfo modInfoFromJson(File file, String code) throws IOException, ModInfoNotFoundException {
		List<ModInfo> oldJson = jsonReader(file);

		ModInfo oldModInfo;
		try {
	        for(int i = 0; i < oldJson.size(); i++) {
	        	oldModInfo = oldJson.get(i);
	        	System.out.println(oldModInfo.getName());
	        	if(code.equals(oldModInfo.getCode()) && !oldModInfo.getName().isEmpty()) {
	        		return oldJson.get(i); 	
	        	}
	        }
		} catch(NullPointerException e) {
			throw new ModInfoNotFoundException(code, file);
		}
		throw new ModInfoNotFoundException(code, file);
	}
	
	public static void createFile(File file) {
		try {
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
	
	public static String buildAddonFile(List<ModInfo> modList) {
		String output = new String();
		String bool;
		output = "AddonList \n" + "{ \n";
		for(int i=0; i < modList.size(); i++) {
			if(modList.get(i).getEnabled() == true) {
				bool = "1";
			}
			else {
				bool = "0";
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
	
	public static String regexParser(Pattern pattern, String text) {
    	Matcher matcher = pattern.matcher(text);
    	if(matcher.find()) {
    		return matcher.group(1);
    	}
    	else return null;
    }
	
	public static String getHtml(String url, String delimiter) throws NoConnectionException {
		String html = null;
    	URLConnection connection = null;
    	
    	InputStreamReader reader;
    	try {
    		connection =  new URL(url).openConnection();
    		reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
    	} catch (IOException e) {
    		throw new NoConnectionException(url);
    	}
    	
		Scanner scanner = new Scanner(reader);
		scanner.useDelimiter(delimiter);
		html = scanner.next();
		scanner.close();

    	return html;
	}
	
	public static String getVPKInfo(File file) throws ArchiveException, IOException, EntryException {
		Archive vpkArchive = null;
		vpkArchive = new Archive(file);
		vpkArchive.load();
		
		for (Directory directory : vpkArchive.getDirectories()) {
			for (ArchiveEntry entry : directory.getEntries()) {
				if(entry.getFullName().equals("addoninfo.txt")) {
					byte[] bytes = entry.readData();
					return new String(bytes, StandardCharsets.UTF_8);
				}
			}
		}
		return null;
	}
}
