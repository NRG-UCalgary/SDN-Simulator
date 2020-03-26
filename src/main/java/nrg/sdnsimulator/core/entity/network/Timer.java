package nrg.sdnsimulator.core.entity.network;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Timer {

	private int id;
	private short type;
	private boolean isActive;

	public Timer(int id, short type) {
		this.id = id;
		this.type = type;
		isActive = true;
	}

}
