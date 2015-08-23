package headmade.ld33.screens;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import headmade.ld33.DirectedGame;
import headmade.ld33.Ld33;
import headmade.ld33.PlayContactListener;
import headmade.ld33.PlayInputAdapter;
import headmade.ld33.assets.AssetTextures;
import headmade.ld33.assets.Assets;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

public class PlayScreen extends AbstractGameScreen {

	private static final float MAX_MOTORSPEED = 18.0f;

	private static final String TAG = PlayScreen.class.getName();

	public static final String	TOUCHMODE_PULL	= "PULL";
	public static final String	TOUCHMODE_DRAG	= "DRAG";

	// protected float TARGET_SCREEN_WIDTH = Gdx.graphics.getWidth() / 32f;
	// protected float TARGET_SCREEN_HEIGHT = Gdx.graphics.getHeight() / 32f;
	protected float	TARGET_SCREEN_WIDTH		= 8;
	protected float	TARGET_SCREEN_HEIGHT	= TARGET_SCREEN_WIDTH * (Gdx.graphics.getWidth() / Gdx.graphics.getHeight());

	public final World		world;
	public Body				monsterBody;
	public Body				wheel;
	public Body				groundBody;
	public WheelJoint		wheelJoint;
	public MouseJoint		mouseJoint;
	public final Vector2	mouseTarget	= new Vector2(TARGET_SCREEN_WIDTH / 2, TARGET_SCREEN_HEIGHT / 2);
	public Vector3			lastTouchDown;

	public String	touchMode;
	public boolean	moveRight;
	public boolean	moveLeft;
	public boolean	cameraShake;

	private boolean	resetPlay;
	private boolean	showFail;
	private float	resetTimer	= 1;

	private TiledDrawable			background;
	private TiledDrawable			lowerBackground;
	private final InputProcessor	inputProcessor;

	private final BodyDef tomatoDef;

	private final CircleShape tomatoShape;

	public PlayScreen(DirectedGame game) {
		super();
		this.game = game;
		this.inputProcessor = new PlayInputAdapter(this);

		if (camera == null) {
			camera = new OrthographicCamera();
		}
		if (viewport == null) {
			Gdx.app.log(TAG, "New x viewport with width: " + TARGET_SCREEN_WIDTH + " and height: " + TARGET_SCREEN_HEIGHT);
			viewport = new ExtendViewport(TARGET_SCREEN_WIDTH, TARGET_SCREEN_HEIGHT, camera);
		}
		viewport.apply();
		Gdx.input.setCatchBackKey(false);

		this.world = new World(new Vector2(0f, -9.81f), false);
		createLevel(world, 0, 0);
		createPlayer(world, 8, 1);

		tomatoShape = new CircleShape();
		tomatoShape.setRadius(0.1f);
		tomatoDef = new BodyDef();
		tomatoDef.type = BodyType.DynamicBody;

		final ContactListener contactListener = new PlayContactListener(this);
		world.setContactListener(contactListener);
	}

	@Override
	public void dispose() {
		super.dispose();
		world.dispose();
		tomatoShape.dispose();
	}

	public void fail() {
		resetPlay = true;
	}

	@Override
	public InputProcessor getInputProcessor() {
		return inputProcessor;
	}

	@Override
	public void render(float delta) {
		update(delta);

		final SpriteBatch batch = ((Ld33) game).batch;
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		background = new TiledDrawable(Assets.instance.atlas.findRegion(AssetTextures.TENT));
		background.draw(batch, 0, 0, 1000, 1000);
		background.draw(batch, 0, 0, 1000, 1000);

		// ((Ld33) game).batch.draw(texture, 0f, 0f, 32f, 32f, 0, 0, 32, 32, false, false);

		Box2DSprite.draw(((Ld33) game).batch, world);

		batch.end();

		((Ld33) game).box2dDebugRenderer.render(world, camera.combined);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	public void throwTomato(int screenX, int screenY) {
		final Vector3 target = camera.unproject(new Vector3(screenX, screenY, 0));
		tomatoDef.position.set(monsterBody.getPosition().x + 5, monsterBody.getPosition().y + 1);
		final Body tomato = world.createBody(tomatoDef);
		tomato.createFixture(tomatoShape, 1);
	}

	private void createLevel(World world2, float x, float y) {
		final PolygonShape groundShape = new PolygonShape();
		groundShape.setAsBox(500f, 50f, new Vector2(-400.0f, -50f), 0);

		final BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;
		bd.position.set(x + 0.0f, y + 0.0f);

		groundBody = world.createBody(bd);
		final Fixture groundFix = groundBody.createFixture(groundShape, 0f);
		groundFix.setFriction(2);
	}

	private void createPlayer(World world, float x, float y) {

		final PolygonShape monsterShape = new PolygonShape();
		monsterShape.setAsBox(0.5f, 0.5f, new Vector2(0.0f, 2f), 0);

		final CircleShape circle = new CircleShape();
		circle.setRadius(0.6f);

		final BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;
		bd.position.set(x + 0.0f, y + 1.0f);
		monsterBody = world.createBody(bd);
		final Fixture monsterFixture = monsterBody.createFixture(monsterShape, 1f);

		final FixtureDef fd = new FixtureDef();
		fd.shape = circle;
		fd.density = 2.0f;
		fd.friction = 2f;

		bd.position.set(x - 0.0f, y - 0.5f);
		wheel = world.createBody(bd);
		final Fixture wheelFixture = wheel.createFixture(fd);

		final Box2DSprite wheelSprite = new Box2DSprite(Assets.instance.atlas.findRegion(AssetTextures.WHEEEL));
		wheel.setUserData(new Box2DSprite(Assets.instance.atlas.findRegion(AssetTextures.WHEEEL)));
		wheelFixture.setUserData(wheelSprite);
		// monsterBody.setUserData(new Box2DSprite(texture));
		// monsterFixture.setUserData(new Box2DSprite(texture));

		final WheelJointDef jd = new WheelJointDef();
		final Vector2 axis = new Vector2(0.0f, 1.0f);

		final float hz = 4;
		final float zeta = 0.1f;
		jd.initialize(monsterBody, wheel, wheel.getPosition(), axis);
		jd.motorSpeed = 0.0f;
		jd.maxMotorTorque = 20.0f;
		jd.enableMotor = true;
		jd.frequencyHz = hz;
		jd.dampingRatio = zeta;
		wheelJoint = (WheelJoint) world.createJoint(jd);

		final MouseJointDef def = new MouseJointDef();
		def.bodyA = groundBody;
		def.bodyB = monsterBody;
		def.collideConnected = true;
		def.target.set(monsterBody.getWorldCenter());
		def.maxForce = 7.0f * monsterBody.getMass();

		mouseJoint = (MouseJoint) world.createJoint(def);
		monsterBody.setAwake(true);

		circle.dispose();
		monsterShape.dispose();
	}

	private void update(float delta) {
		if (resetPlay) {
			final Array<JointEdge> list = monsterBody.getJointList();
			for (final JointEdge item : list) {
				world.destroyJoint(item.joint);
				world.destroyBody(item.joint.getBodyB());
			}
			// actual remove
			world.destroyBody(monsterBody);
			resetPlay = false;
			showFail = true;
		}
		if (showFail) {
			if (resetTimer > 1f) {
				createPlayer(world, 8, 1);
				showFail = false;
			} else {
				resetTimer += delta;
			}
		} else {

			camera.position.set(wheel.getPosition().x, monsterBody.getPosition().y, 0);
			final Random random = new Random();
			if (cameraShake) {
				camera.translate(random.nextFloat() * 0.1f, random.nextFloat() * 0.1f, 0);
			}
			camera.update();

			mouseJoint.setTarget(monsterBody.getPosition());
			if (moveLeft) {
				wheelJoint.enableMotor(true);
				if (wheelJoint.getMotorSpeed() < 0) {
					wheelJoint.setMotorSpeed(0);
				} else {
					wheelJoint.setMotorSpeed(MathUtils.clamp(wheelJoint.getMotorSpeed() + 0.1f, -MAX_MOTORSPEED, MAX_MOTORSPEED));
				}
			} else if (moveRight) {
				wheelJoint.enableMotor(true);
				if (wheelJoint.getMotorSpeed() > 0) {
					wheelJoint.setMotorSpeed(0);
				} else {
					wheelJoint.setMotorSpeed(MathUtils.clamp(wheelJoint.getMotorSpeed() - 0.1f, -MAX_MOTORSPEED, MAX_MOTORSPEED));
				}
			}
		}

		world.step(1 / 60f, 8, 3);

	}

}
