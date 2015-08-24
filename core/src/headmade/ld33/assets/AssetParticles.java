package headmade.ld33.assets;

import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader.ParticleEffectParameter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.Particle;

public class AssetParticles {
	private static final String TAG = AssetParticles.class.getName();

	private static final String PARTICLES_PATH = "particles/";

	private static final ParticleEffectParameter skinParameter = new ParticleEffectLoader.ParticleEffectParameter();

	static {
		// init Skin
		// skinParameter.1
		// ParticleEffectLoader loader = new ParticleEffectLoader(resolver);
		// loader.load(am, fileName, file, param)
	}

	//	@formatter:off
//	@Asset(Particle.class)
	public static final String
	sound1		= PARTICLES_PATH + "smoke.fx";
//	sound2		= SOUNDS_PATH + "sound2.wav";

}
