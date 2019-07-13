package system.utility;

import java.util.ArrayList;

public class Mathematics {

	public Mathematics() {
	}

	public static int MegabitPerSecondTobitPerMsecond(double band) {
		return (int) (band * (int) Math.pow(10, 3));
	}

	public static double Max(double a, double b) {
		if (a >= b) {
			return a;
		} else {
			return b;
		}
	}

	public static int minInteger(int a, int b) {
		if (a <= b) {
			return a;
		} else {
			return b;
		}
	}

	public static int lcm(ArrayList<Integer> vals) {
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

	public static boolean isEven(int num) {
		if (num % 2 == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static double microToMilli(double num) {
		return num / Math.pow(10, 3);
	}

	public static double milliToBase(double num) {
		return num / Math.pow(10, 3);
	}

	public static double gigaToBase(double num) {
		return num * Math.pow(10, 9);
	}

	public static double megaToBase(double num) {
		return num * Math.pow(10, 6);
	}

	public static double kiloToBase(double num) {
		return num * Math.pow(10, 3);
	}

}
