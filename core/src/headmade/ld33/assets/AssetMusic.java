package headmade.ld33.assets;

import com.badlogic.gdx.audio.Music;

import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class AssetMusic {
	private static final String TAG = AssetMusic.class.getName();

	private static final String MUSIC_PATH = "music/";

	//	@formatter:off
	@Asset(Music.class)
	public static final String
	music		= MUSIC_PATH + "bg.ogg";
//	sound2		= MUSIC_PATH + "bg2.mp3";

}
