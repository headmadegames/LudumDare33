package headmade.ld33.assets;

import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class AssetTextures {

	private static final String TAG = AssetTextures.class.getName();

	public static final String	WHEEEL		= "wheel";
	public static final String	TENT		= "tent";
	public static final String	TENTFLOOR	= "tent_floor";

	//	@formatter:off
	//	@Asset(NinePatch.class)
	//	public static final String
	//	block9 = "block",
	//	blockDown9 = "blockDown";

//	@Asset(value = TextureAtlas.class)
//	public static final String atlas = Assets.GAME_ATLAS;

	private static final SkinParameter skinParameter = new SkinLoader.SkinParameter(Assets.GAME_ATLAS);
	//    static {
	//		// init Skin
	//    	skinParameter.
	//	}

	@Asset(value = Skin.class, params = "skinParameter")
	public static final String
	skin = Assets.PACKS_BASE + Assets.PACK + ".json";
}
