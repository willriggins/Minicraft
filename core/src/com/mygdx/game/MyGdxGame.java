package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	TextureRegion up, down, left, right;
	boolean faceUp = true, faceDown, faceRight, faceLeft;

	float x,y, xv, yv;

	int WIDTH = 18;
	int HEIGHT = 26;

	static float NORMAL_VELOCITY = 100;
	static final float HYPER_SPEED = 250;
	static final float DECELERATION = .05f; // try 0.99 for skating on ice effect


	@Override
	public void create () {
		batch = new SpriteBatch();

		Texture tiles = new Texture("tiles.png");
		TextureRegion[][] grid = TextureRegion.split(tiles, 16, 16);
		down = grid[6][0];
		up = grid[6][1];
		right = grid[6][3];
		left = new TextureRegion(right);
		left.flip(true, false);

	}

	@Override
	public void render () {
		move();

		Gdx.gl.glClearColor(0.5f, 0.5f, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		if (faceRight) {
			batch.draw(right, x, y, WIDTH * 3, HEIGHT * 3);
		}
		else if (faceLeft) {
			batch.draw(left, x, y, WIDTH * 3, HEIGHT * 3);
		}
		else if (faceDown) {
			batch.draw(down, x, y, WIDTH * 3, HEIGHT * 3);
		}
		else if (faceUp) {
			batch.draw(up, x, y, WIDTH * 3, HEIGHT * 3);
		}

		if (x > 800) {
			x = 5;
		}

		if (x < 0) {
			x = 795;
		}

		if (y > 600) {
			y = 5;
		}

		if (y < 0) {
			y = 595;
		}

		batch.end();
	}

	public void move() {
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			faceUp();
			yv = NORMAL_VELOCITY;
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			faceDown();
			yv = -NORMAL_VELOCITY;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			faceRight();
			xv = NORMAL_VELOCITY;
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			faceLeft();
			xv = -NORMAL_VELOCITY;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			NORMAL_VELOCITY = HYPER_SPEED;
		}
		else {
			NORMAL_VELOCITY = 100;
		}

		float delta = Gdx.graphics.getDeltaTime();
		y+= yv * delta;
		x+= xv * delta;

		yv = decelerate(yv);
		xv = decelerate(xv);

	}

	public float decelerate(float velocity) {
		velocity *= DECELERATION; // could also cast as float.
		if (Math.abs(velocity) < 1) {
			velocity = 0;
		}
		return velocity;
	}

	public void faceUp() {
		faceUp = true;
		faceDown = false;
		faceLeft = false;
		faceRight = false;
	}

	public void faceDown() {
		faceUp = false;
		faceDown = true;
		faceLeft = false;
		faceRight = false;
	}

	public void faceLeft() {
		faceUp = false;
		faceDown = false;
		faceLeft = true;
		faceRight = false;
	}

	public void faceRight() {
		faceUp = false;
		faceDown = false;
		faceLeft = false;
		faceRight = true;
	}

}
