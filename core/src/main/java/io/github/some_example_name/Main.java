package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.Color;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    FitViewport viewport;
    Map<String, TextureRegion> textures = new HashMap<>();
    Texture texture;

    final private int[][] grid = new int[10][10];
    final private int[][] uncovered = new int[10][10];
    final private int mines = 15;
    final private int[] mineLocations = new int[mines];
    private int btncount = 0;
    private int pressedx, pressedy;


    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(10, 10);
        texture = new Texture("MinesweeperTileset.png");
        textures.put("tile", new TextureRegion(texture, 16, 32, 16, 16));
        int ypixels = 0;
        for (int i = 1; i < 10; i++) {
            if ((i - 1) * 16 == 80) {
                ypixels ++;
            }
            textures.put((i % 9) + "mines", new TextureRegion(texture, ((i - 1) * 16) % 80, 16 * ypixels, 16, 16));
        }
        textures.put("mine", new TextureRegion(texture, 64, 16, 16, 16));
        textures.put("flag", new TextureRegion(texture, 0, 32, 16, 16));

        for (int[] row : uncovered) {
            Arrays.fill(row, 1  );
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }


    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    private void input() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown (int x, int y, int pointer, int button) {
                if (button == 0) {
                    pressedx = x / 75;
                    pressedy = y / 75;
                    btncount++;
                }

                if (btncount == 1 && button == 0) {
                    set_mines(pressedx, pressedy);
                    Arrays.sort(mineLocations);
                    set_tiles();
                }

                return true;
            }
        });

    }

    private void logic() {
        if (btncount >= 1) {
            uncovered[pressedy][pressedx] = 0;
        }

        if (btncount < 1){
            return;
        }

        if (grid[pressedy][pressedx] == -1) {
            System.out.println("Kaboom");
        }
        uncover_tiles(pressedx, pressedy);

    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                batch.draw(textures.get("tile"), i, j, 1, 1);
            }
        }
        reveal_tiles();
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }


    private void set_mines(int x, int y){
        int count = 0;
        int[] rowOffset = {-1, -1, -1, 0, 0, 0, 1, 1, 1};
        int[] colOffest = {-1, 0, 1, -1, 0, 1, -1, 0, 1};
        int[] clickedAreas = new int[9];

        for (int i = 0; i < clickedAreas.length; i++) {
            clickedAreas[i] = (rowOffset[i] + y) * 10 + (x + colOffest[i]);
        }

        outer:
        while (count < mines) {
            int randomMine = (int) (Math.random() * 100);
            for (int j = 0; j < mines; j++) {
                if (randomMine == mineLocations[j]){
                    continue outer;
                }
            }

            for (int clickedArea : clickedAreas) {
                if (randomMine == clickedArea){
                    continue outer;
                }
            }

            mineLocations[count] = randomMine;
            count++;

            grid[randomMine / 10][randomMine % 10] = -1;
        }
    }

    private void set_tiles(){
        for (int mineLocation : mineLocations) {
            int row = mineLocation / 10;
            int column = mineLocation % 10;

            int[] rowOffset = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] colOffest = {-1, 0, 1, -1, 1, -1, 0, 1};

            for (int j = 0; j < rowOffset.length; j++) {
                if (rowOffset[j] + row > 9 || rowOffset[j] + row < 0 || colOffest[j] + column > 9 || colOffest[j] + column < 0) {
                    continue;
                }
                grid[rowOffset[j] + row][colOffest[j] + column] -= (grid[rowOffset[j] + row][colOffest[j] + column] != -1) ? -1 : 0;
            }

        }
    }

    private void uncover_tiles(int x, int y) {

        if (grid[pressedy][pressedx] != 0) {
            return;
        }

        int[] rowOffset = {-1, -1, -1, 0, 0, 0, 1, 1, 1};
        int[] colOffset = {-1, 0, 1, -1, 0, 1, -1, 0, 1};


        for (int i = 0; i < colOffset.length; i++) {
            if (y + colOffset[i] > 9 || y + colOffset[i] < 0 || x + rowOffset[i] > 9 || x + rowOffset[i] < 0) {
                continue;
            }

            if (grid[y + colOffset[i]][x + rowOffset[i]] < 0 || uncovered[y + colOffset[i]][x + rowOffset[i]] == 0) {
                continue;
            }

            if (grid[y + colOffset[i]][x + rowOffset[i]] > 0) {
                uncovered[y + colOffset[i]][x + rowOffset[i]] = 0;
                continue;
            }

            uncovered[y + colOffset[i]][x + rowOffset[i]] = 0;
            uncover_tiles(x + rowOffset[i], y + colOffset[i]);
        }
    }

    private void reveal_tiles() {
        for (int i = 0; i < uncovered.length; i++) {
            for (int j = 0; j < uncovered[i].length; j++) {
                if (uncovered[j][i] == 1) {
                    continue;
                }

                switch (grid[j][i]) {
                    case 0:
                        batch.draw(textures.get("0mines"), i, uncovered[i].length - 1 - j, 1, 1);
                        break;
                    case 1:
                        batch.draw(textures.get("1mines"), i, uncovered[i].length - 1 - j, 1, 1);
                        break;
                    case 2:
                        batch.draw(textures.get("2mines"), i, uncovered[i].length - 1 - j, 1, 1);
                        break;
                    case 3:
                        batch.draw(textures.get("3mines"), i, uncovered[i].length - 1 - j, 1, 1);
                        break;
                    case 4:
                        batch.draw(textures.get("4mines"), i, uncovered[i].length - 1 - j, 1, 1);
                        break;
                    case 5:
                        batch.draw(textures.get("5mines"), i, uncovered[i].length - 1 - j, 1, 1);
                        break;
                    case 6:
                        batch.draw(textures.get("6mines"), i, uncovered[i].length - 1 - j, 1, 1);
                        break;
                    case 7:
                        batch.draw(textures.get("7mines"), i, uncovered[i].length - 1 - j, 1, 1);
                        break;
                    case 8:
                        batch.draw(textures.get("8mines"), i, uncovered[i].length - 1 - j, 1, 1);
                        break;
                    case -1:
                        batch.draw(textures.get("mine"), i, uncovered[i].length - 1 - j, 1, 1);
                        break;
                }
            }
        }
    }

    private void place_flag() {
//        batch.draw(textures.get("flag"), pressedx, )
    }
}
