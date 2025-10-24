package mjl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/*
 * After you press "Draw", resize the window so the image appears.
 */

public class Folderinfo {
	public File path;
	private String name;
	private long size = 0;
	private ArrayList<Folderinfo> subfolder;
	public ArrayList<Folderinfo> biggest;
	private float percentage = 0; //Show subfolders which contain this fraction of the total size
	private float small = 5; //don't bother with subfolders smaller than this many percent of total size
//	float supFrac = 999; //which fraction of above folder this makes. -1 if no above folders
	float mainFrac = 999; //how much of root folder it is
	static int debugLevel;
	
	public Folderinfo(File givenPath, int debugLevel){
		this.debugLevel = debugLevel;
		if(givenPath==null){
			throw new IllegalArgumentException("Folderinfo can't have path null");
		}
		else{
			say("Making new java.Folderinfo, path=" + givenPath.toString(), 2);
			path = givenPath;
			name = path.getName();
		}
		subfolder = new ArrayList<Folderinfo>();
		biggest = new ArrayList<Folderinfo>();
	}
	

	public void setSize(long givenSize){
		size = givenSize;
	}
	
//	public void setFrac(float frac){
//		supFrac = frac;
//	}
	
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
	
	public Folderinfo getSubfolder(String givenname){
		//TODO: find subfolder with that name
		return null;
	}
	
	public ArrayList<Folderinfo> getSubs(){
		return subfolder;
	}

	public String toString(){
		return "[Name=" + name + "; Size=" + size + "; Path=" + path + "]";
	}

	public boolean setSubSize(File path, long givenSize){
		for(Folderinfo folder: subfolder){
			if(folder.path==path){
				folder.setSize(givenSize);
				return true; //success
			}
		}
		
		return false; //fail
	}

	public void addSubfolder(Folderinfo sub){
		//say("java.Folderinfo.addSubfolder: Parameter is..." + sub.toString(), 5);
		subfolder.add(sub);
	}
	
	/*
	 * Combine the smaller folders into one java.Folderinfo called "others".
	 * I think I'm not using this at the moment.
	 */
	public void combineRest(int index){
		//TODO: If this function is obsolete as stated below, remove it sometime.
		say(">>>>>>This function is obsolete, why's it called? Function CombineRest", 0);
		long remainingSize = 0;
		while(biggest.size()>=index){
			//say("Combinerest: while " + biggest.size() + ">=" + index, 5);
			remainingSize += biggest.get(index-1).size;
			biggest.remove(index-1);
		}
		
		Folderinfo extra = new Folderinfo(null, debugLevel);
		extra.setSize(remainingSize);
		biggest.add(extra);
			
	}
	
	public void countMySize(){
		//say("Function Count my size", 4);
		int newsize = 0;
		for(int i=0; i<subfolder.size(); i++){
			newsize += subfolder.get(i).size;
		}
		
		if(newsize!=0) size=newsize;
	
	}
	
	public void getLargestFolders(){
		//TODO: redundant?
		say(">>>>This is redundant, why do it? getLargestFolders von " + name + ": subfolder.size=" + subfolder.size(), 0);
		if(size==0) countMySize();
		if(subfolder.size()==0 || size==0){
			//say("getLargestFolders: sinnlos weil keine Subfolder", 5);
			
		}
		else{
		  sortSubfolders();
		  long addedSize = 0;
		  int i = 0; //index
		  biggest.clear();
		  while(addedSize<size*(percentage/100) && i<=subfolder.size() 
			  	  && subfolder.get(i).size>size*(small/100)){
			  addedSize += subfolder.get(i).size;
			  biggest.add(subfolder.get(i));
			  i++;
		  }
		if(i>0) combineRest(i);
		}
	}
	
	
	/* These two are leftover from when subfolder was an Array=================*/
	
	/*
	 * This method is the middle-man, so I can choose which sorting algorithm to use.
	 * Alternative algorithms to chose from are commented out.
	 */
	public void sortSubfolders(){
		bubbleSortSubfolders();
		//quickSortSubFolders();
		//http://en.wikipedia.org/wiki/Merge_sort
		//http://en.wikipedia.org/wiki/Heapsort
		//http://en.wikipedia.org/wiki/Introsort
		//http://en.wikipedia.org/wiki/Timsort
		//http://en.wikipedia.org/wiki/Smoothsort
	}
	
	public void quickSortSubFolders(){
		//WARNING! This currently contains endless loops.
		int leftmost = 0;
		int rightmost = subfolder.size()-1; //Is the -1 correct?
		quickSort(leftmost, rightmost);
	}
	
	/*
	 * Part of quickSortSubFolders
	 */
	private int partitionSubfolders(int leftmost, int rightmost, int pivotIndex){
		long pivotValue = subfolder.get(pivotIndex).getSize();
		swap(pivotIndex,rightmost); //Move pivot to end
		//long storeIndex = subfolder.get(leftmost).getSize();
		int storeIndex = leftmost;
		for(int i=leftmost; i<rightmost; i++){
			if(subfolder.get(i).getSize()<pivotValue){
				say(subfolder.get(i).getSize() + " < " + pivotValue, 5);
				swap(i,storeIndex);
				storeIndex = storeIndex+1;
			}
		}
		swap(storeIndex,rightmost); //Move pivot to its final place.
		return storeIndex;
	}
	
	/*
	 * Part of quickSortSubFolders
	 */
	private void quickSort(int leftmost, int rightmost){
		if(leftmost<rightmost){
			int pivotIndex = subfolder.size()/2;
			int pivotNewIndex = partitionSubfolders(leftmost, rightmost, pivotIndex);
			say("PivotIndex: " + pivotIndex + ", leftmost: " + leftmost + ", rightmost: " + rightmost, 5);
			
			//Recursively sort elements smaller than pivot
			say("Recursively quicksorting from " + leftmost + " to " + (pivotNewIndex-1), 5);
			quickSort(leftmost, pivotNewIndex-1);
			
			//Recursively sort elements >= pivot
			say("Recursively quicksorting from " + (pivotNewIndex+1) + " to " + rightmost, 5);
			quickSort(pivotNewIndex+1,rightmost);
			
		}
	}
	
	public void bubbleSortSubfolders(){
		say("Function " + name + ".BubbleSortSubfolders", 5);
		boolean tausch = true;


		while(tausch){ //TODO: use arrays.sort
			tausch = false;
			for(int i=0; i < this.subfolder.size()-1; i++){

				//say("i=" + i + ", Comparing " + subfolder.get(i).getName() + ", size=" + subfolder.get(i).size
				//		+ " with " + subfolder.get(i+1).getName() + ", size=" + subfolder.get(i+1).size, 5);
				if(this.subfolder.get(i).size<this.subfolder.get(i+1).size){
					//say(subfolder.get(i).getName() + " is smaller than " + subfolder.get(i+1).getName() + ", so swap them", 5);
					swap(i,i+1);
					tausch = true;
				}//if
				else{
					//say(subfolder.get(i).getName() + " doesn't need to swap with " + subfolder.get(i+1).getName(), 5);
				}
			}//for
		}//while(tausch)
		//say("Ja, bin aus While raus. " + name + " ist fertig sortiert.", 5);
		//say("Jetzt ist die Reihenfolge: " + getSubs(), 5);
	}//sortSubfolders()
	
	
	/*
	 * Only used by the above sortSubfolders() right now.
	 */
	public void swap(int a, int b){
		/*
		say("1 Before swap, subfolders are: " + subfolder.toString(), 5);
		say("I want to swap [" + a + "] and [" + b + "]", 5);
		say("Namely: " + subfolder.get(a).getName() + " and " + subfolder.get(b).getName(), 5);
		*/
		
		Collections.swap(subfolder,a,b);
		
		/*
		subfolder.add(b,subfolder.get(a));
		say("2 after adding, now they are: " + subfolder.toString(), 5);
		subfolder.remove(a);
		say("3 after removing, now they are: " + subfolder.toString(), 5);
		subfolder.add(a,subfolder.get(b));
		say("4 after adding, now they are: " + subfolder.toString(), 5);
		subfolder.remove(b+1); //Need the +1 because now the index is one more due to the previous add
		say("5 after removing, now they are: " + subfolder.toString(), 5);
		*/
		
	}
	
/*
	public void findPercentages(){
	  //say("findPercentages of the folder " + name);
      //TODO: What's the difference between this and the below? Is one obsolete?
	  if(size>0){
		for(int i=0; i<subfolder.size(); i++){
			subfolder.get(i).setFrac(subfolder.get(i).getSize()/size);
			//say("Size of subfolder " + subfolder.get(i).getName() + ": " + subfolder.get(i).getSize(), 5);
			//say("Percentage of subfolder " + subfolder.get(i).getName() + " is " +  subfolder.get(i).getSize()/size, 5);
		}
	  }
	}
*/
	
	/*
	 * Find out what percentage of the total image width this folder takes up,
	 * i.e. how wide it should be drawn.
	 */
	public void findTotalPercentages(long sizeOfTopmostFolder){
		//say("find Total Percentages of the folder " + name);
		//TODO: What's the difference between this and the above? Is one obsolete? This one is used in any case
		setMainFrac((float)((this.size*100.0)/sizeOfTopmostFolder)); //
		if(this.size>0){
			for(int i=0; i<subfolder.size(); i++){
				subfolder.get(i).findTotalPercentages(sizeOfTopmostFolder);
			}
		}
	}
	
	public float findSmallestMainFrac(boolean allFiles){
		say(">>>>(What's this for?)Find smallest mainfrac. Mainfrac is currently: " + mainFrac, 4);
		float minFrac = mainFrac; 
		  for(Folderinfo sub : getSubs()){
			  //say("Now looking at subfolder " + sub.getName() + ", whose mainfrac is " + sub.getMainFrac(), 5);
			  if(sub.getMainFrac()<minFrac){
				  minFrac = sub.getMainFrac();
				  //say(minFrac + " is the new minimum", 5);
			  }
		  }//for
		  //say(minFrac + " is the smallest one in file " + getName(), 5);
		return minFrac;
	}
	
	/*
	 * Prints the info into the command line, for debugging.
	 */
	public void printThis(String prefix){
		System.out.println(prefix + name + " (" + size + ")");
		for(Folderinfo folder: subfolder){
			//System.out.println("+");
			folder.printThis(prefix + "  ");
		}
	}
	
	public void printBiggest(String prefix){
		sortSubfolders();
		//String newPrefix = prefix;
		System.out.println(prefix + name + " (" + size + ")");
		for(Folderinfo folder: biggest){
			//System.out.println("+");
			folder.printThis(prefix + "  ");
		}
	}
	
	/*
	 * For debugging
	 */
	public static void say(String output){ //for debugging
		say(output, 0);
	}

	public static void say(String output, int minDebugLevToPrint){ //for debugging
		if(debugLevel >= minDebugLevToPrint)
			System.out.println(output);
	}
	
	/*
	 * For real messages to the user.
	 */
	public void message(String output){
		System.out.println(output);
	}

}
