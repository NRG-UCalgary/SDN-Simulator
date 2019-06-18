package system.utility;

import java.util.ArrayList;

public class Mathematics {

	public Mathematics() {
	}

	public static int MegabitPerSecondTobitPerMsecond(double band) {
		return (int) (band * (int) Math.pow(10, 4));
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
		if (vals.size() == 1) {
			return vals.get(0);
		} else {
			return (int) ((int) multiply(vals) / (double) findGCD(vals));
		}
	}

	public static int gcd(int a, int b) {
		if (a == 0) {
			return b;
		} else {
			return gcd(b % a, a);
		}
	}

	public static int findGCD(ArrayList<Integer> vals) {

		int result = vals.get(0);
		for (int val : vals) {
			result = gcd(val, result);
		}
		return result;

	}

	public static int multiply(ArrayList<Integer> vals) {
		int sum = 0;
		for (int value : vals) {
			sum += value;
		}
		return sum;
	}
}
