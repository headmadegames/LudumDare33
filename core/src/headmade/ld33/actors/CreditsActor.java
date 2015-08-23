package headmade.ld33.actors;

import headmade.ld33.assets.Assets;

public class CreditsActor extends BaseMenuContainer {

	private static final String TAG = CreditsActor.class.getName();

	public CreditsActor() {
		this.setSkin(Assets.instance.skin);
		add("Headmade Games").row();
		add("Made with Libgdx").row();
	}

}
