package mjl;

import java.awt.BorderLayout;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.TextField;
import java.io.File;
import java.util.Comparator;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


/*
 * 
 * IDEA for future:
 * A program that shows folder structure irrelevant of their size. This would make the folder the widest that has the 
 * most sub-files, even if they're each 1 byte, while a folder of 1GB with one file would be small
 * --> And even better, mix the two. Make a slider where you can go from one to the other. This requires a formula
 * When a value x is 0, all folders are equal irrelevant of size. When x is 1, their portrayal is 100% based on their size.
 * Example: (trying to find formula)
 * A (990 bytes)
 * 		Ab (600 bytes)
 * 		Ac (390 bytes)
 * B (10 bytes)
 * 		Ba (2 bytes)
 * 		Bc (2 bytes)
 * 		Bd (1 bytes)
 * 		Be (1 bytes)
 *		Bf (1 bytes)
 *		Bg (1 bytes)
 *		Bh (1 bytes)
 *		Bi (1 bytes)
 *
 * For x=0 (equal size):
 * Each file makes up 10% of size. Folder A has 20%, Folder B has 80%
 * For x=1 (size)
 * 1 byte = 0.1% of space. Folder A has 99%, Folder B has 1%
 * 
 * Or are these 2 completely separate calculations? 
 * x=0 looks at number of files, ignores byte sizes. x=1 look at byte sizes, ignores numbers.
 * 
 * A 1-time compromise right between the 2 (this means you can't slide, it's discreet):
 * Expected solution: A has 60%, B has 40%. Ab (which had 60% or 10%) now has 35%, Ac has 24.5% (between 10% and 39%)
 * So, taking A as an example: it has "60% of size, 10% of quantity" --> just take the average!
 * THAT'S IT: slide between size-percentage and quantity-percentage!
 * 
 * Testing if this also works for 3 quarters:
 * A : between 99 and 20 : 79.25% --> and that's Ab+Ac, so correct!
 * Ab: between 10 and 60 : 47.5%
 * Ac: between 10 and 39 : 31.75%
 * B:  between 1 and 80  : 60.25% --> correct total, to the point!
 * Ba,Bc: between 0.2 and 10 : 7.55%
 * B.. : between 0.1 and 10: 7.525%
 * 
 * TODO: 
 * - make it so each java.Folderinfo knows its quantity-percentage too (it already knows its size-percentage)
 * 		(this means we have to count somewhere how many files there are in total (empty folders count too. I
 * 		mean "leaves" of the tree). Probably same place we count total size.)
 * - make a value (called x above) that's passed from folderscale to the Folderinfos
 * - The method that decides the absolute width: make it take the average based on how much x is, as its width
 * 		percentage. Then continue the calculation normally.
 * 
 * ------------
 * 
 * 
 * STATUS: 
 * It works. But it's really slow for large folders. 
 * Places where it's slow:
 *  - java.Folderinfo.sortSubfolders : therefore looking at other sorting algorithms. Doesn't work yet
 *   	-> comment out the contents of the sortSubfolder method, see how much faster it goes.
 *  	-> Make Quicksort work properly, and/or write in other methods (Quicksort currently has endless loops.
 *  		It permutes the subfolders one way, then back the other, then back, etc. Try it out e.g. on the dragons\dravere folder
 *  	-> Check em all by measuring time, to see which one is the fastest.
 * 
 *
 *
How much time they take for sortSubfolders:
Remember, depends on number of files, not size!

D:\Manuela's junke\art\3D\ManuGesichtsscan_von_Uni : instant
D:\Manuela's junke\art\Samples\sculptures\Messe-Statues : instant
D:\Manuela's junke\art : instant
D:\Manuela's junke\art\Samples\FANTASII\dragons : 1 sec
D:\Manuela's junke\art\3D\allsides : 3 secs
D:\Manuela's junke\art\Samples\books\Drizzt\Comic01 : 5 secs
D:\Manuela's junke\art\Samples\books\Drizzt\Comic03 : 9 secs
D:\Manuela's junke\art\Samples\books\Drizzt : instant
 * 
 *  - But Starting ScaleCanvas takes the most time.
 *    In ScaleCanvas: l.256 g.drawString(name, 5+poshor, 10+posvert); took 1-2 secs
 *    
 * - It seems the bars are drawn twice!
 * 
 * - BUT: the drawing happens it ScaleCanvas.java. It happens every time you resize the window,
 * and it's instant. So this can hardly be what carries most weight.)
 * 
 * 
 * 
 */

public class Folderscale {
    public static ScaleCanvas myCanvas;
    static JFrame F;
    static TextField fieldForFolder;
    static long starttime;
    static long stoptime;
    static int debugLevel = 1; //the higher, the more debugs
    //0 = No debugs
    //1 = basic framework
    //5 = absolutely freaking everything

    public static Folderinfo thisFolder;  //a tree-like construct with names and sizes of subfolders

    public static void main(String[] args) {
        Folderscale thisProgram = new Folderscale();
        thisProgram.operate();
    }

    private void operate() {
        F = new JFrame();
        F.setTitle("Folder scale");
        F.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        starttime = System.currentTimeMillis();

        F.setSize(400, 300);
        F.setLayout(new BorderLayout());

        // Oben das Label:
        //F.add("North",new JLabel("Farbenspiel"));
        // Im Zentrum die Farben:
        say("rein in ScaleCanvas", 5);
        myCanvas = new ScaleCanvas(thisFolder); //TODO: initialize thisFolder before this line and see if that makes it show up immediately
        //inputShape();

        // Oben die Kn�pfe:
        JPanel top = new JPanel(new BorderLayout());
        JPanel topleft = new JPanel();
        JPanel topright = new JPanel();

        topleft.setMinimumSize(new Dimension(800, 800));

        top.add(topleft, BorderLayout.CENTER);
        top.add(topright, BorderLayout.EAST);

        GridBagConstraints cst = new GridBagConstraints();

        JPanel folderPanel = new JPanel();
        cst.gridx = 0;
        cst.gridy = 0;
        topleft.add(folderPanel, cst);
        cst.gridx = 0;
        cst.gridy = 1;


        JLabel folderLabel = new JLabel("Enter Folder");
        folderPanel.add(folderLabel);

        fieldForFolder = new TextField("D:\\", 30);
        cst.fill = GridBagConstraints.VERTICAL;
        cst.ipady = 30;
        folderPanel.add(fieldForFolder, cst);

        Button btn = new Button("Draw");
        topright.add(btn);

        btn.addActionListener(e -> processInput());

        folderPanel.setMaximumSize(F.getMaximumSize());

        F.add("North", top);

        F.setVisible(true);
    }


    /*
     * Similar to folderSize, but does a bit more
     * Fill the info of all of folder's subfolders into Folderinfos
     */
    public long analyzeFolder(Folderinfo folder) {
        if (folder == null) {
            System.err.println("folder is null!");
            return 0;
        } else if (folder.path == null) {
            System.err.println("folder.path is null!");
            return 0;
        } else if (folder.path.listFiles() == null) {
            System.err.println("folder.path.listFiles() is null for " + folder.path.toString());
            return 0;
        }
        if (folder.path.listFiles() == null) { //TODO: no output in GUI, so if not debugging, won't know it was skipped. Check if the problem is that you don't have admin access.
            System.err.println("Something doesn't work with this folder, so skipping it: " + folder.path);
            return 0;
        }

        //thisFolder = new java.Folderinfo(folder);
        say("java.Folderscale/analyzeFolder: " + folder.getName(), 2);

        Folderinfo newguy; // = new java.Folderinfo(folder.path.getAbsoluteFile());

        long length = 0;
        long additionalLength;

        File[] filesInFolder = folder.path.listFiles();
        for (File file : filesInFolder) {
            if (file.getName().equals("I AM YOUR GOD") || file.getName().contains("System Volume Information")
                    || file.getName().equals("RtBackup") || file.getName().contains("$RECYCLE.BIN") || file.getName().contains("$Recycle.Bin")
                    || file.getName().equals("Windows Defender Advanced Threat Protection")
                    || file.getName().contains("Lenovo\\Vantage")
                    || file.getName().contains("ProgramData\\Microsoft")
                    || file.getName().contains("All Users\\Microsoft")
                    || file.getName().contains("Windows\\System")
                    || file.getName().startsWith(".")) continue;


            additionalLength = 0;
            newguy = new Folderinfo(file.getAbsoluteFile(), debugLevel);

            if (!file.isDirectory()) {
                say("This is a file", 4);
                say("Length of " + folder.getName() + " was previously " + length + ", now I add " + newguy.getName() + " via analyzeFolder"
                        + ", whose length is " + file.length(), 7);
                length += file.length();
                newguy.setSize(file.length());
                folder.addSubfolder(newguy);
            } else {
                //if(folderSize(file)>=minsize){ //potentially silly, cause now we measure the size twice
                say("This is a folder", 4);
                //say("Length of " + folder.getName() + " was previously " + length + ", now I add " + newguy.getName() + " via analyzeFolder", 5);
                additionalLength = analyzeFolder(newguy); //inside, subfolders are added
                length += additionalLength;
                //say("Done analyzing him. Length is now " + length, 5);
                newguy.setSize(additionalLength);
                folder.addSubfolder(newguy);
            }

        }
        //say("end of java.Folderscale/analyzeFolder. Returning length=" + length + " to folder " + folder.getName(), 3);
        say("==================Sorting subfolders of " + folder.getName() + ". Before sorting, they are: ", 4);
//	    say(folder.getSubs().toString(), 4);

        starttime = System.currentTimeMillis();

        folder.getSubs().sort(Comparator.comparing(Folderinfo::getSize).reversed());

        stoptime = System.currentTimeMillis();

        say("After sorting, they are: ", 4);
//	    say(folder.getSubs().toString(), 4);

        say("Time taken to sort " + folder.getName() + ": " + (stoptime - starttime) + ".", 4);

        return length;
    }

    //To find what the paths are: File.listRoots();
    public void processInput() {
        say("Getting the input", 5);
        String input = getTextFromGuiFolderfield();
        say("Input name: " + input);
        File folder = new File(input);
        if (!folder.exists()) {
            say(input + " : This file doesn't exist");
            //TODO: proper error message
        } else {
            long totalsize;

            if (folder.isDirectory()) {
                thisFolder = new Folderinfo(folder, debugLevel);

                stoptime = System.currentTimeMillis();
                System.out.println("After " + (stoptime - starttime) + ", beginning to analyze Folder");
                starttime = System.currentTimeMillis();

                thisFolder.setSize(analyzeFolder(thisFolder)); //inside, subfolders are added

                stoptime = System.currentTimeMillis();
                System.out.println("Done. Analyzing took " + (stoptime - starttime) + ".");
                starttime = System.currentTimeMillis();


            } else { //if it's a single file, not a directory
                totalsize = folder.length();
                output("Size: " + totalsize);
            }

            //System.out.println("\n\n\n");
            //thisFolder.printThis("");

            stoptime = System.currentTimeMillis();
            System.out.println("Done. Analyzing took " + (stoptime - starttime) + ".");
            starttime = System.currentTimeMillis();

            //System.out.println("Done finding sizes. Next: Percentages");
            thisFolder.findTotalPercentages(thisFolder.getSize());
            System.out.println("Done doing percentages");

            stoptime = System.currentTimeMillis();
            System.out.println("Done with percentages. It took " + (stoptime - starttime) + ".");
            starttime = System.currentTimeMillis();

            drawTheResult();
        }
        //}
    }

    protected void drawTheResult() {
        myCanvas.thisFolder = thisFolder; //TODO: I think this is redundant. The canvas gets the folder when it's initialized.
        F.add("Center", myCanvas);

        stoptime = System.currentTimeMillis();
        System.out.println("Done drawing. That took " + (stoptime - starttime) + ".");
        starttime = System.currentTimeMillis();
    }

    protected String getTextFromGuiFolderfield() {
        return fieldForFolder.getText();
    }

    /*
     * Later I'll change this to a label instead of command line
     */
    public static void output(String output) {
        System.out.println(output);
    }


    public void say(String output) { //for debugging
        say(output, 0);
    }

    public void say(String output, int minDebugLevToPrint) { //for debugging
        if (debugLevel >= minDebugLevToPrint)
            System.out.println(output);
    }
}
