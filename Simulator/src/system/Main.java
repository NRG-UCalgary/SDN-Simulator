package system;

import experiments.Validationv2;
import experiments.scenarios.*;

public class Main {
	public static void main(String[] args) {

		Validationv2 vTest = new Validationv2();
		Scenario numFlowsAndBeta = new NumberOfFlowsAndBeta();
		Scenario alphaAndBeta = new AlphaAndBeta();
		Scenario interArrivalFlowSize = new InterArrivalAndFlowSizes();
		Scenario numFlowSize = new NumberOfFlowsAndFlowSizes();
		Scenario numFlowInterArrival = new NumberOfFlowsAndInterArrival();
		Scenario sizeFlowNum = new SizeOfFlowsAndFlowNumber();
		Scenario incastBufferAlpha = new IncastBufferingWithAlpha();
		Scenario incastBufferGamma = new IncastBufferingWithGamma();

		// vTest.validate();
		// numFlowsAndBeta.executeTest();
		// alphaAndBeta.executeTest();
		// numFlowsInterArrival.executeTest();
		// interArrivalFlowSize.executeTest();
		 numFlowSize.executeTest();
		// numFlowInterArrival.executeTest();
		// sizeFlowNum.executeTest();
		// incastBufferAlpha.executeTest();
		//incastBufferGamma.executeTest();
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
