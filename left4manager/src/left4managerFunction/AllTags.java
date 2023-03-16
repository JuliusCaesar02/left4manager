package left4managerFunction;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class AllTags {
	private List<Tags> allTags = new ArrayList<Tags>();
	private String allTagsPath = new String();

	public AllTags(Config config) {
		this.allTagsPath = config.getL4managerJSONDir() +File.separator +"allTags.json";
		try {
			extractAllTags();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<Tags> getAllTags() {
		return allTags;
	}
	
	public void setAllTags(List<Tags> allTags) {
		this.allTags = allTags;
	}

	public void extractAllTags() throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
    	FileReader fr = new FileReader(allTagsPath);
        Type allTagsListType = new TypeToken<List<Tags>>(){}.getType();
        allTags = gson.fromJson(fr, allTagsListType);
        System.out.println(allTags.get(0));
        fr.close();
	}
}
