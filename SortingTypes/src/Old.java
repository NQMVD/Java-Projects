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
		// doQuickSort();  //Schmeisst bei letztem Durchgang StackOverFlow Error > zu viele Rekursionen
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

	private static void sort(int[] array, int type) {
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

	private static void doInsertionSort() {
		System.out.println("--- Insertion Sort ---");

		for (int i = 0; i < sizes.length; i++) {
			lastTime = System.nanoTime();
			insertionSort(arrays.get(i));
			passedTime = (System.nanoTime() - lastTime) / 1000000;
			times[i][1][0] = "Insertion";
			times[i][1][1] = passedTime;
			System.out.println(String.format("%s > %s ms", withCommas(sizes[i]), passedTime));
		}

		System.out.println("----------------------");
		System.out.println();
		arrays = new ArrayList<int[]>(originalArrays);
	}

	private static void doMergeSort() {
		System.out.println("--- Merge Sort ---");

		for (int i = 0; i < sizes.length; i++) {
			lastTime = System.nanoTime();
			mergeSort(arrays.get(i));
			passedTime = (System.nanoTime() - lastTime) / 1000000;
			times[i][2][0] = "Merge";
			times[i][2][1] = passedTime;
			System.out.println(String.format("%s > %s ms", withCommas(sizes[i]), passedTime));
		}

		System.out.println("------------------");
		System.out.println();
		arrays = new ArrayList<int[]>(originalArrays);
	}

	private static void doQuickSort() {
		System.out.println("--- Quick Sort ---");
		boolean failed = false;
		for (int i = 0; i < sizes.length; i++) {
			lastTime = System.nanoTime();
			try {
				quickSort(arrays.get(i));
			} catch (StackOverflowError e) {
				failed = true;
			}
			if (failed) {
				passedTime = 99999;
			} else {
				passedTime = (System.nanoTime() - lastTime) / 1000000;
			}
			times[i][3][0] = "Quick";
			times[i][3][1] = passedTime;
			System.out.println(String.format("%s > %s ms", withCommas(sizes[i]), passedTime));
		}

		System.out.println("------------------");
		System.out.println();
		arrays = new ArrayList<int[]>(originalArrays);
	}

	private static void doSelectionSort() {
		System.out.println("--- Selection Sort ---");
		for (int i = 0; i < sizes.length; i++) {
			lastTime = System.nanoTime();
			selectionSort(arrays.get(i));
			passedTime = (System.nanoTime() - lastTime) / 1000000;
			times[i][4][0] = "Selection";
			times[i][4][1] = passedTime;
			System.out.println(String.format("%s > %s ms", withCommas(sizes[i]), passedTime));
		}

		System.out.println("----------------------");
		System.out.println();
		arrays = new ArrayList<int[]>(originalArrays);
	}

	private static void doShellSort() {
		System.out.println("--- Shell Sort ---");
		for (int i = 0; i < sizes.length; i++) {
			lastTime = System.nanoTime();
			shellSort(arrays.get(i));
			passedTime = (System.nanoTime() - lastTime) / 1000000;
			times[i][5][0] = "Shell";
			times[i][5][1] = passedTime;
			System.out.println(String.format("%s > %s ms", withCommas(sizes[i]), passedTime));
		}

		System.out.println("------------------");
		System.out.println();
		arrays = new ArrayList<int[]>(originalArrays);
	}

	private static void doHeapSort() {
		System.out.println("--- Heap Sort ---");
		for (int i = 0; i < sizes.length; i++) {
			lastTime = System.nanoTime();
			heapSort(arrays.get(i));
			passedTime = (System.nanoTime() - lastTime) / 1000000;
			times[i][6][0] = "Heap";
			times[i][6][1] = passedTime;
			System.out.println(String.format("%s > %s ms", withCommas(sizes[i]), passedTime));
		}

		System.out.println("-----------------");
		System.out.println();
		arrays = new ArrayList<int[]>(originalArrays);
	}

	private static void doCountingSort() {
		System.out.println("--- Counting Sort ---");
		for (int i = 0; i < sizes.length; i++) {
			lastTime = System.nanoTime();
			countingSort(arrays.get(i));
			passedTime = (System.nanoTime() - lastTime) / 1000000;
			times[i][7][0] = "Counting";
			times[i][7][1] = passedTime;
			System.out.println(String.format("%s > %s ms", withCommas(sizes[i]), passedTime));
		}

		System.out.println("---------------------");
		System.out.println();
		arrays = new ArrayList<int[]>(originalArrays);
	}

	private static void doRadixSort() {
		System.out.println("--- Radix Sort ---");
		for (int i = 0; i < sizes.length; i++) {
			lastTime = System.nanoTime();
			radixSort(arrays.get(i));
			passedTime = (System.nanoTime() - lastTime) / 1000000;
			times[i][8][0] = "Radix";
			times[i][8][1] = passedTime;
			System.out.println(String.format("%s > %s ms", withCommas(sizes[i]), passedTime));
		}

		System.out.println("------------------");
		System.out.println();
		arrays = new ArrayList<int[]>(originalArrays);
	}

	private static void doRadixSortIt() {
		System.out.println("--- Radix Sort Iterativ ---");
		for (int i = 0; i < sizes.length; i++) {
			lastTime = System.nanoTime();
			radixSortIt(arrays.get(i));
			passedTime = (System.nanoTime() - lastTime) / 1000000;
			times[i][9][0] = "Radix Iterativ";
			times[i][9][1] = passedTime;
			System.out.println(String.format("%s > %s ms", withCommas(sizes[i]), passedTime));
		}

		System.out.println("---------------------------");
		System.out.println();
		arrays = new ArrayList<int[]>(originalArrays);
	}

	private static void doBucketSort() {
		System.out.println("--- Bucket Sort ---");
		for (int i = 0; i < sizes.length; i++) {
			lastTime = System.nanoTime();
			bucketSort(arrays.get(i));
			passedTime = (System.nanoTime() - lastTime) / 1000000;
			times[i][10][0] = "Bucket";
			times[i][10][1] = passedTime;
			System.out.println(String.format("%s > %s ms", withCommas(sizes[i]), passedTime));
		}

		System.out.println("-------------------");
		System.out.println();
		arrays = new ArrayList<int[]>(originalArrays);
	}








	private static void bubbleSort(int array[]) {
		for (int i = 0; i < array.length - 1; i++) {
			for (int j = 0; j < array.length - i - 1; j++) {
				if (array[j] > array[j + 1]) {
					int temp = array[j];
					array[j] = array[j + 1];
					array[j + 1] = temp;
				}
			}
		}
	}

	private static void insertionSort(int[] array) {
		for (int i = 1; i < array.length; i++) {
			int temp = array[i];
			int j = i - 1;

			while (j >= 0 && array[j] > temp) {
				array[j + 1] = array[j];
				j--;
			}
			array[j + 1] = temp;
		}
	}


	private static void mergeSort(int[] array) {
		int length = array.length;
		if (length <= 1) return;

		int middle = length / 2;
		int[] leftArray = new int[middle];
		int[] rightArray = new int[length - middle];

		int i = 0;
		int j = 0;

		for (; i < length; i++) {
			if (i < middle)
				leftArray[i] = array[i];
			else {
				rightArray[j] = array[i];
				j++;
			}
		}
		mergeSort(leftArray);
		mergeSort(rightArray);
		merge(leftArray, rightArray, array);
	}

	private static void merge(int[] leftArray, int[] rightArray, int[] array) {
		int leftSize = array.length / 2;
		int rightSize = array.length - leftSize;
		int i = 0, l = 0, r = 0;

		while (l < leftSize && r < rightSize) {
			if (leftArray[l] < rightArray[r]) {
				array[i] = leftArray[l];
				i++;
				l++;
			} else {
				array[i] = rightArray[r];
				i++;
				r++;
			}
		}
		while (l < leftSize) {
			array[i] = leftArray[l];
			i++;
			l++;
		}
		while (r < rightSize) {
			array[i] = rightArray[r];
			i++;
			r++;
		}
	}

	private static void quickSort(int[] array) {
		quickSort(array, 0, array.length - 1);
	}

	private static void quickSort(int[] array, int start, int end) {
		if (end <= start) return;

		int pivot = partition(array, start, end);
		quickSort(array, start, pivot - 1);
		quickSort(array, pivot + 1, end);
	}

	private static int partition(int[] array, int start, int end) {
		int pivot = array[end];
		int i = start - 1;

		for (int j = start; j <= end - 1; j++) {
			if (array[j] < pivot) {
				i++;
				int temp = array[i];
				array[i] = array[j];
				array[j] = temp;
			}
		}

		i++;
		int temp = array[i];
		array[i] = array[end];
		array[end] = temp;

		return i;
	}


	private static void selectionSort(int[] array) {
		for (int i = 0; i < array.length - 1; i++) {
			int min = i;
			for (int j = i + 1; j < array.length; j++) {
				if (array[min] > array[j])
					min = j;
			}

			int temp = array[i];
			array[i] = array[min];
			array[min] = temp;
		}
	}

	public static void shellSort(int[] array) {
		int inner, outer;
		int tmp;
		int h = 1;
		while (h <= array.length / 4) {
			h = h * 4 + 1;
		}

		while (h > 0) {
			for (outer = h; outer < array.length; outer++) {
				tmp = array[outer];
				inner = outer;
				while (inner > h - 1 && array[inner - h ] >= tmp) {
					array[inner] = array[inner - h];
					inner -= h;
				}
				array[inner] = tmp;
			}
			h = (h - 1) / 4;
		}
	}

	private static void heapSort(int[] array) {
		buildMaxHeap(array);
		for (int i = array.length - 1; i > 0; i--) {
			swap(array, i, 0);
			seep(array, 0, i);
		}
	}
	// create maxHeap tree in array
	private static void buildMaxHeap(int[] arr) {
		for (int i = (arr.length / 2) - 1; i >= 0 ; i--) {
			seep(arr, i, arr.length);
		}
	}
	// seep - Downheap
	private static void seep(int[] arr, int i, int j) {

		while (i <= (j / 2) - 1) {

			// left child
			int childIndex = ((i + 1) * 2) - 1;
			// right child
			if (childIndex + 1 <= j - 1) {
				if (arr[childIndex] < arr[childIndex + 1]) {
					childIndex++;
				}
			}
			// check if seep is neccessary
			if (arr[i] < arr[childIndex]) {
				swap(arr, i, childIndex);
				i = childIndex;
			} else break;
		}
	}
	// swap
	private static void swap(int[] arr, int i, int childIndex) {
		int k = arr[i];
		arr[i] = arr[childIndex];
		arr[childIndex] = k;
	}

	private static void countingSort(int[] array) {
		int max = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] > max)
				max = array[i];
		}
		int[] helpArray = new int[max + 1];
		for (int i = 0; i < array.length; i++) {
			helpArray[array[i]]++;
		}
		int pos = 0;
		for (int i = 0; i <= max; i++) {
			for (int j = 0; j < helpArray[i]; j++) {
				array[pos] = i;
				pos++;
			}
		}
	}



	private static int maximum(int array[]) {
		int max = array[0];
		for (int a = 1; a < array.length; a++)
			if (array[a] > max)
				max = array[a];
		return max;
	}


	private static void countingsort(int array[], int factor) {
		int i = array.length;
		int output[] = new int[i];
		int a;
		int counting[] = new int[10];
		Arrays.fill(counting, 0);

		for (a = 0; a < i; a++)
			counting[ (array[a] / factor) % 10 ]++;
		for (a = 1; a < 10; a++)
			counting[a] += counting[a - 1];

		for (a = i - 1; a >= 0; a--) {
			output[counting[ (array[a] / factor) % 10 ] - 1] = array[a];
			counting[ (array[a] / factor) % 10 ]--;
		}

		for (a = 0; a < i; a++)
			array[a] = output[a];
		System.arraycopy(output, 0, array, 0, i);
	}

	private static void radixSort(int[] array) {
		int m = maximum(array);

		for (int factor = 1; m / factor > 0; factor *= 10)
			countingsort(array, factor);
	}

	private static void radixIterativSort(int[] array) {
		int     nummer;
		int[]   anzahlfach = new int[2];
		int[][] fach  = new int[2][array.length];
		for (int j = 0; j < 32; j++) {
			anzahlfach[0] = 0;
			anzahlfach[1] = 0;
			for (int k = 0; k < array.length; k++) {
				nummer = (array[k] >> j) & 1;
				fach[nummer][anzahlfach[nummer]++] = array[k];
			}
		}
		System.arraycopy(fach[0], 0, array, 0, anzahlfach[0]);
		System.arraycopy(fach[1], 0, array, anzahlfach[0], anzahlfach[1]);
	}

	private static void bucketSortFAIL(int[] array) {
		ArrayList<Integer>[] buckets = new ArrayList[array.length];
		for (int i = 0; i < array.length; i++) {
			buckets[i] = new ArrayList<Integer>();
		}

		int bucket = 0;
		for (int i = 0; i < array.length; i++) {
			bucket = (int) array[i] * array.length;
			buckets[bucket].add(array[i]);
		}

		for (int i = 0; i < array.length; i++) {
			int[] arr = new int[buckets[i].size()];
			for (int j = 0; j < buckets[i].size(); j++) arr[j] = buckets[i].get(j);
			countingSort(arr);
		}

		int index = 0;
		for (int i = 0; i < array.length; i++) {
			int len = buckets[i].size();
			for (int j = 0; j < len; j++) {
				array[index++] = buckets[i].get(j);
			}
		}
	}

	private static void bucketSort(int[] array) {
		int max = maximum(array);
		int[] bucket = new int[max + 1];

		for (int i = 0; i < array.length; i++)
			bucket[array[i]]++;

		int outPos = 0;
		for (int i = 0; i < bucket.length; i++) {
			for (int j = 0; j < bucket[i]; j++) {
				array[outPos++] = i;
			}
		}
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
			printArray(what);
		} else {
			System.out.println(what);
			System.out.flush();
		}
	}


	private static void printArray(Object what) {
		if (what == null) {
			// special case since this does fugly things on > 1.1
			System.out.println("null");
		} else {
			String name = what.getClass().getName();
			if (name.charAt(0) == '[') {
				switch (name.charAt(1)) {
				case '[':
					// don't even mess with multi-dimensional arrays (case '[')
					// or anything else that's not int, float, boolean, char
					System.out.println(what);
					break;

				case 'L':
					// print a 1D array of objects as individual elements
					Object[] poo = (Object[]) what;
					for (int i = 0; i < poo.length; i++) {
						if (poo[i] instanceof String) {
							System.out.println("[" + i + "] \"" + poo[i] + "\"");
						} else {
							System.out.println("[" + i + "] " + poo[i]);
						}
					}
					break;

				case 'Z':  // boolean
					boolean[] zz = (boolean[]) what;
					for (int i = 0; i < zz.length; i++) {
						System.out.println("[" + i + "] " + zz[i]);
					}
					break;

				case 'B':  // byte
					byte[] bb = (byte[]) what;
					for (int i = 0; i < bb.length; i++) {
						System.out.println("[" + i + "] " + bb[i]);
					}
					break;

				case 'C':  // char
					char[] cc = (char[]) what;
					for (int i = 0; i < cc.length; i++) {
						System.out.println("[" + i + "] '" + cc[i] + "'");
					}
					break;

				case 'I':  // int
					int[] ii = (int[]) what;
					for (int i = 0; i < ii.length; i++) {
						System.out.println("[" + i + "] " + ii[i]);
					}
					break;

				case 'J':  // int
					long[] jj = (long[]) what;
					for (int i = 0; i < jj.length; i++) {
						System.out.println("[" + i + "] " + jj[i]);
					}
					break;

				case 'F':  // float
					float[] ff = (float[]) what;
					for (int i = 0; i < ff.length; i++) {
						System.out.println("[" + i + "] " + ff[i]);
					}
					break;

				case 'D':  // double
					double[] dd = (double[]) what;
					for (int i = 0; i < dd.length; i++) {
						System.out.println("[" + i + "] " + dd[i]);
					}
					break;

				default:
					System.out.println(what);
				}
			} else { // not an array
				System.out.println(what);
			}
		}
		System.out.flush();
	}
}
