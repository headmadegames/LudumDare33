package headmade.ld33;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import headmade.ld33.screens.PlayScreen;

public class PlayContactListener implements ContactListener {
	private static final String TAG = PlayContactListener.class.getName();

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
		if (play.groundBody.equals(contact.getFixtureA().getBody())) {
			if (play.monsterBody.equals(contact.getFixtureB().getBody())) {
				Gdx.app.log(TAG, "Contact!");
				play.fail();
			}
		} else if (play.monsterBody.equals(contact.getFixtureA().getBody())) {
			if (play.groundBody.equals(contact.getFixtureB().getBody())) {
				play.fail();
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

}
