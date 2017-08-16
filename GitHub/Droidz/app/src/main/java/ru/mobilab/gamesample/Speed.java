package ru.mobilab.gamesample;

public class Speed {

	private float xv = 1;	// составляющая скорости по оси X
	private float yv = 1;	// составляющая скорости по оси Y

	public Speed() {
		this.xv = 1;
		this.yv = 1;
	}

	public Speed(float xv, float yv) {
		this.xv = xv;
		this.yv = yv;
	}

	public float getXv() {
		return xv;
	}
	public void setXv(float xv) {
		this.xv = xv;
	}
	public float getYv() {
		return yv;
	}
	public void setYv(float yv) {
		this.yv = yv;
	}

	// изменяем направление по оси X
	public void toggleXDirection() {
		xv = -xv;
	}

	// изменяем направление по оси  Y
	public void toggleYDirection() {
		yv=-yv;
	}
}
