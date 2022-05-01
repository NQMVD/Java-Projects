import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class AutoImport {
	public File file;
	public String text;
	public String[] imports;
	public ArrayList<String[]> toImport;

	public AutoImport(File file_) {
		imports = read(new File("C:\\Users\\noah1\\Desktop\\Coding\\Java\\imports.txt")).split("\n");
		for (int INDEX = 0; INDEX < imports.length; INDEX++)
			imports[INDEX] = imports[INDEX].trim();
		this.file = file_;
		this.text = read(this.file);
		toImport = new ArrayList<String[]>();
		checkForClasses();
		write(this.text);
	}

	public void checkForClasses() {
		for (int i = 0; i < imports.length; i++) {
			String fullname = imports[i];
			String[] temp = fullname.split("[.]");
			String name = temp[temp.length - 1];
			if (this.text.contains(name)) {
				if (!this.text.contains(fullname))
					// toImport.add(new String[] {fullname, name});
					this.text = "import " + fullname + ";\n" + this.text;
			}
		}


	}

	public void write(String data) {
		try {
			FileWriter myWriter = new FileWriter(file);
			myWriter.write(data);
			myWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String read(File file) {
		String result = "";
		try {
			FileReader reader = new FileReader(file);
			int data = reader.read();
			while (data != -1) {
				result += (char)data;
				data = reader.read();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
