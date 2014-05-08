package com.thekemkid.duckgame.game;

import com.thekemkid.duckgame.utils.CameraHelper;
import com.thekemkid.duckgame.utils.Constants;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.thekemkid.duckgame.game.objects.BunnyHead;
import com.thekemkid.duckgame.game.objects.Carrot;
import com.thekemkid.duckgame.game.objects.Feather;
import com.thekemkid.duckgame.game.objects.Goal;
import com.thekemkid.duckgame.game.objects.GoldCoin;
import com.thekemkid.duckgame.game.objects.Rock;
import com.thekemkid.duckgame.game.objects.BunnyHead.JUMP_STATE;

public class WorldController extends InputAdapter {

	private static final String TAG = WorldController.class.getName();

	public CameraHelper cameraHelper;
	public Level level;
	public int lives;
	public int score;
	public int curLevel;
	public int overallScore;
	public float timeLeft;

	// Rectangles for collision detection
	private Rectangle r1 = new Rectangle();
	private Rectangle r2 = new Rectangle();

	private float timeLeftGameOverDelay;
	private float timeLeftWinDelay;

	private void initLevel() {
		if (curLevel < Constants.NUM_LEVELS) {
			Gdx.input.setInputProcessor(this);
			cameraHelper = new CameraHelper();
			score = overallScore;
			switch (curLevel) {
			case 0:
				level = new Level(Constants.LEVEL_01);
				timeLeft = 180.0f;
				break;
			case 1:
				level = new Level(Constants.LEVEL_02);
				timeLeft = 180.0f;
				break;
			}
			cameraHelper.setTarget(level.bunnyHead);
		} else {
			init();
		}
	}

	public WorldController() {
		init();
	}

	private void init() {
		lives = Constants.LIVES_START;
		timeLeftGameOverDelay = 0;
		timeLeftWinDelay = 0;
		overallScore = 0;
		curLevel = 0;

		initLevel();
	}

	public void update(float deltaTime) {

		deltaTime = MathUtils.clamp(deltaTime, 0.0f, 0.03f);

		handleDebugInput(deltaTime);
		if (isGameOver()) {
			timeLeftGameOverDelay -= deltaTime;
			if (timeLeftGameOverDelay < 0)
				init();
		} else if (isGoalCollected()) {
			timeLeftWinDelay -= deltaTime;
			if (timeLeftWinDelay < 0) {
				curLevel++;
				initLevel();
			}
		} else {
			timeLeft -= deltaTime;
			handleInputGame(deltaTime);
		}
		level.update(deltaTime);
		testCollisions();
		cameraHelper.update(deltaTime);
		if (!isGameOver() && isPlayerDead()) {
			lives--;
			if (isGameOver())
				timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
			else
				initLevel();
		}
	}

	@Override
	public boolean keyUp(int keycode) {

		if (keycode == Keys.R) { // Reset game world
			init();
			Gdx.app.debug(TAG, "Game world resetted");
		} else if (keycode == Keys.ENTER) { // Toggle camera follow
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null
					: level.bunnyHead);
			Gdx.app.debug(TAG,
					"Camera follow enabled: " + cameraHelper.hasTarget());
		}
		return false;
	}

	private void handleInputGame(float deltaTime) {

		if (cameraHelper.hasTarget(level.bunnyHead)) {

			// Player Movement
			if (Gdx.input.isKeyPressed(Keys.LEFT)) {
				level.bunnyHead.velocity.x = -level.bunnyHead.terminalVelocity.x;
			} else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
				level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
			} else {
				// Execute auto-forward movement on non-desktop platform
				if (Gdx.app.getType() != ApplicationType.Desktop) {
					level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
				}
			}

			// Bunny Jump
			if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE)
					|| Gdx.input.isKeyPressed(Keys.UP)) {
				level.bunnyHead.setJumping(true);
			} else {
				level.bunnyHead.setJumping(false);
			}

		}
	}

	private void handleDebugInput(float deltaTime) {

		if (Gdx.app.getType() != ApplicationType.Desktop)
			return;

		// Camera Controls (move)
		if (!cameraHelper.hasTarget(level.bunnyHead)) {

			float camMoveSpeed = 5 * deltaTime;
			float camMoveSpeedAccelerationFactor = 5;
			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
				camMoveSpeed *= camMoveSpeedAccelerationFactor;
			if (Gdx.input.isKeyPressed(Keys.LEFT))
				moveCamera(-camMoveSpeed, 0);
			if (Gdx.input.isKeyPressed(Keys.RIGHT))
				moveCamera(camMoveSpeed, 0);
			if (Gdx.input.isKeyPressed(Keys.UP))
				moveCamera(0, camMoveSpeed);
			if (Gdx.input.isKeyPressed(Keys.DOWN))
				moveCamera(0, -camMoveSpeed);
			if (Gdx.input.isKeyPressed(Keys.BACKSPACE))
				cameraHelper.setPosition(0, 0);
		}
		// Camera Controls (zoom)
		float camZoomSpeed = 1 * deltaTime;
		float camZoomSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
			camZoomSpeed *= camZoomSpeedAccelerationFactor;
		if (Gdx.input.isKeyPressed(Keys.COMMA))
			cameraHelper.addZoom(camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.PERIOD))
			cameraHelper.addZoom(-camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.SLASH))
			cameraHelper.setZoom(1);
	}

	private void moveCamera(float x, float y) {
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}

	private void onCollisionBunnyHeadWithRock(Rock rock) {
		BunnyHead bunnyHead = level.bunnyHead;
		float heightDifference = Math.abs(bunnyHead.position.y
				- (rock.position.y + rock.bounds.height));
		if (heightDifference > 0.25f) {
			boolean hitLeftEdge = bunnyHead.position.x > (rock.position.x + rock.bounds.width / 2.0f);
			if (hitLeftEdge) {
				bunnyHead.position.x = rock.position.x + rock.bounds.width;
			} else {
				bunnyHead.position.x = rock.position.x - bunnyHead.bounds.width;
			}
			return;
		}

		switch (bunnyHead.jumpState) {
		case GROUNDED:
			break;
		case FALLING:
		case JUMP_FALLING:
			bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height
					+ bunnyHead.origin.y;
			bunnyHead.jumpState = JUMP_STATE.GROUNDED;
			break;
		case JUMP_RISING:
			bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height
					+ bunnyHead.origin.y;
			break;
		}
	}

	private void onCollisionBunnyWithGoldCoin(GoldCoin goldcoin) {
		goldcoin.collected = true;
		score += goldcoin.getScore();
		Gdx.app.log(TAG, "Gold coin collected");
	}

	private void onCollisionBunnyWithFeather(Feather feather) {
		feather.collected = true;
		score += feather.getScore();
		level.bunnyHead.setFeatherPowerup(true);
		Gdx.app.log(TAG, "Feather collected");
	}

	private void onCollisionBunnyWithGoal(Goal goal) {
		goal.collected = true;
		score += goal.getScore();
		overallScore = score;
		timeLeftWinDelay = Constants.TIME_DELAY_WIN;
		Gdx.app.log(TAG, "Goal collected");
	}

	private void onCollisionBunnyWithCarrot(Carrot carrot) {
		carrot.collected = true;
		if (lives < 3)
			lives++;
		score += carrot.getScore();
		overallScore += score;
		Gdx.app.log(TAG, "Carrot collected");
	}

	private void testCollisions() {
		r1.set(level.bunnyHead.position.x, level.bunnyHead.position.y,
				level.bunnyHead.bounds.width, level.bunnyHead.bounds.height);

		// Test collision: Bunny Head <-> Rocks
		for (Rock rock : level.rocks) {
			r2.set(rock.position.x, rock.position.y, rock.bounds.width,
					rock.bounds.height);
			if (!r1.overlaps(r2))
				continue;
			onCollisionBunnyHeadWithRock(rock);
			// IMPORTANT: must do all collisions for valid
			// edge testing on rocks.
		}

		// Test collision: Bunny Head <-> Gold Coins
		for (GoldCoin goldCoin : level.goldCoins) {
			if (goldCoin.collected)
				continue;
			r2.set(goldCoin.position.x, goldCoin.position.y,
					goldCoin.bounds.width, goldCoin.bounds.height);
			if (!r1.overlaps(r2))
				continue;
			onCollisionBunnyWithGoldCoin(goldCoin);
			break;
		}

		// Test collision: Bunny Head <-> Feathers
		for (Feather feather : level.feathers) {
			if (feather.collected)
				continue;
			r2.set(feather.position.x, feather.position.y,
					feather.bounds.width, feather.bounds.height);
			if (!r1.overlaps(r2))
				continue;
			onCollisionBunnyWithFeather(feather);
			break;
		}

		// Test collision: Bunny Head <-> Carrots
		for (Carrot carrot : level.carrots) {
			if (carrot.collected)
				continue;
			r2.set(carrot.position.x, carrot.position.y, carrot.bounds.width,
					carrot.bounds.height);
			if (!r1.overlaps(r2))
				continue;
			onCollisionBunnyWithCarrot(carrot);
			break;
		}

		// Test collision: Bunny Head <-> Goal
		Goal goal = level.goal;
		if (!goal.collected) {
			r2.set(goal.position.x, goal.position.y, goal.bounds.width,
					goal.bounds.height);
			if (r1.overlaps(r2))
				onCollisionBunnyWithGoal(goal);
		}
	}

	public boolean isGameOver() {
		return lives <= 0;
	}

	public boolean isGoalCollected() {
		return level.goal.collected;
	}

	public boolean isPlayerDead() {
		return level.bunnyHead.position.y < -5 || timeLeft <= 0;
	}
}
