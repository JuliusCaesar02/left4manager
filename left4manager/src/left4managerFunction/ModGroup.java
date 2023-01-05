package left4managerFunction;

import java.util.ArrayList;
import java.util.List;

public class ModGroup {
	private List<ModInfo> groupModList = new ArrayList<ModInfo>();
	private String groupName = new String();
	
	public ModGroup(String groupName, int modNumber, List<ModInfo> modList) {
		this.groupName = groupName;
		addModToList(modNumber, modList);
	}
	
	public ModGroup(String groupName, int[] modNumber, List<ModInfo> modList) {
		this.groupName = groupName;
		addModToList(modNumber, modList);
	}
	
	public void addModToList(int modNumber, List<ModInfo> modList) {
		this.groupModList.add(modList.get(modNumber));
	}
	
	public void addModToList(int[] modNumber, List<ModInfo> modList) {
		for(int i = 0; i < modNumber.length; i++) {
			addModToList(modNumber[i], modList);
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
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public List<ModInfo> getGroupModList() {
		return this.groupModList;
	}
}
