package headmade.ld33;

import headmade.ld33.assets.AssetSounds;
import headmade.ld33.assets.Assets;
import headmade.ld33.screens.PlayScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class PlayContactListener implements ContactListener {
	private static final String TAG = PlayContactListener.class.getName();

	public static final short CATEGORY_TOMATO = 0x0002;

	private final PlayScreen play;

	public PlayContactListener(PlayScreen playScreen) {
		this.play = playScreen;
	}

	@Override
	public void beginContact(Contact contact) {
	}

	@Override
	public void endContact(Contact contact) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		if (contact.getFixtureA().getFilterData().categoryBits == CATEGORY_TOMATO) {
			play.splatterTomato(contact.getFixtureA().getBody());
		} else if (contact.getFixtureB().getFilterData().categoryBits == CATEGORY_TOMATO) {
			play.splatterTomato(contact.getFixtureB().getBody());
			// } else if (play.groundBody.equals(contact.getFixtureA().getBody())) {
			// if (play.monsterBody.equals(contact.getFixtureB().getBody())) {
			// Gdx.app.log(TAG, "Contact!");
			// play.fail();
			// }
		} else if (play.groundBody.equals(contact.getFixtureA().getBody())) {
			if (play.monsterBody.equals(contact.getFixtureB().getBody())) {
				Gdx.app.log(TAG, "Contact!");
				play.fail();
			}
		} else if (play.monsterBody.equals(contact.getFixtureA().getBody())) {
			if (play.groundBody.equals(contact.getFixtureB().getBody())) {
				play.fail();
			} else {
				for (final float i : impulse.getNormalImpulses()) {
					if (i > 3) {
						Assets.instance.playSound(AssetSounds.hit, 0.3f, MathUtils.clamp(i / 6f, 0.5f, 1f));
						break;
					}
				}
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

}
