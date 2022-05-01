import java.util.Random;


public class Main {

	public static void main(String args[]) {
		Random rand = new Random();
		double[] array = new double[100];
		for (int i = 0; i < array.length; i++) array[i] = rand.nextDouble();
		double[] newArray = Sorter.mergeSort(array);
		for (int INDEX = 0; INDEX < newArray.length; INDEX++)
			System.out.println("[" + INDEX + "] " + newArray[INDEX]);
	}

}
