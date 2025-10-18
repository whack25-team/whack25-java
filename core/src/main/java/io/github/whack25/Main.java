package io.github.whack25;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch spriteBatch;
    FitViewport viewport;
    private static final int TILE_SIZE = 32;
    private static final int ROWS = 2;
    private static final int COLS = 3;

    



    // Textures and sprites declaration
    private Texture roadUpTex;
    private Texture roadDownTex;
    private Texture roadLeftTex;
    private Texture roadRightTex;
    private Texture grassTex;
    private Texture carTex;
    private Texture collisionTex;
    private Texture houseTex;
    private Texture junctionTex;

    private Cell[][] grid;


    private class Cell {
        // Cell properties
        boolean isRoad;
        boolean hasCar;
        boolean hasCollision;
        boolean hasHouse;
        boolean isJunction;
        boolean isGrass;
    }
    
    @Override
    public void create() {
        /* Load textures, sounds here - you should not create these at constructor
        or init level as LibGDX needs to be loaded first
        */

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8,5);

        roadUpTex = new Texture("roadUp.png");
        roadDownTex = new Texture("roadDown.png");
        roadLeftTex = new Texture("roadLeft.png");
        roadRightTex = new Texture("roadRight.png");
        grassTex = new Texture("grass.png");
        carTex = new Texture("car.png");
        collisionTex = new Texture("collision.png");
        houseTex = new Texture("house.png");
        junctionTex = new Texture("junction.png");

        grid = new Cell[COLS][ROWS];

        // Initialize each Cell to avoid NullPointerException
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                grid[x][y] = new Cell();
            }
        }

        grid[0][0].isRoad = true;
        grid[0][1].isRoad = true;
        grid[1][0].isRoad = true;
        grid[1][1].isGrass = true;
        grid[2][0].hasHouse = true;

        // Place a c
        grid[0][1].hasCar = true;

        // Place a collision
        grid[0][0].hasCollision = true;
    }

    @Override
    public void render() {
        input();
        logic();
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

    private void input() {
        float speed = .25f;
        float delta = Gdx.graphics.getDeltaTime(); // time since last frame
    }

    private void logic() {

    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        // Draw in world units
        // .draw draws from the bottom left corner (as the coords)

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        drawGrid();

        spriteBatch.end();
    }

    private Texture backgroundChecker(Cell cell, int x, int y) {
        if (cell.isRoad) {
            if (y - 1 >= 0 && grid[x][y-1].isRoad) return roadUpTex;
            if (y + 1 < ROWS && grid[x][y+1].isRoad) return roadDownTex;
            if (x - 1 >= 0 && grid[x-1][y].isRoad) return roadLeftTex;
            if (x + 1 < COLS && grid[x+1][y].isRoad) return roadRightTex;
            return roadUpTex; // fallback for an isolated road cell
        } else if (cell.isGrass) {
            return grassTex;
        } else if (cell.hasHouse) {
            return houseTex;
        } else if (cell.isJunction) {
            return junctionTex;
        } else {
            return grassTex; // default
        }
    }

    private void drawGrid() {
        // Background layer
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                Cell cell = grid[x][y];
                Texture tex = backgroundChecker(cell, x, y);
                spriteBatch.draw(tex, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        // Foreground layer
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                Cell cell = grid[x][y];
                if (cell.hasCar) {
                    spriteBatch.draw(carTex, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                } else if (cell.hasCollision) {
                    spriteBatch.draw(collisionTex, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }
}
