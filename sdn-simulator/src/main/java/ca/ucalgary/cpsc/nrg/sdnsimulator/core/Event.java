package ca.ucalgary.cpsc.nrg.sdnsimulator.core;

public abstract class Event {

	protected float eventTime;

	public Event(float eventTime) {
		this.eventTime = eventTime;
	}

	public abstract void execute(Network net);
}
