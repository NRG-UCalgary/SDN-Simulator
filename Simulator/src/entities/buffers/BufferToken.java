package entities.buffers;

public class BufferToken {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ACKCounter;
		result = prime * result + Float.floatToIntBits(arrivalToBufferTime);
		result = prime * result + initialACKsToGo;
		result = prime * result + Float.floatToIntBits(initialCycleDelay);
		result = prime * result + (isActive ? 1231 : 1237);
		result = prime * result + (isFirstCycle ? 1231 : 1237);
		result = prime * result + steadyACKsToGo;
		result = prime * result + Float.floatToIntBits(steadyCycleDelay);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BufferToken other = (BufferToken) obj;
		if (ACKCounter != other.ACKCounter)
			return false;
		if (Float.floatToIntBits(arrivalToBufferTime) != Float.floatToIntBits(other.arrivalToBufferTime))
			return false;
		if (initialACKsToGo != other.initialACKsToGo)
			return false;
		if (Float.floatToIntBits(initialCycleDelay) != Float.floatToIntBits(other.initialCycleDelay))
			return false;
		if (isActive != other.isActive)
			return false;
		if (isFirstCycle != other.isFirstCycle)
			return false;
		if (steadyACKsToGo != other.steadyACKsToGo)
			return false;
		if (Float.floatToIntBits(steadyCycleDelay) != Float.floatToIntBits(other.steadyCycleDelay))
			return false;
		return true;
	}

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
