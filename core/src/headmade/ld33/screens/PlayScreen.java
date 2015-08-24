package headmade.ld33.screens;

import headmade.ld33.DirectedGame;
import headmade.ld33.Ld33;
import headmade.ld33.PlayContactListener;
import headmade.ld33.PlayInputAdapter;
import headmade.ld33.assets.AssetSounds;
import headmade.ld33.assets.AssetTextures;
import headmade.ld33.assets.Assets;
import headmade.ld33.screens.transitions.ScreenTransition;
import headmade.ld33.screens.transitions.ScreenTransitionFade;

import java.util.Random;

import net.dermetfan.gdx.graphics.g2d.Box2DSprite;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;
import net.dermetfan.gdx.physics.box2d.Chain;
import net.dermetfan.gdx.physics.box2d.Chain.Builder;
import net.dermetfan.gdx.physics.box2d.Chain.Connection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class PlayScreen extends AbstractGameScreen {

	private static final float	MAX_MOTORSPEED	= 28.0f;
	private static final float	WHEEL_RADIUS	= 0.6f;

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
	public boolean	resetPlay;
	public boolean	showFail;

	private boolean	bikeBroken;
	private float	resetTimer	= 0f;

	private final InputProcessor inputProcessor;

	private final Box2DSprite			tomatoSprite;
	private final Box2DSprite			boardSprite;
	private final Box2DSprite			rockerSprite;
	private final ParticleEffect		tomatoEffect;
	private final ParticleEffect		smokeEffect;
	private final TiledDrawable			background;
	private final TiledDrawable			lowerBackground;
	private final Array<PooledEffect>	effects				= new Array();
	private final ParticleEffectPool	tomatoEffectPool;
	// private final ParticleEffectPool smokeEffectPool;
	private final TextureRegion			bikeTexture;
	private final Array<Body>			toBeDestroyedBodies	= new Array<Body>();
	private float						accumDelta;
	private final float					bikeHeight;
	private boolean						createTomato;
	private int							throwTomatoX;
	private int							throwTomatoY;
	private final Random				random;
	private final Array<Body>			tomatoes			= new Array<Body>();

	public PlayScreen(DirectedGame game) {
		super();
		this.game = game;
		this.inputProcessor = new PlayInputAdapter(this);
		this.random = new Random();

		if (camera == null) {
			camera = new OrthographicCamera();
		}
		if (viewport == null) {
			Gdx.app.log(TAG, "New x viewport with width: " + TARGET_SCREEN_WIDTH + " and height: " + TARGET_SCREEN_HEIGHT);
			viewport = new ExtendViewport(TARGET_SCREEN_WIDTH, TARGET_SCREEN_HEIGHT, camera);
		}
		viewport.apply();
		Gdx.input.setCatchBackKey(false);

		this.world = new World(new Vector2(0f, -9.81f), true);

		tomatoSprite = new Box2DSprite(Assets.instance.atlas.findRegion(AssetTextures.TOMATO));
		boardSprite = new Box2DSprite(Assets.instance.atlas.findRegion(AssetTextures.BOARD));
		rockerSprite = new Box2DSprite(Assets.instance.atlas.findRegion(AssetTextures.ROCKER));

		createPlayer(8, 1);
		final Vector2 diffVec = monsterBody.getWorldCenter().cpy().sub(wheel.getWorldCenter());
		bikeHeight = diffVec.len() - WHEEL_RADIUS / 2;
		createLevel(0, 0);

		final ContactListener contactListener = new PlayContactListener(this);
		world.setContactListener(contactListener);

		smokeEffect = new ParticleEffect();
		smokeEffect.load(Gdx.files.internal("particles/smoke.fx"), Assets.instance.atlas);
		// smokeEffectPool = new ParticleEffectPool(smokeEffect, 2, 4);

		tomatoEffect = new ParticleEffect();
		tomatoEffect.load(Gdx.files.internal("particles/tomato.fx"), Assets.instance.atlas);
		tomatoEffectPool = new ParticleEffectPool(tomatoEffect, 2, 8);

		background = new TiledDrawable(Assets.instance.atlas.findRegion(AssetTextures.TENT));
		lowerBackground = new TiledDrawable(Assets.instance.atlas.findRegion(AssetTextures.TENTFLOOR));
		bikeTexture = Assets.instance.atlas.findRegion(AssetTextures.BIKE);
	}

	@Override
	public void dispose() {
		super.dispose();
		world.dispose();
		tomatoEffect.dispose();
		smokeEffect.dispose();
	}

	public void fail() {
		resetPlay = true;
		Assets.instance.playSound(AssetSounds.hit, 0.5f);
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
		Gdx.gl.glClearColor(0.2f, 0.15f, 0.1f, 1f);
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		// lowerBackground.draw(batch, -200, -5f, 1000, 10);
		background.draw(batch, -200, 0, 1000, 5);
		// lowerBackground.draw(batch, 0, 0, 0, 0, 3.2f, 3.2f, 10f, 1f, 0);

		{// bike
			final Vector2 diffVec = monsterBody.getWorldCenter().cpy().sub(wheel.getWorldCenter());
			// final Vector2 posVec = wheel.getWorldCenter().cpy().add(diffVec.cpy().scl(0.5f));

			final float width = bikeHeight * (new Float(bikeTexture.getRegionWidth()) / new Float(bikeTexture.getRegionHeight())) * 0.4f;
			batch.draw(bikeTexture, wheel.getWorldCenter().x - width, wheel.getWorldCenter().y, width, 0, width * 2, bikeHeight, 1f, 1f,
					diffVec.angle() - 90);
		}

		Box2DSprite.draw(((Ld33) game).batch, world);

		{// particles
			final float newA = MathUtils.clamp(wheel.getLinearVelocity().len() / 10f, 0.1f, 1f);
			if (newA > 0.3f) {
				if (accumDelta > 0) {
					smokeEffect.reset();
				}
				smokeEffect.draw(batch, delta);
				accumDelta = 0.0f;
			} else {
				accumDelta += delta;
			}

			for (int i = effects.size - 1; i >= 0; i--) {
				final PooledEffect effect = effects.get(i);
				effect.draw(batch, delta);
				if (effect.isComplete()) {
					effect.free();
					effects.removeIndex(i);
				}
			}
		}

		batch.end();

		// ((Ld33) game).box2dDebugRenderer.render(world, camera.combined);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	public void splatterTomato(Body tomato) {
		if (resetPlay || showFail) {
			return;
		}
		final PooledEffect effect = tomatoEffectPool.obtain();
		effect.setPosition(tomato.getPosition().x, tomato.getPosition().y);
		effects.add(effect);
		toBeDestroyedBodies.add(tomato);
		Assets.instance.playSound(AssetSounds.squish);
	}

	public void throwTomato(int screenX, int screenY) {
		if (resetPlay || showFail) {
			return;
		}
		throwTomatoX = screenX;
		throwTomatoY = screenY;
		createTomato = true;
	}

	private Chain createBridge(final int x1, final int y1, final int x2, final int y2) {
		final PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.5f, 0.05f);
		final int bridgeWidth = x2 - x1;

		final Builder builder = new Builder() {

			BodyDef bodyDef = new BodyDef();
			FixtureDef fixtureDef = new FixtureDef();
			RevoluteJointDef jointDef = new RevoluteJointDef();

			{ // constructor
				bodyDef.type = BodyType.DynamicBody;
				fixtureDef.shape = shape;
				fixtureDef.density = 2;
				jointDef.localAnchorA.x = -Box2DUtils.width(shape) / 2;
				jointDef.localAnchorB.x = Box2DUtils.width(shape) / 2;
				// jointDef.localAnchorA.y = -Box2DUtils.height(shape) / 2;
				// jointDef.localAnchorB.y = Box2DUtils.height(shape) / 2;
			}

			@Override
			public Connection createConnection(Body seg1, int seg1index, Body seg2, int seg2index) {
				jointDef.bodyA = seg1;
				jointDef.bodyB = seg2;
				return new Connection(world.createJoint(jointDef));
			}

			@Override
			public Body createSegment(int index, int length, Chain chain) {
				bodyDef.position.set(x1 + index, y1 + index * (y2 - y1) / length);
				final Body segment = world.createBody(bodyDef);
				final Fixture fix = segment.createFixture(fixtureDef);
				fix.setUserData(boardSprite);
				segment.setUserData(boardSprite);
				return segment;
			}

		};

		final Chain chain = new Chain(bridgeWidth - 1, builder);
		shape.dispose();

		return chain;
	}

	private void createLevel(float x, float y) {
		final PolygonShape groundShape = new PolygonShape();
		groundShape.setAsBox(5000f, 50f, new Vector2(-400.0f, -50f), 0);

		final BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;
		bd.position.set(x + 0.0f, y + 0.0f);

		groundBody = world.createBody(bd);
		final Fixture groundFix = groundBody.createFixture(groundShape, 0f);
		groundFix.setFriction(2);

		bd.position.set(-50f, 0);
		final Body wallBody = world.createBody(bd);
		groundShape.setAsBox(100f, 100f, new Vector2(-100f, 0), 0);
		final Fixture wallFix = wallBody.createFixture(groundShape, 0f);
		wallBody.setUserData(boardSprite);
		wallFix.setUserData(boardSprite);

		groundShape.dispose();

		// createRocker(8, 0, 1, 0.2f);
		// createRamp(12, 1, 3, 1.5f, 0);
		// createBridge(0, 0, 0, 0);
		// createRamp(0, 0, 2, 0.5f, 0);
		// createBridge(2, 1, 6, 1);
		createRamp(14, 0f, 2, 0.25f, 0);
		createRocker(24, 0, 3, 0.3f);
		createRamp(36, 0f, 2, 0.4f, 0);
		createRocker(48, 0, 6, 0.6f);
		createRamp(60, 0f, 3, 0.75f, 0);
		createRocker(80, 0, 8, 0.9f);
	}

	private void createPlayer(float x, float y) {
		Gdx.app.log(TAG, "Create Monster");
		// final CircleShape monsterShape = new CircleShape();
		// monsterShape.setRadius(0.6f);
		// monsterShape.setPosition(new Vector2(0.0f, 2f));
		final PolygonShape monsterShape = new PolygonShape();
		monsterShape.setAsBox(0.5f, 0.5f, new Vector2(0.0f, 2f), 0);

		final CircleShape circle = new CircleShape();
		circle.setRadius(WHEEL_RADIUS);

		final BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;
		bd.position.set(x + 0.0f, y + 0.5f);
		monsterBody = world.createBody(bd);
		final Fixture monsterFixture = monsterBody.createFixture(monsterShape, 1f);
		monsterFixture.setRestitution(0.7f);

		final Box2DSprite monsterSprite = new Box2DSprite(Assets.instance.atlas.findRegion(AssetTextures.MONSTER));
		monsterBody.setUserData(monsterSprite);
		monsterFixture.setUserData(monsterSprite);

		Gdx.app.log(TAG, "Create Wheel");
		final FixtureDef fd = new FixtureDef();
		fd.shape = circle;
		fd.density = 2.0f;
		fd.friction = 2f;
		fd.restitution = 0.2f;

		bd.position.set(x - 0.0f, y - 0.0f);
		wheel = world.createBody(bd);
		final Fixture wheelFixture = wheel.createFixture(fd);

		final Box2DSprite wheelSprite = new Box2DSprite(Assets.instance.atlas.findRegion(AssetTextures.WHEEEL));
		wheel.setUserData(new Box2DSprite(Assets.instance.atlas.findRegion(AssetTextures.WHEEEL)));
		wheelFixture.setUserData(wheelSprite);
		// monsterBody.setUserData(new Box2DSprite(texture));
		// monsterFixture.setUserData(new Box2DSprite(texture));

		Gdx.app.log(TAG, "Create Monster Wheel joints");
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

		// Gdx.app.log(TAG, "Create Mouse joints");
		// final MouseJointDef def = new MouseJointDef();
		// def.bodyA = groundBody;
		// def.bodyB = monsterBody;
		// def.collideConnected = true;
		// def.target.set(monsterBody.getWorldCenter());
		// def.maxForce = 7.0f * monsterBody.getMass();
		//
		// mouseJoint = (MouseJoint) world.createJoint(def);
		// monsterBody.setAwake(true);

		// { // bike
		// final Box2DSprite bikeSprite = new Box2DSprite(Assets.instance.atlas.findRegion(AssetTextures.BIKE));
		// final Vector2 diffVec = monsterBody.getPosition().cpy().sub(wheel.getPosition());
		// final Vector2 posVec = wheel.getPosition().cpy().add(diffVec.cpy().scl(0.5f));
		// final Body bike = world.createBody(bd);
		// bd.position.set(posVec);
		//
		// final PolygonShape bikeShape = new PolygonShape();
		// bikeShape.setAsBox(0.1f, diffVec.y);
		// final Fixture bikeFixture = bike.createFixture(bikeShape, 0.0001f);
		//
		// bike.setUserData(bikeSprite);
		// bikeFixture.setUserData(bikeSprite);
		// }

		circle.dispose();
		monsterShape.dispose();
	}

	private void createRamp(float x, float y, float width, float height, float angle) {
		final PolygonShape shape = new PolygonShape();
		// Math.toDegrees(1.0 / Math.tan(height / width));
		shape.setAsBox(width / 2, height / 2, new Vector2(width / 2, height / 2), 0);

		final BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;
		bd.position.set(x - width / 2, y);
		bd.angle = angle;

		final Body rampBody = world.createBody(bd);
		final Fixture rampFix = rampBody.createFixture(shape, 1f);
		rampFix.setFriction(2);
		rampBody.setUserData(boardSprite);
		rampFix.setUserData(boardSprite);

		shape.dispose();
	}

	private void createRocker(float x, float y, float width, float height) {
		{ // board
			final PolygonShape shape = new PolygonShape();
			shape.setAsBox(width / 2, 0.1f, new Vector2(width / 2, height / 2), 0);

			final BodyDef bd = new BodyDef();
			bd.type = BodyType.DynamicBody;
			bd.position.set(x - width * 0.53f, y + height);

			final Body boardBody = world.createBody(bd);
			final Fixture boardFix = boardBody.createFixture(shape, 1f);
			boardFix.setFriction(2);
			boardBody.setUserData(boardSprite);
			boardFix.setUserData(boardSprite);

			shape.dispose();
		}

		{ // rocker
			final PolygonShape rockerShape = new PolygonShape();
			final Vector2[] vertices = { new Vector2(-width / 10f, y), new Vector2(width / 10f, y), new Vector2(0, y + height) };
			rockerShape.set(vertices);
			final BodyDef bd = new BodyDef();
			bd.type = BodyType.DynamicBody;
			bd.position.set(x, y);
			final Body rockerBody = world.createBody(bd);
			final Fixture rockerFix = rockerBody.createFixture(rockerShape, 0f);
			rockerBody.setUserData(rockerSprite);
			rockerFix.setUserData(rockerSprite);

			rockerShape.dispose();
		}
	}

	private void update(float delta) {

		if (resetPlay) {
			for (final Body body : tomatoes) {
				world.destroyBody(body);
			}
			tomatoes.clear();

			final Array<JointEdge> list = monsterBody.getJointList();
			for (final JointEdge item : list) {
				toBeDestroyedBodies.add(item.joint.getBodyB());
				world.destroyJoint(item.joint);
			}
			// actual remove
			resetPlay = false;
			showFail = true;
		}
		if (showFail) {
			if (resetTimer > 0.9f && toBeDestroyedBodies.size > 0) {
				world.destroyBody(monsterBody);
				for (final Body body : toBeDestroyedBodies) {
					world.destroyBody(body);
				}
				toBeDestroyedBodies.clear();
				resetTimer += delta;
			} else if (resetTimer > 1f) {
				createPlayer(8, 1);
				showFail = false;
				resetTimer = 0.0f;
			} else {
				resetTimer += delta;
			}
		} else {

			if (monsterBody.getPosition().x > 100) {
				// Win!
				final ScreenTransition transition = ScreenTransitionFade.init(0.5f);
				game.setScreen(new WinScreen(game), transition);
			}

			final float speed = wheel.getLinearVelocity().len();
			if (speed > 2) {
				Assets.instance.bikeSound.resume(Assets.instance.bikeSoundId);
				Assets.instance.bikeSound.setVolume(Assets.instance.bikeSoundId, MathUtils.clamp((speed - 5) / 10, 0f, 0.3f));
			} else {
				Assets.instance.bikeSound.pause(Assets.instance.bikeSoundId);
			}

			if (toBeDestroyedBodies.size > 0) {
				for (final Body body : toBeDestroyedBodies) {
					world.destroyBody(body);
				}
				toBeDestroyedBodies.clear();
			}

			if (createTomato) {
				final Filter tomatoFilter = new Filter();
				tomatoFilter.categoryBits = PlayContactListener.CATEGORY_TOMATO;
				final CircleShape tomatoShape = new CircleShape();
				tomatoShape.setRadius(0.2f);
				final BodyDef tomatoDef = new BodyDef();
				tomatoDef.type = BodyType.DynamicBody;

				createTomato = false;
				final Vector3 target = camera.unproject(new Vector3(throwTomatoX, throwTomatoY, 0));
				tomatoDef.position.set(10, 2);
				final Body tomato = world.createBody(tomatoDef);
				final Fixture tomatoFix = tomato.createFixture(tomatoShape, 4);
				tomatoFix.setFilterData(tomatoFilter);
				tomato.setUserData(tomatoSprite);
				tomatoFix.setUserData(tomatoSprite);

				final Vector2 targetV2 = new Vector2(target.x, target.y);
				tomato.applyLinearImpulse(tomatoDef.position.sub(targetV2), tomato.getWorldCenter(), true);
				tomato.applyTorque(random.nextFloat() * 10, true);

				tomatoShape.dispose();

				tomatoes.add(tomato);
			}

			// smokeEffect.reset();
			smokeEffect.setPosition(wheel.getPosition().x, wheel.getPosition().y - WHEEL_RADIUS);
			// smokeEffect.scaleEffect(MathUtils.clamp(wheel.getLinearVelocity().len(), 0.1f, 10f));

			camera.position.set(wheel.getPosition().x, monsterBody.getPosition().y + WHEEL_RADIUS * 2, 0);
			final Random random = new Random();
			if (cameraShake) {
				camera.translate(random.nextFloat() * 0.1f, random.nextFloat() * 0.1f, 0);
			}
			camera.update();

			// mouseJoint.setTarget(monsterBody.getPosition());
			final float volume = MathUtils.clamp(wheel.getLinearVelocity().len() / 20f, 0.0f, 0.1f);
			if (moveLeft) {
				wheelJoint.enableMotor(true);
				if (wheelJoint.getMotorSpeed() < 0) {
					if (volume > 0.05f) {
						// Assets.instance.playSound(AssetSounds.brake, volume);
					}
					wheelJoint.setMotorSpeed(wheelJoint.getMotorSpeed() < -0.5 ? wheelJoint.getMotorSpeed() * 0.7f : 0f);
				} else {
					wheelJoint.setMotorSpeed(MathUtils.clamp(wheelJoint.getMotorSpeed() + 0.2f, -MAX_MOTORSPEED, MAX_MOTORSPEED));
				}
			} else if (moveRight) {
				wheelJoint.enableMotor(true);
				if (wheelJoint.getMotorSpeed() > 0) {
					if (volume > 0.5f) {
						// Assets.instance.playSound(AssetSounds.brake, volume);
					}
					wheelJoint.setMotorSpeed(wheelJoint.getMotorSpeed() > 0.5 ? wheelJoint.getMotorSpeed() * 0.7f : 0f);
				} else {
					wheelJoint.setMotorSpeed(MathUtils.clamp(wheelJoint.getMotorSpeed() - 0.2f, -MAX_MOTORSPEED, MAX_MOTORSPEED));
				}
			}
		}

		world.step(1 / 60f, 8, 3);
	}

}
