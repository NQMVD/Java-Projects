import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;

import java.util.Stack;
import java.util.regex.Pattern;
import java.lang.reflect.*;

interface Formatter {
    String format(String text);
}


public class AutoFormat implements Formatter {
    private char[] chars;
    private final StringBuilder buf = new StringBuilder();
    private final StringBuilder result = new StringBuilder();

    /** The number of spaces in one indent. Constant. */
    private int indentValue = 4;

    /** Set when the end of the chars array is reached. */
    private boolean EOF;

    private boolean inStatementFlag; // in a line of code
    private boolean overflowFlag;   // line overrunning?
    private boolean startFlag;     // No buf has yet been writen to this line.
    private boolean if_flg;
    private boolean elseFlag;

    /** -1 if not in array or if just after it, otherwise increases from 0. */
    private int arrayLevel;
    private int arrayIndent; // Lowest value of the above for this line.

    /** Number of ? entered without exiting at : of a?b:c structures. */
    private int conditionalLevel;

    private int[][] sp_flg;
    private boolean[][] s_ind;
    private int if_lev;

    /** chars[pos] is where we're at. */
    private int pos;
    private int level;

    /** Number of curly brackets entered and not exited,
     excluding arrays. */
    private int curlyLvl;

    /** Number of parentheses entered and not exited. */
    private int parenLevel;

    private boolean[] ind;
    private int[] p_flg;
    private int[][] s_tabs;

    /** At every {, this has a true pushed if it's a do-while,
     and a false otherwise. It is then popped at }. */
    private Stack<Boolean> doWhileFlags;

    /** At every (, this has a true pushed for if, while, or for,
     and a false otherwise. Popped at ). */
    private Stack<Boolean> ifWhileForFlags;

    private boolean jdoc_flag;

    /** The number of times to indent at a given point */
    private int tabs;

    /** The last non-space seen by nextChar(). */
    private char lastNonWhitespace = 0;


    private void handleMultiLineComment() {
        final boolean savedStartFlag = startFlag;
        buf.append(nextChar()); // So /*/ isn't self-closing.

        for (char ch = nextChar(); !EOF; ch = nextChar()) {
            buf.append(ch);
            while (ch != '/' && !EOF) {
                if (ch == '\n') {
                    writeIndentedComment();
                    startFlag = true;
                }
                buf.append(ch = nextChar());
            }
            if (buf.length() >= 2 && buf.charAt(buf.length() - 2) == '*') {
                jdoc_flag = false;
                break;
            }
        }

        writeIndentedComment();
        startFlag = savedStartFlag;
        jdoc_flag = false;
    }


    /**
     * Pumps nextChar into buf until \n or EOF, then calls
     * writeIndentedLine() and sets startFlag to true.
     */
    private void handleSingleLineComment() {
        char ch = nextChar();
        while (ch != '\n' && !EOF) {
            buf.append(ch);
            ch = nextChar();
        }
        writeIndentedLine();
        startFlag = true;
    }


    private void writeIndentedLine() {
        if (buf.length() == 0) {
            if (startFlag) startFlag = elseFlag = false;
            return;
        }
        if (startFlag) {
            // Indent suppressed at eg. if<nl>{ and when
            // buf is close-brackets only followed by ';'.
            boolean indentMore = !buf.toString().matches("[\\s\\]\\}\\)]+;")
                && (buf.charAt(0) != '{' || arrayLevel >= 0)
                && overflowFlag;
            if (indentMore) {
                tabs++;
                if (arrayIndent > 0) tabs += arrayIndent;
            }
            printIndentation();
            startFlag = false;
            if (indentMore) {
                tabs--;
                if (arrayIndent > 0) tabs -= arrayIndent;
            }
        }
        if (lastNonSpaceChar() == '}' && bufStarts("else"))
            result.append(' ');

        if (elseFlag) {
            if (lastNonSpaceChar() == '}') {
                trimRight(result);
                result.append(' ');
            }
            elseFlag = false;
        }

        // If we're still in a statement at \n, that's overflow.
        overflowFlag = inStatementFlag;
        arrayIndent = arrayLevel;
        result.append(buf);

        buf.setLength(0);
    }


    /**
     * @return the last character in <tt>result</tt> not ' ' or '\n'.
     */
    private char lastNonSpaceChar() {
        for (int i = result.length() - 1; i >= 0; i--) {
            char chI = result.charAt(i);
            if (chI != ' ' && chI != '\n') return chI;
        }
        return 0;
    }


    /**
     * Called by handleMultilineComment.<br />
     * Sets jdoc_flag if at the start of a doc comment.
     * Sends buf to result with proper indents, then clears buf.<br />
     * Does nothing if buf is empty.
     */
    private void writeIndentedComment() {
        if (buf.length() == 0) return;

        int firstNonSpace = 0;
        while (buf.charAt(firstNonSpace) == ' ') firstNonSpace++;
        if (lookup_com("/**")) jdoc_flag = true;

        if (startFlag) printIndentation();

        if (buf.charAt(firstNonSpace) == '/' && buf.charAt(firstNonSpace + 1) == '*') {
            if (startFlag && lastNonWhitespace != ';')
                result.append(buf.substring(firstNonSpace));
            else
                result.append(buf);
        } else {
            if (buf.charAt(firstNonSpace) == '*' || !jdoc_flag)
                result.append(" " + buf.substring(firstNonSpace));
            else
                result.append(" * " + buf.substring(firstNonSpace));
        }
        buf.setLength(0);
    }


    /**
     * Makes tabs &gt;= 0 and appends <tt>tabs*indentValue</tt>
     * spaces to result.
     */
    private void printIndentation() {
        if (tabs <= 0) {
            tabs = 0;
            return;
        }
        // final int spaces = tabs * indentValue;
        // for (int i = 0; i < spaces; i++)
        //     result.append(' ');

        for (int i = 0; i < tabs; i++)
            result.append('\t');
    }


    /**
     * @return <tt>chars[pos+1]</tt> or '\0' if out-of-bounds.
     */
    private char peek() {
        return (pos + 1 >= chars.length) ? 0 : chars[pos + 1];
    }


    /**
     * Sets pos to the position of the next character that is not ' '
     * in chars. If chars[pos] != ' ' already, it will still move on.
     * Then sets EOF if pos has reached the end, or reverses pos by 1 if it
     * has not.
     * <br/> Does nothing if EOF.
     * @param allWsp (boolean) Eat newlines too (all of Character.isWhiteSpace()).
     */
    private void advanceToNonSpace(boolean allWsp) {
        if (EOF) return;

        if (allWsp) {
            do {
                pos++;
            } while (pos < chars.length && Character.isWhitespace(chars[pos]));
        } else {
            do {
                pos++;
            } while (pos < chars.length && chars[pos] == ' ');
        }

        if (pos == chars.length - 1)
            EOF = true;
        else {
            pos--; // reset for nextChar()
        }
    }


    /**
     * Increments pos, sets EOF if needed, and returns the new
     * chars[pos] or zero if out-of-bounds.
     * Sets lastNonWhitespace if chars[pos] isn't whitespace.
     * Does nothing and returns zero if already at EOF.
     */
    private char nextChar() {
        if (EOF) return 0;
        pos++;
        if (pos >= chars.length - 1) EOF = true;
        if (pos >= chars.length) return 0;

        char retVal = chars[pos];
        if (!Character.isWhitespace(retVal)) lastNonWhitespace = retVal;
        return retVal;
    }


    /**
     * Called after else.
     */
    private void gotElse() {
        tabs = s_tabs[curlyLvl][if_lev];
        p_flg[level] = sp_flg[curlyLvl][if_lev];
        ind[level] = s_ind[curlyLvl][if_lev];
        if_flg = true;
        // We can't expect else to be followed by a semicolon, so must
        // end the statemant manually.
        inStatementFlag = false;
    }


    /**
     *  Pump any '\t' and ' ' to buf, handle any following comment,
     *  and if the next character is '\n', discard it.
     *  @return Whether a '\n' was found and discarded.
     */
    private boolean readForNewLine() {
        final int savedTabs = tabs;
        char c = peek();
        while (!EOF && (c == '\t' || c == ' ')) {
            buf.append(nextChar());
            c = peek();
        }

        if (c == '/') {
            buf.append(nextChar());
            c = peek();
            if (c == '*') {
                buf.append(nextChar());
                handleMultiLineComment();
            } else if (c == '/') {
                buf.append(nextChar());
                handleSingleLineComment();
                return true;
            }
        }

        c = peek();
        if (c == '\n') {
            // eat it
            nextChar();
            tabs = savedTabs;
            return true;
        }
        return false;
    }


    /**
     * @return last non-wsp in result+buf, or 0 on error.
     */
    private char prevNonWhitespace() {
        StringBuffer tot = new StringBuffer();
        tot.append(result);
        tot.append(buf);
        for (int i = tot.length() - 1; i >= 0; i--) {
            if (!Character.isWhitespace(tot.charAt(i)))
                return tot.charAt(i);
        }
        return 0;
    }


    /**
     * Sees if buf is of the form [optional whitespace][keyword][optional anything].
     * It won't allow keyword to be directly followed by an alphanumeric, _, or &amp;.
     * Will be different if keyword contains regex codes.
     */
    private boolean bufStarts(final String keyword) {
        return Pattern.matches("^\\s*" + keyword + "(?![a-zA-Z0-9_&]).*$", buf);
    }


    /**
     * Sees if buf is of the form [optional anything][keyword][optional whitespace].
     * It won't allow keyword to be directly preceded by an alphanumeric, _, or &amp;.
     * Will be different if keyword contains regex codes.
     */
    private boolean bufEnds(final String keyword) {
        return Pattern.matches("^.*(?<![a-zA-Z0-9_&])" + keyword + "\\s*$", buf);
    }


    /**
     * Allows you to increase if_lev safely. Enlarges related arrays where needed;
     * does not change if_lev itself.
     */
    private void if_levSafe() {
        if (s_tabs[0].length <= if_lev) {
            for (int i = 0; i < s_tabs.length; i++) {
                s_tabs[i] = expand(s_tabs[i]); //[N] mit eigener expand() function replaceable
            }
        }
        if (sp_flg[0].length <= if_lev) {
            for (int i = 0; i < sp_flg.length; i++)
                sp_flg[i] = expand(sp_flg[i]);
        }
        if (s_ind[0].length <= if_lev) {
            for (int i = 0; i < s_ind.length; i++)
                s_ind[i] = expand(s_ind[i]);
        }
    }

    /**
     * Increases the size of an array. By default, this function doubles the
     * size of the array, but the optional newSize parameter provides
     * precise control over the increase in size.
     *
     * When using an array of objects, the data returned from the function must
     * be cast to the object array's data type. For example: SomeClass[]
     * items = (SomeClass[]) expand(originalArray).
     *
     */
    public boolean[] expand(boolean[] list) {
        return expand(list, list.length > 0 ? list.length << 1 : 1);
    }

    /**
     * @param newSize new size for the array
     */
    public boolean[] expand(boolean[] list, int newSize) {
        boolean[] temp = new boolean[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public byte[] expand(byte[] list) {
        return expand(list, list.length > 0 ? list.length << 1 : 1);
    }

    public byte[] expand(byte[] list, int newSize) {
        byte[] temp = new byte[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public char[] expand(char[] list) {
        return expand(list, list.length > 0 ? list.length << 1 : 1);
    }

    public char[] expand(char[] list, int newSize) {
        char[] temp = new char[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public int[] expand(int[] list) {
        return expand(list, list.length > 0 ? list.length << 1 : 1);
    }

    public int[] expand(int[] list, int newSize) {
        int[] temp = new int[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public long[] expand(long[] list) {
        return expand(list, list.length > 0 ? list.length << 1 : 1);
    }

    public long[] expand(long[] list, int newSize) {
        long[] temp = new long[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public float[] expand(float[] list) {
        return expand(list, list.length > 0 ? list.length << 1 : 1);
    }

    public float[] expand(float[] list, int newSize) {
        float[] temp = new float[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public double[] expand(double[] list) {
        return expand(list, list.length > 0 ? list.length << 1 : 1);
    }

    public double[] expand(double[] list, int newSize) {
        double[] temp = new double[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public String[] expand(String[] list) {
        return expand(list, list.length > 0 ? list.length << 1 : 1);
    }

    public String[] expand(String[] list, int newSize) {
        String[] temp = new String[newSize];
        // in case the new size is smaller than list.length
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    /**
     * @nowebref
     */
    public Object expand(Object array) {
        int len = Array.getLength(array);
        return expand(array, len > 0 ? len << 1 : 1);
    }

    public Object expand(Object list, int newSize) {
        Class<?> type = list.getClass().getComponentType();
        Object temp = Array.newInstance(type, newSize);
        System.arraycopy(list, 0, temp, 0,
            Math.min(Array.getLength(list), newSize));
        return temp;
    }


    /**
     * Sees if buf is of the form [optional whitespace][keyword][optional anything].
     * It *will* allow keyword to be directly followed by an alphanumeric, _, or &amp;.
     * Will be different if keyword contains regex codes (except *, which is fine).
     */
    private boolean lookup_com(final String keyword) {
        final String regex = "^\\s*" + keyword.replace("*", "\\*") + ".*$";
        return Pattern.matches(regex, buf);
    }


    /**
     * Takes all whitespace off the end of its argument.
     */
    static private void trimRight(final StringBuilder sb) {
        while (sb.length() >= 1 && Character.isWhitespace(sb.charAt(sb.length() - 1)))
            sb.setLength(sb.length() - 1);
    }


    /** Entry point */
    public String format(final String source) {
        final String normalizedText = source.replaceAll("\r", "");
        String cleanText = normalizedText;
        if (!normalizedText.endsWith("\n"))
            cleanText += "\n";

        // Globals' description at top of file.
        result.setLength(0);
        //indentValue =  2; //Preferences.getInteger("editor.tabs.size"); //[N] mit eigener nummer replaceable

        boolean forFlag = if_flg = false;
        startFlag = true;
        int forParenthLevel = 0;
        conditionalLevel = parenLevel = curlyLvl = if_lev = level = 0;
        tabs = 0;
        jdoc_flag = inStatementFlag = overflowFlag = false;
        pos = arrayLevel = -1;

        int[] s_level = new int[10];
        sp_flg = new int[20][10];
        s_ind = new boolean[20][10];
        int[] s_if_lev = new int[10];            // Stack
        boolean[] s_if_flg = new boolean[10];    // Stack
        ind = new boolean[10];
        p_flg = new int[10];
        s_tabs = new int[20][10];
        doWhileFlags = new Stack<>();
        ifWhileForFlags = new Stack<>();

        chars = cleanText.toCharArray();

        EOF = false; // set in nextChar() when EOF

        while (!EOF) {
            char c = nextChar();

            switch (c) {
                default:
                    inStatementFlag = true;
                buf.append(c);
                break;

            case ',':
                inStatementFlag = true;
                trimRight(buf);
                buf.append(", ");
                advanceToNonSpace(false);
                break;

            case ' ':
            case '\t':
                elseFlag = bufEnds("else");
                if (elseFlag) {
                    gotElse();
                    if (!startFlag || buf.length() > 0)
                        buf.append(c);

                    writeIndentedLine();
                    startFlag = false;
                    break;
                }
                // Only allow in the line, to nuke the old indent.
                if (!startFlag || buf.length() > 0) buf.append(c);
                break;

            case '\n':
                if (EOF) break;

                elseFlag = bufEnds("else");
                if (elseFlag) gotElse();

                if (lookup_com("//")) {
                    if (buf.charAt(buf.length() - 1) == '\n')
                        buf.setLength(buf.length() - 1);
                }

                if (elseFlag) {
                    writeIndentedLine();
                    result.append("\n");

                    p_flg[level]++;
                    tabs++;
                } else {
                    writeIndentedLine();
                    result.append("\n");
                }
                startFlag = true;
                break;

            case '{':
                elseFlag = bufEnds("else");
                if (elseFlag) gotElse();

                doWhileFlags.push(Boolean.valueOf(bufEnds("do")));

                char prevChar = prevNonWhitespace();
                if (arrayLevel >= 0 || prevChar == '=' || prevChar == ']') {
                    // If we're already in an array (lvl >= 0), increment level.
                    // Otherwise, the presence of a = or ] indicates an array is starting
                    // and we should start counting (set lvl=0).
                    arrayLevel++;
                    buf.append(c);
                    break; // Nothing fancy.
                }

                inStatementFlag = false; // eg. class declaration ends

                if (s_if_lev.length == curlyLvl) {
                    s_if_lev = expand(s_if_lev); //[N] PApplet usage
                    s_if_flg = expand(s_if_flg);
                }
                s_if_lev[curlyLvl] = if_lev;
                s_if_flg[curlyLvl] = if_flg;
                if_lev = 0;
                if_flg = false;
                curlyLvl++;
                if (startFlag && p_flg[level] != 0) {
                    p_flg[level]--;
                    tabs--;
                }

                trimRight(buf);
                if (buf.length() > 0 || (result.length() > 0 &&
                    !Character.isWhitespace(result.charAt(result.length() - 1))))
                    buf.append(" ");
                buf.append(c);
                writeIndentedLine();
                readForNewLine();
                writeIndentedLine();

                result.append('\n');
                tabs++;
                startFlag = true;

                if (p_flg[level] > 0) {
                    ind[level] = true;
                    level++;
                    s_level[level] = curlyLvl;
                }
                break;

            case '}':
                if (arrayLevel >= 0) {
                    // Even less fancy. Note that }s cannot end array behaviour;
                    // a semicolon is needed.
                    if (arrayLevel > 0) arrayLevel--;
                    if (arrayIndent > arrayLevel) arrayIndent = arrayLevel;
                    buf.append(c);
                    break;
                }

                // In a simple enum, there's not necessarily a `;` to end the statement.
                inStatementFlag = false;

                curlyLvl--;
                if (curlyLvl < 0) {
                    curlyLvl = 0;
                    buf.append(c);
                    writeIndentedLine();
                } else {
                    if_lev = s_if_lev[curlyLvl] - 1;
                    if (if_lev < 0) if_lev = 0;
                    if_levSafe();

                    if_flg = s_if_flg[curlyLvl];
                    trimRight(buf);
                    writeIndentedLine();
                    tabs--;

                    trimRight(result);
                    result.append('\n');
                    overflowFlag = false; // Would normally be done in writeIndentedLine.
                    printIndentation();
                    result.append(c);
                    if (peek() == ';') result.append(nextChar());

                    // doWhileFlags contains a TRUE if and only if the
                    // corresponding { was preceeded (bufEnds) by "do".
                    // Just checking for while would fail on if(){} while(){}.
                    if (doWhileFlags.empty() || !doWhileFlags.pop().booleanValue()
                        || !new String(chars, pos + 1, chars.length - pos - 1).trim().startsWith("while")) {
                        readForNewLine();
                        writeIndentedLine();
                        result.append('\n');
                        startFlag = true;
                    } else {
                        // Correct spacing in "} while".
                        result.append(' ');
                        advanceToNonSpace(true);
                        startFlag = false;
                    }

                    if (curlyLvl < s_level[level] && level > 0) level--;

                    if (ind[level]) {
                        tabs -= p_flg[level];
                        p_flg[level] = 0;
                        ind[level] = false;
                    }
                }
                break;

            case '"':
            case '\'':
                inStatementFlag = true;
                char realQuote = c;
                buf.append(realQuote);

                char otherQuote = c;

                char cc = nextChar();
                // In a proper string, all the quotes tested are c. In a curly-quoted
                // string, there are three possible end quotes: c, its reverse, and
                // the correct straight quote.
                while (!EOF && cc != otherQuote && cc != realQuote && cc != c) {
                    buf.append(cc);
                    if (cc == '\\')
                        buf.append(cc = nextChar());

                    // Syntax error: unterminated string. Leave \n in nextChar, so it
                    // feeds back into the loop.
                    if (peek() == '\n') break;
                    cc = nextChar();
                }
                if (cc == otherQuote || cc == realQuote || cc == c) {
                    buf.append(realQuote);
                    if (readForNewLine()) {
                        // push a newline into the stream
                        chars[pos--] = '\n';
                    }
                } else {
                    // We've had a syntax error if the string wasn't terminated by EOL/
                    // EOF, just abandon this statement.
                    inStatementFlag = false;
                }
                break;

            case ';':
                if (forFlag) {
                    // This is like a comma.
                    trimRight(buf);
                    buf.append("; ");
                    // Not non-whitespace: allow \n.
                    advanceToNonSpace(false);
                    break;
                }
                buf.append(c);
                inStatementFlag = false;
                writeIndentedLine();
                if (p_flg[level] > 0 && !ind[level]) {
                    tabs -= p_flg[level];
                    p_flg[level] = 0;
                }
                readForNewLine();
                writeIndentedLine();
                result.append("\n");
                startFlag = true;
                // Array behaviour ends at the end of a statement.
                arrayLevel = -1;

                if (if_lev > 0) {
                    if (if_flg) {
                        if_lev--;
                        if_flg = false;
                    } else
                        if_lev = 0;
                }
                break;

            case '\\':
                buf.append(c);
                buf.append(nextChar());
                break;

            case '?':
                conditionalLevel++;
                buf.append(c);
                break;

            case ':':
                // Java 8 :: operator.
                if (peek() == ':') {
                    result.append(c).append(nextChar());
                    break;
                }

                // End a?b:c structures.
                else if (conditionalLevel > 0) {
                    conditionalLevel--;
                    buf.append(c);
                    break;
                } else if (forFlag) {
                    trimRight(buf);
                    buf.append(" : ");
                    // Not to non-whitespace: allow \n.
                    advanceToNonSpace(false);
                    break;
                }

                buf.append(c);
                inStatementFlag = false;
                arrayLevel = -1; // Unlikely to be needed; just in case.

                // Same format for case, default, and other labels.
                if (tabs > 0) {
                    tabs--;
                    writeIndentedLine();
                    tabs++;
                } else
                    writeIndentedLine();

                readForNewLine();
                writeIndentedLine();
                result.append('\n');
                startFlag = true;
                break;

            case '/':
                final char next = peek();
                if (next == '/') {
                    // call nextChar to move on.
                    buf.append(c).append(nextChar());
                    handleSingleLineComment();
                    result.append("\n");
                } else if (next == '*') {
                    if (buf.length() > 0)
                        writeIndentedLine();
                    buf.append(c).append(nextChar());
                    handleMultiLineComment();
                } else
                    buf.append(c);
                break;

            case ')':
                parenLevel--;

                // If we're further back than the start of a for loop, we've
                // left it.
                if (forFlag && forParenthLevel > parenLevel) forFlag = false;

                if (parenLevel < 0) parenLevel = 0;
                buf.append(c);

                boolean wasIfEtc = !ifWhileForFlags.empty() && ifWhileForFlags.pop().booleanValue();
                if (wasIfEtc) {
                    inStatementFlag = false;
                    arrayLevel = -1; // This is important as it allows arrays in if statements.
                }

                writeIndentedLine();
                // Short-circuiting means readForNewLine is only called for if/while/for;
                // this is important.
                if (wasIfEtc && readForNewLine()) {
                    chars[pos] = '\n';
                    pos--; // Make nextChar() return the new '\n'.
                    if (parenLevel == 0) {
                        p_flg[level]++;
                        tabs++;
                        ind[level] = false;
                    }
                }
                break;

            case '(':
                final boolean isFor = bufEnds("for");
                final boolean isIf = bufEnds("if");

                if (isFor || isIf || bufEnds("while")) {
                    if (!Character.isWhitespace(buf.charAt(buf.length() - 1)))
                        buf.append(' ');
                    ifWhileForFlags.push(true);
                } else
                    ifWhileForFlags.push(false);

                buf.append(c);
                parenLevel++;

                // isFor says "Is it the start of a for?". If it is, we set forFlag and
                // forParenthLevel. If it is not parenth_lvl was incremented above and
                // that's it.
                if (isFor && !forFlag) {
                    forParenthLevel = parenLevel;
                    forFlag = true;
                } else if (isIf) {
                    writeIndentedLine();
                    s_tabs[curlyLvl][if_lev] = tabs;
                    sp_flg[curlyLvl][if_lev] = p_flg[level];
                    s_ind[curlyLvl][if_lev] = ind[level];
                    if_lev++;
                    if_levSafe();
                    if_flg = true;
                }
            } // end switch
        } // end while not EOF

        if (buf.length() > 0) writeIndentedLine();

        final String formatted = simpleRegexCleanup(result.toString());
        return formatted.equals(cleanText) ? source : formatted;
    }

    /**
     * Make minor regex-based find / replace changes to execute simple fixes to limited artifacts.
     *
     * @param result The code to format.
     * @return The formatted code.
     */
    static private String simpleRegexCleanup(String result) {
        return result.replaceAll("([^ \n]+) +\n", "$1\n"); // Remove trail whitespace
    }

    // public void readFile() throws IOException {
    //     Path path = Paths.get("src/data/fileTest.java");

    //     BufferedReader reader = Files.newBufferedReader(path);
    //     String line = reader.readLine();
    // }

//
//    public static void main(String[] args) {
//        System.out.println(args[0]);
//        // new AutoFormat.format();
//    }
}
