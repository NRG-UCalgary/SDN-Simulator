package tests;

public class RandomVariableGenerator {
	private int seed;

	public RandomVariableGenerator(int seed) {
		this.seed = seed;
	}

	public int generate() {
		int dummy = seed;
		return dummy;

	}

	public void setSeed(int seed) {
		this.seed = seed;
	}

}
