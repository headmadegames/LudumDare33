package headmade.ld33.assets;

import com.badlogic.gdx.audio.Sound;

import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class AssetSounds {
	private static final String TAG = AssetSounds.class.getName();

	private static final String SOUNDS_PATH = "sounds/";

	//	@formatter:off
	@Asset(Sound.class)
	public static final String
	squish		= SOUNDS_PATH + "squish.wav",
	brake		= SOUNDS_PATH + "brake.wav",
	jump		= SOUNDS_PATH + "jump.wav",
	bike		= SOUNDS_PATH + "bike.wav",
	hit			= SOUNDS_PATH + "hit.wav";

}
