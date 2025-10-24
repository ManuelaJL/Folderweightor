package mjl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.FolderscaleDummyForTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FolderscaleTest {
    static String TEST_DIRECTORY = ".\\\\src\\\\test";
    static String CREATED_TEST_FOLDER = "folderToAnalyze";
    Folderscale testedEntity;

    @BeforeEach
    void setUp() {
        testedEntity = new FolderscaleDummyForTest();
        createTestfolders();
    }

    @AfterEach
    void tearDown() {
        deleteTestfolders();
    }

    public static void main(String[] args) { //just useful for testing.
        createTestfolders();
    }

    public static void createTestfolders() {
        File baseFolder = new File(TEST_DIRECTORY + "\\\\" + CREATED_TEST_FOLDER);
        baseFolder.mkdir();
        createSubfileWith(baseFolder.getPath(), "BigFileA", 50);
        File folderB = new File(TEST_DIRECTORY + "\\\\" + CREATED_TEST_FOLDER + "\\\\FolderB");
        folderB.mkdir();
        File folderC = new File(TEST_DIRECTORY + "\\\\" + CREATED_TEST_FOLDER + "\\\\EmptyFolderC");
        folderC.mkdir();
        createSubfileWith(baseFolder.getPath(), "TooSmallFileD", 1);
        File folderBA = new File(TEST_DIRECTORY + "\\\\" + CREATED_TEST_FOLDER + "\\\\FolderB\\\\FolderBA");
        folderBA.mkdir();
        createSubfileWith(folderBA.getPath(), "FileBAA", 15);
        createSubfileWith(folderBA.getPath(), "FileBAB", 10);
        File folderBB = new File(TEST_DIRECTORY + "\\\\" + CREATED_TEST_FOLDER + "\\\\FolderB\\\\FolderBB");
        folderBB.mkdir();
        createSubfileWith(folderBB.getPath(), "FileBBA", 20);
        File folderBC = new File(TEST_DIRECTORY + "\\\\" + CREATED_TEST_FOLDER + "\\\\FolderB\\\\FolderBC");
        folderBC.mkdir();
        createSubfileWith(folderBC.getPath(), "FileBCA", 1);
        createSubfileWith(folderBC.getPath(), "FileBCB", 1);
        createSubfileWith(folderBC.getPath(), "FileBCC", 1);
        createSubfileWith(folderBC.getPath(), "FileBCD", 1);

    }


    public static void createSubfileWith(String location, String name, int size) {
        File newfile = new File(location, name);
        String content = "a".repeat(size);
        try {
            Files.write(Paths.get(newfile.getPath()), Arrays.asList(content));
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }


    public static void deleteTestfolders() {
        File file = new File(TEST_DIRECTORY + "\\\\" + CREATED_TEST_FOLDER);
        deleteRecursively(file);
    }

    public static void deleteRecursively(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }


    @Test
    void analyzeFolderTest_HappyCase() {
        File baseFolder = new File(TEST_DIRECTORY + "\\\\" + CREATED_TEST_FOLDER);
        Folderinfo thisFolder = new Folderinfo(baseFolder, 0);

        long resultSize = testedEntity.analyzeFolder(thisFolder);

        assertEquals(118, resultSize);
        ArrayList<Folderinfo> basefolderSubfolders = thisFolder.getSubs();
        assertEquals(4, basefolderSubfolders.size());
        for (Folderinfo fold : basefolderSubfolders) {
            ArrayList<Folderinfo> subfolder1_subs = fold.getSubs();
            switch (fold.getName()) {
                case "BigFileA":
                    assertEquals(0, subfolder1_subs.size());
                    break;
                case "EmptyFolderC":
                    assertEquals(0, subfolder1_subs.size());
                    break;
                case "TooSmallFileD":
                    assertEquals(0, subfolder1_subs.size());
                    break;

                case "FolderB":
                    assertEquals(3, subfolder1_subs.size());

                    for (Folderinfo sub : subfolder1_subs) {
                        ArrayList<Folderinfo> subfolder2_subs = sub.getSubs();
                        switch (sub.getName()) {
                            case "FolderBA":
                                assertEquals(2, subfolder2_subs.size()); // FileBAA, FileBAB
                                for (Folderinfo subsub : subfolder2_subs) {
                                    ArrayList<Folderinfo> subfolder3_subs = subsub.getSubs();
                                    switch (subsub.getName()) {
                                        case "FileBAA":
                                            assertEquals(0, subfolder3_subs.size());
                                            break;
                                        case "FileBAB":
                                            assertEquals(0, subfolder3_subs.size());
                                            break;
                                        default:
                                            fail("Unexpected subfolder in FolderBA: " + subsub.getName());
                                    }
                                }
                                break;

                            case "FolderBB":
                                assertEquals(1, subfolder2_subs.size()); // FileBBA
                                Folderinfo expectedFileBBA = subfolder2_subs.get(0);
                                assertEquals(0, expectedFileBBA.getSubs().size());
                                assertEquals("FileBBA", expectedFileBBA.getName());
                                break;

                            case "FolderBC":
                                assertEquals(4, subfolder2_subs.size()); // FileBCA, FileBCB, FileBCC, FileBCD
                                for (Folderinfo subsub : subfolder2_subs) {
                                    assertEquals(0, subsub.getSubs().size());
                                    switch (subsub.getName()) {
                                        case "FileBCA":
                                            break;
                                        case "FileBCB":
                                            break;
                                        case "FileBCC":
                                            break;
                                        case "FileBCD":
                                            break;
                                        default:
                                            fail("Unexpected subfolder in FolderBC: " + subsub.getName());
                                    }
                                }
                                break;

                            default:
                                fail("Unexpected subfolder in FolderB: " + sub.getName());
                        }
                    }
                    break;
                default:
                    fail("Unexpected subfolder in base folder: " + fold.getName());
            }
        }
    }

    @Test
    void analyzeFolderTest_EmptyFolder() {
        File baseFolder = new File(TEST_DIRECTORY + "\\\\" + CREATED_TEST_FOLDER + "\\\\EmptyFolderC");
        Folderinfo thisFolder = new Folderinfo(baseFolder, 0);

        long resultSize = testedEntity.analyzeFolder(thisFolder);

        assertEquals(0, resultSize);
    }

    @Test
    void analyzeFolderTest_NonexistantFolder() {
        File baseFolder = new File(TEST_DIRECTORY + "\\\\" + CREATED_TEST_FOLDER + "\\\\DontExist");
        Folderinfo thisFolder = new Folderinfo(baseFolder, 0);

        long resultSize = testedEntity.analyzeFolder(thisFolder);

        assertEquals(0, resultSize);
    }

    @Test
    void analyzeFolderTest_NullFolder() {
        try {
            Folderinfo thisFolder = new Folderinfo(null, 0);
            fail("Should have thrown IllegalArgumentexception");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    void findTotalPercentages() {
        File baseFolder = new File(TEST_DIRECTORY + "\\\\" + CREATED_TEST_FOLDER);
        Folderinfo thisFolder = new Folderinfo(baseFolder, 0);
        long resultSize = testedEntity.analyzeFolder(thisFolder);
        thisFolder.setSize(resultSize);

        thisFolder.findTotalPercentages(resultSize);

        assertEquals(100, thisFolder.getMainFrac()); //base folder has the default value
        ArrayList<Folderinfo> basefolderSubfolders = thisFolder.getSubs();
        for (Folderinfo fold : basefolderSubfolders) {
            float roundedMainFrac = Math.round(fold.getMainFrac());
            switch (fold.getName()) {
                case "BigFileA":
                    assertEquals(44, roundedMainFrac);
                    break;
                case "EmptyFolderC":
                    assertEquals(0, roundedMainFrac);
                    break;
                case "TooSmallFileD":
                    assertEquals(3, roundedMainFrac);
                    break;
                case "FolderB":
                    assertEquals(53, roundedMainFrac);
                    ArrayList<Folderinfo> subfolder1_subs = fold.getSubs();
                    for (Folderinfo sub : subfolder1_subs) {
                        float roundedMainFracsub = Math.round(sub.getMainFrac());
                        ArrayList<Folderinfo> subfolder2_subs = sub.getSubs();
                        switch (sub.getName()) {
                            case "FolderBA":
                                assertEquals(25, roundedMainFracsub);
                                for (Folderinfo subsub : subfolder2_subs) {
                                    float roundedMainFracsubsub = Math.round(subsub.getMainFrac());
                                    switch (subsub.getName()) {
                                        case "FileBAA":
                                            assertEquals(14, roundedMainFracsubsub);
                                            break;
                                        case "FileBAB":
                                            assertEquals(10, roundedMainFracsubsub);
                                            break;
                                        default:
                                            fail("Unexpected subfolder in FolderBA: " + subsub.getName());
                                    }
                                }
                                break;

                            case "FolderBB":
                                assertEquals(19, roundedMainFracsub);
                                Folderinfo expectedFileBBA = subfolder2_subs.get(0);
                                assertEquals("FileBBA", expectedFileBBA.getName());
                                assertEquals(19, Math.round(expectedFileBBA.getMainFrac()));
                                break;

                            case "FolderBC":
                                assertEquals(10., roundedMainFracsub);
                                for (Folderinfo subsub : subfolder2_subs) {
                                    float roundedMainFracsubsub = Math.round(subsub.getMainFrac());
                                    assertEquals(0, subsub.getSubs().size());
                                    switch (subsub.getName()) {
                                        case "FileBCA":
                                            assertEquals(3, roundedMainFracsubsub);
                                            break;
                                        case "FileBCB":
                                            assertEquals(3, roundedMainFracsubsub);
                                            break;
                                        case "FileBCC":
                                            assertEquals(3, roundedMainFracsubsub);
                                            break;
                                        case "FileBCD":
                                            assertEquals(3, roundedMainFracsubsub);
                                            break;
                                        default:
                                            fail("Unexpected subfolder in FolderBC: " + subsub.getName());
                                    }
                                }
                                break;

                            default:
                                fail("Unexpected subfolder in FolderB: " + sub.getName());
                        }
                    }
                    break;
                default:
                    fail("Unexpected subfolder in base folder: " + fold.getName());
            }
        }
    }
}