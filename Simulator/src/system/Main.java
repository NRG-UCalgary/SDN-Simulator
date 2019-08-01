package system;

import experiments.Validation;

public class Main {

	public static void error(String className, String methodName, String errorMessage) {
		print("Error--inClass--" + className + "." + methodName + "()::" + errorMessage);
	}

	public static void generatingOutputFiles() {
		print("Generating the output files...");
	}

	public static void main(String[] args) {
		// Scenario study = new FlowSizeMeanAndSTDStudy();
		// study.executeTest();
		// Main.print("First Study Completed. Running GC.");
		// study = new FunctionalityMockScenario();
		// study.executeTest();
		// Main.print("Second Study Completed. Running GC.");
		// NumberOfFlowsAndDelayTypes study = new NumberOfFlowsAndDelayTypes();
		// study.executeTest();
		// Main.print("Third Study Completed. Running GC.");
		// MockCategoryFactorStudy study = new MockCategoryFactorStudy();
		// study.executeTest();

		Validation vTest = new Validation();
		vTest.validate();
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
