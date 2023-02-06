package left4managerFunction;

import java.util.ArrayList;
import java.util.List;

public class ModGroup {
	private List<String> groupModList = new ArrayList<String>();
	private String groupName = new String();
	
	public ModGroup() {
	}
	
	public ModGroup(String groupName) {
		this.groupName = groupName;
	}
	
	public ModGroup(String groupName, int modNumber, List<ModInfo> modList) {
		this.groupName = groupName;
		add(modNumber, modList);
	}
	
	public ModGroup(String groupName, int[] modNumber, List<ModInfo> modList) {
		this.groupName = groupName;
		add(modNumber, modList);
	}
	
	public void add(int index, List<ModInfo> modList) {
		this.groupModList.add(modList.get(index).getCode());
	}
	
	public void add(String code) {
		this.groupModList.add(code);
	}
	
	public void add(int[] index, List<ModInfo> modList) {
		for(int i = 0; i < index.length; i++) {
			add(index[i], modList);
		}
	}
	
	public void remove(int index) {
		this.groupModList.remove(index);
	}
	public void remove(String code) {
		for(int i = 0; i < this.getSize(); i++) {
			if(this.getGroupMod(i).equals(code)) {
				this.remove(i);		
			}
		}
	}
	
	public String getGroupMod(int index){
		return this.groupModList.get(index);
	}
	
	public String getGroupName() {
		return this.groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public List<String> getGroupModList() {
		return this.groupModList;
	}
	
	public int getSize() {
		return this.groupModList.size();
	}
}
