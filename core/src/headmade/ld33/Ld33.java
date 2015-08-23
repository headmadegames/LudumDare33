package headmade.ld33;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import headmade.ld33.assets.Assets;
import headmade.ld33.screens.IntroScreen;
import headmade.ld33.screens.transitions.ScreenTransition;
import headmade.ld33.screens.transitions.ScreenTransitionFade;

public class Ld33 extends DirectedGame {

	public World				world;
	public Box2DDebugRenderer	box2dDebugRenderer;

	@Override
	public void create() {
		batch = new SpriteBatch();
		box2dDebugRenderer = new Box2DDebugRenderer();

		// world = new World(new Vector2(0, -10f), true);

		// Load all assets
		Assets.instance.init();

		// Start game with Playground Screen
		final ScreenTransition transition = ScreenTransitionFade.init(0.0f);
		setScreen(new IntroScreen(this), transition);
	}

	@Override
	public void dispose() {
		super.dispose();
		Assets.instance.dispose();
	}

}
