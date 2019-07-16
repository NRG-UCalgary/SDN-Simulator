package entities.buffers;

public class BufferToken {

	public boolean isActive;
	public boolean isFirstCycle;
	public float arrivalToBufferTime;
	public float initialCycleDelay;
	public float steadyCycleDelay;
	public int initialACKsToGo;
	public int steadyACKsToGo;
	public int ACKCounter;

	public BufferToken() {
		this.ACKCounter = 0;
		arrivalToBufferTime = 0;
		this.isActive = false;
	}

	public void activate(boolean isFirstCycle, float initialDelay, int initialACKsToGo, float steadyDelay,
			int steadyACKsToGo) {
		this.isActive = true;
		this.isFirstCycle = isFirstCycle;
		this.initialCycleDelay = initialDelay;
		this.initialACKsToGo = initialACKsToGo;
		this.steadyCycleDelay = steadyDelay;
		this.steadyACKsToGo = steadyACKsToGo;
	}

	public void setArrivalToBufferTime(float arrivalToBufferTime) {
		this.arrivalToBufferTime = arrivalToBufferTime;
	}

	public float getCongestionControlDelay(float currentTime) {
		float delay = 0;
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
