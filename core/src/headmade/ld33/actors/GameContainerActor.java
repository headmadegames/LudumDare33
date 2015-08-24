package headmade.ld33.actors;

import headmade.ld33.Ld33;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class GameContainerActor extends Widget {

	private static final String	TAG	= GameContainerActor.class.getName();
	private final Ld33			game;
	private final World			world;

	public GameContainerActor(Ld33 game) {
		this.game = game;
		this.world = game.world;

		this.addListener(new InputListener() {

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.LEFT) {
					moveBy(-10, 0);
				} else if (keycode == Keys.RIGHT) {
					moveBy(10, 0);
				}
				return super.keyDown(event, keycode);
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				super.touchDragged(event, x, y, pointer);
				Gdx.app.log(TAG, "x: " + x + " - y: " + y);
				moveBy(x, y);
			}

		});
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.end();

		final Matrix4 combined = getStage().getCamera().combined;
		final Vector3 vec3 = new Vector3(0, 0, 0);
		getStage().getCamera().project(vec3);
		game.box2dDebugRenderer.render(world, getStage().getCamera().combined);

		batch.begin();
	}

}
