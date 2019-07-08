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
	public double interSegmentDelay;

	public BufferToken() {
		this.ACKCounter = 0;
		arrivalToBufferTime = 0;
		this.isActive = false;
	}

	public void activate(boolean isFirstCycle, double interSegmentDelay, double initialDelay, int initialACKsToGo,
			double steadyDelay, int steadyACKsToGo) {
		this.isActive = true;
		this.isFirstCycle = isFirstCycle;
		this.initialCycleDelay = initialDelay;
		this.initialACKsToGo = initialACKsToGo;
		this.steadyCycleDelay = steadyDelay;
		this.steadyACKsToGo = steadyACKsToGo;
		this.interSegmentDelay = interSegmentDelay;
	}

	public void setArrivalToBufferTime(double time) {
		this.arrivalToBufferTime = time;
	}

	public double getCongestionControlDelay(double currentTime) {
		double delay = 0;
		if (this.isActive) {
			if (isFirstCycle) { // First cycle
				isFirstCycle = false;
				if (initialCycleDelay > (currentTime - arrivalToBufferTime)) {
					delay = initialCycleDelay - (currentTime - arrivalToBufferTime);
				}
			} else { // After first cycle
				delay = steadyCycleDelay;
			}
		}
		return delay;
	}

}
