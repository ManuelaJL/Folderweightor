package mjl;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

class ScaleCanvas extends Canvas
{   
	//public String shapey;
	//public String coley;
	public Folderinfo thisFolder;
	private Color[] color;
	int barwidth = 18;
	public boolean allFiles = true;
	private int fontsize = 15;
	private int currentColor;
	int minlength = 10; //How many pixels I want a folder to have minimum for me to still display it. This is like 1 letter.
	int breite;
	int hoehe;
	int y_offset = 15;
	int x_offset = 5;
	
	
	public ScaleCanvas(Folderinfo input){
		thisFolder = input;
	}
	
	private void setColors(){
		color = new Color[7]; //rgb
		color[0] = new Color( Integer.parseInt("FF3535", 16)); //red
		color[1] = new Color( Integer.parseInt("FF8111", 16)); //orange
		color[2] = new Color( Integer.parseInt("DDDD00", 16)); //yellow
		color[3] = new Color( Integer.parseInt("00FF00", 16)); //green
		color[4] = new Color( Integer.parseInt("00FFFF", 16)); //lightblue
		color[5] = new Color( Integer.parseInt("3535FF", 16)); //blue
		color[6] = new Color( Integer.parseInt("FF00FF", 16)); //purple
		//color[7] = new Color( Integer.parseInt("", 16));
		
	}
	
	public void paint (Graphics g)
    {   
      if(thisFolder!=null){
		System.out.println("Starting java.ScaleCanvas");
		setColors();
		this.setBackground(Color.white);
		

		Font myfont = new Font("Arial Narrow", Font.PLAIN, fontsize);
		g.setFont(myfont);
		
		currentColor = 0; //see array above. Switch with nextColor(currentColor);
		
		say("This is the folder I'm analyzing: " + thisFolder.toString());
		
		
		//g.setColor(color[1]);
		//g.setColor(Color.black);
		say("Position 1 in java.ScaleCanvas");
		
	/*
		Color color;
		if(coley!=null){
			color = new Color( Integer.parseInt( coley, 16 ) );
			g.setColor(color);
		}
		*/
		
		Dimension d=getSize();
        breite=d.width;
        //hoehe=d.height;

        
        drawSubFolders(thisFolder,0,0,g);
        
      }//if thisfolder!=null
    }
	
	private void say(String string) {
		System.out.println(string);
	}

	/*
	 * Finds how much space the String text uses up, using fontsize.
	 */
	public int findStringWidth(String text){
		return text.length()*fontsize/2+2;
	}
	
	public static String readableFileSize(long size) {
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
	
	/*
	 * Draw this bar and the bars of its subfolders
	 * 
	 * poshor		horizontal position we start from (= parent folder's horizontal position)
	 * posvert		vertical position we start from (= how many subfolders down we are)
	 * widthperc	how many percent of the total width this folder makes up (absolute width varies with window)
	 * name			Name of subfolder we're working on
	 * g			Graphics component, same as we used above.
	 */
	public void drawSubFolders(Folderinfo currFolder, int poshor, int posvert, Graphics g){

		say("drawSubFolders: Folder=" + currFolder.getName() + ", poshor=" + poshor
				+ ", posvert=" + posvert);
		
		String name = currFolder.getName() + " (" + readableFileSize(currFolder.getSize()) + ")";
		int widthperc = (int)currFolder.getMainFrac();
		int absWidth = widthperc*breite/100; //actual width in pixels
		say("And the absWidth ends up being: " + absWidth);
		
		if(absWidth<minlength) return;
		
		/////Draw this bar
        g.setColor(color[currentColor]);
        //say("Draw a bar of width=" + absWidth + ", color=" + colorhex[currentColor]);
        name = cropString(name,absWidth);
        g.fillRect(poshor, posvert, absWidth, barwidth);
        g.setColor(Color.black);
		g.drawString(name, x_offset +poshor, y_offset +posvert);
        ////done drawing this bar

        //int temphor = poshor;
        int nextwidth;
        nextColor();
        ArrayList<Folderinfo> listOfSubfolders = currFolder.getSubs();
        int numSubfolders = currFolder.getSubs().size();
        for(int i=0; i<numSubfolders; i++){
        	//say("(1) Looking at subfolders of " + thisFolder.getName());
        //	if(!currFolder.path.isFile()){ //If it's a folder, which can actually have subfolders
        //      if(numSubfolders>0){ //and it has subfolders //TODO: how is this line necessary?
                //say(currFolder.getSubs().get(i).getName());
        	    //"nextwidth" is currently redundant. It's also calculated inside function
        	    nextwidth = (int)currFolder.getSubs().get(i).getMainFrac()*breite/100;
        	    if(nextwidth<1) break;
        	    say("(3) The width of " + currFolder.getName() + " will be " + nextwidth);
        	    //TODO: They're sorted by size, right? If I'm zero,
        	    //don't bother to calculate my smaller siblings either. Return to parent.
        	    drawSubFolders(currFolder.getSubs().get(i),poshor,posvert+barwidth,g);
        	    poshor += nextwidth;
        	    nextColor();
         //     }
         //	}
        	//else{ say("It's a file, or just otherwise has no subfolders"); }
        }
        
        //say("Done looking at the Folder " + thisFolder.getName() + " and all its subfolders");
        
	}

	/*
	 * Make the text fit into the bar of size 'size'
	 * (This changes if you resize the window)
	 */
	public String cropString(String text, int size){ //
		//say("function cropstring. Crop '" + text + "' to " + size);
		int allowedChars = size/(fontsize/2);
		//say("allowedchars: " + allowedChars);
		if(allowedChars==0) return "";
		else if (text.length()<allowedChars) return text; 
		else return text.substring(0, allowedChars-1); 
	}

	/*
	 * Change the current color, to draw the next bar in.
	 */
	public void nextColor(){
		if(currentColor>=color.length-1) currentColor = 0;
		else currentColor++;
	}
	
}

