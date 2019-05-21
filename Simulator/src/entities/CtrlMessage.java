package entities;

public class CtrlMessage extends Entity {

	public int switchID;
	public int bufferMode;
	public double interFlowDelay;
	public double waitBeforeRelease;
	public int ackNumber;

	public CtrlMessage() {
		super(-1);
	}

}
