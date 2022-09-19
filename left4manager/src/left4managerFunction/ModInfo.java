package left4managerFunction;

public class ModInfo{
	private String name = new String();
	private String code = new String();
	private String author = new String();
	private boolean enabled = false; 
	
	public ModInfo(String code, boolean enabled) {
		this.code = code;
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
	public boolean getEnabled() {
		return this.enabled;
	}
	public Object[] getObject(){
		return new Object[] {this.name, this.code, this.author, this.enabled};
	}
}

