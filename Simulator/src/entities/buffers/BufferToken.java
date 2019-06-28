package entities.buffers;

public class BufferToken {

	// Solution A
	public double waitTime;
	public int ACKsToGo;
	public boolean isFirst;

	// Solution B
	public double initialDelay;
	public double steadyDelay;
	public int initialACKsToGo;
	public int steadyACKsToGo;
	public int ACKCounter;

	public BufferToken(double releaseTime, int ACKsToGo) {

		// Solution A
		this.waitTime = releaseTime;
		this.ACKsToGo = ACKsToGo;
		this.isFirst = true;

		/*
		 * // Solution B this.initialDelay = initialDelay; this.steadyDelay =
		 * steadyDelay; this.initialACKsToGo = initialACKsToGo; this.steadyACKsToGo =
		 * steadyACKsToGo; this.ACKCounter = 0;
		 */

	}

}
