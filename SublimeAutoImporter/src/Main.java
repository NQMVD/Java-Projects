import java.io.File;

public class Main {

    public static void main(String[] args) {
        if (args.length > 0) {
            File file = new File("");
            try {
                file = new File(args[0]);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
            
            new AutoImport(file);
        } else
            System.out.println("No args passed in");
    }
}