package com.nrg.sdnsimulator.core.utility;

import java.util.Random;

public class RandomVariableGenerator extends Random {
	private static final long serialVersionUID = 1L;
	private final int MAXINT = Integer.MAX_VALUE;
	private Random rand;
	private int seed;

	public RandomVariableGenerator(int seed) {
		this.seed = seed;
		rand = new Random(seed);
	}

	public void changeRngSeed(int newSeed) {
		this.seed = newSeed;
		rand = new Random(seed);
	}

	/**********************************************************************/
	/** Generating a RANDOM Double NUMBER uniformly from (0,1) ************/
	/**********************************************************************/
	private double equalLikly() {
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

	/***********************************************************************/
	/** Generating an Exponential RANDOM Double NUMBER with mean = lambda **/
	/***********************************************************************/
	private double getNextExponential(double mean) {
		double lambda = 1 / mean;
		return ((-1) * (1 / lambda)) * Math.log(1.0 - equalLikly());
	}

	/***********************************************************************/
	/** Generating a Gamma double NUMBER with mean = mean and std = alpha **/
	/***********************************************************************/
	private double getNextGamma(double mean, double std) {
		return 0;
	}

	/**************************************************************************/
	/** Generating a Gaussian double NUMBER with mean = mean and std = alpha **/
	/**************************************************************************/
	private double getNextGaussian(double mean, double std) {
		return (rand.nextGaussian() * std) + mean;
	}

	/***************************************************************************/
	/** Generating a LogNormal double NUMBER with mean = mean and std = alpha **/
	/***************************************************************************/
	private double getNextLogNormal(double mean, double std) {
		mean = Math.log(mean);
		std = Math.log(std);
		// Debugger.debugToConsole("---------------------------------------");
		double gaussian = getNextGaussian(mean, std);
		double rvg = Math.exp(gaussian);
		// Debugger.debugToConsole("lnx = " + lnx);
		// Debugger.debugToConsole("STD = " + std);
		// Debugger.debugToConsole("The Gaussian is: " + gaussian);
		// Debugger.debugToConsole("The Log Normal RVG: " + rvg);
		return rvg;
	}

	/**********************************************************************/
	/** Generating a RANDOM double NUMBER uniformly from [lower, upper] ***/
	/**********************************************************************/
	public double getNextUniform(double lower, double upper) {
		double randnum;
		randnum = lower + equalLikly() * (upper - lower + 1);
		return randnum;
	}

	public double getNextValue(int distribution, double mean, double standardDeviation) {
		switch (distribution) {
		case Keywords.RandomVariableGenerator.Distributions.Constant:
			return mean;
		case Keywords.RandomVariableGenerator.Distributions.Uniform:
			// TODO the formula must be corrected
			return getNextUniform(mean - standardDeviation, mean + standardDeviation);
		case Keywords.RandomVariableGenerator.Distributions.Exponential:
			return getNextExponential(mean);
		case Keywords.RandomVariableGenerator.Distributions.Guassian:
			return getNextGaussian(mean, standardDeviation);
		case Keywords.RandomVariableGenerator.Distributions.LogNormal:
			return getNextLogNormal(mean, standardDeviation);
		case Keywords.RandomVariableGenerator.Distributions.Gamma:
			return getNextGamma(mean, standardDeviation);
		default:
			break;
		}
		return 0;
	}

	public void resetRng() {
		rand = new Random(seed);
	}

}
