package left4managerFunction;

import java.util.ArrayList;
import java.util.List;

public class ModGroup {
	private List<ModInfo> groupModList = new ArrayList<ModInfo>();
	private String groupName = new String();
	private List<ModInfo> modList;
	
	public ModGroup(String groupName, ExtractModList modList) {
		this.groupName = groupName;
		this.modList = modList.getModList();
	}
	
	public void addModToList(int modNumber) {
		this.groupModList.add(modList.get(modNumber));
	}
	
	public void addModToList(int[] modNumber) {
		for(int i = 0; i < modNumber.length; i++) {
			addModToList(modNumber[i]);
		}
	}
	
	public void removeModToList(int listNumber) {
		this.groupModList.remove(listNumber);
	}
	
	public void removeModToList(int[] modNumber) {
		for(int i = 0; i < modNumber.length; i++) {
			removeModToList(modNumber[i]);
		}
	}
	
	public ModInfo getGroupMod(int index){
		return this.groupModList.get(index);
	}
	
	public String getGroupName() {
		return this.groupName;
	}
}
