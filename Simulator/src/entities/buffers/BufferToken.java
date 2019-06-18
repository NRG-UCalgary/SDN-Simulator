package entities.buffers;

public class BufferToken {

	public double waitTime;
	public int ACKsToGo;
	public boolean isFirst;

	public BufferToken(double releaseTime, int ACKsToGo) {
		this.waitTime = releaseTime;
		this.ACKsToGo = ACKsToGo;
		this.isFirst = true;
	}

}
