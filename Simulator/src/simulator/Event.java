package simulator;

public abstract class Event {

	protected float eventTime;

	public Event(float eventTime) {
		this.eventTime = eventTime;
	}

	public abstract void execute(Network net);
}
