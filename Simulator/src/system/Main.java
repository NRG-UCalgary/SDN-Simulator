package system;

import system.utility.*;
import tests.Scenario;

public class Main {

	public static void main(String[] args) {
		Scenario testScenario = new Scenario(Keywords.LAN, 0);
		testScenario.numberOfFlowsStudy(1, 1, 1);
	}

	public static void print(Object o) {
		System.out.println(o);
	}

	public static void error(String className, String methodName, String errorMessage) {
		print("Error--inClass--" + className + "." + methodName + "()::" + errorMessage);
	}

}
