package org.joltshpere.main.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import org.joltshpere.testing.main.JoltSphereTesting;

public class TestingLauncher {
	
	public static void main (String[] arg) {
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
	
		config.width = JoltSphereTesting.WIDTH;
		config.height = JoltSphereTesting.HEIGHT;
		
		config.foregroundFPS = JoltSphereTesting.FPS;
		config.backgroundFPS = 60;
		
		new LwjglApplication(new JoltSphereTesting(), config);
	
	}
	
}

package org.joltshpere.testing.main;

import org.joltshpere.testing.scenes.scene1;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class JoltSphereTesting extends Game {
	
	public static int displayScale = 200;
	public int width = 16 * displayScale;
	public int height = 9 * displayScale;
	
	public static int WIDTH = 16 * displayScale;
	public static int HEIGHT = 9 * displayScale;
	public static final int FPS = 60;
	
	public static String title = "Jolt Sphere Testing";
	
	public BitmapFont font; //testing font
	private Texture fontTex; //texture for font
	
	public OrthographicCamera cam;
	public OrthographicCamera phys2Dcam;
	public Viewport view;
	public Viewport phys2Dview;
	
	public SpriteBatch batch;
	public ShapeRenderer shapeRender;
	
	public static float ppm = 200; //pixels per meter 
	
	@Override
	public void create () {
		
		fontTex = new Texture(Gdx.files.internal("testing/font.png"), true); //mipmaps=true
		fontTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		font = new BitmapFont(Gdx.files.internal("testing/font.fnt"), new TextureRegion(fontTex), false); //flipped=font
		
		cam = new OrthographicCamera();
		phys2Dcam = new OrthographicCamera();
		
		cam.setToOrtho(false, width, height);
		phys2Dcam.setToOrtho(false, width / ppm, height / ppm); //physics world by meters
		view = new ExtendViewport(width, height, cam);
		phys2Dview = new ExtendViewport(width / ppm, height / ppm, phys2Dcam);
		
		this.setScreen(new scene1(this));
		
		Gdx.graphics.setVSync(true);
		
		batch = new SpriteBatch();
		shapeRender = new ShapeRenderer();
		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		super.render();
		
		Gdx.graphics.setTitle(title + " : " + subtitle + "     FPS: " + Gdx.graphics.getFramesPerSecond());

		if (Gdx.input.isKeyJustPressed(Keys.ENTER) && !Gdx.graphics.isFullscreen()) Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
			else if (Gdx.input.isKeyJustPressed(Keys.ENTER)) Gdx.graphics.setWindowedMode(width, height);
		
		batch.setProjectionMatrix(cam.combined);
		shapeRender.setProjectionMatrix(cam.combined);
	}
		
	String subtitle = "Basic";
	
		int currentScene = 1;
		
	public void switchScene() {
		currentScene++;
		switch (currentScene) {
			case 1:	this.setScreen(new scene1(this)); subtitle = "Basic";
				break;
			default: this.setScreen(new scene1(this)); currentScene = 1; subtitle = "Basic";
				break;
		}
	}
	
	@Override 
	public void resize (int width, int height) {
		view.update(width, height);
		phys2Dview.update(width, height);
	}
	
	public void dispose () {
		 
	}
}

package org.joltshpere.testing.scenes;

import org.joltshpere.testing.main.JoltSphereTesting;
import org.joltshpere.testing.mechanics.TestEntities;
import org.joltshpere.testing.mechanics.TestPlayer;
import org.joltshpere.testing.mechanics.TestingContactListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class scene1 implements Screen {

	final JoltSphereTesting game;
	 
	World world;
	TestingContactListener contLis;
	Box2DDebugRenderer debugRender;
	TestEntities ent;
	
	TestPlayer player1;
	TestPlayer player2;
	
	float ppm = JoltSphereTesting.ppm;
	
	public scene1 (final JoltSphereTesting gam) {
		game = gam;
		
		world = new World(new Vector2(0, -9.8f), false); //ignore inactive objects false
		contLis = new TestingContactListener();
		world.setContactListener(contLis);
		
		debugRender = new Box2DDebugRenderer(); 
		
		ent = new TestEntities();
		
		ent.createPlatform(world);
		world = ent.world;
		
		int x = 300;
		player1 = new TestPlayer(game.width/2 + x, 300, world, 1);
		player2 = new TestPlayer(game.width/2 - x, 300, world, 2);
		
	} 
	
	void update(float dv) {
		if (Gdx.input.isKeyJustPressed(Keys.TAB)) game.switchScene();
		
		player1.update(contLis.player1Contact, dv, game.width, game.height);
		player2.update(contLis.player2Contact, dv, game.width, game.height);
		
		if (Gdx.input.isKeyPressed(Keys.LEFT)) player1.moveLeft();
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) player1.moveRight();
		if (Gdx.input.isKeyJustPressed(Keys.UP)) player1.jump();
			if (Gdx.input.isKeyPressed(Keys.UP)) player1.jumpHold();
		if (Gdx.input.isKeyPressed(Keys.DOWN)) player1.smash(); else player1.notSmashing();
		
		if (Gdx.input.isKeyPressed(Keys.A)) player2.moveLeft();
		if (Gdx.input.isKeyPressed(Keys.D)) player2.moveRight();
		if (Gdx.input.isKeyJustPressed(Keys.W)) player2.jump();
			if (Gdx.input.isKeyPressed(Keys.W)) player2.jumpHold();
		if (Gdx.input.isKeyPressed(Keys.S)) player2.smash(); else player2.notSmashing();
		
	}
	
	public void render(float delta) {
		update(delta);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.cam.update();
		game.phys2Dcam.update();
		world.step(delta, 6, 2);
		
		game.batch.begin();
			game.font.draw(game.batch, "" + player1.knockouts, game.width*0.27f, game.height * 0.085f);
			game.font.draw(game.batch, "" + player2.knockouts, game.width*0.72f, game.height * 0.085f);
			
			if (player1.canSmash) game.font.draw(game.batch, "Jolt! < ^ >", game.width * 0.85f, game.height * 0.1f);
			if (player2.canSmash) game.font.draw(game.batch, "Jolt! WASD", game.width * 0.05f, game.height * 0.1f);
		
			game.font.draw(game.batch, Math.round((player1.fdefBall.density / 5 * 100) * 10f)/10f + "%", game.width * 0.85f, game.height * 0.6f);
			game.font.draw(game.batch, Math.round((player2.fdefBall.density / 5 * 100) * 10f)/10f + "%", game.width * 0.06f, game.height * 0.6f);
			
			
		game.batch.end();
		
		game.shapeRender.begin(ShapeType.Filled);
			
			player1.shapeRender(game.shapeRender, Color.FIREBRICK);
			player2.shapeRender(game.shapeRender, Color.BLUE);
			
		game.shapeRender.end();
		
		debugRender.render(world, game.phys2Dcam.combined);
		
	}
	
	public void dispose() {
		
	}
	
	public void resize(int width, int height) {
		game.resize(width, height);
	}
	
	public void show() {}

	public void pause() {}

	public void resume() {}

	public void hide() {}
	
}

package org.joltshpere.testing.mechanics;

import org.joltshpere.testing.main.JoltSphereTesting;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class TestPlayer {
	
	
	public float ppm = JoltSphereTesting.ppm;
	public float FPSdv = 1f / JoltSphereTesting.FPS;
	
	public Body body;
	public Fixture fixture;
	public CircleShape circShape;
	public CircleShape jumpShape;
	public PolygonShape smashShape;
	public World world;
	public Vector2 locationIndicator;
	public Vector2 startingLocation;
	
	public FixtureDef fdefBall;
	public FixtureDef fdefSmash;
	public FixtureDef fdefSmashJump;
	
	public int knockouts = 0;
	public int player;
	private float dv;
	
	public boolean isSmashing = false;
	public boolean isSmashJumping = false;
	public boolean hasDoubled = true;
	public boolean canJump = false;
	public boolean canHold = false;
	public boolean canSmash = false;
	public boolean canSmashJump = false;
	public boolean previousSmash = false;
	public boolean shouldLocationIndicate = false;
	public boolean isGrounded = false;
	
	public int jumpDelay = 5;
	public float jumpTimer = jumpDelay;
	
	public float jumpHoldPhase = 15; //half jump time
	public float jumpHoldTimer = jumpHoldPhase;
	
	public float smashLength = 60;
	public float smashTimer = smashLength;
	
	public float smashCooldownLength = 250;
	public float smashCooldown = smashCooldownLength;

	public float energyTimerSpeed = 0.1f;
	public float energyTimer = 0;
	
	public float smashJumpLength = 17; //length of jump
	public float smashJumpPeriodLength = 40; //period to jump
	public float smashJumpPeriod = smashJumpPeriodLength;
	
	public float arenaSpace;
	private float indicatorScl = 1; // place holder value of 1
	private float indicatorSclLimit = 0.01f;
	public float indicatorSize = 1f;
	
	public TestPlayer (int xpos, int ypos, World realWorld, int playah) {
	
		player = playah;
		
		world = realWorld;
		
		circShape = new CircleShape();
		circShape.setRadius(26 / 100f);
		
		jumpShape = new CircleShape();
		jumpShape.setRadius(60 / 100f);
		
		locationIndicator = new Vector2();
		startingLocation = new Vector2(xpos, ypos);
		
		createFixtureDefs();
		
		createBall(xpos, ypos);
				
	}
	
	
	public void update(int contact, float delta, int width, int height) {
		dv = delta;
		arenaSpace = 0.5f * height;
		
		/* Basic Values if on the Ground */
		
		if (contact > 0) {//if on ground
			hasDoubled = false; //reset double jump
			canJump = true;
			jumpTimer = jumpDelay;
			isGrounded = true;
		} 
		else isGrounded = false;
		
		/* Creates timer to jump while bouncing around */
		
		if (jumpTimer > 0) {
			canJump = true;
			jumpTimer -= 60 * dv; 
		}
		else canJump = false;
			//similar, except timer for held jumps
			if (jumpHoldTimer > 0) {
				canHold = true;
				jumpHoldTimer -= 60 * dv;
			} 
			else canHold = false;
			
		/* Allows for smash jump after end of smash */
		
		if (smashJumpPeriod > 0) {
			canSmashJump = true;
			smashJumpPeriod -= 60 * dv;
		}
		else if (canSmashJump){
			smashCooldown=0;
			canSmashJump = false;
			isSmashJumping = false;
			body.setLinearVelocity(body.getLinearVelocity().x * 0.5f, body.getLinearVelocity().y * 0.05f); //not absolute stop
		}
		
		/* Sequence to preform if no longer smashing */
		
		if (!isSmashing) {
			if (smashCooldown == 0) {
				smashTimer = smashLength;
				canSmash = false;
				smashCooldown = 1;
				body.destroyFixture(fixture);
				fixture = body.createFixture(fdefBall);
				fixture.setUserData("p" + player);
			}
			else {
				if (smashCooldown > smashCooldownLength) canSmash = true;
				else smashCooldown += 60 * dv;
			}			
		}
		
		// Updates Indicator
		updateLocationIndicator(width, height);
		// Checks if Dead
		checkIfDead(width, height);
		
		if (!isSmashing && !isSmashJumping) weakenPlayer();
		
	}

	
	public void shapeRender(ShapeRenderer sRender, Color skinColor) {
		
		sRender.setColor(skinColor);
		/*if (shouldLocationIndicate) {
			float r = (indicatorSize / 2) *(1/indicatorSclLimit);
			sRender.rect(locationIndicator.x - r, locationIndicator.y - r, r*2, r*2);
			sRender.circle(locationIndicator.x, locationIndicator.y, r);
		}*/
		
		sRender.setColor(Color.GOLD);
			if (!isSmashing && !isSmashJumping) 
				sRender.circle(body.getPosition().x * ppm, body.getPosition().y * ppm, (circShape.getRadius()*100 + 1) * (ppm / 100f));
			
			if (shouldLocationIndicate) {
				float r = (indicatorSize / 2) *(1/indicatorSclLimit);
				//sRender.rect(locationIndicator.x - r, locationIndicator.y - r, r*2, r*2);
				sRender.circle(locationIndicator.x, locationIndicator.y, r);
			}
		
		
		sRender.setColor(skinColor);
		if (isSmashing) 
			sRender.circle(body.getPosition().x * ppm, body.getPosition().y * ppm, 15 * (ppm / 100f));
		else if (isSmashJumping) 
			sRender.circle(body.getPosition().x * ppm, body.getPosition().y * ppm, jumpShape.getRadius()*100 * (ppm / 100f));
		else 
			sRender.circle(body.getPosition().x * ppm, body.getPosition().y * ppm, circShape.getRadius()*100 * (ppm / 100f));
		
		if (shouldLocationIndicate) {
			float area = (float) (Math.pow(		(indicatorSize / 2) *(1/indicatorSclLimit)		, 2) * Math.PI);
			float r = (float) (Math.pow((		// sqrt( pi*r^2 * 1/scl  ) 
					(area  * (1.01 - indicatorScl)) 	/ Math.PI),	
					0.5 /* square root */) + 2);	
			sRender.circle(locationIndicator.x, locationIndicator.y, r);
			//sRender.rect(locationIndicator.x - r, locationIndicator.y - r, r*2, r*2);
		}
		
	}
	
	public void moveLeft () {
		moveHorizontal(-1);
	}
	public void moveRight () {
		moveHorizontal(1);
	} 
	
	private void moveHorizontal (int dir) {	
		if (isSmashing) {
			if (canJump) {
				body.applyForceToCenter(500000 * dir * dv, 0, true);
				body.applyAngularImpulse(-500f * dir * dv, true);
			} else {
				body.applyForceToCenter(60000 * dir * dv, 0, true);
				body.applyForceToCenter(-500f * dir * dv, 0, true);
			}
		}
		else {
			if (isGrounded) {
				body.applyAngularImpulse(-12f * dir * dv, true);
				body.applyForceToCenter((fdefBall.density / 5f) * 1200 * dir * dv, 0, true);
			} else {
				body.applyForceToCenter((fdefBall.density / 5f) * 900 * dir * dv, 0, true);
			}
		}	
	}
	
	
	
	public void jump () { //no delta because single impulse
		if (canJump) { //if on ground
			if (canSmashJump) smashJump();
			else { 
				body.setLinearVelocity(body.getLinearVelocity().x * 0.3f, body.getLinearVelocity().y * 0.3f);
				body.applyForceToCenter(0, (fdefBall.density / 5f) * 280, true);
				//body.applyForceToCenter(0, 500, true);
				jumpHoldTimer = jumpHoldPhase;
			}
		}
		else if (!hasDoubled) {
			body.setAngularVelocity(0);
			body.setLinearVelocity(0, 0);
			body.applyForceToCenter(0, (fdefBall.density / 5f) * 310, true);
			hasDoubled = true;
		}
	}
	public void jumpHold () {
		if (!hasDoubled && canHold && !isSmashing) {
			body.applyForceToCenter(0, (fdefBall.density / 5f) * 900 * dv, true);
		}
	}
	
	private void smashJump() {
		isSmashJumping = true;
		
		body.destroyFixture(fixture);
		fixture = body.createFixture(fdefSmashJump);
		fixture.setUserData("p" + player);
		
		smashJumpPeriod = smashJumpLength;
		body.setAngularVelocity(body.getAngularVelocity() * 0.3f);
		body.setLinearVelocity(body.getLinearVelocity().x * 0.3f, body.getLinearVelocity().y * 0.1f);
		body.applyForceToCenter(0, 2000000, true);
		
	}
	
	
	public void smash() {
		if (canSmash) {
			if (smashTimer == smashLength) {
				body.destroyFixture(fixture);
				fixture = body.createFixture(fdefSmash);
				fixture.setUserData("p" + player);
			}
			if (!isGrounded) body.applyForceToCenter(0, -30000 * dv, true);
			isSmashing = true;
			if (canJump) canSmashJump = true;
			previousSmash = true;
			smashTimer-=60*dv;
			if (smashTimer < 0) canSmash = false;
				smashCooldown = 0; //resetting cooldown timer
				
		}
		else isSmashing = false;
	}
	public void notSmashing() { 
		if (previousSmash) {
			isSmashing = false; 
			smashJumpPeriod = smashJumpPeriodLength;
			previousSmash = false;
		}
		else isSmashing = false;  
	}
	
	
	public void knockedOut() {
		knockouts++;
		
		body.setAngularVelocity(0);
		body.setLinearVelocity(0, 0);
		body.setTransform(startingLocation.x / ppm, startingLocation.y / ppm, 0);
		
		createFixtureDefs();
		energyTimer = 0;
	}
	
	void weakenPlayer() {
		body.destroyFixture(fixture);
		
		if (fdefBall.density > 0.1f) fdefBall.density = 5 + energyTimer;
		
		fixture = body.createFixture(fdefBall);
		fixture.setUserData("p" + player);
		energyTimer -= energyTimerSpeed * dv;
	}
	
	void updateLocationIndicator(int width, int height) {
		int w = width;
		int wMid = width / 2;
		int h = height;
		int hMid = height / 2;
		
		float x = body.getPosition().x * ppm;
		float y = body.getPosition().y * ppm;
		
		float xFin = 0;
		float yFin = 0;
		
		float cls = 0.02f; //close
		float far = 1 - cls;
		
		boolean xin = x < 0 || x > w ? false : true; 
		boolean yin = y < 0 || y > h ? false : true;
		
		if (xin && yin) shouldLocationIndicate = false;
		else { // set position of square
			shouldLocationIndicate = true;
			
			if (!xin && yin) { // ball off edge
				yFin = y;
				if (x < wMid) xFin = w * cls;
				else xFin = w * far;
			}
			else if (xin && !yin) { // ball above or below
				xFin = x;
				if (y < hMid) yFin = w * cls;
				else yFin = h - (w * cls);
			}
			else if (!xin && !yin) { // ball in corner
				if (x < wMid) xFin = w * cls; // to left
					else xFin = w * far; // to right
				if (y < hMid) yFin = w * cls; // below
					else yFin = h - (w * cls); // above
			}
		}
		
		if (xFin < w*cls) xFin = w*cls;
		if (xFin > w*far) xFin = w*far;
		if (yFin < w*cls) yFin = w * cls;
		if (yFin > (h  - w*cls)) yFin = h - (w * cls);
		
		locationIndicator.x = xFin;
		locationIndicator.y = yFin;
		
		if (!xin || !yin) { // set scale of square
			float rng = 1 - indicatorSclLimit; //range
			float shrnk = rng / arenaSpace; // shrink
			
			if (!xin && yin) { // ball off edge
				if (x < wMid) indicatorScl = 1 - ((-1*x) * shrnk);
				else indicatorScl = 1 - ((x - w) * shrnk);
			}
			else if (xin && !yin) { // ball above or below
				if (y < hMid) indicatorScl = 1 - ((-1*y) * shrnk);
				else indicatorScl = 1 - ((y - h) * shrnk);
			}
			else if (!xin && !yin) { // ball in corner
				float tempX, tempY;
				if (x < wMid) tempX = 1 - ((-1*x) * shrnk); // to left
					else tempX = 1 - ((x - w) * shrnk); // to right
				if (y < hMid) tempY = 1 - ((-1*y) * shrnk); // below
					else tempY = 1 - ((y - h) * shrnk); // above
				
				if (tempX < tempY) indicatorScl = tempX; else indicatorScl = tempY; 
			}
		
		}
		
	}
	
	void checkIfDead(int width, int height) {
		//int w = width;
		int h = height;
		//float x = body.getPosition().x * ppm;
		float y = body.getPosition().y * ppm;
		
		//if (x < -arenaSpace || x > (w + arenaSpace)) knockedOut();
		if (y < -arenaSpace || y > (h + arenaSpace)) knockedOut();
		
	}
	
	
	void createBall (int xpos, int ypos) {
		
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(xpos / ppm, ypos / ppm);
		bdef.fixedRotation = false;
		bdef.bullet = true;
		
		body = world.createBody(bdef);
		
		fixture = body.createFixture(fdefBall);
		fixture.setUserData("p" + player);
		
	}
	
	void createFixtureDefs() {
		
		fdefBall = new FixtureDef();
		fdefBall.shape = circShape;
		fdefBall.friction = 0.1f;
		fdefBall.restitution = 0;
		fdefBall.density = 5;//(5 / 0.01666666f) * FPSdv;        
		
		fdefSmash = new FixtureDef();
		fdefSmash.shape = createSmashShape(1/1.2f);
		fdefSmash.friction = 0.2f;
		fdefSmash.restitution = 0.4f;
		fdefSmash.density = 80;//(80 / 0.01666666f) * FPSdv;        
		
		fdefSmashJump = new FixtureDef();
		fdefSmashJump.shape = jumpShape;//createSmashShape(2f);
		fdefSmashJump.friction = 0.5f;
		fdefSmashJump.restitution = .6f;
		fdefSmashJump.density = 1500;//(1500 / 0.01666666f) * FPSdv;
		
	}
	
	private PolygonShape createSmashShape(float scl) {
		
		PolygonShape shape = new PolygonShape();
		
		Vector2[] v = new Vector2[8];
		//bottom left
		v[0] = new Vector2(-30 * scl / 100, -15 * scl / 100);
		v[1] = new Vector2(-15 * scl / 100, -30 * scl / 100);
		
		//bottom right
		v[2] = new Vector2(15 * scl / 100, -30 * scl / 100);
		v[3] = new Vector2(30 * scl / 100, -15 * scl / 100);
		
		//top right
		v[4] = new Vector2(30 * scl / 100, 15 * scl / 100);
		v[5] = new Vector2(15 * scl / 100, 30 * scl / 100);
		
		//top left
		v[6] = new Vector2(-15 * scl / 100, 30 * scl / 100);
		v[7] = new Vector2(-30 * scl / 100, 15 * scl / 100);
	
		shape.set(v);
		
		return shape;
		
	}
	
}

package org.joltshpere.testing.mechanics;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class TestingContactListener implements ContactListener {
	
	public byte player1Contact = 0;
	public byte player2Contact = 0; 
	
	public void beginContact(Contact contact) {
		
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();
		
		if (fa.getUserData().equals("ground") && fb.getUserData().equals("p1")) {
			player1Contact++;
		}
		
		if (fa.getUserData().equals("ground") && fb.getUserData().equals("p2")) {
			player2Contact++;
		}

		//System.out.println(fa.getUserData() + ", " + fb.getUserData());
		//System.out.println("P1: " + player1Contact +  ", P2: " + player2Contact + "; " + fa.getUserData() + ", " + fb.getUserData() );
	
	}
	
	public void endContact(Contact contact) {
	
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();
		
		if (fa.getUserData().equals("ground") && fb.getUserData().equals("p1")) {
			player1Contact--;
		}
		
		if (fa.getUserData().equals("ground") && fb.getUserData().equals("p2")) {
			player2Contact--;
		}
		
	}

	public void preSolve(Contact contact, Manifold oldManifold) {
				
	}

	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}
	
}

package org.joltshpere.testing.mechanics;

import org.joltshpere.testing.main.JoltSphereTesting;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class TestEntities {
	
	float ppm = JoltSphereTesting.ppm;
	
	public World world;
	
	public void createPlatform(World realWorld) {
		 
		world = realWorld;
		
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.StaticBody;
		bdef.position.x = 0;
		bdef.position.y = 0;
		 
		Body body = world.createBody(bdef);
		
		ChainShape chain = new ChainShape();
		Vector2[] v = new Vector2[12];
		int x = JoltSphereTesting.WIDTH / 2;
		
		int xpnt1 = 800;
		int xpnt2 = 760;
		int xpnt3 = 690;
		int xpnt4 = 600;
		int xpnt5 = 490;
		int xpnt6 = 410;
		
		int ypnt1 = 300;
		int ypnt2 = 250;
		int ypnt3 = 190;
		int ypnt4 = 140;
		int ypnt5 = 115;
		int ypnt6 = 100;
		
		v[0] = new Vector2((x-xpnt1) / ppm, ypnt1 / ppm);
		v[1] = new Vector2((x-xpnt2)  / ppm, ypnt2 / ppm);
		v[2] = new Vector2((x-xpnt3)  / ppm, ypnt3 / ppm);
		v[3] = new Vector2((x-xpnt4)  / ppm, ypnt4 / ppm);
		v[4] = new Vector2((x-xpnt5)  / ppm, ypnt5 / ppm);
		v[5] = new Vector2((x-xpnt6) / ppm, ypnt6 / ppm);
		
		v[11] = new Vector2((x+xpnt1) / ppm, ypnt1 / ppm);
		v[10] = new Vector2((x+xpnt2) / ppm, ypnt2 / ppm);
		v[9] = new Vector2((x+xpnt3) / ppm, ypnt3 / ppm);
		v[8] = new Vector2((x+xpnt4) / ppm, ypnt4 / ppm);
		v[7] = new Vector2((x+xpnt5) / ppm, ypnt5 / ppm);
		v[6] = new Vector2((x+xpnt6) / ppm, ypnt6 / ppm);
			
		chain.createChain(v);
		FixtureDef fdef  = new FixtureDef();
		fdef.shape = chain;
		fdef.friction = 1;
		
		body.createFixture(fdef).setUserData("ground");
		
		chain.dispose();
		
	}

}
