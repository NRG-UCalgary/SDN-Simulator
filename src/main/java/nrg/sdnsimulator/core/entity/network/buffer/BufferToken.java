package nrg.sdnsimulator.core.entity.network.buffer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BufferToken {

	private int ACKCounter;
	private float arrivalToBufferTime;
	private int initialACKsToGo;
	private float initialCycleDelay;
	private boolean isActive;
	private boolean isFirstCycle;
	private int steadyACKsToGo;
	private float steadyCycleDelay;

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

	public void setArrivalToBufferTime(float arrivalToBufferTime) {
		this.arrivalToBufferTime = arrivalToBufferTime;
	}

	public int getACKCounter() {
		return ACKCounter;
	}

	public void setACKCounter(int aCKCounter) {
		ACKCounter = aCKCounter;
	}

	public int getInitialACKsToGo() {
		return initialACKsToGo;
	}

	public void setInitialACKsToGo(int initialACKsToGo) {
		this.initialACKsToGo = initialACKsToGo;
	}

	public float getInitialCycleDelay() {
		return initialCycleDelay;
	}

	public void setInitialCycleDelay(float initialCycleDelay) {
		this.initialCycleDelay = initialCycleDelay;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isFirstCycle() {
		return isFirstCycle;
	}

}
