package entities.buffers;

import system.utility.Debugger;

public class BufferToken {

	public boolean isActive;
	public boolean isFirstCycle;
	public double arrivalToBufferTime;
	public double initialCycleDelay;
	public double steadyCycleDelay;
	public int initialACKsToGo;
	public int steadyACKsToGo;
	public int ACKCounter;

	public BufferToken() {
		this.ACKCounter = 0;
		arrivalToBufferTime = 0;
		this.isActive = false;
	}

	public void activate(boolean isActive, boolean isFirstCycle, double initialDelay, int initialACKsToGo,
			double steadyDelay, int steadyACKsToGo) {
		this.isActive = true;
		this.isFirstCycle = true;
		this.initialCycleDelay = initialDelay;
		this.initialACKsToGo = initialACKsToGo;
		this.steadyCycleDelay = steadyDelay;
		this.steadyACKsToGo = steadyACKsToGo;
	}

	public void setArrivalToBufferTime(double time) {
		this.arrivalToBufferTime = time;
	}

	public double getCongestionControlDelay(double currentTime) {
		double delay = 0;
		if (this.isActive) {
			ACKCounter++;
			if (isFirstCycle) {
				if (ACKCounter == 1) {
					delay = initialCycleDelay - (currentTime - arrivalToBufferTime);
				} else if (ACKCounter == initialACKsToGo) {
					isFirstCycle = false;
					ACKCounter = 0;
				} else if (ACKCounter > initialACKsToGo) {
					Debugger.debugToConsole("BufferToken::We should not be here!");
				}
			} else {
				if (ACKCounter == 1) {
					delay = steadyCycleDelay;
				} else if (ACKCounter == steadyACKsToGo) {
					ACKCounter = 0;
				} else if (ACKCounter > steadyACKsToGo) {
					Debugger.debugToConsole("BufferToken::We should not be here!");
				}
			}
		}
		return delay;
	}

}
