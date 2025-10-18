package io.github.whack25;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import livegraph.Graph;
import livegraph.GraphNode;
import livegraph.NodeType;
import livegraph.RobotMovement;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch spriteBatch;
    private Texture image;
    FitViewport viewport;
    Texture backgroundTexture;
    Texture bucketTexture;
    Texture dropTexture;
    Texture houseTexture;
    Texture roadTexture;
    Texture carTexture;
    Texture grassTexture;
    Sound dropSound;
    Music music;
    Sprite bucketSprite;
    private Graph<String, String> gameGraph;
    private final int MAX_FRAME_RATE = 2;

    @Override
    public void create() {
        /* Load textures, sounds here - you should not create these at constructor
        or init level as LibGDX needs to be loaded first
        */
        gameGraph = Graph.exampleGraph();
        spriteBatch = new SpriteBatch();
        image = new Texture("libgdx.png");
        viewport = new FitViewport(gameGraph.getGridWidth(), gameGraph.getGridHeight());
        backgroundTexture = new Texture("background.png");
        bucketTexture = new Texture("bucket.png");
        dropTexture = new Texture("drop.png");
        houseTexture = new Texture("house.png");
        roadTexture = new Texture("roadDown.png"); // TODO
        grassTexture = new Texture("Grass.png");
        carTexture = new Texture("car.png");
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(1,1);
    }

    @Override
    public void render() {
        logic();
        // waitBeforeFrame();
        // input();
        draw();
//        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
//        batch.begin();
//        batch.draw(image, 140, 210);
//        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Wait for frame stabilisation to cap frame rate.
     */
    private void waitBeforeFrame() {
        float delta = Gdx.graphics.getDeltaTime();
        float targetFrameTime = 1.0f / MAX_FRAME_RATE;
        if (delta < targetFrameTime) {
            try {
                Thread.sleep((long) ((targetFrameTime - delta) * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // Frame took longer than target frame time; no wait needed
            System.out.println("Warning: Frame time exceeded target frame time: " + delta + " seconds");
        }
    }

    private void input() {
//        float speed = .25f;
//        float delta = Gdx.graphics.getDeltaTime(); // time since last frame
//
//        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
//            bucketSprite.translateX(speed * delta);
//        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
//            bucketSprite.translateX(-speed * delta);
//        }
    }

    private void logic() {
        gameGraph.tick();
    }

    private void draw() {
        System.out.println(System.currentTimeMillis()+": drawing new frame at time");

        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        // Draw in world units
        // .draw draws from the bottom left corner (as the coords)

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);

        // Add background textures
        for (GraphNode<String, String> node : gameGraph.getNodes().values()) {
            spriteBatch.draw(node.getTileType() == NodeType.HOUSE ? houseTexture :
                             node.getTileType() == NodeType.ROAD ? roadTexture :
                             grassTexture,
                node.getX(), node.getY(), 1,1);

        }

        // Draw robots
        for (GraphNode<String, String> node : gameGraph.getNodes().values()) {
            if (!node.getOccupiers().isEmpty()) {
                for (RobotMovement<String, String> movement : node.getOccupiers()) {
                    float progress = 1.0f - ((float) movement.getRemainingProgression() / (float) movement.getTotalEdgeWeight());
                    spriteBatch.draw(carTexture,
                        0.25f+node.getX()*progress + movement.getOriginX()*(1-progress),
                        0.25f+node.getY()*progress + movement.getOriginY()*(1-progress),
                        0.5f,0.5f);
                }
            }
        }

//        bucketSprite.draw(spriteBatch);

        spriteBatch.end();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        image.dispose();
    }
}
