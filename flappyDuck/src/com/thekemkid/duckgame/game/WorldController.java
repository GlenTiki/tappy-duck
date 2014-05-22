package com.thekemkid.duckgame.game;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.thekemkid.duckgame.game.objects.Rock;
import com.thekemkid.duckgame.game.objects.RubberDuck;
import com.thekemkid.duckgame.game.objects.RubberDuck.JUMP_STATE;
import com.thekemkid.duckgame.utils.CameraHelper;
import com.thekemkid.duckgame.utils.Constants;

public class WorldController extends InputAdapter {

	private static final String TAG = WorldController.class.getName();

	public CameraHelper cameraHelper;
	public Level level;
	public int score;
	// Rectangles for collision detection
	private Rectangle r1 = new Rectangle();
	private Rectangle r2 = new Rectangle();

	private float timeLeftGameOverDelay;
	private float timeLeftWinDelay;

	public WorldController() {
		init();
	}

	private void init() {
		Gdx.input.setInputProcessor(this);
		cameraHelper = new CameraHelper();
		score = 0;
		level = new Level(Constants.LEVEL_01);
		cameraHelper.setTarget(level.player);
		timeLeftGameOverDelay = 0;
		timeLeftWinDelay = 0;
	}

	public void update(float deltaTime) {

		deltaTime = MathUtils.clamp(deltaTime, 0.0f, 0.03f);

		handleDebugInput(deltaTime);
		if (isPlayerDead()) {
			timeLeftGameOverDelay -= deltaTime;
			if (timeLeftGameOverDelay < 0)
				init();
		} else {
			handleInputGame(deltaTime);
		}
		level.update(deltaTime);
		testCollisions();
		cameraHelper.update(deltaTime);
	}

	@Override
	public boolean keyUp(int keycode) {

		if (keycode == Keys.R) { // Reset game world
			init();
			Gdx.app.debug(TAG, "Game world resetted");
		} else if (keycode == Keys.ENTER) { // Toggle camera follow
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null
					: level.player);
			Gdx.app.debug(TAG,
					"Camera follow enabled: " + cameraHelper.hasTarget());
		}
		return false;
	}

	private void handleInputGame(float deltaTime) {

		if (cameraHelper.hasTarget(level.player)) {
			level.player.velocity.x = level.player.terminalVelocity.x;

			// Bunny Jump
			if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE)
					|| Gdx.input.isKeyPressed(Keys.UP)) {
				level.player.setJumping(true);
			} else {
				level.player.setJumping(false);
			}

		}
	}

	private void handleDebugInput(float deltaTime) {

		if (Gdx.app.getType() != ApplicationType.Desktop)
			return;

		// Camera Controls (move)
		if (!cameraHelper.hasTarget(level.player)) {

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
		RubberDuck bunnyHead = level.player;
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

	private void testCollisions() {
		r1.set(level.player.position.x, level.player.position.y,
				level.player.bounds.width, level.player.bounds.height);

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
	}

	public boolean isPlayerDead() {
		return level.player.jumpState == level.player.jumpState.GROUNDED;
	}
}
