package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;

	TextureRegion up, down, left, right, upFlipped, downFlipped, right2, left2;
	TextureRegion upZ, downZ, leftZ, rightZ, upFlippedZ, downFlippedZ, right2Z, left2Z;
	TextureRegion background, heart, greyHeart, tree;
	boolean faceUp = true, faceDown, faceRight, faceLeft;
	Animation walkUp, walkDown, walkRight, walkLeft;
	Animation walkUpZ, walkDownZ, walkRightZ, walkLeftZ;


	float time;
	int i, w, h, direction = 2;

	int status = 1;

	float x = 100,y = 100, xv, yv;
	float zx = 25, zy = 15 , zxv, zyv;

	int WIDTH = 18;
	int HEIGHT = 26;

	static float VELOCITY = 125;
	static float NORMAL_VELOCITY = 125;
	static float ZOMBIE_VELOCITY = 125;
	static final float HYPER_SPEED = 400;
	static final float DECELERATION = .25f; // try 0.99 for skating on ice effect


	@Override
	public void create () {
		batch = new SpriteBatch();

		//get grid region for player and zombie

		Texture tiles = new Texture("tiles.png");
		TextureRegion[][] grid = TextureRegion.split(tiles, 16, 16);

		// player definitions

		down = grid[6][0];
		up = grid[6][1];
		right = grid[6][3];
		left = new TextureRegion(right);
		left.flip(true, false);
		right2 = grid[6][2];
		left2 = new TextureRegion(right2);
		left2.flip(true, false);

		upFlipped = new TextureRegion(up);
		upFlipped.flip(true, false);
		downFlipped = new TextureRegion(down);
		downFlipped.flip(true, false);

		//zombie definitions

		downZ = grid[6][4];
		upZ = grid[6][5];
		rightZ = grid[6][7];
		leftZ = new TextureRegion(rightZ);
		leftZ.flip(true, false);
		right2Z = grid[6][6];
		left2Z = new TextureRegion(right2Z);
		left2Z.flip(true, false);

		upFlippedZ = new TextureRegion(upZ);
		upFlippedZ.flip(true, false);
		downFlippedZ = new TextureRegion(downZ);
		downFlippedZ.flip(true, false);

		// get background tile
		TextureRegion[][] backgroundRegion = TextureRegion.split(tiles, 8, 8);
		background = backgroundRegion[0][0];

		TextureRegion[][] hud = TextureRegion.split(tiles, 8, 8);
		heart = hud[0][10];
		greyHeart = hud[0][8];

		//animate player
		walkUp = new Animation (0.2f, up, upFlipped);
		walkDown = new Animation (0.2f, down, downFlipped);
		walkRight = new Animation(0.2f, right, right2);
		walkLeft = new Animation(0.2f, left, left2);

		//animate zombie
		walkUpZ = new Animation (0.2f, upZ, upFlippedZ);
		walkDownZ = new Animation (0.2f, downZ, downFlippedZ);
		walkRightZ = new Animation(0.2f, rightZ, right2Z);
		walkLeftZ = new Animation(0.2f, leftZ, left2Z);

		//get tree - bad find, couldn't isolate tree so it pulled in some grass from tileset
		TextureRegion[][] findTree = TextureRegion.split(tiles, 18, 24);
		tree = findTree[0][0];
	}

	@Override
	public void render () {
		move();
		moveZ();
		keepInBounds();
		zombieLoop();

		time += Gdx.graphics.getDeltaTime();

		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// set up player's conditions for animation

		TextureRegion upward;
		if (yv > 0) {
			upward = walkUp.getKeyFrame(time, true);
		}
		else {
			upward = up;
		}

		TextureRegion downward;
		if (yv < 0) {
			downward = walkDown.getKeyFrame(time, true);
		}
		else {
			downward = down;
		}

		TextureRegion rightward;
		if (xv > 0) {
			rightward = walkRight.getKeyFrame(time, true);
		}
		else {
			rightward = right2;
		}

		TextureRegion leftward;
		if (xv < 0) {
			leftward = walkLeft.getKeyFrame(time, true);
		}
		else {
			leftward = left2;
		}

		batch.begin();

		// draw background

		w = 0;
		h = 0;
		i = 0;

		while (i < 24) {
			if (w < 800) {
				batch.draw(background, w, h, 36, 30);
				w += (WIDTH * 2);
			}
			else if (w >= 800) {
				h += HEIGHT;
				w = 0;
				i += 1;
			}
		}

		// draw character

		if (faceRight) {
			batch.draw(rightward, x, y, WIDTH * 3, HEIGHT * 3);
		}
		else if (faceLeft) {
			batch.draw(leftward, x, y, WIDTH * 3, HEIGHT * 3);
		}
		else if (faceDown) {
			batch.draw(downward, x, y, WIDTH * 3, HEIGHT * 3);
		}
		else if (faceUp) {
			batch.draw(upward, x, y, WIDTH * 3, HEIGHT * 3);
		}

		// draw zombie

		if (direction == 1) {
			batch.draw(walkUpZ.getKeyFrame(time, true), zx, zy, WIDTH * 3, HEIGHT * 3);
		}
		else if (direction == 2) {
			batch.draw(walkRightZ.getKeyFrame(time, true), zx, zy, WIDTH * 3, HEIGHT * 3);
		}
		else if (direction == 3) {
			batch.draw(walkDownZ.getKeyFrame(time, true), zx, zy, WIDTH * 3, HEIGHT * 3);
		}
		else if (direction == 4) {
			batch.draw(walkLeftZ.getKeyFrame(time, true), zx, zy, WIDTH * 3, HEIGHT * 3);
		}


		// draw hud

		if (status == 1) {
			batch.draw(heart, 650, 550, 50, 50);
		}
		else if (status == 2) {
			batch.draw(greyHeart, 650, 550, 50, 50);
		}
		batch.draw(heart, 700, 550, 50, 50);
		batch.draw(heart, 750, 550, 50, 50);


		//testing collision with player and zombie.. not very functional

		if (zx == x && zy == y) {
			status = 2;
		}

		//play sound if units collide.. (rough)
		//press 1 to make units touch, tho this still isn't 100%. have to spam it to get units to collide.
		if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
			Sound sound = Gdx.audio.newSound(Gdx.files.internal("playerhurt.wav"));
			sound.play(1.0f);
			zx = x;
			zy = y;
		}

		//draw tree
		batch.draw(tree, 400, 300, 100, 100);
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

		if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
			NORMAL_VELOCITY = HYPER_SPEED;
		}
		else {
			NORMAL_VELOCITY = VELOCITY;
		}

		float delta = Gdx.graphics.getDeltaTime();
		y+= yv * delta;
		x+= xv * delta;

		yv = decelerate(yv);
		xv = decelerate(xv);

	}

	public void moveZ() {
		if (direction == 1) {
			zyv = ZOMBIE_VELOCITY;
		}
		else if (direction == 3) {
			zyv = -ZOMBIE_VELOCITY;
		}
		if (direction == 2) {
			zxv = ZOMBIE_VELOCITY;
		}
		else if (direction == 4) {
			zxv = -ZOMBIE_VELOCITY;
		}


		float deltaZ = Gdx.graphics.getDeltaTime();
		zy+= zyv * deltaZ;
		zx+= zxv * deltaZ;

		zyv = decelerate(zyv);
		zxv = decelerate(zxv);

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

	public void keepInBounds() {
		if (x > 800) {
			x = 0;
		}

		if (x < 0) {
			x = 800;
		}

		if (y > 600) {
			y = 0;
		}

		if (y < 0) {
			y = 600;
		}

	}

	public void zombieLoop() {
		if (zx > 750) {
			zx = 750;
			direction = 1;
		}

		if (zy > 475) {
			zy = 475;
			direction = 4;
		}

		if (zx < 20) {
			zx = 20;
			direction = 3;
		}
		if (zy < 5) {
			zy = 5;
			direction = 2;
		}
	}
}
