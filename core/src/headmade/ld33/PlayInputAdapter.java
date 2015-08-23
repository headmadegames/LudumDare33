package headmade.ld33;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;

import headmade.ld33.screens.PlayScreen;

public class PlayInputAdapter extends InputAdapter {

	private static final String TAG = PlayInputAdapter.class.getName();

	private final PlayScreen play;

	public PlayInputAdapter(PlayScreen playScreen) {
		this.play = playScreen;
	}

	public void jump() {
		final Vector2 force = play.wheelJoint.getLocalAxisA();
		play.wheel.applyForceToCenter(force.scl(400f), true);
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.LEFT) {
			play.getCamera().translate(-1, 0, 0);
			play.getCamera().update();
			return true;
		} else if (keycode == Keys.RIGHT) {
			play.getCamera().translate(1, 0, 0);
			play.getCamera().update();
			return true;
		} else if (keycode == Keys.DOWN) {
			play.getCamera().translate(0, -1, 0);
			play.getCamera().update();
			return true;
		} else if (keycode == Keys.UP) {
			play.getCamera().translate(0, 1, 0);
			play.getCamera().update();
			return true;
		} else if (keycode == Keys.A) {
			play.moveLeft = true;
			return true;
		} else if (keycode == Keys.D) {
			play.moveRight = true;
			return true;
		} else if (keycode == Keys.W) {
			jump();
		}
		return super.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.A) {
			play.moveLeft = false;
			return true;
		} else if (keycode == Keys.D) {
			play.moveRight = false;
			return true;
		}
		return super.keyUp(keycode);
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		final Vector3 target3 = play.getCamera().unproject(new Vector3(screenX, screenY, 0f));
		play.mouseTarget.x = target3.x;
		play.mouseTarget.y = target3.y;
		play.mouseJoint.setTarget(play.mouseTarget);
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		((OrthographicCamera) play.getCamera()).zoom = MathUtils.clamp(((OrthographicCamera) play.getCamera()).zoom + amount, 1, 10);
		play.getCamera().update();
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == Buttons.LEFT) {
			play.throwTomato(screenX, screenY);
		} else {
			play.touchMode = play.TOUCHMODE_DRAG;
		}
		if (null == play.lastTouchDown) {
			play.lastTouchDown = new Vector3(screenX, screenY, 0);
		} else {
			play.lastTouchDown.x = screenX;
			play.lastTouchDown.y = screenY;
		}
		// final Array<Body> bodies = new Array<Body>();
		// world.getBodies(bodies);
		// for (final Body body : bodies) {
		// Gdx.app.log(TAG, "body.getPosition();" + body.getPosition());
		// }
		return true; // super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		final Vector3 newTouchDown = new Vector3(screenX, screenY, 0);
		final Vector3 delta = play.lastTouchDown.cpy().sub(newTouchDown);

		if (play.touchMode == play.TOUCHMODE_DRAG) {
			play.getCamera().translate(delta.scl(0.01f, -0.01f, 1f));
			play.getCamera().update();
		} else if (play.touchMode == play.TOUCHMODE_PULL) {
			final Vector3 worldLastTouchdown = play.getCamera().unproject(play.lastTouchDown);
			Gdx.app.log(TAG, "Query " + worldLastTouchdown.x + ", " + worldLastTouchdown.y);
			play.world.QueryAABB(new QueryCallback() {
				@Override
				public boolean reportFixture(Fixture fixture) {
					fixture.getBody().applyForceToCenter(new Vector2(-delta.x, delta.y).nor().scl(50), true);
					return false;
				}
			}, worldLastTouchdown.x - .001f, worldLastTouchdown.y - .001f, worldLastTouchdown.x + .001f, worldLastTouchdown.y + .001f);
		}
		play.lastTouchDown = newTouchDown;
		return true;
	}
}