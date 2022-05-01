package T2;

public class NotebookProgram {

    public static NotebookCMD cmd;

    public static void main(String[] args) {

        cmd = new NotebookCMD();
        createTestNotes();

        cmd.start();

        System.out.println(" >>> Finished! <<<");
    }

    public static void createTestNotes() {
        cmd.myNotebook.createNewNote("English Notes");
        cmd.myNotebook.createNewNote("Maths Notes");
        cmd.myNotebook.createNewNote("German Notes");
        cmd.myNotebook.createNewNote("History Notes");
        cmd.myNotebook.createNewNote("General Notes");
    }
}