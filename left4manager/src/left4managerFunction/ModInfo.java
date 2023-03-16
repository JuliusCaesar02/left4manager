package left4managerFunction;

import java.util.ArrayList;
import java.util.List;

public class ModInfo{
	private String name = new String();
	private String code = new String();
	private String author = new String();
	private String description = new String();
	private List<Tags> tags = new ArrayList<Tags>();
	private boolean enabled = false; 
	private short infoSource = 0;
	
	
	/***************
	 * 
	 * @param code
	 * @param enabled
	 */
	public ModInfo(String code, boolean enabled) {
		this.code = code;
		this.enabled = enabled;
	}
	
	/****************
	 * 
	 * @param name
	 * @param code
	 * @param author
	 * @param description
	 * @param enabled
	 */
	public ModInfo(String name, String code, String author, String description, List<Tags> tags, boolean enabled, byte infoSource) {
		this.name = name;
		this.code = code;
		this.author = author;
		this.description = description;
		this.tags = tags;
		this.enabled = enabled;
		this.infoSource = infoSource;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	/***
	 * 
	 * @param infoSource
	 * 0- None,
	 * 1- VPK,
	 * 2- Internet
	 */
	public void setInfoSource(short infoSource) {
		this.infoSource = infoSource;
	}
	public void setInfo(String name, String author, String description, List<Tags> tags) {
		this.name = name;
		this.author = author;
		this.description = description;
		this.tags = tags;
	}
	
	public String getCode() {
		return this.code;
	}
	public String getName() {
		return this.name;
	}
	public String getAuthor() {
		return this.author;
	}
	public String getDescription() {
		return this.description;
	}
	public boolean getEnabled() {
		return this.enabled;
	}
	public short getInfoSource() {
		return this.infoSource;
	}
	public Object[] getObject(){
		return new Object[] {this.name, this.code, this.author, this.enabled};
	}

	public List<Tags> getTags() {
		return tags;
	}

	public void setTags(List<Tags> tags) {
		this.tags = tags;
	}

}

