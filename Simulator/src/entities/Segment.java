package entities;


public class Segment extends Entity {

	/** my protocol variables **/
	public int sWnd_;
	public double rtt_;
	public int bigRTT_;
	/**************************/
	private int flowID_;
	private int seq_num;
	private int type;
	private int size; // in bits
	private int srcHostID;
	private int dstHostID;

	public Segment(int flowID, int type, int seq_num, int size, int sourceID, int destinationID) {
		super(-1);
		this.flowID_ = flowID;
		this.sWnd_ = 0;
		this.rtt_ = 0;
		this.bigRTT_ = 0;
		this.type = type;
		this.seq_num = seq_num;
		this.size = size; // The size from user is in Bytes. The size in segment is in bits
		this.srcHostID = sourceID;
		this.dstHostID = destinationID;

	}

	/**********************************************************************/
	/********************** Getters and Setters ***************************/
	/**********************************************************************/

	public int getSeqNum() {
		return seq_num;
	}

	public int getFlowID() {

		return flowID_;
	}

	public void setFlowID(int flowID) {
		this.flowID_ = flowID;
	}

	public int getType() {
		return type;
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
