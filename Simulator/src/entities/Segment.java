package entities;

public class Segment extends Entity {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + bigRTT_;
		result = prime * result + dstHostID;
		result = prime * result + flowID;
		result = prime * result + Float.floatToIntBits(interSegmentDelay_);
		result = prime * result + Float.floatToIntBits(rtt_);
		result = prime * result + sWnd_;
		result = prime * result + seqNum;
		result = prime * result + size;
		result = prime * result + srcHostID;
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Segment other = (Segment) obj;
		if (bigRTT_ != other.bigRTT_)
			return false;
		if (dstHostID != other.dstHostID)
			return false;
		if (flowID != other.flowID)
			return false;
		if (Float.floatToIntBits(interSegmentDelay_) != Float.floatToIntBits(other.interSegmentDelay_))
			return false;
		if (Float.floatToIntBits(rtt_) != Float.floatToIntBits(other.rtt_))
			return false;
		if (sWnd_ != other.sWnd_)
			return false;
		if (seqNum != other.seqNum)
			return false;
		if (size != other.size)
			return false;
		if (srcHostID != other.srcHostID)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

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
