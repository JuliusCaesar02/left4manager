package left4managerFunction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModList {
	private List<ModInfo> modList = new ArrayList<ModInfo>();
	private File jsonFile;
	private File addonListFile;
	
	public ModList(Config config) {
		this.addonListFile = config.getL4d2AddonFile();
		this.jsonFile = new File(config.getL4managerJSONDir() +File.separator +"modJson.json");
	}
	
	public void setJsonFile(File file) {
		this.jsonFile = file;
	}
	
	public File getJsonFile() {
		return this.jsonFile;
	}
	
	public List<ModInfo> getModList() {
    	return this.modList;
    }
	
	public ModInfo getModInfoByCode(String code) {
		try {
			return this.modList.get(getModIndexByCode(code));
		} catch(Exception e) {
			return null;
		}
    }
	
	public int getModIndexByCode(String code) {
    	for(int i = 0; i < this.modList.size(); i++) {
    		if(this.modList.get(i).getCode().equals(code)) {
    			return i;
    		}
    	}
		return -1;
    }
	
	public List<ModInfo> getL4D2ModList() throws IOException{
		String text = Utilities.fileReader(addonListFile);
		List<ModInfo> l4d2ModList = new ArrayList<ModInfo>();
		
		Pattern pattern = Pattern.compile("(\\d+).vpk\"\\s*\"(\\d*)");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
        	boolean isEnabled = false;
        	if(matcher.group(2).equals("1")) {
        		isEnabled = true;
        	}
        	System.out.println(matcher.group(1) + isEnabled);
        	l4d2ModList.add(new ModInfo(matcher.group(1), isEnabled));
        }
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
    
    public String[] getAdditionalInfo(String html) {
    	String[] result = new String[3];
    	
		Pattern titleRegex = Pattern.compile("<div class=\"workshopItemTitle\">(.*?)\\n*</div>");
    	//System.out.println(regexParser(titleRegex, html));
    	result[0] = Utilities.regexParser(titleRegex, html);
    	
    	Pattern authorRegex = Pattern.compile("<div class=\"friendBlockContent\">\\n*(.*?)<br>", Pattern.DOTALL);
    	result[1] = Utilities.regexParser(authorRegex, html);
    	//System.out.println(regexParser(authorRegex, html));
    	
    	Pattern descriptionRegex = Pattern.compile("<div class=\"workshopItemDescription\" id=\"highlightContent\">(.+?)<script>", Pattern.DOTALL);
    	//System.out.println(regexParser(descriptionRegex, html));
    	result[2] = Utilities.regexParser(descriptionRegex, html);
    	Pattern removeImg = Pattern.compile("\\u003cimg[\\s\\S]*?\\u003e");
    	Matcher removeImgMatcher = removeImg.matcher(result[2]);
    	result[2] = removeImgMatcher.replaceAll("");
    		
    	return result;
    }
    
    public List<Tags> getTags(String html){  	
    	List<Tags> tags = new ArrayList<Tags>();   
    	Pattern bigCutRegex = Pattern.compile("<div class=\"col_right responsive_local_menu\">([\\s\\S]*?)<div class=\"detailsStatsContainerLeft\">", Pattern.DOTALL);
    	String bigCutString = Utilities.regexParser(bigCutRegex, html);	
    	//System.out.println(bigCutString);
    	
    	Pattern tagGroupRegex = Pattern.compile("<div data-panel=[\\s\\S]*?class=\"workshopTags\">([\\s\\S]*?)<\\/div>", Pattern.DOTALL);
    	Matcher tagGroupMatcher = tagGroupRegex.matcher(bigCutString);
    	
    	while (tagGroupMatcher.find()) {
    		//System.out.println(tagGroupMatcher.group(i) +i +"/" +tagGroupMatcher.groupCount());
			String tagGroupString = tagGroupMatcher.group();
    		
			Pattern primaryTagRegex = Pattern.compile("<span class=\"workshopTagsTitle\">([\\s\\S]*?):&nbsp;", Pattern.DOTALL);
	    	String primaryTagsString = Utilities.regexParser(primaryTagRegex, tagGroupString);
	    	
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
    
    public String[] parseVPKInfo(String text) {
    	String[] result = new String[3];
    	Pattern titleRegex = Pattern.compile("addontitle\\s+([\\s\\S]*?)\\n\\s*\"*addon", Pattern.CASE_INSENSITIVE);
    	result[0] = Utilities.regexParser(titleRegex, text);
    	
    	Pattern authorRegex = Pattern.compile("addonauthor\\s+([\\s\\S]*?)\\n\\s*\"*addon", Pattern.CASE_INSENSITIVE);
    	result[1] = Utilities.regexParser(authorRegex, text);
    	
    	Pattern descriptionRegex = Pattern.compile("addondescription\\s+\"*([\\s\\S]*?)\"\\n*", Pattern.CASE_INSENSITIVE);
    	result[2] = Utilities.regexParser(descriptionRegex, text);
    	
		return result;
    }
}
