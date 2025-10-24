package mjl;

import java.io.File;
import java.util.ArrayList;

/*
 * After you press "Draw", resize the window so the image appears.
 */

public class Folderinfo {
	public File path;
	private final String name;
	private long size = 0;
	private final ArrayList<Folderinfo> subfolder;
	public ArrayList<Folderinfo> biggest;
	float mainFrac = 999; //how much of topmost root folder it is
	int debugLevel;
	
	public Folderinfo(File givenPath, int debugLevel){
		this.debugLevel = debugLevel;
		if(givenPath==null){
			throw new IllegalArgumentException("Folderinfo can't have path null");
		}
		else{
			say("Making new java.Folderinfo, path=" + givenPath, 2);
			path = givenPath;
			name = path.getName();
		}
		subfolder = new ArrayList<>();
		biggest = new ArrayList<>();
	}
	

	public void setSize(long givenSize){
		size = givenSize;
	}

	public void setMainFrac(float frac){
		mainFrac = frac;
	}
	
	public float getMainFrac(){
		return mainFrac;
	}

	public String getName(){
		return name;
	}
	
	public long getSize(){
		return size;
	}

	public ArrayList<Folderinfo> getSubs(){
		return subfolder;
	}

	public String toString(){
		return "[Name=" + name + "; Size=" + size + "; Path=" + path + "]";
	}

	public void addSubfolder(Folderinfo sub){
		//say("java.Folderinfo.addSubfolder: Parameter is..." + sub.toString(), 5);
		subfolder.add(sub);
	}

	/*
	 * Find out what percentage of the total image width this folder takes up,
	 * i.e. how wide it should be drawn.
	 */
	public void findTotalPercentages(long sizeOfTopmostFolder){
		//say("find Total Percentages of the folder " + name);
		setMainFrac((float)((this.size*100.0)/sizeOfTopmostFolder)); //
		if(this.size>0){
			for (Folderinfo folderinfo : subfolder) {
				folderinfo.findTotalPercentages(sizeOfTopmostFolder);
			}
		}
	}


	public void say(String output, int minDebugLevToPrint){ //for debugging
		if(debugLevel >= minDebugLevToPrint)
			System.out.println(output);
	}


}
