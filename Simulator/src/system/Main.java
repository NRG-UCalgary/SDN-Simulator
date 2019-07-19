package system;

import java.util.ArrayList;

import experiments.scenarios.NumberOfFlowsPerFlowSizeDistributionStudy;
import system.utility.Keywords;

public class Main {

	public static void main(String[] args) {
		NumberOfFlowsPerFlowSizeDistributionStudy numberOfFlowsStudy = new NumberOfFlowsPerFlowSizeDistributionStudy();
		ArrayList<Integer> numberOfFlowsValues = new ArrayList<Integer>();
		for (int i = 1; i <= 10; i++) {
			numberOfFlowsValues.add(i);
		}
		ArrayList<Integer> flowSizeDistributions = new ArrayList<Integer>();
		flowSizeDistributions.add(Keywords.Uniform);
		flowSizeDistributions.add(Keywords.Guassian);
		flowSizeDistributions.add(Keywords.Exponential);
		numberOfFlowsStudy.executeTest(numberOfFlowsValues, flowSizeDistributions, Keywords.LAN, Keywords.Dumbbell,
				Keywords.GeneralTraffic);
	}

	public static void print(Object o) {
		System.out.println(o);
	}

	public static void error(String className, String methodName, String errorMessage) {
		print("Error--inClass--" + className + "." + methodName + "()::" + errorMessage);
	}

	public static void studyStartMessage(String studyName) {
		print("Starting the " + studyName + " study...");
	}

	public static void singleFactoractorMessage(String factorName, String factorValue) {
		print("  Starting the simulation for " + factorName + " = " + factorValue);
	}

	public static void simulationDoneMessage() {
		print("  Simulation done.");
	}

	public static void studyCompletionMessage() {
		print("Study Completed.");
	}

	public static void generatingOutputFiles() {
		print("Generating the output files...");
	}

	public static void outputGenerationDone() {
		print("Output files generated.");
	}
}
