package test;
import mjl.Folderscale;

public class FolderscaleDummyForTest extends Folderscale{
    String outputMessages = "";
    String textInGuiFolderfield = "";

    @Override
    public void say(String output) {
        outputMessages += "\n" + output;
    }

    @Override
    public void say(String output, int minDebugLevToPrint) {
        outputMessages += "\n" + output;
    }

    @Override
    protected String getTextFromGuiFolderfield() {
        return getTextInGuiFolderfield();
    }

    public String getTextInGuiFolderfield() {
        return textInGuiFolderfield;
    }

    public void setTextInGuiFolderfield(String textInGuiFolderfield) {
        this.textInGuiFolderfield = textInGuiFolderfield;
    }

    @Override
    protected void drawTheResult() {
        //noop
    }
}
