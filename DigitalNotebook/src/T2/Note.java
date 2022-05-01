package T2;

/**
 * {@code Note}<p>
 * Just for storing different things of the note
 */


public class Note { // TODO: make use of state and expireDate

    public IDN idn;
    public String noteContent;
    public State state;
    public int expireDate; // in days
    
    enum State {
        IMPORTANT, URGENT, PRIVATE, PUBLIC
    }

    public Note(int noteId, String noteName) {
        this.idn = new IDN(noteId, noteName);
    }
    
    public void setName(String name) {
        this.idn.name = name;
    }
    
    public void setContent(String content) {
        this.noteContent = content;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setExpireDate(int expireDate) {
        this.expireDate = expireDate;
    }

    public int getId() {
        return this.idn.id;
    }

    public String getName() {
        return this.idn.name;
    }
    public String getContent() {
        return this.noteContent;
    }
    public State getState() {
        return this.state;
    }
    public int getExpireDate() {
        return this.expireDate;
    }
    
    public String stateToString() {
        return (state == State.IMPORTANT ? "IMPORTANT" : (state == State.URGENT ? "URGENT" : (state == State.PRIVATE ? "PRIVATE" : "PUBLIC")));
    }
    
    public String toStringSimple() {
        return idn.toString();
    }

    @Override
    public String toString() {
        return idn + " | " + stateToString() + " (expires in " + expireDate + " days)";
    }
}
