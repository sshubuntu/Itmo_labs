import static java.lang.Math. *;

public class Lab1_Main {
	// создание функции для проверки наличия элемента в массиве
	public static boolean check_function(long[] arr, long num) {
		boolean result = false;
		for (long i:arr){
			if (i==num) {
				result=true;
				break;
			}
		}
		return result;
	}
	//  создание статического метода для заполнения двумерного массива по условию
	public static void insertion(long[] arr1, float[] arr2, double[][] arr3,
								 int len_1, int len_2, long[] num_set) {
		for (int i=0; i< len_1; i++) {
			for (int j = 0; j < len_2; j++) {
				if (arr1[i] == 6) arr3[i][j] = pow(E, log(acos((arr2[j] + 3.5) / 11)));
				else if (check_function(num_set, arr1[i])) arr3[i][j] = cbrt(log(pow(E, arr2[j])));
				else arr3[i][j] = log(pow(sin(sin(cos(exp(arr2[j])))), 2));
			}
		}
	}

	// создание статического метода для вывода двумерного массива
	public static void output(double[][] arr) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 12; j++) {
				System.out.printf("%10.5f;", arr[i][j]); // форматируем вывод до пяти знаков после запятой
			}
			if (i != 8) System.out.println();
		}
	}
	public static void main(String[] args) {
		//использования констант для длин массивов
		final int len_w = 9;
		final int len_x = 12;
		// создание одномерного массива типа long
		long[] w = new long[len_w];
		// определение нулевого индекса массива
		int index = 0;
		//заполнение массива четными числами от 6 до 22 в порядке убывания
		for (int i = 22; i >= 6; i -= 2) {
			w[index] = i;
			index++;
		}
		// создание одномерного массива типа float
		float[] x = new float[len_x];
		//заполнение массива рандомными числами в диапазоне от -2.0 до 9.0
		for (int i = 0; i < len_x; i++) {
			x[i] = ((float)random()* (float)(9.+2.))-(float)2.;
		}
		// создание двумерного массива типа double
		double [][] w1 = new double[len_w][len_x];
		// создание и инициализация массива для проверки условия
		long[] num_set = {10,12,16,18};
		// заполнение массива с помощью статического метода
		insertion(w,x,w1, len_w,len_x,  num_set);
		// вывод массива с помощью статического метода
		output(w1);
	}
}







