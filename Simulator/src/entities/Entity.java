package entities;

public abstract class Entity {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (ID != other.ID)
			return false;
		return true;
	}

	protected final int ID;

	public Entity(int id) {
		this.ID = id;
	}

	public int getID() {
		return this.ID;
	}
}
