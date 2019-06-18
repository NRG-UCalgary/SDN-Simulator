package entities;

public abstract class Entity {
	protected final int ID;

	public Entity(int id) {
		this.ID = id;
	}

	public int getID() {
		return this.ID;
	}
}
