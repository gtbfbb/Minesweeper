package io.github.some_example_name;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Timer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements Screen {
    final Minesweeper game;
    private SpriteBatch batch;
    Map<String, TextureRegion> textures = new HashMap<>();
    static public Map<String, int[]> difficulties = new HashMap<>(Map.of(
        "easy", new int[]{10, 9, 9, 100},
        "medium", new int[]{40, 16, 16, 50},
        "hard", new int[]{99, 30, 16, 50}
    ));
    Texture texture;

    public static String difficulty = StartScreen.difficulty;
    static public int xsize = difficulties.get(difficulty)[1];
    static public int ysize = difficulties.get(difficulty)[2];
    static public int[][] grid = new int[ysize][xsize];
    static public  int[][] uncovered = new int[ysize][xsize];
    static public int mines = difficulties.get(difficulty)[0];
    static public int[] mineLocations = new int[mines];
    private int btncount = 0;
    private int uncoveredTiles = 0;
    private int pressedx, pressedy;
    private boolean win = false;



    public Main(final Minesweeper game){
        this.game = game;
        batch = new SpriteBatch();
        texture = new Texture("MinesweeperTileset.png");
        textures.put("tile", new TextureRegion(texture, 16, 32, 16, 16));

        //set up a dictionary for all the textures
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
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }


    @Override
    public void render(float delta) {
        draw();
        input();

    }


    public boolean game_over_status() {
        return win;
    }


    private void end_game() {
        if (uncoveredTiles == grid.length * grid[0].length - mines){
//            System.out.println("You won");
            win = true;
            game.setScreen(game.endScreen);
        }

        if (grid[pressedy][pressedx] == -1) {
//            System.out.println("You lose");
            game.setScreen(game.endScreen);
        }
    }


    private void input() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown (int x, int y, int pointer, int button) {
                if (button == 0) {
                    if (uncovered[y / difficulties.get(difficulty)[3]][x / difficulties.get(difficulty)[3]] != -1) {
                        pressedx = x / difficulties.get(difficulty)[3];
                        pressedy = y / difficulties.get(difficulty)[3];
                    }


                    btncount++;

                    if (btncount == 1 && button == 0) {
                        set_mines(pressedx, pressedy);
                        Arrays.sort(mineLocations);
                        set_tiles();
                    }

                    if (uncovered[pressedy][pressedx] == 1) {
                        uncoveredTiles++;
                        uncovered[pressedy][pressedx] = 0;
                        uncover_tiles(pressedx, pressedy);
                    }

                    batch.begin();
                    reveal_tiles();
                    batch.end();
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            end_game();
                        }
                    }, .1f);


                }



                if (button == 1) {
                    place_flag(x / difficulties.get(difficulty)[3], y / difficulties.get(difficulty)[3]);
                }

                return true;
            }
        });


        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            game.setScreen(game.mainMenu);
        }

    }



    private void logic() {
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        batch.setProjectionMatrix(game.viewport.getCamera().combined);
        batch.begin();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                batch.draw(textures.get("tile"), j, i, 1, 1);
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

        //the clicked tile and its neighbors should not be covering a mine
        for (int i = 0; i < clickedAreas.length; i++) {
            clickedAreas[i] = (rowOffset[i] + y) * xsize + (x + colOffest[i]);
        }

        outer:
        while (count < mines) {
            int randomMine = (int) (Math.random() * xsize * ysize);
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

            grid[randomMine / xsize][randomMine % xsize] = -1;
        }
    }

    private void set_tiles(){
        for (int mineLocation : mineLocations) {
            int row = mineLocation / xsize;
            int column = mineLocation % xsize;

            int[] rowOffset = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] colOffest = {-1, 0, 1, -1, 1, -1, 0, 1};


            for (int j = 0; j < rowOffset.length; j++) {
                //skip if the tile is off the board's grid
                if (rowOffset[j] + row > ysize - 1 || rowOffset[j] + row < 0 || colOffest[j] + column > xsize - 1 || colOffest[j] + column < 0) {
                    continue;
                }
                //add one to the tile's value if there is an adjacent mine
                grid[rowOffset[j] + row][colOffest[j] + column] += (grid[rowOffset[j] + row][colOffest[j] + column] != -1) ? 1 : 0;
            }
        }
    }

    private void uncover_tiles(int x, int y) {
        int[] rowOffset = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] colOffset = {-1, 0, 1, -1, 1, -1, 0, 1};


        //if there is a flag
        if (uncovered[y][x] == -1) {
            return;
        }

        //if the tile that is clicked has at least one adjacent mine
        if (grid[y][x] != 0) {
            return;
        }

        // if any of the conditionals are true, then the function will not call itself again
        for (int i = 0; i < colOffset.length; i++) {
            //make sure that the checked tiles does not extend beyond the board
            if (y + rowOffset[i] > ysize - 1 || y + rowOffset[i] < 0 || x + colOffset[i] > xsize - 1 || x + colOffset[i] < 0) {
                continue;
            }

            //make sure that the checked tile has not already been uncovered
            if (uncovered[y + rowOffset[i]][x + colOffset[i]] == 0) {
                continue;
            }

            //if the tile has not been uncovered yet and the tile has adjacent mines
            if (grid[y + rowOffset[i]][x + colOffset[i]] > 0 && uncovered[y + rowOffset[i]][x + colOffset[i]] != 0) {
                uncoveredTiles++;
                uncovered[y + rowOffset[i]][x + colOffset[i]] = 0;
                continue;
            }

            uncoveredTiles++;
            uncovered[y + rowOffset[i]][x + colOffset[i]] = 0;
            uncover_tiles(x + colOffset[i], y + rowOffset[i]);
        }
    }

    private void reveal_tiles() {
        for (int i = 0; i < uncovered[0].length; i++) {
            for (int j = 0; j < uncovered.length; j++) {
                if (uncovered[j][i] == 1) {
                    continue;
                }

                if (uncovered[j][i] == -1) {
                    batch.draw(textures.get("flag"), i, uncovered.length - 1 - j, 1, 1);
                    continue;
                }

                switch (grid[j][i]) {
                    case 0:
                        batch.draw(textures.get("0mines"), i, uncovered.length - 1 - j, 1, 1);
                        break;
                    case 1:
                        batch.draw(textures.get("1mines"), i, uncovered.length - 1 - j, 1, 1);
                        break;
                    case 2:
                        batch.draw(textures.get("2mines"), i, uncovered.length - 1 - j, 1, 1);
                        break;
                    case 3:
                        batch.draw(textures.get("3mines"), i, uncovered.length - 1 - j, 1, 1);
                        break;
                    case 4:
                        batch.draw(textures.get("4mines"), i, uncovered.length - 1 - j, 1, 1);
                        break;
                    case 5:
                        batch.draw(textures.get("5mines"), i, uncovered.length - 1 - j, 1, 1);
                        break;
                    case 6:
                        batch.draw(textures.get("6mines"), i, uncovered.length - 1 - j, 1, 1);
                        break;
                    case 7:
                        batch.draw(textures.get("7mines"), i, uncovered.length - 1 - j, 1, 1);
                        break;
                    case 8:
                        batch.draw(textures.get("8mines"), i, uncovered.length - 1 - j, 1, 1);
                        break;
                    case -1:
                        batch.draw(textures.get("mine"), i, uncovered.length - 1 - j, 1, 1);
                        break;
                }
            }
        }
    }

    private void place_flag(int x, int y) {
        uncovered[y][x] *= -1;
    }
}
