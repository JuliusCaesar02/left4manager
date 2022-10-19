package left4managerFunction;

public class ModInfo{
	private String name = new String();
	private String code = new String();
	private String author = new String();
	private String description = new String();
	private boolean enabled = false; 
	
	
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
	public ModInfo(String name, String code, String author, String description, boolean enabled) {
		this.name = name;
		this.code = code;
		this.author = author;
		this.description = description;
		this.enabled = enabled;
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
	public Object[] getObject(){
		return new Object[] {this.name, this.code, this.author, this.enabled};
	}
}

