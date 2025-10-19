package io.github.whack25.graphDisplay;

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

import io.github.whack25.graphGen.GraphGenerator;

public class GraphDisplay extends ApplicationAdapter {
    private SpriteBatch spriteBatch;
    private Texture groundimage;
    private Texture roadimage;
    FitViewport viewport;

    private int[][] graphoutput;

    @Override
    public void create() {
        /* Load textures, sounds here - you should not create these at constructor
        or init level as LibGDX needs to be loaded first
        */

        spriteBatch = new SpriteBatch();
        groundimage = new Texture("groundtile.png");
        roadimage = new Texture("roadtile.png");
        viewport = new FitViewport(8,5);

        //graphoutput = (new GraphGenerator()).generateGraph(32,32,0.5);

    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    private void input() {}

    private void logic() {}

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        // Draw in world units
        // .draw draws from the bottom left corner (as the coords)

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.end();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        groundimage.dispose();
        roadimage.dispose();
    }
}
