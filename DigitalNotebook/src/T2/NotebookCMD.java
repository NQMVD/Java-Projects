package T2;

import java.util.Objects;
import java.util.Scanner;

public class NotebookCMD {
	
	public Notebook myNotebook;
	public boolean running;
	
	public NotebookCMD() {
		myNotebook = new Notebook(20);
		running = false;
	}
	
	public void start() {
		running = true;
		while (running)
			mainMenu();
		
		showMessage("End of Program");
		
		System.console().;
	}
	
	public void mainMenu() {
		int input = getInputForMenu("Enter [1 - 5]: ", 5);
		
		switch (input) {
			case 0 -> running = false;
			case 1 -> createNewNote();
			case 2 -> editNote();
			case 3 -> searchNote();
			case 4 -> deleteNote();
			case 5 -> emptyAllNotes();
			default -> {
			}
		}
	}
	
	/*######################################## Notebook functions ########################################*/
	
	public void createNewNote() {
		if (myNotebook.hasSpace()) {
			String name = getInputForName("> Enter Name for new Note: ", false);
			if (!Objects.equals(name, "#CANCELED#")) {
				Note note = myNotebook.createNewNote(name);
				showMessage("+ Created Note " + note.idn);
			} else showMessage("< Canceled...");
		} else
			showError("No more space in your Notebook!\nDelete notes to get more space...");
		
	}
	
	public void searchNote() { //TODO: print note with this function
		boolean found, canceled = false;
		IDN input;
		do {
			input = getInput("> Search with Name or ID: ", true);
			if (input != null) {
				found = myNotebook.noteExists(input);
				if (!found) showMessage("No Note [" + input + "] found...");
			} else {
				canceled = true;
				found = true;
			}
			
		} while (!found);
		
		if (!canceled) {
			printNote(input);
			showMessage("< Go Back");
		} else
			showMessage("< Canceled...");
	}
	
	public void deleteNote() {
		boolean found, canceled = false;
		IDN input;
		do {
			input = getInput("> Enter Name or ID of Note to delete: ", true);
			if (input != null) {
				found = myNotebook.noteExists(input);
				if (!found) showMessage("No Note [" + input + "] found...");
			} else {
				canceled = true;
				found = true;
			}
			
		} while (!found);
		
		if (!canceled) {
			Note note = myNotebook.deleteNote(input);
			showMessage("- Deleted [" + note.getName() + "]");
		} else
			showMessage("< Canceled...");
	}
	
	public void emptyAllNotes() {
		myNotebook.emptyAllNotes();
		showMessage("* Cleared all Notes");
	}
	
	/*------------------------- Edit functions -------------------------*/
	
	public void editNote() { //TODO: make NAME, CONTENT, STATE, EXPIRE editable
		boolean found;
		IDN input;
		do {
			input = getInput("> Enter Name or ID to edit: ", true);
			found = myNotebook.noteExists(input);
			if (!found) showMessage("No Note [" + input + "] found...");
		} while (!found);
		
		
		int menuInput = getInputForNoteMenu("> Enter [1 - 4]: ", 4, input);
		
		switch (menuInput) {
			case -1 -> showMessage("< Canceled...");
			case 1 -> editNoteContent(input);   // Edit Content
			case 2 -> editNoteName(input);      // Edit Name
			case 3 -> editNoteSate(input);      // Edit State
			case 4 -> editNoteExpire(input);    // Edit Expire
			default -> {
			}
		}
		
	}
	
	private void editNoteContent(IDN idn) {
		printNote(idn);
		String input = getInputRaw("> ");
		myNotebook.setNoteContent(idn, input);
		printNote(idn);
		showMessage("< Go Back");
	}
	
	private void editNoteName(IDN idn) {
		String name = getInputForName("> Enter new Name for Note: ", false);
		myNotebook.setNoteName(idn, name);
		showMessage("+ Changed Name to [" + name + "]");
	}
	
	private void editNoteSate(IDN idn) {
		System.out.println("States:");
		System.out.println(" 1. IMPORTANT");
		System.out.println(" 2. URGENT");
		System.out.println(" 3. PRIVATE");
		System.out.println(" 4. PUBLIC");
		int state = getInputForNumber("> Enter new State : ");
		if (state > -1) {
			myNotebook.setNoteState(idn, state);
			showMessage("+ Changed State to [" + myNotebook.getNoteStateString(idn) + "]");
		} else showMessage("< Canceled...");
	}
	
	private void editNoteExpire(IDN idn) {
		int days = getInputForNumber("> Enter new ExpireDate (in days): ");
		if (days > -1) {
			myNotebook.setNoteExpireDate(idn, days);
			showMessage("+ Changed ExpireDate to [" + days + "] days");
		} else showMessage("< Canceled...");
	}
	
	
	
	
	/*######################################## Menu functions ########################################*/
	
	public void showMenu() {
		clearScreen();
		System.out.println("========== Digital Notebook Program ==========\n");
		System.out.println("Options:");
		System.out.println(" 1. Create new Note");
		System.out.println(" 2. Edit Note");
		System.out.println(" 3. Search Note");
		System.out.println(" 4. Delete Note");
		System.out.println(" 5. Empty all Notes");
	}
	
	public void showNoteMenu(IDN idn) {
		clearScreen();
		System.out.println("========== Edit Menu for " + myNotebook.getNote(idn).toStringSimple() + " ==========\n");
		System.out.println("Options:");
		System.out.println(" 1. Edit");
		System.out.println(" 2. Edit Name");
		System.out.println(" 3. Edit State");
		System.out.println(" 4. Edit Expire Date");
	}
	
	public void listNotes() {
		clearScreen();
		System.out.println("========== Your Notebook - [" + myNotebook.notes.size() + " Notes] ==========\n");
		for (int i = 0; i < myNotebook.notes.size(); i++) {
			Note note = myNotebook.notes.get(i);
			System.out.println("> " + note);
		}
	}
	
	public void showError(String details) {
		clearScreen();
		System.out.println("***** Error! *****\n");
		System.out.println(details);
		System.out.print("\n\n> Press Enter to continue...");
		new Scanner(System.in).nextLine();
	}
	
	public void showMessage(String message) {
		System.out.println();
		System.out.print(message);
		new Scanner(System.in).nextLine();
	}
	
	
	public void printNote(IDN idn) {
		clearScreen();
		System.out.println(myNotebook.printNote(idn));
		System.out.println();
	}
	
	public void clearScreen(int lines) {
		for (int i = 0; i < lines; i++) System.out.println();
	}
	
	public void clearScreen() {
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
	}
	
	
	/*######################################## Input functions ########################################*/
	
	public String getInputRaw(String prefix) {
		String result;
		System.out.print(prefix);
		result = new Scanner(System.in).nextLine().trim().replaceAll("`", "\n");
		return result;
	}
	
	public int getInputForMenu(String prefix, int lastMenuNumber) {
		int result = 0;
		boolean waiting = true;
		
		do {
			showMenu();
			System.out.print(prefix);
			String input = new Scanner(System.in).nextLine().trim();
			if (isNumber(input)) {
				try {
					result = Integer.parseInt(input);
				} catch (NumberFormatException e) {
					showMessage("Not a Valid Number!");
				}
				
				waiting = (result < 0 || result > lastMenuNumber);
			}
		} while (waiting);
		System.out.println();
		
		return result;
	}
	
	public int getInputForNoteMenu(String prefix, int lastMenuNumber, IDN idn) { // returns 0-max OR -1 if canceled
		int result = 0;
		boolean waiting = true;
		
		do {
			showNoteMenu(idn);
			System.out.print(prefix);
			String input = new Scanner(System.in).nextLine().trim();
			
			if (isCommand(input) && input.substring(1).equals("c")) {
				result = -1;
				waiting = false;
			} else if (isNumber(input)) {
				try {
					result = Integer.parseInt(input);
				} catch (NumberFormatException e) {
					showMessage("Not a Valid Number!");
				}
				waiting = (result < 0 || result > lastMenuNumber);
			}
		} while (waiting);
		System.out.println();
		
		return result;
	}
	
	public int getInputForNumber(String prefix) { // returns 0-max OR -1 if canceled
		int result = 0;
		boolean waiting = true;
		
		do {
			System.out.print(prefix);
			String input = new Scanner(System.in).nextLine().trim();
			if (isNumber(input)) {
				try {
					result = Integer.parseInt(input);
					waiting = false;
				} catch (NumberFormatException e) {
					showMessage("Not a Valid Number!");
				}
			} else if (isCommand(input) && input.substring(1).equals("c")) {
				result = -1;
				waiting = false;
			}
		} while (waiting);
		
		return result;
	}
	
	public String getInputForName(String prefix, boolean clear) { // returns valid word OR #CANCELED# if canceled
		String result = "";
		boolean waiting = true;
		
		do {
			if (clear) clearScreen();
			System.out.print(prefix);
			String input = new Scanner(System.in).nextLine().trim();
			if (isCommand(input) && input.substring(1).equals("c")) {
				result = "#CANCELED#";
				waiting = false;
			} else if (isWord(input)) {
				result = input;
				waiting = false;
			}
		} while (waiting);
		
		return result;
	}
	
	public IDN getInput(String prefix, boolean showList) { // returns IDN OR null if canceled
		IDN result = new IDN();
		boolean waiting = true;
		
		do {
			if (showList) listNotes();
			System.out.println();
			System.out.print(prefix);
			String input = new Scanner(System.in).nextLine().trim();
			if (isNumber(input)) {
				result = new IDN(Integer.parseInt(input));
				waiting = false;
			} else if (isWord(input)) {
				result = new IDN(input);
				waiting = false;
			} else if (isCommand(input) && input.substring(1).equals("c")) {
				result = null;
				waiting = false;
			}
		} while (waiting);
		
		return result;
	}
	
	/*######################################## isWhat ########################################*/
	
	public boolean isWord(String input) {
		boolean valid = false;
		if (!input.isEmpty())
			valid = input.replaceAll("[a-zA-Z]|\s", "").isEmpty();
		return valid;
	}
	
	public boolean isNumber(String input) {
		boolean valid = false;
		if (!input.isEmpty())
			valid = input.replaceAll("[0-9]", "").isEmpty();
		return valid;
	}
	
	public boolean isCommand(String input) {
		boolean valid = false;
		if (!input.isEmpty())
			valid = input.startsWith("/") && input.length() > 1;
		return valid;
	}
	
}
