package com.ezard.navmeshdebugger;

public class Test {

	public static void main(String[] args) {
		float x = 0;
		float y = 0;
		float yVelocity = -25;
		float maxHeight = 0;

		int count = 0;
		while (y <= 0) {
			System.out.println(count++ + ": " + x + ", " + y);
			x += 10;
			y += yVelocity;
			yVelocity += 1.25f;
			if (y < maxHeight) {
				maxHeight = y;
			}
		}

		System.out.println(maxHeight);
	}
}
