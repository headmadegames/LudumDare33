package headmade.ld33.screens;

import headmade.ld33.DirectedGame;
import headmade.ld33.assets.Assets;
import headmade.ld33.screens.transitions.ScreenTransition;
import headmade.ld33.screens.transitions.ScreenTransitionFade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

//import com.kotcrab.vis.ui.widget.VisProgressBar;

public class IntroScreen extends StageScreen {

	private static final String TAG = IntroScreen.class.getName();

	// private final VisProgressBar progress;

	private final Cell bottomCell;

	private final Table rootTable;

	public IntroScreen(final DirectedGame game) {
		super(game);

		rootTable = new Table();
		rootTable.setFillParent(true);
		final Actor logo = new Image(Assets.assetsManager.get(
				Assets.HEADMADE_LOGO, Texture.class));
		// logo.setOrigin(logo.getWidth() / 2, logo.getHeight() / 2);
		// logo.scaleBy(2f);
		logo.setColor(Color.BLACK);
		// progress = new VisProgressBar(0, 1, 0.01f, false);

		rootTable.add(logo).center().expand();
		rootTable.row();
		// bottomCell = rootTable.add(progress);
		bottomCell = rootTable.add();

		bottomCell.center().pad(10f);

		// rootTable.setDebug(true);
		this.stage.addActor(rootTable);

		Assets.instance.loadAll();
	}

	@Override
	public void preDraw(float delta) {
		super.preDraw(delta);

		Assets.assetsManager.update();
		// progress.setValue(Assets.assetsManager.getProgress());
		if (MathUtils.isEqual(1, Assets.assetsManager.getProgress())) {
			// done loading eh
			Assets.instance.onFinishLoading();

			final TextButton startButton = new TextButton("Start",
					Assets.instance.skin);
			startButton.addListener(new InputListener() {

				@Override
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					startGame();
					return super.touchDown(event, x, y, pointer, button);
				}
			});

			bottomCell.setActor(startButton);
			startGame();
		}
	}

	protected void startGame() {
		Gdx.app.log(TAG, "Start Button clicked");

		final ScreenTransition transition = ScreenTransitionFade.init(0.0f);
		// game.setScreen(new MenuScreen(game), transition);
		game.setScreen(new PlayScreen(game), transition);
	}
}
