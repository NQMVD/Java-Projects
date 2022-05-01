import java.util.Arrays;


public class Sorter {

	private static int[] intArray = new int[0];
	private static float[] floatArray = new float[0];
	private static double[] doubleArray = new double[0];
	private static long[] longArray = new long[0];
	private static short[] shortArray = new short[0];
	private static byte[] byteArray = new byte[0];

	public static int[] getintArray() {
		return intArray;
	}
	public static float[] getfloatArray() {
		return floatArray;
	}
	public static double[] getdoubleArray() {
		return doubleArray;
	}
	public static long[] getlongArray() {
		return longArray;
	}
	public static short[] getshortArray() {
		return shortArray;
	}
	public static byte[] getbyteArray() {
		return byteArray;
	}



	// ---------------------------------------- Bubble Sort ---------------------------------------- //

	// public static $[] bubbleSort($[] array) {
	// 	$Array = new $[array.length];
	// 	System.arraycopy(array, 0, $Array, 0, array.length);
	// 	for (int i = 0; i < $Array.length - 1; i++) for (int j = 0; j < $Array.length - i - 1; j++) if ($Array[j] > $Array[j + 1]) swap($Array, j, j + 1);
	// 	return $Array;
	// }

	public static int[] bubbleSort(int[] array) {
		intArray = new int[array.length];
		System.arraycopy(array, 0, intArray, 0, array.length);
		for (int i = 0; i < intArray.length - 1; i++) for (int j = 0; j < intArray.length - i - 1; j++) if (intArray[j] > intArray[j + 1]) swap(intArray, j, j + 1);
		return intArray;
	}

	public static float[] bubbleSort(float[] array) {
		floatArray = new float[array.length];
		System.arraycopy(array, 0, floatArray, 0, array.length);
		for (int i = 0; i < floatArray.length - 1; i++) for (int j = 0; j < floatArray.length - i - 1; j++) if (floatArray[j] > floatArray[j + 1]) swap(floatArray, j, j + 1);
		return floatArray;
	}

	public static double[] bubbleSort(double[] array) {
		doubleArray = new double[array.length];
		System.arraycopy(array, 0, doubleArray, 0, array.length);
		for (int i = 0; i < doubleArray.length - 1; i++) for (int j = 0; j < doubleArray.length - i - 1; j++) if (doubleArray[j] > doubleArray[j + 1]) swap(doubleArray, j, j + 1);
		return doubleArray;
	}

	public static long[] bubbleSort(long[] array) {
		longArray = new long[array.length];
		System.arraycopy(array, 0, longArray, 0, array.length);
		for (int i = 0; i < longArray.length - 1; i++) for (int j = 0; j < longArray.length - i - 1; j++) if (longArray[j] > longArray[j + 1]) swap(longArray, j, j + 1);
		return longArray;
	}

	public static short[] bubbleSort(short[] array) {
		shortArray = new short[array.length];
		System.arraycopy(array, 0, shortArray, 0, array.length);
		for (int i = 0; i < shortArray.length - 1; i++) for (int j = 0; j < shortArray.length - i - 1; j++) if (shortArray[j] > shortArray[j + 1]) swap(shortArray, j, j + 1);
		return shortArray;
	}

	public static byte[] bubbleSort(byte[] array) {
		byteArray = new byte[array.length];
		System.arraycopy(array, 0, byteArray, 0, array.length);
		for (int i = 0; i < byteArray.length - 1; i++) for (int j = 0; j < byteArray.length - i - 1; j++) if (byteArray[j] > byteArray[j + 1]) swap(byteArray, j, j + 1);
		return byteArray;
	}






	// ---------------------------------------- Insertion Sort ---------------------------------------- //

	// public static $[] insertionSort($[] array) {
	// 	$Array = new $[array.length];
	// 	System.arraycopy(array, 0, $Array, 0, array.length);
	// 	for (int i = 1; i < $Array.length; i++) {
	// 		$ temp = $Array[i];
	// 		int j = i - 1;
	// 		while (j >= 0 && $Array[j] > temp) {
	// 			$Array[j + 1] = $Array[j];
	// 			j--;
	// 		}
	// 		$Array[j + 1] = temp;
	// 	}
	// 	return $Array;
	// }

	public static int[] insertionSort(int[] array) {
		intArray = new int[array.length];
		System.arraycopy(array, 0, intArray, 0, array.length);
		for (int i = 1; i < intArray.length; i++) {
			int temp = intArray[i];
			int j = i - 1;
			while (j >= 0 && intArray[j] > temp) {
				intArray[j + 1] = intArray[j];
				j--;
			}
			intArray[j + 1] = temp;
		}
		return intArray;
	}

	public static float[] insertionSort(float[] array) {
		floatArray = new float[array.length];
		System.arraycopy(array, 0, floatArray, 0, array.length);
		for (int i = 1; i < floatArray.length; i++) {
			float temp = floatArray[i];
			int j = i - 1;
			while (j >= 0 && floatArray[j] > temp) {
				floatArray[j + 1] = floatArray[j];
				j--;
			}
			floatArray[j + 1] = temp;
		}
		return floatArray;
	}

	public static double[] insertionSort(double[] array) {
		doubleArray = new double[array.length];
		System.arraycopy(array, 0, doubleArray, 0, array.length);
		for (int i = 1; i < doubleArray.length; i++) {
			double temp = doubleArray[i];
			int j = i - 1;
			while (j >= 0 && doubleArray[j] > temp) {
				doubleArray[j + 1] = doubleArray[j];
				j--;
			}
			doubleArray[j + 1] = temp;
		}
		return doubleArray;
	}

	public static long[] insertionSort(long[] array) {
		longArray = new long[array.length];
		System.arraycopy(array, 0, longArray, 0, array.length);
		for (int i = 1; i < longArray.length; i++) {
			long temp = longArray[i];
			int j = i - 1;
			while (j >= 0 && longArray[j] > temp) {
				longArray[j + 1] = longArray[j];
				j--;
			}
			longArray[j + 1] = temp;
		}
		return longArray;
	}

	public static short[] insertionSort(short[] array) {
		shortArray = new short[array.length];
		System.arraycopy(array, 0, shortArray, 0, array.length);
		for (int i = 1; i < shortArray.length; i++) {
			short temp = shortArray[i];
			int j = i - 1;
			while (j >= 0 && shortArray[j] > temp) {
				shortArray[j + 1] = shortArray[j];
				j--;
			}
			shortArray[j + 1] = temp;
		}
		return shortArray;
	}

	public static byte[] insertionSort(byte[] array) {
		byteArray = new byte[array.length];
		System.arraycopy(array, 0, byteArray, 0, array.length);
		for (int i = 1; i < byteArray.length; i++) {
			byte temp = byteArray[i];
			int j = i - 1;
			while (j >= 0 && byteArray[j] > temp) {
				byteArray[j + 1] = byteArray[j];
				j--;
			}
			byteArray[j + 1] = temp;
		}
		return byteArray;
	}






	// ---------------------------------------- Merge Sort ---------------------------------------- //

	// public static $[] mergeSort($[] array) {
	// 	$[] result = new $[array.length];
	// 	System.arraycopy(array, 0, result, 0, result.length);

	// 	int length = result.length;
	// 	if (length <= 1) return;

	// 	int middle = length / 2;
	// 	int[] leftArray = new int[middle];
	// 	int[] rightArray = new int[length - middle];

	// 	int i = 0;
	// 	int j = 0;

	// 	for (; i < length; i++) {
	// 		if (i < middle)
	// 			leftArray[i] = result[i];
	// 		else {
	// 			rightArray[j] = result[i];
	// 			j++;
	// 		}
	// 	}
	// 	mergeSort(leftArray);
	// 	mergeSort(rightArray);
	// 	merge(leftArray, rightArray, result);

	// 	return result;
	// }

	// public static void merge(int[] leftArray, int[] rightArray, int[] array) {
	// 	int leftSize = array.length / 2;
	// 	int rightSize = array.length - leftSize;
	// 	int i = 0, l = 0, r = 0;

	// 	while (l < leftSize && r < rightSize) {
	// 		if (leftArray[l] < rightArray[r]) {
	// 			array[i] = leftArray[l];
	// 			i++;
	// 			l++;
	// 		} else {
	// 			array[i] = rightArray[r];
	// 			i++;
	// 			r++;
	// 		}
	// 	}
	// 	while (l < leftSize) {
	// 		array[i] = leftArray[l];
	// 		i++;
	// 		l++;
	// 	}
	// 	while (r < rightSize) {
	// 		array[i] = rightArray[r];
	// 		i++;
	// 		r++;
	// 	}
	// }

	static int counter = 0;
	public static double[] mergeSort(double[] array) {
		doubleArray = new double[array.length];
		System.arraycopy(array, 0, doubleArray, 0, array.length);
		myMergeSort(doubleArray);
		return doubleArray;
	}

	public static void myMergeSort(double[] array) {
		counter++;
		// System.out.println(counter);
		int length = doubleArray.length;

		int middle = length / 2;
		double[] leftArray = new double[middle];
		double[] rightArray = new double[length - middle];

		int i = 0;
		int j = 0;

		for (; i < length; i++) {
			if (i < middle)
				leftArray[i] = doubleArray[i];
			else {
				rightArray[j] = doubleArray[i];
				j++;
			}
		}
		mergeSort(leftArray);
		mergeSort(rightArray);
		merge(leftArray, rightArray, doubleArray);
	}

	public static void merge(double[] leftArray, double[] rightArray, double[] array) {
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

	public static void mergeSort(int[] array) {
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

	public static void merge(int[] leftArray, int[] rightArray, int[] array) {
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

	// ---------------------------------------- Quick Sort ---------------------------------------- //

	public static void quickSort(int[] array) {
		quickSort(array, 0, array.length - 1);
	}

	public static void quickSort(int[] array, int start, int end) {
		if (end <= start) return;

		int pivot = partition(array, start, end);
		quickSort(array, start, pivot - 1);
		quickSort(array, pivot + 1, end);
	}

	public static int partition(int[] array, int start, int end) {
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

	// ---------------------------------------- Selection Sort ---------------------------------------- //

	public static void selectionSort(int[] array) {
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


	// ---------------------------------------- Shell Sort ---------------------------------------- //

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


	// ---------------------------------------- Heap Sort ---------------------------------------- //

	public static void heapSort(int[] array) {
		buildMaxHeap(array);
		for (int i = array.length - 1; i > 0; i--) {
			swap(array, i, 0);
			seep(array, 0, i);
		}
	}
	// create maxHeap tree in array
	public static void buildMaxHeap(int[] arr) {
		for (int i = (arr.length / 2) - 1; i >= 0 ; i--) {
			seep(arr, i, arr.length);
		}
	}
	// seep - Downheap
	public static void seep(int[] arr, int i, int j) {

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
	public static void swap(int[] arr, int a, int b) {
		int k = arr[a];
		arr[a] = arr[b];
		arr[b] = k;
	}
	public static void swap(float[] arr, int a, int b) {
		float k = arr[a];
		arr[a] = arr[b];
		arr[b] = k;
	}
	public static void swap(double[] arr, int a, int b) {
		double k = arr[a];
		arr[a] = arr[b];
		arr[b] = k;
	}
	public static void swap(long[] arr, int a, int b) {
		long k = arr[a];
		arr[a] = arr[b];
		arr[b] = k;
	}
	public static void swap(short[] arr, int a, int b) {
		short k = arr[a];
		arr[a] = arr[b];
		arr[b] = k;
	}
	public static void swap(byte[] arr, int a, int b) {
		byte k = arr[a];
		arr[a] = arr[b];
		arr[b] = k;
	}



	// ---------------------------------------- Counting Sort ---------------------------------------- //

	public static void countingSort(int[] array) {
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


	// ---------------------------------------- Radix Sort ---------------------------------------- //

	public static int maximum(int array[]) {
		int max = array[0];
		for (int a = 1; a < array.length; a++)
			if (array[a] > max)
				max = array[a];
		return max;
	}


	public static void countingsort(int array[], int factor) {
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

	public static void radixSort(int[] array) {
		int m = maximum(array);

		for (int factor = 1; m / factor > 0; factor *= 10)
			countingsort(array, factor);
	}


	// ---------------------------------------- Radix Iterative Sort ---------------------------------------- //

	public static void radixIterativeSort(int[] array) {
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


	// ---------------------------------------- Bucket Sort ---------------------------------------- //

	public static void bucketSort(int[] array) {
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
}