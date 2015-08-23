package headmade.ld33.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import headmade.ld33.DirectedGame;
import headmade.ld33.assets.Assets;

public class WinScreen extends StageScreen {

	private static final String TAG = WinScreen.class.getName();

	private final Table rootTable;

	public WinScreen(final DirectedGame game) {
		super(game);

		rootTable = new Table(Assets.instance.skin);
		rootTable.setFillParent(true);

		rootTable.add("Congratulations!").center();
		rootTable.row();
		rootTable.add("You won!").center();

		// rootTable.setDebug(true);
		this.stage.addActor(rootTable);
	}

}
