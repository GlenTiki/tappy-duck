package com.thekemkid.duckgame.game.objects;

import com.thekemkid.duckgame.game.Assets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Carrot extends AbstractGameObject{

	private TextureRegion regCarrot;
	public boolean collected;

	public Carrot() {
		init();
	}

	private void init() {
		dimension.set(0.5f, 0.5f);
		regCarrot = Assets.instance.carrot.carrot;
		// Set bounding box for collision detection
		bounds.set(0, 0, dimension.x, dimension.y);
		collected = false;
	}

	public void render(SpriteBatch batch) {
		if (collected)
			return;
		TextureRegion reg = null;
		reg = regCarrot;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x,
				origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation,
				reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(),
				reg.getRegionHeight(), false, false);
	}

	public int getScore() {
		return 300;
	}

}
