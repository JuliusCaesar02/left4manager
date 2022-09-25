package left4managerFunction;

import java.util.ArrayList;
import java.util.List;

public class ModGroup {
	private List<ModInfo> groupModList = new ArrayList<ModInfo>();
	private String groupName = new String();
	
	ExtractModList extractModList = new ExtractModList();
	List<ModInfo> modList = extractModList.getModList();
	
	public ModGroup(String groupName) {
		this.groupName = groupName;
	}
	
	public void addModToList(int listNumber, int modNumber) {
		this.groupModList.add(modList.get(modNumber));
	}
	
	public ModInfo getGroupMod(int index){
		return this.groupModList.get(index);
	}
	
	public String getGroupName() {
		return this.groupName;
	}
}
