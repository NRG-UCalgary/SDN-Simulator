package entities;

public class Segment extends Entity {

	/** my protocol variables **/
	public int sWnd_;
	public float rtt_;
	public int bigRTT_;
	public float interSegmentDelay_;
	/**************************/
	private int flowID;
	private int seqNum;
	private int type;
	private int size; // in bits
	private int srcHostID;
	private int dstHostID;

	public Segment(int flowID, int type, int seqNum, int size, int srcHostID, int dstHostID) {
		super(-1);
		this.flowID = flowID;
		sWnd_ = 0;
		rtt_ = 0;
		bigRTT_ = 0;
		this.type = type;
		this.seqNum = seqNum;
		this.size = size;
		this.srcHostID = srcHostID;
		this.dstHostID = dstHostID;

	}

	/**********************************************************************/
	/********************** Getters and Setters ***************************/
	/**********************************************************************/

	public int getSeqNum() {
		return seqNum;
	}

	public int getFlowID() {

		return flowID;
	}

	public int getType() {
		return type;
	}

	public void changeType(int newType) {
		type = newType;
	}

	public int getSize() {
		return size;
	}

	public int getSrcHostID() {
		return srcHostID;
	}

	public int getDstHostID() {
		return dstHostID;
	}

	public void setDstHostID(int dstHostID) {
		this.dstHostID = dstHostID;
	}

	/***********************************************************************/
}
