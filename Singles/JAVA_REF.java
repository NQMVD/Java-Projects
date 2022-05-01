// Snap to Grid Positioning

XPOS - (XPOS % RESOLUTION)

// PLUS Centration
(XPOS - (XPOS % RESOLUTION)) + (RESOLUTION / 2)


noise() [0; 1]


arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
Arrays.fill(array, 0);

// outline algorithm

for (pixel p : newImage) {
	if (nextToPixelInOldImage) p = full;
}


System.out.println(new File("").getAbsolutePath());



// console input
System.out.print("Enter something:");
String input = System.console().readLine();

// ODER

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter String");
		String s = br.readLine();
		System.out.print("Enter Integer:");
		try {
			int i = Integer.parseInt(br.readLine());
		} catch (NumberFormatException nfe) {
			System.err.println("Invalid Format!");
		}
	}
}

public class StringToCharJava {

	public static void main(String[] args) {
		String str = "journaldev";

		//string to char array
		char[] chars = str.toCharArray();
		System.out.println(chars.length);

		//char at specific index
		char c = str.charAt(2);
		System.out.println(c);

		//Copy string characters to char array
		char[] chars1 = new char[7];
		str.getChars(0, 7, chars1, 0);
		System.out.println(chars1);
	}
}

int unhex(char a, char b) {
	int result = 0;
	int ai = (int) a, bi = (int) b;

	if (ai >= 48 && ai <= 57) {
		result = (ai - 48) * 16;
	} else if (ai >= 65 && ai <= 70) {
		result = (ai - 55) * 16;
	}

	if (bi >= 48 && bi <= 57) {
		result += (bi - 48);
	} else if (bi >= 65 && bi <= 70) {
		result += (bi - 55);
	}
	return result;
}

/*
    %a  floating point (except BigDecimal)                              Returns Hex output of floating point number.
    %b  Any type                                                        "true" if non-null, "false" if null
    %c  character                                                       Unicode character
    %d  integer (incl. byte, short, int, long, bigint)                  Decimal Integer
    %e  floating point                                                  decimal number in scientific notation
   >%f  floating point                                                  decimal number
    %g  floating point                                                  decimal number, possibly in scientific notation depending on the precision and value.
    %h  any type                                                        Hex String of value from hashCode() method.
    %n  none                                                            Platform-specific line separator.
    %o  integer (incl. byte, short, int, long, bigint)                  Octal number
    %s  any type                                                        String value
    %t  Date/Time (incl. long, Calendar, Date and TemporalAccessor)     %t is the prefix for Date/Time conversions. More formatting flags are needed after this. See Date/Time conversion below.
    %x  integer (incl. byte, short, int, long, bigint)                  Hex string.
*/

void printf(String format, Object... args) {
	System.out.print(String.format(format, args));
}

void printlnf(String format, Object... args) {
	System.out.println(String.format(format, args));
}

String withCommas(int a) {
	String out = "" + a;
	if (a > 999) {
		StringBuffer buff = new StringBuffer(out);
		for (int i = out.length() - 3; i > 0; i -= 3) {
			buff.insert(i, ",");
		}
		out = buff.toString();
	}

	return out;
}

int[] replaceAndShift(int[] a, int index, int num) {
	int[] result = new int[a.length];
	System.arraycopy(a, 0, result, 0, index);
	System.arraycopy(a, index, result, index + 1, a.length - index - 1);
	result[index] = num;
	return result;
}

int[] insert(int arr[], int pos, int x) {
	int i, n = arr.length;

	// create a new array of size n+1
	int newarr[] = new int[n + 1];

	// insert the elements from
	// the old array into the new array
	// insert all elements till pos
	// then insert x at pos
	// then insert rest of the elements
	for (i = 0; i < n + 1; i++) {
		if (i < pos - 1)
			newarr[i] = arr[i];
		else if (i == pos - 1)
			newarr[i] = x;
		else
			newarr[i] = arr[i - 1];
	}
	return newarr;
}

// DOESNT WORK YET
// https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/ArrayUtils.html
// array functions


String fill(String s, char f) {
	String n = "";
	for (int i = 0; i < s.length(); i++) n += f;
	return n;
}

Object[] arr = new Object[6];

arr[0] = new String("First Pair");
arr[1] = new Integer(1);
arr[2] = new String("Second Pair");
arr[3] = new Integer(2);
arr[4] = new String("Third Pair");
arr[5] = new Integer(3);


import java.math.BigDecimal;

BigDecimal premium = BigDecimal.valueOf("1586.6");
BigDecimal netToCompany = BigDecimal.valueOf("708.75");
BigDecimal commission = premium.subtract(netToCompany);
System.out.println(commission + " = " + premium + " - " + netToCompany);



// public void selectFolder(String callback) {
//  JFileChooser chooser = new JFileChooser();
//  int result = chooser.showOpenDialog(null);

//  if (result == JFileChooser.APPROVE_OPTION) {
//      method(callback, chooser.getSelectedFile());
//  }
// }


public static String generate(double[] in, String name) {
	String out = "double[] " + name + " = {\n";
	int c = 0;

	for (int i = 0; i < in.length; i++) {
		out += in[i] + ", ";
		c++;
		if (c > 5) {
			out += "\n";
			c = 0;
		}
	}
	out += "};";

	return out;
}