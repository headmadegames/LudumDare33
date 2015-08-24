package headmade.ld33;

import headmade.ld33.assets.AssetSounds;
import headmade.ld33.assets.Assets;
import headmade.ld33.screens.PlayScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PlayInputAdapter extends InputAdapter {

	private static final String TAG = PlayInputAdapter.class.getName();

	private final PlayScreen play;

	private long lastJumpTime = 0;

	public PlayInputAdapter(PlayScreen playScreen) {
		this.play = playScreen;
	}

	public void jump() {
		if (System.currentTimeMillis() > lastJumpTime + 1200) {
			lastJumpTime = System.currentTimeMillis();
			final Vector2 force = play.wheelJoint.getLocalAxisA().cpy();
			play.monsterBody.applyForceToCenter(force.scl(400f), true);

			final Vector2 force2 = play.monsterBody.getPosition().cpy().sub(play.wheel.getPosition()).scl(4f);
			play.wheel.applyForceToCenter(force2.scl(100f), true);

			Assets.instance.playSound(AssetSounds.jump);
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.A || keycode == Keys.LEFT) {
			play.moveLeft = true;
			return true;
		} else if (keycode == Keys.D || keycode == Keys.RIGHT) {
			play.moveRight = true;
			return true;
		} else if (keycode == Keys.W || keycode == Keys.UP) {
			Gdx.app.log(TAG, "Pos: " + play.wheel.getPosition());
			jump();
			return true;
		} else if (keycode == Keys.S || keycode == Keys.DOWN) {
			brake();
			return true;
		}
		return super.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.A || keycode == Keys.LEFT) {
			play.moveLeft = false;
			return true;
		} else if (keycode == Keys.D || keycode == Keys.RIGHT) {
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
		// play.mouseJoint.setTarget(play.mouseTarget);
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
//		((OrthographicCamera) play.getCamera()).zoom = MathUtils.clamp(((OrthographicCamera) play.getCamera()).zoom + amount, 1, 10);
//		play.getCamera().update();
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == Buttons.LEFT) {
//			play.throwTomato(screenX, screenY);
			// } else {
			// play.touchMode = play.TOUCHMODE_DRAG;
			// play.fail();
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
			// } else if (play.touchMode == play.TOUCHMODE_PULL) {
			// final Vector3 worldLastTouchdown = play.getCamera().unproject(play.lastTouchDown);
			// Gdx.app.log(TAG, "Query " + worldLastTouchdown.x + ", " + worldLastTouchdown.y);
			// play.world.QueryAABB(new QueryCallback() {
			// @Override
			// public boolean reportFixture(Fixture fixture) {
			// fixture.getBody().applyForceToCenter(new Vector2(-delta.x, delta.y).nor().scl(50), true);
			// return false;
			// }
			// }, worldLastTouchdown.x - .001f, worldLastTouchdown.y - .001f, worldLastTouchdown.x + .001f, worldLastTouchdown.y + .001f);
		}
		play.lastTouchDown = newTouchDown;
		return true;
	}

	private void brake() {
		play.wheelJoint.setMotorSpeed(0f);
		play.wheelJoint.enableMotor(false);
	}
}
