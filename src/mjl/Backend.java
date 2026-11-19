package mjl;

import java.io.File;
import java.util.Comparator;

public class Backend {
    static long starttime;
    static long stoptime;
    static int debugLevel = 1; //the higher, the more debugs
    //0 = No debugs
    //1 = basic framework
    //5 = absolutely freaking everything

    public void say(String output) { //for debugging
        say(output, 0);
    }

    public void say(String output, int minDebugLevToPrint) { //for debugging
        if (debugLevel >= minDebugLevToPrint)
            System.out.println(output);
    }
    protected Folderinfo processInputInBackend(String input) {  //move this to backend-class
        File folder = new File(input);
        if (!folder.exists()) {
            say(input + " : This file doesn't exist");
            //TODO: candidate for error message in GUI
            return null;        //TODO: probably better to not return null
        }
        long totalsize;

        Folderinfo thisFolder = null;

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
        return thisFolder;
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

    /*
     * Later I'll change this to a label instead of command line
     */
    public static void output(String output) {
        System.out.println(output);
    }
}
