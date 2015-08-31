package headmade.ld33.actors;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import headmade.ld33.assets.Assets;

public class HowToActor extends BaseMenuContainer {
	private static final String TAG = HowToActor.class.getName();

	public HowToActor() {
		this.setSkin(Assets.instance.skin);
		
		if(Gdx.app.getType() == ApplicationType.Android || Gdx.app.getType() == ApplicationType.iOS) {
			add("Control the bike by tilting your device.");
		} else {
			add("Controls:").colspan(2).row();
			add("W / Up");
			add("Jump").row();
			
			add("A / Left");
			add("Cycle left").row();
			
			add("D / Right");
			add("Cycle right").row();
		}
	}
}
