package io.github.some_example_name;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import javax.swing.*;

import java.util.Arrays;

import static io.github.some_example_name.Main.difficulty;


public class StartScreen implements Screen {
    final Minesweeper game;
    private Stage stage;
    private Skin skin;
    private SelectBox<String> selectBox;
    static public String difficulty = "easy";


    public StartScreen(Minesweeper game) {
        this.game = game;
    }


    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("glassy-ui.json"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-VariableFont_wdth,wght.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;
        BitmapFont font32 = generator.generateFont(parameter);
        String startText = "Welcome to Minesweeper";

        Label.LabelStyle style = new Label.LabelStyle(font32, Color.WHITE);
        Label text = new Label(startText, style);


        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);

        table.add(text).padBottom(25);
        generator.dispose();
        table.row();

        stage.addActor(table);
        selectBox = new SelectBox<>(skin);

        String[] options = {"easy", "medium", "hard"};
        selectBox.setItems(options);
        selectBox.setSelected(difficulty);

        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                difficulty = selectBox.getSelected();
                Main.difficulty = difficulty;
            }
        });



        TextButton myButton = new TextButton("Begin!", skin);

        myButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //set inital conditions based on difficulty
                game.gameScreen = new Main(game);

                Main.xsize = Main.difficulties.get(difficulty)[1];
                Main.ysize = Main.difficulties.get(difficulty)[2];
                game.viewport = new FitViewport(Main.difficulties.get(difficulty)[1], Main.difficulties.get(difficulty)[2]);
                Main.grid = new int[Main.ysize][Main.xsize];
                Main.uncovered = new int[Main.ysize][Main.xsize];
                Main.mines = Main.difficulties.get(difficulty)[0];
                Main.mineLocations = new int[Main.mines];
                for (int[] row : Main.uncovered) {
                    Arrays.fill(row, 1  );
                }

                Gdx.graphics.setWindowedMode(Main.xsize * Main.difficulties.get(difficulty)[3], Main.ysize * Main.difficulties.get(difficulty)[3]);
                game.setScreen(game.gameScreen);
                dispose();
            }
        });

        selectBox.setAlignment(Align.center);
        selectBox.getList().setAlignment(Align.center);

        table.add(myButton).size(myButton.getPrefWidth(), myButton.getPrefHeight()).padBottom(25);
        table.row();

        table.add(selectBox).width(350).height(50).padLeft(50);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);


        stage.act(delta);
        stage.draw();

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
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        skin.dispose();
        stage.dispose();
    }
}
