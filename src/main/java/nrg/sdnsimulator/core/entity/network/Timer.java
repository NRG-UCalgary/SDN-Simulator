package nrg.sdnsimulator.core.entity.network;

public class Timer {

	private int id;
	private short type;
	private boolean isActive;

	public Timer(int id, short type) {
		this.id = id;
		this.type = type;
		isActive = true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
