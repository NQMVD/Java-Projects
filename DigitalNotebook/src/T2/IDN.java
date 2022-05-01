package T2;

/**
 * {@code Custom ID Class}<p>
 * Helps when storing the input<p>
 * (Name or ID can be entered)
 */

public class IDN {
	public int id;
	public String name;
	public Type type;
	
	public IDN() {
	}
	
	public IDN(int id, String name) {
		this.id = id;
		this.name = name;
		this.type = Type.BOTH;
	}
	
	public IDN(int id) {
		this.id = id;
		this.type = Type.ID;
	}
	
	public IDN(String name) {
		this.name = name;
		this.type = Type.NAME;
	}
	
	public boolean equals(Note note) {
		return ((isId() && note.getId() == id) || (isName() && note.getName().equals(name)));
	}
	
	public boolean isId() {
		return this.type == Type.ID;
	}
	
	public boolean isName() {
		return this.type == Type.NAME;
	}
	
	@Override
	public String toString() {
		return isId() ? Integer.toString(id) : isName() ? name : "[" + name + "] ID: #" + id;
	}
	
	public enum Type {
		ID, NAME, BOTH
	}
}
