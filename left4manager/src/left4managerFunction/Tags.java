package left4managerFunction;

import java.util.ArrayList;
import java.util.List;

public class Tags {
	private String primaryTag = new String();
	private List<String> secondaryTag = new ArrayList<String>();
	
	public Tags(String primaryTag) {
		this.primaryTag = primaryTag;
	}
	
	public Tags(String primaryTag, List<String> secondaryTag) {
		this.primaryTag = primaryTag;
		this.secondaryTag = secondaryTag;
	}

	public String getPrimaryTag() {
		return primaryTag;
	}

	public void setPrimaryTag(String primaryTag) {
		this.primaryTag = primaryTag;
	}

	public List<String> getSecondaryTag() {
		return secondaryTag;
	}

	public void setSecondaryTag(List<String> secondaryTag) {
		this.secondaryTag = secondaryTag;
	}
	
	public void addSecondaryTag(String tag) {
		this.secondaryTag.add(tag);
	}
}

