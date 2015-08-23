package headmade.ld33.actors;

import headmade.ld33.assets.Assets;

public class HowToActor extends BaseMenuContainer {
	private static final String TAG = HowToActor.class.getName();

	public HowToActor() {
		this.setSkin(Assets.instance.skin);
		add("Controls:").colspan(2).row();
		add("W / Up");
		add("Jump").row();

		add("A / Left");
		add("Cycle left").row();

		add("D / Right");
		add("Cycle right").row();
	}
}
