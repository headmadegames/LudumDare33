package headmade.ld33.screens;

import headmade.ld33.DirectedGame;
import headmade.ld33.actions.ActionFactory;
import headmade.ld33.actors.CreditsActor;
import headmade.ld33.actors.HowToActor;
import headmade.ld33.actors.JigglyImageTextButton;
import headmade.ld33.actors.SettingsActor;
import headmade.ld33.assets.AssetMusic;
import headmade.ld33.assets.Assets;
import headmade.ld33.screens.transitions.ScreenTransition;
import headmade.ld33.screens.transitions.ScreenTransitionFade;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class MenuScreen extends StageScreen {
	private static final String	TAG	= MenuScreen.class.getName();
	private final SettingsActor	settings;
	private final CreditsActor	credits;
	private final HowToActor	howTo;
	private final Music			music;

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

		// ((OrthographicCamera) stage.getCamera()).zoom = 0.5f;

		music = Assets.instance.assetsManager.get(AssetMusic.music, Music.class);
		music.setVolume(0.5f);
		music.play();
		music.setLooping(true);
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
				play();
				return true;
			}
		});

		final JigglyImageTextButton settingsButton = new JigglyImageTextButton("Mute music", Assets.instance.skin, "settings", null);
		settingsButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (music.isPlaying()) {
					music.pause();
					settingsButton.setText("Unmute music");
				} else {
					music.play();
					settingsButton.setText("Mute music");
				}
				return true;
			}
		});

		final Table menu = new Table();
		menu.add(playButton).fill().row();
		menu.add(settingsButton).fill().space(5).row();
		return menu;
	}

	protected void play() {
		final ScreenTransition transition = ScreenTransitionFade.init(0.5f);
		// game.setScreen(new MenuScreen(game), transition);
		game.setScreen(new PlayScreen(game), transition);
	}

	protected void showCredits() {
		credits.addAction(Actions.fadeIn(0.3f));
		howTo.addAction(Actions.fadeOut(0.3f));
		settings.addAction(Actions.fadeOut(0.3f));
	}

	protected void showSettings() {
		settings.addAction(Actions.fadeIn(0.3f));
		credits.addAction(Actions.fadeOut(0.3f));
		howTo.addAction(Actions.fadeOut(0.3f));
	}
}
