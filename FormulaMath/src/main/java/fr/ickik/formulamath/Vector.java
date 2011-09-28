package fr.ickik.formulamath;

public class Vector {

	private int xMoving;
	private int yMoving;

	public Vector() {
		this(0, 0);
	}

	public Vector(int xMoving, int yMoving) {
		this.xMoving = xMoving;
		this.yMoving = yMoving;
	}

	public void setXMoving(int xMoving) {
		this.xMoving = xMoving;
	}

	public int getXMoving() {
		return xMoving;
	}

	public void setYMoving(int yMoving) {
		this.yMoving = yMoving;
	}

	public int getYMoving() {
		return yMoving;
	}
}
