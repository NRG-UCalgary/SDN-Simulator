package nrg.sdnsimulator.core.system;

import nrg.sdnsimulator.core.Simulator;

public class SimApp {
	public static void main(String[] args) {
		Simulator sim = new Simulator();

	}

	public static void error(String className, String methodName, String errorMessage) {
		print("Error--inClass--" + className + "." + methodName + "()::" + errorMessage);
	}

	public static void generatingOutputFiles() {
		print("Generating the output files...");
	}

	public static void outputGenerationDone() {
		print("Output files generated.");
	}

	public static void print(Object o) {
		System.out.println(o);
	}

	public static void simulationDoneMessage() {
		print("  Simulation done.");
	}

	public static void singleFactoractorMessage(String factorName, String factorValue) {
		print("  Starting the simulation for " + factorName + " = " + factorValue);
	}

	public static void studyCompletionMessage() {
		print("Study Completed.");
	}

	public static void studyStartMessage(String studyName) {
		print("Starting the " + studyName + " study...");
	}
}
