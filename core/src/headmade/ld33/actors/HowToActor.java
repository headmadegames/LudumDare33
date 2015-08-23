package headmade.ld33.actors;

import headmade.ld33.assets.Assets;

public class HowToActor extends BaseMenuContainer {
	private static final String TAG = HowToActor.class.getName();

	public HowToActor() {
		this.setSkin(Assets.instance.skin);
		add("Controls:").row();
		add("Cursor Keys").row();
	}
}
