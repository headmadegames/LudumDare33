package headmade.ld33.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.VisUI;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class Assets implements Disposable, AssetErrorListener {

	private static final String TAG = Assets.class.getName();

	public static final String	HEADMADE_LOGO	= "headmade_large.png";
	public static final String	PACKS_BASE		= "packs/";
	public static final String	PACK			= "pack";
	public static final String	GAME_ATLAS		= PACKS_BASE + PACK + ".atlas";

	public static final Assets instance = new Assets(); // Singleton

	public static AnnotationAssetManager assetsManager = new AnnotationAssetManager();

	public TextureAtlas	atlas;	// Don't make this static!!!
	public Skin			skin;

	// singleton: prevent instantiation from other classes
	private Assets() {
		assetsManager = new AnnotationAssetManager();
		// set asset manager error handler
		assetsManager.setErrorListener(this);
	}

	@Override
	public void dispose() {
		Gdx.app.debug(TAG, "Disposing assets...");
		assetsManager.dispose();
		VisUI.dispose();
	}

	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Error in Assets", throwable);
	}

	/**
	 * Loads minimal stuff
	 */
	public void init() {
		Gdx.app.debug(TAG, "Init minimal assets...");

		assetsManager.load(HEADMADE_LOGO, Texture.class);

		VisUI.load();

		assetsManager.finishLoading();

		final Texture logo = assetsManager.get(HEADMADE_LOGO, Texture.class);
		logo.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
	}

	/**
	 * Load all assets using the {@link AssetManager#load}. It blocks until all loading is finished. This method must be called before
	 * accessing any asset.
	 */
	public void loadAll() {
		Gdx.app.debug(TAG, "Init assets...");

		assetsManager.load(AssetSounds.class);
		assetsManager.load(AssetMusic.class);
		assetsManager.load(AssetTextures.class);
	}

	public void onFinishLoading() {
		atlas = assetsManager.get(GAME_ATLAS, TextureAtlas.class);
		skin = assetsManager.get(AssetTextures.skin, Skin.class);
		setTextureFilter(atlas, TextureFilter.Nearest);
	}

	public void playSound(String name) {
		final Sound sound = assetsManager.get(name, Sound.class);
		if (sound != null) {
			sound.play();
		} else {
			Gdx.app.error(TAG, "No Sound with name " + name);
		}
	}

	/**
	 * // enable texture filtering for pixel smoothing
	 *
	 * @param atlas
	 * @param typeOfFilter
	 */
	private void setTextureFilter(TextureAtlas atlas, TextureFilter typeOfFilter) {
		for (final Texture t : atlas.getTextures()) {
			t.setFilter(typeOfFilter, typeOfFilter); // min=mag
		}

		final Skin skin = assetsManager.get(AssetTextures.skin, Skin.class);
		final BitmapFont font = skin.getFont("default-font");
		for (int i = 0; i < font.getRegions().size; i++) {
			font.getRegion(i).getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		}
	}
}
