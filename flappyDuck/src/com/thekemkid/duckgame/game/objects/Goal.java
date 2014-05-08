package com.thekemkid.duckgame.game.objects;

import com.thekemkid.duckgame.game.Assets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Goal extends AbstractGameObject {

	 	private TextureRegion   regGoal;
	    public boolean          collected;

	    public Goal() {
	        init();
	    }

	    private void init() {
	        dimension.set(2.0f, 2.0f);
	        regGoal = Assets.instance.goal.goal;
	        // Set bounding box for collision detection
	        bounds.set(0, 0, dimension.x, dimension.y);
	        collected = false;
	    }

	    public void render(SpriteBatch batch) {
	        if (collected) return;
	        TextureRegion reg = null;
	        reg = regGoal;
	        batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x,
	                scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
	                false, false);
	    }

	    public int getScore() {
	        return 500;
	    }
}
