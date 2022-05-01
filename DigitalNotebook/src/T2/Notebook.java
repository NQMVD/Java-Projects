package T2;

import java.util.ArrayList;

/**
 * {@code Notebook}<p>
 * Stores all {@code Notes} in an {@code ArrayList}<p>
 * Maximum amount of {@code Notes} is determined by {@code maxNumberNotes}
 */


public class Notebook {
	
	public ArrayList<Note> notes;
	public int noteIdCounter;
	public int maxNumberNotes;
	
	public Notebook(int max) {
		notes = new ArrayList<>(max);
		maxNumberNotes = max;
		noteIdCounter = 0;
	}
	
	
	/*######################################## Notebook functions ########################################*/
	
	/**
	 * Creates a new {@linkplain Note Note} with given name.<p>
	 * ID gets assigned automatically.<p>
	 * Returns a Note to allow things like displaying the newly created Note.<p>
	 *
	 * @return {@linkplain Note Note}
	 */
	public Note createNewNote(String name) {
		Note newNote = new Note(noteIdCounter, name);
		newNote.setContent("Note Number #" + noteIdCounter);
		newNote.setState(Note.State.PUBLIC);
		newNote.setExpireDate(7);
		notes.add(newNote);
		noteIdCounter++;
		return newNote;
	}
	
	/**
	 * Deletes the Note with a matching {@linkplain IDN IDN}.<p>
	 * Returns a {@linkplain Note Note} to allow things like displaying the name of the deleted {@linkplain Note Note}.<p>
	 *
	 * @return {@linkplain Note Note} if the Note exists, otherwise {@code null}
	 */
	public Note deleteNote(IDN idn) {
		int index = getNoteIndex(idn);
		Note note = notes.get(index);
		if (index > -1) {
			notes.remove(index);
			return note;
		} else
			return null;
	}
	
	/**
	 * Clears all {@linkplain Note Note} in the {@linkplain Notebook Notebook} but keeps the notes.
	 */
	
	public void emptyAllNotes() {
		for (Note n : notes)
			n.noteContent = "";
	}
	
	/*######################################## Booleans ########################################*/
	
	public boolean hasSpace() {
		return notes.size() < maxNumberNotes;
	}
	
	public boolean noteExists(IDN idn) {
		boolean exists = false;
		for (Note note : notes) if (idn.equals(note)) exists = true;
		return exists;
	}
	
	
	/*######################################## Get Note and Vars ########################################*/
	
	public Note getNote(IDN idn) {
		int index = getNoteIndex(idn);
		if (index > -1)
			return notes.get(index);
		else
			return null;
	}
	
	public String getNoteName(IDN idn) {
		int index = getNoteIndex(idn);
		if (index > -1)
			return notes.get(index).getName();
		else
			return "?";
	}
	
	public Note.State getNoteState(IDN idn) {
		int index = getNoteIndex(idn);
		if (index > -1)
			return notes.get(index).getState();
		else
			return null;
	}
	
	public String getNoteStateString(IDN idn) {
		int index = getNoteIndex(idn);
		if (index > -1)
			return notes.get(index).stateToString();
		else
			return null;
	}
	
	public String getNoteContent(IDN idn) {
		int index = getNoteIndex(idn);
		if (index > -1)
			return notes.get(index).getContent();
		else
			return "?";
	}
	
	public int getNoteExpireDate(IDN idn) {
		int index = getNoteIndex(idn);
		if (index > -1)
			return notes.get(index).getExpireDate();
		else
			return 0;
	}
	
	
	
	/*######################################## Set Note and Vars ########################################*/
	
	public void setNoteName(IDN idn, String name) {
		int index = getNoteIndex(idn);
		notes.get(index).setName(name);
	}
	
	public void setNoteState(IDN idn, int state) {
		int index = getNoteIndex(idn);
		notes.get(index).setState((state == 1 ? Note.State.IMPORTANT : (state == 2 ? Note.State.URGENT : (state == 3 ? Note.State.PRIVATE : Note.State.PUBLIC))));
	}
	
	public void setNoteContent(IDN idn, String content) {
		int index = getNoteIndex(idn);
		notes.get(index).setContent(content);
	}
	
	public void setNoteExpireDate(IDN idn, int expire) {
		int index = getNoteIndex(idn);
		notes.get(index).setExpireDate(expire);
	}
	
	
	/*######################################## Get Index of Note by IDN ########################################*/
	
	public int getNoteIndex(IDN idn) {
		int index = -1;
		for (int i = 0; i < notes.size(); i++)
			if (idn.equals(notes.get(i))) index = i;
		return index;
	}
	
	
	/*######################################## Print Note ########################################*/
	
	public String printNote(IDN idn) {
		return "========== " + getNote(idn).toString() + " ==========" +
				"\n" +
				getNote(idn).noteContent;
	}
	
}
