package system;

import experiments.*;
import experiments.scenarios.*;
import experiments.scenarios.myScenarios.*;
import experiments.validations.Validationv2;

public class Main {
	public static void main(String[] args) {

		Validationv2 vTest = new Validationv2();
		// vTest.validate();

		Scenario interArrivalFlowSize = new InterArrivalAndFlowSizes();
		// interArrivalFlowSize.executeTest();

		Scenario gammaFlowSize = new GammaAndFlowInterArrival();
		// gammaFlowSize.executeTest();

		Scenario numFlowsInterArrival = new NumberOfFlowsAndFlowInterArrival();
		// numFlowsInterArrival.executeTest();

		Scenario numFlowsSizes = new NumberOfFlowsAndFlowSizes();
		numFlowsSizes.executeTest();

		Scenario incastGammaNumFlows = new IncastFlowNumbersGamma();
		// incastGammaNumFlows.executeTest();
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
