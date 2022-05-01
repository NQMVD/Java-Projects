import java.math.BigDecimal;
import java.lang.StringBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class Main {
	private static double lastTime;
	private static BigDecimal passedTime;
	private static int[] sizes = {10, 100, 1000, 10000, 100000};
	private static ArrayList<int[]> originalArrays;
	private static ArrayList<int[]> arrays;
	private static Object[][][] times;
	private static int iterations = 10;

	private static String[] sortingTypes = {"Bubble", "Insertion", "Merge", "Quick", "Selection", "Shell", "Heap", "Counting", "Radix", "Radix Iterativ", "Bucket"};

	public static void main(String args[]) {
		lastTime = 0;
		passedTime = BigDecimal.valueOf(0);
		times = new Object[sizes.length][11][2];
		originalArrays = new ArrayList<int[]>(sizes.length);

		Random rand = new Random();

		for (int s = 0; s < sizes.length; s++) {
			int[] arr = new int[sizes[s]];
			for (int i = 0; i < sizes[s]; i++)
				arr[i] = rand.nextInt(sizes[s]);
			originalArrays.add(arr);
		}
		arrays = new ArrayList<int[]>(originalArrays);

		System.out.println("\n\nAll times are average times of " + iterations + " iterations\n\n");

		// doBubbleSort();
		// doInsertionSort();
		// doMergeSort();
		// doQuickSort();      //Schmeisst bei letztem Durchgang StackOverFlow Error > zu viele Rekursionen
		// doSelectionSort();
		// doShellSort();
		// doHeapSort();
		// doCountingSort();
		// doRadixSort();
		// doRadixSortIt();
		// doBucketSort();
		System.out.println();

		for (int i = 0; i < sizes.length; i++) {
			sortTimes(times[i]);
		}

		for (int i = 0; i < sizes.length; i++) {
			String p = String.format("--- Winner with [%s] Elements ---", withCommas(sizes[i]));
			System.out.println(p);
			for (int j = 0; j < times[i].length; j++) {
				System.out.println(String.format("%s. %s Sort = %s ms", j, times[i][j][0], times[i][j][1]));
			}
			System.out.println(fill(p, '-') + "\n");
		}

	}


	private static void sortTimes(Object array[][]) {
		for (int i = 0; i < array.length - 1; i++) {
			for (int j = 0; j < array.length - i - 1; j++) {
				if ((double) array[j][1] > (double) array[j + 1][1]) {
					Object[] temp = array[j];
					array[j] = array[j + 1];
					array[j + 1] = temp;
				}
			}
		}
	}







	private static void doSorting() {
		for (int type = 0; type < 11; type++) {
			messureSortingTime(arrays[type], type);
		}
	}

	private static void messureSortingTime(int[] array, int type) {
		String printOut = String.format("--- %s Sort ---", sortingTypes[type]);
		System.out.println(printOut);

		for (int s = 0; s < sizes.length; s++) {
			double averageTime = 0;

			for (int i = 0; i < iterations; i++) {
				lastTime = System.nanoTime();
				sort(arrays.get(s), type);
				passedTime = BigDecimal.valueOf(System.nanoTime() - lastTime);
				passedTime.divide(BigDecimal.valueOf(1000000));
				System.out.println(String.format("%s. %s > %s ms", i, withCommas(sizes[s]), averageTime));
				averageTime += passedTime.doubleValue();
				arrays = new ArrayList<int[]>(originalArrays);
			}
			averageTime /= iterations;
			times[s][0][0] = "Bubble";
			times[s][0][1] = averageTime;
			System.out.println(String.format("%s > %s ms", withCommas(sizes[s]), averageTime));
		}

		System.out.println(fill(printOut, '-'));
		System.out.println();
		arrays = new ArrayList<int[]>(originalArrays);
	}

	private static boolean sort(int[] array, int type) {
		boolean failed = false;
		try {
			switch (type) {
			case 0:
				bubbleSort(array);
				break;
			case 1:
				insertionSort(array);
				break;
			case 2:
				mergeSort(array);
				break;
			case 3:
				quickSort(array);
				break;
			case 4:
				selectionSort(array);
				break;
			case 5:
				shellSort(array);
				break;
			case 6:
				heapSort(array);
				break;
			case 7:
				countingSort(array);
				break;
			case 8:
				radixSort(array);
				break;
			case 9:
				radixIterativSort(array);
				break;
			case 10:
				bucketSort(array);
				break;
			}
		} catch (Exception e) {
			failed = true;
		}
		return !failed;
	}








	private static void doBubbleSort() {
		System.out.println("--- Bubble Sort ---");

		for (int s = 0; s < sizes.length; s++) {
			double averageTime = 0;

			for (int i = 0; i < iterations; i++) {
				lastTime = System.nanoTime();
				bubbleSort(arrays.get(s));
				passedTime = BigDecimal.valueOf(System.nanoTime() - lastTime);
				passedTime.divide(BigDecimal.valueOf(1000000));
				System.out.println(String.format("%s. %s > %s ms", i, withCommas(sizes[s]), averageTime));
				averageTime += passedTime.doubleValue();
				arrays = new ArrayList<int[]>(originalArrays);
			}
			averageTime /= iterations;
			times[s][0][0] = "Bubble";
			times[s][0][1] = averageTime;
			System.out.println(String.format("%s > %s ms", withCommas(sizes[s]), averageTime));
		}

		System.out.println("-------------------");
		System.out.println();
		arrays = new ArrayList<int[]>(originalArrays);
	}


	




	private static String withCommas(int a) {
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

	private static String fill(String s, char f) {
		String n = "";
		for (int i = 0; i < s.length(); i++) n += f;
		return n;
	}

	private static void printf(String format, Object... args) {
		System.out.print(String.format(format, args));
	}

	private static void printlnf(String format, Object... args) {
		System.out.println(String.format(format, args));
	}

	private static void println(Object what) {
		if (what == null) {
			System.out.println("null");
		} else if (what.getClass().isArray()) {
			System.out.println("is Array");
		} else {
			System.out.println(what);
			System.out.flush();
		}
	}
}
