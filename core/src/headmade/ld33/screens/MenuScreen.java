package headmade.ld33.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import headmade.ld33.DirectedGame;
import headmade.ld33.actions.ActionFactory;
import headmade.ld33.actors.CreditsActor;
import headmade.ld33.actors.HowToActor;
import headmade.ld33.actors.JigglyImageTextButton;
import headmade.ld33.actors.SettingsActor;
import headmade.ld33.assets.Assets;

public class MenuScreen extends StageScreen {
	private static final String	TAG	= MenuScreen.class.getName();
	private final SettingsActor	settings;
	private final CreditsActor	credits;
	private final HowToActor	howTo;

	public MenuScreen(DirectedGame game) {
		super(game);

		final Table rootTable = new Table();
		rootTable.setFillParent(true);

		howTo = new HowToActor();
		settings = new SettingsActor();
		credits = new CreditsActor();

		settings.getColor().a = 0f;
		credits.getColor().a = 0f;

		final Stack mainContainer = new Stack();
		mainContainer.add(howTo);
		mainContainer.add(settings);
		mainContainer.add(credits);

		rootTable.add(mainContainer).expand().fill(1f, 1f);
		rootTable.add(buildMenu()).expandY().center().padRight(10f);
		rootTable.row();
		// rootTable.setDebug(true);

		this.stage.addActor(rootTable);

		// ((OrthographicCamera) stage.getCamera()).zoom = 2f;
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		stage.getBatch().setShader(null);
	}

	private Table buildMenu() {
		final JigglyImageTextButton playButton = new JigglyImageTextButton("Play", Assets.instance.skin, "play",
				ActionFactory.wiggleRepeat(1f, 0.8f));
		playButton.setName("play");
		playButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				showPlaySettings();
				return true;
			}
		});

		final JigglyImageTextButton settingsButton = new JigglyImageTextButton("Settings", Assets.instance.skin, "settings", null);
		settingsButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				showSettings();
				return true;
			}
		});

		final JigglyImageTextButton creditsButton = new JigglyImageTextButton("Credits", Assets.instance.skin, "credits", null);
		creditsButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				showCredits();
				return true;
			}
		});

		final JigglyImageTextButton quitButton = new JigglyImageTextButton("Quit", Assets.instance.skin, "quit", null);
		quitButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.exit();
				return true;
			}
		});

		final Table menu = new Table();
		menu.add(playButton).fill().row();
		menu.add(settingsButton).fill().space(5).row();
		menu.add(creditsButton).fill().space(5).row();
		menu.add(quitButton).fill().space(15).row();
		return menu;
	}

	protected void showCredits() {
		credits.addAction(Actions.fadeIn(0.3f));
		howTo.addAction(Actions.fadeOut(0.3f));
		settings.addAction(Actions.fadeOut(0.3f));
	}

	protected void showPlaySettings() {
		howTo.addAction(Actions.fadeIn(0.3f));
		credits.addAction(Actions.fadeOut(0.3f));
		settings.addAction(Actions.fadeOut(0.3f));
	}

	protected void showSettings() {
		settings.addAction(Actions.fadeIn(0.3f));
		credits.addAction(Actions.fadeOut(0.3f));
		howTo.addAction(Actions.fadeOut(0.3f));
	}
}
