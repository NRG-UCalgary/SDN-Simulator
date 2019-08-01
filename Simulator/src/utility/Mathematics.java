package utility;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Mathematics {

	public static float addFloat(float a, float b) {
		BigDecimal ap = BigDecimal.valueOf(Double.valueOf(Float.toString(a)));
		BigDecimal bp = BigDecimal.valueOf(Double.valueOf(Float.toString(b)));
		float result = ap.add(bp).floatValue();
		return result;
	}

	public static double average(ArrayList<Float> values) throws NullPointerException {
		double sum = 0;
		for (double value : values) {
			sum += value;
		}
		return sum / (double) values.size();

	}

	public static double bitPerSecondTobitPerMicroSecond(double band) {
		return band * Math.pow(10, -6);
	}

	public static float divideFloat(float a, float b) {
		BigDecimal ap = BigDecimal.valueOf(Double.valueOf(Float.toString(a)));
		BigDecimal bp = BigDecimal.valueOf(Double.valueOf(Float.toString(b)));
		float result = ap.divide(bp).floatValue();
		return result;
	}

	public static double gigabitPerSecondTobitPerMicroSecond(double band) {
		return bitPerSecondTobitPerMicroSecond(gigaToBase(band));
	}

	public static double gigaToBase(double num) {
		return num * Math.pow(10, 9);
	}

	public static boolean isEven(int num) {
		if (num % 2 == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static double kiloToBase(double num) {
		return num * Math.pow(10, 3);
	}

	public static long lcm(ArrayList<Integer> vals) {
		long lcm_of_array_elements = 1;
		int divisor = 2;

		while (true) {
			int counter = 0;
			boolean divisible = false;

			for (int i = 0; i < vals.size(); i++) {

				// lcm_of_array_elements (n1, n2, ... 0) = 0.
				// For negative number we convert into
				// positive and calculate lcm_of_array_elements.

				if (vals.get(i) == 0) {
					return 0;
				} else if (vals.get(i) < 0) {
					vals.set(i, vals.get(i) * (-1));
				}
				if (vals.get(i) == 1) {
					counter++;
				}

				// Divide vals by devisor if complete
				// division i.e. without remainder then replace
				// number with quotient; used for find next factor
				if (vals.get(i) % divisor == 0) {
					divisible = true;
					vals.set(i, vals.get(i) / divisor);
				}
			}

			// If divisor able to completely divide any number
			// from array multiply with lcm_of_array_elements
			// and store into lcm_of_array_elements and continue
			// to same divisor for next factor finding.
			// else increment divisor
			if (divisible) {
				lcm_of_array_elements = lcm_of_array_elements * divisor;
			} else {
				divisor++;
			}

			// Check if all vals is 1 indicate
			// we found all factors and terminate while loop.
			if (counter == vals.size()) {
				return (int) lcm_of_array_elements;
			}
		}
	}

	public static double Max(double a, double b) {
		if (a >= b) {
			return a;
		} else {
			return b;
		}
	}

	public static double megabitPerSecondTobitPerMicroSecond(double band) {
		return bitPerSecondTobitPerMicroSecond(megaToBase(band));
	}

	public static double megaToBase(double num) {
		return num * Math.pow(10, 6);
	}

	public static double microToBase(double num) {
		return num / Math.pow(10, 6);
	}

	public static double microToMilli(double num) {
		return num / Math.pow(10, 3);
	}

	public static double milliToBase(double num) {
		return num / Math.pow(10, 3);
	}

	public static double milliToMicro(double num) {
		return num * Math.pow(10, 3);
	}

	public static double minInteger(double a, double b) {
		if (a <= b) {
			return a;
		} else {
			return b;
		}
	}

	public static double standartDeviation(ArrayList<Float> values) throws NullPointerException {
		return Math.sqrt(variance(values));
	}

	public static float subtractFloat(float a, float b) {
		BigDecimal ap = BigDecimal.valueOf(Double.valueOf(Float.toString(a)));
		BigDecimal bp = BigDecimal.valueOf(Double.valueOf(Float.toString(b)));
		float result = ap.subtract(bp).floatValue();
		return result;
	}

	public static double variance(ArrayList<Float> values) throws NullPointerException {
		double sum = 0;
		double mean = average(values);
		for (double value : values) {
			sum += Math.pow((value - mean), 2);
		}
		return sum / (double) values.size();
	}

	public Mathematics() {
	}
}
