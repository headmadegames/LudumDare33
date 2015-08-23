package headmade.ld33;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

import headmade.ld33.assets.Assets;

public class Ld33Launcher {
	private static boolean	rebuildAtlas		= false;
	// private static boolean rebuildAtlas = false;
	private static boolean	drawDebugOutline	= false;

	public static void main(String[] arg) {
		if (rebuildAtlas) {
			final Settings settings = new Settings();
			settings.maxWidth = 2048;
			settings.maxHeight = 2048;
			settings.debug = drawDebugOutline;

			TexturePacker.process(settings, "assets-raw/images", "../android/assets/" + Assets.PACKS_BASE, Assets.PACK);
		}

		final LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Headmade Game";
		// cfg.useGL30 = true;
		cfg.width = 800;
		cfg.height = 480;
		cfg.samples = 4;
		new LwjglApplication(new Ld33(), cfg);
	}
}
