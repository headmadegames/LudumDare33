package headmade.ld33.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import headmade.ld33.DirectedGame;

public abstract class AbstractGameScreen extends InputAdapter implements Screen {

	private static final String TAG = AbstractGameScreen.class.getName();

	protected Viewport	viewport;
	protected Camera	camera;

	/* At the moment we are going to work with pixel dimensions */
	protected float			TARGET_SCREEN_WIDTH		= Gdx.graphics.getWidth();
	protected float			TARGET_SCREEN_HEIGHT	= Gdx.graphics.getHeight();
	protected float			MAX_SCENE_WIDTH			= 1920.0f;
	protected float			MAX_SCENE_HEIGHT		= 1080.0f;
	protected DirectedGame	game;

	public AbstractGameScreen() {
		// TODO Auto-generated constructor stub
	}

	public AbstractGameScreen(DirectedGame game) {
		this.game = game;
		if (camera == null) {
			camera = new OrthographicCamera();
		}
		if (viewport == null) {
			Gdx.app.log(TAG, "New viewport with width: " + TARGET_SCREEN_WIDTH + " and height: " + TARGET_SCREEN_HEIGHT);
			viewport = new ExtendViewport(TARGET_SCREEN_WIDTH, TARGET_SCREEN_HEIGHT, camera);
		}
		viewport.apply();
		Gdx.input.setCatchBackKey(false); // will be set to true only when the
		// child override the InputAdapter
		// class methods
	}

	@Override
	public void dispose() {
	}

	public Camera getCamera() {
		return camera;
	}

	public DirectedGame getGame() {
		return game;
	}

	public abstract InputProcessor getInputProcessor();

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resize(int width, int height) {
		// Whenever a resize event occurs, the viewport needs to be informed
		// about it and updated. This will automatically recalculate the
		// viewport parameters and update the camera (centered to the screen)
		Gdx.app.log(TAG, "Resizing to " + width + "x" + height);
		viewport.update(width, height, true);
	}

	@Override
	public void resume() {

	}

	@Override
	public void show() {

	}

}
