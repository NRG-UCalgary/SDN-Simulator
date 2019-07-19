package system.utility;

import java.util.Random;

public class RandomVariableGenerator extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int MAXINT = Integer.MAX_VALUE;
	int distributionType;
	int seed;
	Random rand;

	public RandomVariableGenerator(int seed) {
		this.seed = seed;
		rand = new Random(seed);
	}

	public void changeRngSeed(int newSeed) {
		this.seed = newSeed;
		rand = new Random(seed);
	}

	public void resetRng() {
		rand = new Random(seed);
	}
	
	
	
	
	/**********************************************************************/
	/** Generating a RANDOM Double NUMBER uniformly from (0,1) ************/
	/**********************************************************************/
	public double equalLikly() {
		double randnum;
		randnum = rand.nextInt(MAXINT - 1) + 1;
		if (randnum == MAXINT) {
			System.out.print("The MAXINT is generated!: " + randnum + "\n");
			randnum = rand.nextInt(MAXINT - 1) + 1;
		} else if (randnum == 0) {
			System.out.print("Zero is generated!:" + randnum + "\n");
			randnum = rand.nextInt(MAXINT - 1) + 1;
		}

		randnum = randnum / (double) MAXINT;
		return randnum;
	}

	/**********************************************************************/
	/** Generating a RANDOM INTEGER NUMBER uniformly from [lower, upper] **/
	/**********************************************************************/
	public int getNextUniformInteger(int lower, int upper) {
		int randnum;
		randnum = (int) (lower + equalLikly() * (upper - lower + 1));
		return randnum;
	}

	/**********************************************************************/
	/** Generating a RANDOM double NUMBER uniformly from [lower, upper] ***/
	/**********************************************************************/
	public double getNextUniform(double lower, double upper) {
		double randnum;
		randnum = lower + equalLikly() * (upper - lower + 1);
		return randnum;
	}

	/***********************************************************************/
	/** Generating an Exponential RANDOM Double NUMBER with mean = lambda **/
	/***********************************************************************/
	public double getNextExponential(double mean) {
		double lambda = 1 / mean;
		return ((-1) * (1 / lambda)) * Math.log(1.0 - equalLikly());
	}

	/**************************************************************************/
	/** Generating a Gaussian double NUMBER with mean = mean and std = alpha **/
	/**************************************************************************/
	public double getNextGaussian(double mean, double std) {
		return (rand.nextGaussian() * std) + mean;
	}

	/***************************************************************************/
	/** Generating a Gaussian Integer NUMBER with mean = mean and std = alpha **/
	/***************************************************************************/
	public int getNextGuassianInteger(int mean, double std) {
		return (int) Math.round(((rand.nextGaussian() * std))) + mean;
	}

}
